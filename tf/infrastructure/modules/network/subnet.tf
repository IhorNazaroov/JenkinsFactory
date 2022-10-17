resource aws_subnet jenkins_subnets {
  for_each = local.jenkins_subnets_az_to_cidr

  vpc_id            = aws_vpc.this.id
  availability_zone = each.key
  cidr_block        = each.value

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-${var.env}-jenkins-subnet-${each.key}"
    }
  )
}

resource aws_subnet jenkins_slave_factory {
  vpc_id            = aws_vpc.this.id
  availability_zone = "eu-central-1a"
  cidr_block        = local.jenkins_slave_factory_subnet_cidr

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-${var.env}-jenkins-slave-factory-subnet"
    }
  )
}
