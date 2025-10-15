# ===== 用户配置 =====
$Region         = "us-east-2"
$ClusterName    = "dev-eks"
$FargateProfile = "fp-default"
$CreateLogsVPCE = $true   # 如不需要 CloudWatch Logs VPCE，改为 $false

# ===== 小工具：调用 aws 并把 JSON 解析成对象 =====
function AwsJson { param([string[]]$ArgsArray)
  $raw = & aws @ArgsArray 2>$null
  if ([string]::IsNullOrWhiteSpace($raw)) { return $null }
  return $raw | ConvertFrom-Json
}
function Info($m){Write-Host "[INFO] $m" -ForegroundColor Cyan}
function Ok($m){Write-Host "[ OK ] $m" -ForegroundColor Green}
function Err($m){Write-Host "[ERR ] $m" -ForegroundColor Red}

# ===== 1) Pod Execution Role 权限 =====
Info "Querying Fargate Pod Execution Role..."
$fp = AwsJson @('eks','describe-fargate-profile','--region',$Region,'--cluster-name',$ClusterName,'--fargate-profile-name',$FargateProfile)
if (-not $fp) { Err "Cannot describe fargate profile."; exit 1 }
$RoleArn  = $fp.fargateProfile.podExecutionRoleArn
$RoleName = ($RoleArn -split '/')[ -1 ]
Ok "PodExecutionRole: $RoleName"

& aws iam attach-role-policy --role-name $RoleName --policy-arn arn:aws:iam::aws:policy/AmazonEKSFargatePodExecutionRolePolicy | Out-Null
& aws iam attach-role-policy --role-name $RoleName --policy-arn arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly | Out-Null
Ok "Role policies ensured."

# ===== 2) 发现 VPC / 私网子网 / 私网路由表 =====
$desc = AwsJson @('eks','describe-cluster','--region',$Region,'--name',$ClusterName)
$VpcId   = $desc.cluster.resourcesVpcConfig.vpcId
$Subnets = @($desc.cluster.resourcesVpcConfig.subnetIds)
Ok "VPC: $VpcId"
Ok ("Subnets: " + ($Subnets -join ", "))

$PrivateSubnets = @()
$PrivateRouteTables = @()

foreach ($sn in $Subnets) {
  $rtb = AwsJson @('ec2','describe-route-tables','--region',$Region,'--filters',"Name=association.subnet-id,Values=$sn")
  $rt  = $rtb.RouteTables | Select-Object -First 1
  if (-not $rt) { continue }
  $hasIgwDefault = $false
  foreach ($r in $rt.Routes) {
    if ($r.DestinationCidrBlock -eq '0.0.0.0/0' -and ($r.GatewayId -like 'igw-*')) { $hasIgwDefault = $true }
  }
  if (-not $hasIgwDefault) {
    $PrivateSubnets += $sn
    $PrivateRouteTables += $rt.RouteTableId
  }
}
$PrivateSubnets = $PrivateSubnets | Sort-Object -Unique
$PrivateRouteTables = $PrivateRouteTables | Sort-Object -Unique

if ($PrivateSubnets.Count -eq 0) { Err "No private subnets detected."; exit 1 }
Ok ("Private subnets: " + ($PrivateSubnets -join ", "))
Ok ("Private route tables: " + ($PrivateRouteTables -join ", "))

$vpc = AwsJson @('ec2','describe-vpcs','--region',$Region,'--vpc-ids',$VpcId)
$VpcCidr = $vpc.Vpcs[0].CidrBlock

# ===== 3) 为 Interface VPCE 创建/复用 SG（放行 443 from VPC） =====
$EpSgName = "$ClusterName-ecr-logs-endpoint-sg"
$sgList = AwsJson @('ec2','describe-security-groups','--region',$Region,'--filters',"Name=vpc-id,Values=$VpcId","Name=group-name,Values=$EpSgName")
if ($sgList -and $sgList.SecurityGroups.Count -gt 0) {
  $EpSgId = $sgList.SecurityGroups[0].GroupId
} else {
  $res = AwsJson @('ec2','create-security-group','--region',$Region,'--group-name',$EpSgName,'--description','SG for ECR/Logs interface endpoints','--vpc-id',$VpcId)
  $EpSgId = $res.GroupId
  & aws ec2 authorize-security-group-ingress --region $Region --group-id $EpSgId --ip-permissions "IpProtocol=tcp,FromPort=443,ToPort=443,IpRanges=[{CidrIp=$VpcCidr,Description='VPC CIDR'}]" | Out-Null
}
Ok "Endpoint SG: $EpSgId"

# ===== 4) 创建/确保 ECR API / DKR Interface Endpoints =====
function EnsureInterfaceEndpoint { param([string]$ServiceName)
  $vpces = AwsJson @('ec2','describe-vpc-endpoints','--region',$Region,'--filters',"Name=vpc-id,Values=$VpcId","Name=service-name,Values=$ServiceName")
  if (-not ($vpces -and $vpces.VpcEndpoints.Count -gt 0)) {
    $args = @('ec2','create-vpc-endpoint','--region',$Region,'--vpc-id',$VpcId,'--vpc-endpoint-type','Interface','--service-name',$ServiceName,'--security-group-ids',$EpSgId,'--private-dns-enabled','--subnet-ids') + $PrivateSubnets
    $null = AwsJson $args
  }
}

EnsureInterfaceEndpoint "com.amazonaws.$Region.ecr.api"
EnsureInterfaceEndpoint "com.amazonaws.$Region.ecr.dkr"

# ===== 5) 创建/确保 S3 Gateway Endpoint（挂到所有私网路由表） =====
$S3Svc = "com.amazonaws.$Region.s3"
$s3ep = AwsJson @('ec2','describe-vpc-endpoints','--region',$Region,'--filters',"Name=vpc-id,Values=$VpcId","Name=service-name,Values=$S3Svc")
if (-not ($s3ep -and $s3ep.VpcEndpoints.Count -gt 0)) {
  $args = @('ec2','create-vpc-endpoint','--region',$Region,'--vpc-id',$VpcId,'--vpc-endpoint-type','Gateway','--service-name',$S3Svc,'--route-table-ids') + $PrivateRouteTables
  $null = AwsJson $args
}

# ===== 6) （可选）CloudWatch Logs Interface Endpoint =====
if ($CreateLogsVPCE) {
  EnsureInterfaceEndpoint "com.amazonaws.$Region.logs"
}

Ok "Done."
