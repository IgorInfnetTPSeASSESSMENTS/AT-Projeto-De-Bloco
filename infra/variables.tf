variable "project_name" {
  description = "Prefixo para nomear recursos"
  type        = string
  default     = "adopet"
}

variable "environment_name" {
  description = "Nome do ambiente de deploy"
  type        = string
}

variable "vpc_id" {
  description = "VPC onde ECS/ALB vão rodar"
  type        = string
}

variable "public_subnet_ids" {
  description = "Subnets públicas para ALB e Fargate (string com IDs separados por vírgula)"
  type        = string
}

locals {
  public_subnet_ids_list = split(",", var.public_subnet_ids)
  full_project_name      = "${var.project_name}-${var.environment_name}"
}

variable "docker_image" {
  description = "Imagem Docker da aplicação"
  type        = string
}

variable "alert_email" {
  description = "Email que receberá os alertas do CloudWatch via SNS"
  type        = string
  default     = ""
}
