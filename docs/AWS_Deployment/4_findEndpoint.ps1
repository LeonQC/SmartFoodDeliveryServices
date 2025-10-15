# 集群的 Cluster Security Group（Fargate Pod 一般就用它）
$Region = "us-east-2"
$Cluster = "dev-eks"

$clusterSgId = aws eks describe-cluster --region $Region --name $Cluster `
  --query 'cluster.resourcesVpcConfig.clusterSecurityGroupId' --output text
$clusterSgId

# ECR DKR 接口型 VPCE 的信息（看挂了哪个 SG / 子网 / 是否开了 Private DNS）
aws ec2 describe-vpc-endpoints --region $Region `
  --filters Name=service-name,Values=com.amazonaws.$Region.ecr.dkr `
            Name=vpc-id,Values=$(aws eks describe-cluster --region $Region --name $Cluster --query 'cluster.resourcesVpcConfig.vpcId' --output text) `
  --query 'VpcEndpoints[0].{id:VpcEndpointId,sg:Groups[*].GroupId,subnets:SubnetIds,privateDns:PrivateDnsEnabled}'

aws ec2 describe-vpc-endpoints --region $Region `
  --filters Name=service-name,Values=com.amazonaws.$Region.ecr.api `
            Name=vpc-id,Values=$(aws eks describe-cluster --region $Region --name dev-eks --query 'cluster.resourcesVpcConfig.vpcId' --output text) `
  --query 'VpcEndpoints[0].Groups[*].GroupId' --output text  
  
# 取你事件里 Pod 所在的私网 IP（10.0.11.224）去反查它的 ENI 和绑定的 SG
aws ec2 describe-network-interfaces --region $Region `
  --filters Name=private-ip-address,Values=10.0.11.224 `
  --query 'NetworkInterfaces[0].Groups[*].GroupId'
