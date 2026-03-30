terraform {
  required_version = ">= 1.5.0"

  backend "s3" {
    bucket         = "terraform-state-adopet-igor"
    region         = "sa-east-1"
    dynamodb_table = "terraform-locks-adopet"
    encrypt        = true
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}
