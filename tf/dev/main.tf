terraform {
  backend s3 {
    bucket = "rdua-dooas-terraform-remote-state"
    key    = "dev/infrastructure.tf"
    region = "eu-central-1"
    dynamodb_table = "rdua-doaas-terraform-execution-lock"
  }
}

provider aws {
  region = "eu-central-1"
}

module infrastructure {
  source = "../infrastructure"

  env = "dev"

  tags = {
    ManagedBy = "terraform"
    Environment = "dev"
    Project = "DOaaS"
  }
}