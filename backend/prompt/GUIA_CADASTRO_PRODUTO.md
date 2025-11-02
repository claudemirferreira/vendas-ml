# 📦 Guia: Cadastro de Produto no Mercado Livre

## 🔗 Endpoint

**URL:** `POST /api/mercadolivre/produtos?userId={userId}`

**Base URL:** `http://localhost:8080`

**URL Completa:** `http://localhost:8080/api/mercadolivre/produtos?userId={userId}`

---

## 📋 Detalhes do Endpoint

### Método HTTP
`POST`

### Parâmetros

#### Query Parameter (URL)
- **`userId`** (obrigatório): ID do usuário que possui o token de acesso
  - Exemplo: `userId=123456789`

#### Request Body (JSON)
Objeto `ItemRequest` com os dados do produto:

```json
{
  "title": "Nome do Produto",
  "category_id": "MLB1144",
  "price": 99.90,
  "currency_id": "BRL",
  "available_quantity": 10,
  "buying_mode": "buy_it_now",
  "condition": "new",
  "listing_type_id": "gold_special",
  "description": {
    "plain_text": "Descrição detalhada do produto"
  },
  "pictures": [
    {
      "source": "https://http2.mlstatic.com/D_123456-O.jpg"
    }
  ]
}
```

### Headers
- **Content-Type:** `application/json`
- **Authorization:** Não é necessário (o token é obtido automaticamente pelo `userId`)

---

## ✅ Exemplo Completo

### cURL
```bash
curl -X POST "http://localhost:8080/api/mercadolivre/produtos?userId=123456789" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Produto de Teste",
    "category_id": "MLB1144",
    "price": 99.90,
    "currency_id": "BRL",
    "available_quantity": 10,
    "buying_mode": "buy_it_now",
    "condition": "new",
    "listing_type_id": "gold_special",
    "description": {
      "plain_text": "Descrição detalhada do produto de teste"
    },
    "pictures": [
      {
        "source": "https://http2.mlstatic.com/D_123456-O.jpg"
      }
    ]
  }'
```

### JavaScript (Fetch)
```javascript
fetch('http://localhost:8080/api/mercadolivre/produtos?userId=123456789', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    title: "Produto de Teste",
    category_id: "MLB1144",
    price: 99.90,
    currency_id: "BRL",
    available_quantity: 10,
    buying_mode: "buy_it_now",
    condition: "new",
    listing_type_id: "gold_special",
    description: {
      plain_text: "Descrição detalhada do produto de teste"
    },
    pictures: [
      {
        source: "https://http2.mlstatic.com/D_123456-O.jpg"
      }
    ]
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

### Postman
1. **Method:** `POST`
2. **URL:** `http://localhost:8080/api/mercadolivre/produtos?userId=123456789`
3. **Headers:**
   - `Content-Type: application/json`
4. **Body:** (raw JSON)
   ```json
   {
     "title": "Produto de Teste",
     "category_id": "MLB1144",
     "price": 99.90,
     "currency_id": "BRL",
     "available_quantity": 10,
     "buying_mode": "buy_it_now",
     "condition": "new",
     "listing_type_id": "gold_special",
     "description": {
       "plain_text": "Descrição detalhada do produto de teste"
     },
     "pictures": [
       {
         "source": "https://http2.mlstatic.com/D_123456-O.jpg"
       }
     ]
   }
   ```

---

## 📝 Estrutura do Request Body

### Campos Obrigatórios

| Campo | Tipo | Descrição | Exemplo |
|-------|------|-----------|---------|
| `title` | String | Título do produto (máx 256 chars) | "Produto de Teste" |
| `category_id` | String | ID da categoria | "MLB1144" |
| `price` | Double | Preço do produto | 99.90 |
| `currency_id` | String | Moeda (3 letras maiúsculas) | "BRL", "USD", "ARS" |
| `available_quantity` | Integer | Quantidade disponível (mín 1) | 10 |
| `buying_mode` | String | Modo de compra | "buy_it_now" |
| `condition` | String | Condição do produto | "new", "used", "not_specified" |
| `listing_type_id` | String | Tipo de anúncio | "gold_special", "gold_pro", "gold", "silver", "bronze" |
| `description.plain_text` | String | Descrição (máx 50000 chars) | "Descrição detalhada" |
| `pictures` | Array | Lista de imagens (máx 12) | Ver exemplo abaixo |

### Campos Opcionais
- Outros campos podem ser adicionados conforme necessário pela API do Mercado Livre

### Validações

- `title`: Máximo 256 caracteres
- `currency_id`: Deve seguir padrão `^[A-Z]{3}$` (ex: BRL, USD)
- `price`: Deve ser maior que 0.01
- `available_quantity`: Mínimo 1
- `description.plain_text`: Máximo 50000 caracteres
- `pictures`: Mínimo 1, máximo 12 imagens

