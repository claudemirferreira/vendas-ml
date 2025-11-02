# 📊 Monitoramento com Prometheus e Grafana

## 🚀 Início Rápido

### 1. Iniciar Serviços
```bash
docker-compose up -d
```

### 2. Iniciar Aplicação Spring Boot
```bash
mvn spring-boot:run
```

### 3. Acessar Ferramentas

| Ferramenta | URL | Credenciais |
|-----------|-----|-------------|
| **Grafana** | http://localhost:3000 | admin / admin |
| **Prometheus** | http://localhost:9090 | - |
| **Spring Boot Metrics** | http://localhost:8080/actuator/prometheus | - |

## 📈 Métricas Disponíveis

### Métricas HTTP
- `http_server_requests_seconds_count` - Contador de requisições
- `http_server_requests_seconds_sum` - Soma do tempo de resposta
- `http_server_requests_seconds_bucket` - Histograma de latência

### Métricas JVM
- `jvm_memory_used_bytes` - Memória utilizada
- `jvm_memory_max_bytes` - Memória máxima
- `jvm_threads_live_threads` - Threads ativas
- `jvm_gc_pause_seconds` - Pausas do Garbage Collector

### Métricas de Banco de Dados
- `hikari_connections_active` - Conexões ativas
- `hikari_connections_idle` - Conexões ociosas
- `hikari_connections_pending` - Conexões pendentes

### Métricas de Sistema
- `process_cpu_usage` - Uso de CPU
- `system_cpu_usage` - Uso de CPU do sistema
- `jvm_classes_loaded_classes` - Classes carregadas

## 🔍 Queries Úteis no Prometheus

### Taxa de Requisições por Minuto
```promql
rate(http_server_requests_seconds_count{application="vendasml"}[5m])
```

### Tempo de Resposta (p95)
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application="vendasml"}[5m]))
```

### Uso de Memória Heap
```promql
jvm_memory_used_bytes{application="vendasml", area="heap"}
```

### Conexões Ativas do Banco
```promql
hikari_connections_active{application="vendasml"}
```

## 📊 Dashboards no Grafana

O projeto inclui um dashboard pré-configurado com:
- ✅ Taxa de requisições HTTP
- ✅ Tempo de resposta (p95)
- ✅ Uso de memória JVM
- ✅ Pool de conexões do banco
- ✅ Status de saúde da aplicação
- ✅ Threads JVM

## ⚙️ Configuração

### Alterar Intervalo de Coleta

Edite `prometheus/prometheus.yml`:
```yaml
global:
  scrape_interval: 30s  # Ajuste conforme necessário
```

### Adicionar Novas Métricas Customizadas

No código Java:
```java
@Autowired
private MeterRegistry meterRegistry;

public void exemploMetrica() {
    Counter.builder("custom.metric")
        .tag("application", "vendasml")
        .register(meterRegistry)
        .increment();
}
```

## 🐛 Troubleshooting

### Prometheus não consegue coletar métricas

1. Verifique se a aplicação está rodando:
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

2. No Windows, o `host.docker.internal` pode não funcionar. Tente:
   - Usar o IP da máquina host
   - Ou rodar a aplicação também no Docker

3. Verifique os targets no Prometheus:
   - Acesse http://localhost:9090/targets
   - O target `vendasml` deve estar "UP"

### Grafana não carrega dashboards

1. Verifique os volumes do Docker:
   ```bash
   docker-compose ps
   ```

2. Verifique os logs:
   ```bash
   docker-compose logs grafana
   ```

3. Recarregue a configuração:
   ```bash
   docker-compose restart grafana
   ```

## 📚 Referências

- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Prometheus](https://micrometer.io/docs/registry/prometheus)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)

