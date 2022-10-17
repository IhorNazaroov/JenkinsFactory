data aws_eip jenkins_master_public_ip {
  tags = {
    Name = "rdua-doaas-jenkins-master-ip"
  }
}

data aws_vpc rdua_doaas {
  tags = {
    Name = "rdua-doaas-dev"
  }
}

data aws_security_group epam_europe_office_whitelist {
  name   = "epam-europe"
  vpc_id = data.aws_vpc.rdua_doaas.id
}

data aws_security_group default {
  name   = "default"
  vpc_id = data.aws_vpc.rdua_doaas.id
}

data aws_subnet_ids jenkins_subnet_ids {
  vpc_id = data.aws_vpc.rdua_doaas.id

  tags = {
    Name = "rdua-doaas-dev-jenkins-subnet-eu-central-1a"
  }
}

data aws_ami rdua_doaas_base {
  owners = ["self"]
  name_regex = "rdua-doaas-base-ami-dev-stable"
}
