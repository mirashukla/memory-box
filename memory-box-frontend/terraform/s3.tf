resource "aws_s3_bucket" "static" {
  bucket = "mira-memory-box"
}

resource "aws_s3_bucket_website_configuration" "static-website-bucket-config" {
  bucket = aws_s3_bucket.static.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }

}

resource "aws_s3_bucket_public_access_block" "static-website-bucket-block" {
  bucket = aws_s3_bucket.static.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "static" {
  bucket = aws_s3_bucket.static.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Sid       = "PublicReadGetObject"
      Effect    = "Allow"
      Principal = "*"
      Action    = "s3:GetObject"
      Resource  = "${aws_s3_bucket.static.arn}/*"
    }]
  })
}

resource "aws_s3_object" "site" {
  for_each = fileset(local.site_dir, "**")

  bucket = aws_s3_bucket.static.id
  key    = each.value
  source = "${local.site_dir}/${each.value}"

  etag = filemd5("${local.site_dir}/${each.value}")

  content_type = lookup(
    local.content_types,
    regex("\\.[^.]+$", each.value),
    "binary/octet-stream"
  )
}

locals {
  site_dir = "${path.module}/../dist"

  content_types = {
    ".html" = "text/html"
    ".css"  = "text/css"
    ".js"   = "text/javascript"
    ".png"  = "image/png"
    ".jpg"  = "image/jpeg"
    ".gif"  = "image/gif"
  }
}

resource "aws_s3_bucket_cors_configuration" "static" {
  bucket = aws_s3_bucket.static.id

  cors_rule {
    allowed_headers = ["Authorization", "Content-Length"]
    allowed_methods = ["GET"]
    allowed_origins = ["*"]
    max_age_seconds = 3000
  }
}
