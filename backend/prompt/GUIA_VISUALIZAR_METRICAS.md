# 📊 Guia: Como Visualizar Métricas no Grafana

## 🚀 Passo a Passo Completo

### 1️⃣ Verificar se os Serviços Estão Rodando

```bash
# Verificar containers
docker-compose ps

# Se não estiverem rodando, inicie-os
docker-compose up -d

# Verificar logs
docker-compose logs -f grafana
docker-compose logs -f prometheus
```

### 2️⃣ Verificar se a Aplicação Spring Boot Está Rodando

```bash
# Verificar se o endpoint de métricas está acessível
curl http://localhost:8080/actuator/prometheus

# Ou abra no navegador:
# http://localhost:8080/actuator/prometheus
```

Se você ver métricas no formato Prometheus (texto com `# HELP`, `# TYPE`, etc.), está funcionando! ✅

### 3️⃣ Verificar se o Prometheus Está Coletando Métricas

1. Abra: **http://localhost:9090**
2. Vá em **Status → Targets**
3. Verifique se o target `vendasml` está **UP** (verde)
4. Se estiver **DOWN**, verifique:
   - Se a aplicação está rodando
   - Se o endpoint `/actuator/prometheus` está acessível
   - Se o `host.docker.internal` funciona no seu sistema

**Teste uma query no Prometheus:**
- Vá em **Graph** (no topo)
- Digite: `up{job="vendasml"}`
- Execute (clicando em "Execute" ou Enter)
- Deve retornar `1` (UP) ou `0` (DOWN)

### 4️⃣ Acessar o Grafana

1. Abra: **http://localhost:3000**
2. **Login:**
   - Usuário: `admin`
   - Senha: `admin`
3. No primeiro acesso, o Grafana pedirá para alterar a senha (opcional)

### 5️⃣ Verificar Data Source

1. No Grafana, vá em: **⚙️ (Configurações) → Data Sources**
2. Deve existir um data source chamado **"Prometheus"**
3. Clique nele e verifique:
   - **URL:** `http://prometheus:9090`
   - **Status:** Deve mostrar "Data source is working" (verde)
4. Se não estiver funcionando:
   - Clique em **Save & Test**
   - Verifique os logs: `docker-compose logs grafana`

### 6️⃣ Acessar o Dashboard

**Opção 1: Dashboard Automático (Recomendado)**
- Após o login, o dashboard pode aparecer automaticamente no menu lateral
- Procure por: **Dashboards → Spring Boot Metrics - Vendas ML**

**Opção 2: Criar/Importar Dashboard Manualmente**
1. Vá em **+ (Plus) → Import**
2. Se o dashboard não aparecer automaticamente:
   - Clique em **+ (Plus) → Dashboard**
   - Clique em **Add visualization**
   - Selecione o data source **Prometheus**
   - Digite uma query, por exemplo: `rate(http_server_requests_seconds_count{application="vendasml"}[5m])`

**Opção 3: Verificar se o Dashboard Foi Carregado**
1. Vá em **Dashboards → Browse**
2. Procure por "Spring Boot Metrics - Vendas ML"
3. Se não encontrar, verifique:
   - `docker-compose logs grafana` para erros
   - Se os arquivos estão nos volumes corretos

### 7️⃣ Criar Dashboard Manualmente (Se Necessário)

Se o dashboard automático não funcionar, crie manualmente:

1. **Criar Novo Dashboard:**
   - **+ (Plus) → Dashboard → Add visualization**

2. **Adicionar Painéis (Panels):**

   **Painel 1: Taxa de Requisições HTTP**
   - Query: `rate(http_server_requests_seconds_count{application="vendasml"}[5m])`
   - Legend: `{{method}} {{uri}}`
   - Title: "HTTP Requests Rate"

   **Painel 2: Tempo de Resposta p95**
   - Query: `histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application="vendasml"}[5m]))`
   - Legend: "p95"
   - Title: "HTTP Response Time (p95)"

   **Painel 3: Memória JVM**
   - Query 1: `jvm_memory_used_bytes{application="vendasml", area="heap"}`
   - Query 2: `jvm_memory_max_bytes{application="vendasml", area="heap"}`
   - Title: "JVM Memory Usage"

   **Painel 4: Pool de Conexões**
   - Query 1: `hikari_connections_active{application="vendasml"}`
   - Query 2: `hikari_connections_idle{application="vendasml"}`
   - Title: "Database Connection Pool"

   **Painel 5: Saúde da Aplicação**
   - Query: `up{job="vendasml"}`
   - Visualização: **Stat** (não gráfico)
   - Title: "Application Health"

   **Painel 6: Threads JVM**
   - Query: `jvm_threads_live_threads{application="vendasml"}`
   - Title: "JVM Threads"

