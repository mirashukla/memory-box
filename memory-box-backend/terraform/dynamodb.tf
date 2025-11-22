
resource "aws_dynamodb_table" "memories" {
  name         = "MemoryBoxTable"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "username"
  range_key    = "createdAt"

  attribute {
    name = "username"
    type = "S"
  }

  attribute {
    name = "createdAt"
    type = "S"
  }

  tags = {
    Project = "memory-box"
  }
}

resource "aws_dynamodb_table" "users" {
  name         = "MemoryBoxUsers"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "email"

  attribute {
    name = "email"
    type = "S"
  }
}

resource "aws_iam_role_policy" "lambda_dynamodb" {
  role = aws_iam_role.lambda_exec.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "dynamodb:PutItem",
          "dynamodb:Query",
          "dynamodb:GetItem"
        ]
        Resource = [
          aws_dynamodb_table.memories.arn,
          aws_dynamodb_table.users.arn
        ]
      }
    ]
  })
}