---

## 📸 Exemplo com Múltiplas Imagens

```json
{
  "title": "Smartphone Samsung Galaxy",
  "category_id": "MLB1144",
  "price": 1299.90,
  "currency_id": "BRL",
  "available_quantity": 5,
  "buying_mode": "buy_it_now",
  "condition": "new",
  "listing_type_id": "gold_special",
  "description": {
    "plain_text": "Smartphone Samsung Galaxy com 128GB de armazenamento, 6GB RAM, tela de 6.7 polegadas. Inclui carregador e capa protetora."
  },
  "pictures": [
    {
      "source": "https://http2.mlstatic.com/D_123456-O.jpg"
    },
    {
      "source": "https://http2.mlstatic.com/D_789012-O.jpg"
    },
    {
      "source": "https://http2.mlstatic.com/D_345678-O.jpg"
    }
  ]
}
```

---

## 📤 Resposta de Sucesso

**Status Code:** `201 Created`

**Body:**
```json
{
  "id": "MLB123456789",
  "title": "Produto de Teste",
  "price": 99.90,
  "availableQuantity": 10,
  "status": "active",
  "permalink": "https://produto.mercadolivre.com.br/MLB-123456789"
}
```

---

## ❌ Respostas de Erro

### 400 Bad Request
```json
{
  "timestamp": "2025-11-02T10:00:00",
  "status": 400,
  "error": "Validation Error",
  "message": "Erro de validação nos dados fornecidos",
  "details": [
    "title: Título é obrigatório",
    "price: Preço deve ser maior que zero"
  ]
}
```

### 401 Unauthorized
Token inválido ou expirado. O sistema tentará fazer refresh automático.

### 404 Not Found
```json
{
  "timestamp": "2025-11-02T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Token não encontrado para usuário: 123456789"
}
```

---

## 🔍 Testando no Swagger

1. Acesse: http://localhost:8080/swagger-ui.html
2. Procure pela seção **"Mercado Livre"**
3. Encontre: `POST /api/mercadolivre/produtos`
4. Clique em **"Try it out"**
5. Preencha:
   - `userId`: ID do usuário (ex: `123456789`)
   - Request body: Cole o JSON do produto
6. Clique em **"Execute"**

---

## 💡 Dicas Importantes

### 1. Obter category_id
Use o endpoint de categorias para descobrir o ID correto:
```bash
GET /api/mercadolivre/categorias?siteId=MLB
GET /api/mercadolivre/categorias/MLB5672  # Para ver subcategorias
```

### 2. Obter userId
O `userId` é retornado quando você troca o código de autorização por token:
```bash
POST /api/mercadolivre/token
# Resposta inclui: "userId": 123456789
```

### 3. URL das Imagens
As imagens devem estar hospedadas publicamente. O Mercado Livre suporta:
- URLs HTTP/HTTPS válidas
- Imagens em formato: JPG, PNG, GIF
- Recomendado: Imagens maiores que 500x500 pixels

### 4. listing_type_id
Tipos disponíveis:
- `gold_special` - Destaque especial (mais visibilidade)
- `gold_pro` - Destaque profissional
- `gold` - Destaque
- `silver` - Prata
- `bronze` - Bronze

### 5. Token Automático
O sistema obtém e renova o token automaticamente baseado no `userId`. Você não precisa enviar o token no header.

---

## 🔗 Endpoint Original da API do Mercado Livre

Este endpoint utiliza internamente:
```
POST https://api.mercadolibre.com/items
Authorization: Bearer {access_token}
```

O token é obtido automaticamente do banco de dados usando o `userId`.

---

## 📚 Referências

- [Documentação Oficial - Publicações](https://developers.mercadolivre.com.br/pt_br/publicacao-de-produtos)
- [Validador de Publicações](https://developers.mercadolivre.com.br/pt_br/validador-de-publicacoes)
- [Lista de Categorias](https://developers.mercadolivre.com.br/pt_br/categorias-e-publicacoes)

---

## ✅ Checklist Antes de Cadastrar

- [ ] Token de acesso válido para o usuário (obtido via `/token`)
- [ ] `category_id` válido (verificado via `/categorias`)
- [ ] Todas as imagens acessíveis publicamente
- [ ] Preço maior que 0.01
- [ ] Quantidade mínima de 1
- [ ] Título com no máximo 256 caracteres
- [ ] Descrição com no máximo 50000 caracteres
- [ ] Máximo de 12 imagens
- [ ] `currency_id` correto para o site (BRL para Brasil)

