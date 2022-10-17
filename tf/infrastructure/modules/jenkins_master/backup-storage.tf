resource aws_s3_bucket backup_storage {
  bucket = "rdua-doaas-jenkins-master-backups"
  acl = "private"
  region = "eu-central-1"

  tags = merge(
    var.tags,
    {
      Name = "rdua-doaas-jenkins-master-backups"
    }
  )
}

resource aws_s3_bucket_public_access_block this {
  bucket = aws_s3_bucket.backup_storage.id

  block_public_acls   = true
  block_public_policy = true
}

resource aws_s3_bucket_object sso_integration_config_folder {
  bucket = aws_s3_bucket.backup_storage.id
  key = "sso-integration-config/"

  tags = merge(
    var.tags,
    {
      Name = "sso-integration-config"
    }
  )
}