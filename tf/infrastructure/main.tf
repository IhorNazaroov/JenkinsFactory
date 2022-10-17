module network {
  source      = "./modules/network"
  tags        = var.tags
  env         = var.env
  name_prefix = var.name_prefix
}

module jenkins_master {
  source      = "./modules/jenkins_master"
  tags        = var.tags
  env         = var.env
  name_prefix = var.name_prefix
}
