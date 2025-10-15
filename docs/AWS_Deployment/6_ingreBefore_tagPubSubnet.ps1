$region  = "us-east-2"
$subnets = "subnet-0ddef7f980fe19c7c","subnet-0d733b663891343be"   # 公网子网 A/B

aws ec2 create-tags --region $region --resources $subnets `
  --tags Key=kubernetes.io/cluster/dev-eks,Value=shared `
         Key=kubernetes.io/role/elb,Value=1
aws ec2 describe-subnets --region $region --subnet-ids $subnets --query 'Subnets[].{id:SubnetId,tags:Tags}'



# Or add in ingress.yaml: alb.ingress.kubernetes.io/subnets: subnet-xxxxxxxx,subnet-yyyyyyyy