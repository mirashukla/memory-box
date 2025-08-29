terraform {
  backend "s3" {
    bucket         = "mira-terraform-project-tf-state"
    key            = "memory-box/terraform.tfstate"
    region         = "eu-west-1"
    dynamodb_table = "terraform-state-locking"
    encrypt        = true
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.11.0"
    }
  }
}

provider "aws" {
  region = "eu-west-1"
}