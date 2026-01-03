variable "auth_secret_name" {
  description = "Name of the JWT secret in SSM Parameter Store"
  type        = string
  sensitive   = true
}

variable "auth_secret_arn" {
  description = "ARN of the JWT secret in SSM Parameter Store"
  type        = string
  sensitive   = true
}
