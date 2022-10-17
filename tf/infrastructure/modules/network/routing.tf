resource aws_route_table main_rt {
  vpc_id = aws_vpc.this.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.this.id
  }

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-${var.env}-router"
    }
  )
}

resource aws_main_route_table_association main_rt_vpc_link {
  vpc_id         = aws_vpc.this.id
  route_table_id = aws_route_table.main_rt.id
}

resource aws_route_table_association jenkins_subnets_to_rt_links {
  for_each = aws_subnet.jenkins_subnets

  subnet_id      = each.value.id
  route_table_id = aws_route_table.main_rt.id
}

resource aws_route_table_association jenkins_slave_factory_subnet_to_rt_link {
  subnet_id      = aws_subnet.jenkins_slave_factory.id
  route_table_id = aws_route_table.main_rt.id
}

