output "alb_dns_name" {
  description = "DNS do Load Balancer da aplicação"
  value       = aws_lb.app.dns_name
}

output "ecs_cluster_name" {
  value = aws_ecs_cluster.app.name
}

output "ecs_service_name" {
  value = aws_ecs_service.app.name
}

output "alerts_topic_arn" {
  description = "ARN do tópico SNS usado para alertas"
  value       = aws_sns_topic.alerts.arn
}
