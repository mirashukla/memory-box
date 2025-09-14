# S3 bucket to store Lambda code
resource "aws_s3_bucket" "lambda_bucket" {
  bucket = "memory-box-lambda-artifacts"
}

# Upload your JAR to S3
resource "aws_s3_object" "lambda_jar" {
  bucket = aws_s3_bucket.lambda_bucket.id
  key    = "memory-box.jar"
  source = "../build/libs/memory-box.jar"
  etag   = filemd5("../build/libs/memory-box.jar")
}

# IAM role for Lambda
resource "aws_iam_role" "lambda_exec" {
  name = "memory-box-lambda-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
        Effect = "Allow"
      }
    ]
  })
}

# Attach AWSLambdaBasicExecutionRole policy (for CloudWatch logs)
resource "aws_iam_role_policy_attachment" "lambda_logs" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# Lambda function
resource "aws_lambda_function" "memory_box" {
  function_name = "memory-box"
  s3_bucket     = aws_s3_bucket.lambda_bucket.id
  s3_key        = aws_s3_object.lambda_jar.key
  handler       = "org.mira.lambda.MemoryHandler"
  runtime       = "java21"
  role          = aws_iam_role.lambda_exec.arn
  source_code_hash = filebase64sha256("${path.module}/../build/libs/memory-box.jar")
}

# API Gateway (HTTP API)
resource "aws_apigatewayv2_api" "http_api" {
  name          = "memory-box-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_integration" "lambda_integration" {
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "AWS_PROXY"
  integration_uri        = aws_lambda_function.memory_box.invoke_arn
  payload_format_version = "2.0"
}

resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}

# Permission so API Gateway can invoke Lambda
resource "aws_lambda_permission" "apigw_invoke" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.memory_box.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.http_api.execution_arn}/*/*"
}

output "api_endpoint" {
  value = aws_apigatewayv2_stage.default_stage.invoke_url
}

# Route: POST /memories
resource "aws_apigatewayv2_route" "post_memories" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "POST /memories"
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"
}

# Route: GET /memories
resource "aws_apigatewayv2_route" "get_memories" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "GET /memories"
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"
}