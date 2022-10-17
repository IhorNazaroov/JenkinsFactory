locals {
  jenkins_master_deploy_subnet_id = sort(data.aws_subnet_ids.jenkins_subnet_ids.ids)[0]
}

resource aws_network_interface jenkins_interface {
  subnet_id       = local.jenkins_master_deploy_subnet_id
  security_groups = [
    data.aws_security_group.epam_europe_office_whitelist.id,
    data.aws_security_group.default.id,
  ]
  
  private_ips     = ["10.0.1.5"]

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-jenkins-master-interface"
    }
  )
}

resource aws_eip_association jenkins_ip {
  network_interface_id = aws_network_interface.jenkins_interface.id
  allocation_id        = data.aws_eip.jenkins_master_public_ip.id
}

resource aws_instance jenkins {
  ami           = data.aws_ami.rdua_doaas_base.id
  instance_type = "t2.micro"

  network_interface {
    network_interface_id = aws_network_interface.jenkins_interface.id
    device_index         = 0
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-jenkins-master-node"
    }
  )
  volume_tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-jenkins-master-node-volume"
    }
  )
}
