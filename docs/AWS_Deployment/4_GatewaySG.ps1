$Region    = "us-east-2"
$ClusterSG = "sg-0dddd4a719085fce7"   # Cluster SG
$DkrSG     = "sg-0b893858e226fdcf1"   # DKR Endpoint SG
$ApiSG     = "sg-0b893858e226fdcf1"   # API Endpoint SG（如不同就换成 API 的）

# DKR
aws ec2 authorize-security-group-ingress --region $Region `
  --group-id $DkrSG `
  --ip-permissions IpProtocol=tcp,FromPort=443,ToPort=443,UserIdGroupPairs="[{GroupId='$ClusterSG',Description='EKS Cluster SG'}]"

# API
aws ec2 authorize-security-group-ingress --region $Region `
  --group-id $ApiSG `
  --ip-permissions IpProtocol=tcp,FromPort=443,ToPort=443,UserIdGroupPairs="[{GroupId='$ClusterSG',Description='EKS Cluster SG'}]"
