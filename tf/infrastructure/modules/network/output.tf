output vpc_id {
  value = aws_vpc.this.id
}

# Can be used as a waiter for module execution finish
output igw_id {
  value = aws_internet_gateway.this.id
}