3. **Salvar o Dashboard:**
   - Clique em **Save** (ícone de disquete)
   - Dê um nome: "Spring Boot Metrics - Vendas ML"

## 🔍 Queries Úteis para Testar

Cole estas queries no Grafana para verificar se as métricas estão chegando:

```promql
# Verificar se a aplicação está UP
up{job="vendasml"}

# Taxa de requisições HTTP
rate(http_server_requests_seconds_count{application="vendasml"}[5m])

# Memória heap usada
jvm_memory_used_bytes{application="vendasml", area="heap"}

# Conexões ativas do banco
hikari_connections_active{application="vendasml"}

# Threads JVM
jvm_threads_live_threads{application="vendasml"}

# CPU usado
process_cpu_usage{application="vendasml"}
```

## 🐛 Troubleshooting

### Problema: "No data" nos painéis do Grafana

**Soluções:**
1. Verifique se o Prometheus está coletando:
   - Acesse http://localhost:9090/graph
   - Execute: `up{job="vendasml"}`
   - Deve retornar `1`

2. Verifique o intervalo de tempo:
   - No Grafana, ajuste o seletor de tempo (canto superior direito)
   - Use: **Last 15 minutes** ou **Last 1 hour**

3. Verifique se há métricas disponíveis:
   ```bash
   curl http://localhost:8080/actuator/prometheus | grep vendasml
   ```

### Problema: Prometheus não consegue acessar a aplicação

**No Windows:**
- `host.docker.internal` pode não funcionar
- **Solução:** Edite `prometheus/prometheus.yml` e use:
  ```yaml
  - targets: ['SEU_IP_LOCAL:8080']
  ```
  Ou use o IP da sua máquina na rede Docker:
  ```yaml
  - targets: ['172.17.0.1:8080']  # IP padrão do Docker no Windows
  ```

### Problema: Dashboard não aparece automaticamente

**Soluções:**
1. Verifique os logs do Grafana:
   ```bash
   docker-compose logs grafana | grep -i dashboard
   ```

2. Reinicie o Grafana:
   ```bash
   docker-compose restart grafana
   ```

3. Verifique se os arquivos estão corretos:
   ```bash
   ls -la grafana/dashboards/
   ls -la grafana/provisioning/dashboards/
   ```

4. Crie o dashboard manualmente (veja Passo 7 acima)

### Problema: Data Source não conecta

**Soluções:**
1. Verifique se o Prometheus está acessível:
   ```bash
   docker-compose exec grafana wget -O- http://prometheus:9090/api/v1/status/config
   ```

2. Edite o data source manualmente:
   - Grafana → Configuration → Data Sources → Prometheus
   - URL: `http://prometheus:9090` (dentro do Docker)
   - Ou `http://localhost:9090` (se acessando de fora do Docker)

## ✅ Checklist Final

Antes de visualizar as métricas, confirme:

- [ ] Docker Compose está rodando (`docker-compose ps`)
- [ ] Aplicação Spring Boot está rodando na porta 8080
- [ ] Endpoint `/actuator/prometheus` retorna métricas
- [ ] Prometheus mostra target `vendasml` como UP
- [ ] Grafana está acessível em http://localhost:3000
- [ ] Data source Prometheus está configurado e testado
- [ ] Dashboard foi carregado ou criado manualmente

## 📚 Próximos Passos

Após visualizar as métricas:

1. **Customize os painéis** conforme suas necessidades
2. **Configure alertas** no Prometheus ou Grafana
3. **Adicione métricas customizadas** no seu código Java
4. **Crie dashboards adicionais** para métricas de negócio específicas

