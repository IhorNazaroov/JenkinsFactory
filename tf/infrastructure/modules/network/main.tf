locals {
  jenkins_subnets_az_to_cidr = {
    eu-central-1a = "10.0.1.0/24"
    eu-central-1b = "10.0.2.0/24"
    eu-central-1c = "10.0.3.0/24"
  }
  jenkins_slave_factory_subnet_cidr = "10.0.4.0/24"
}
