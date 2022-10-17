resource aws_vpc this {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-${var.env}"
    }
  )
}

resource aws_internet_gateway this {
  vpc_id = aws_vpc.this.id

  tags = merge(
    var.tags,
    {
      Name = "${var.name_prefix}-${var.env}-igw"
    }
  )
}
