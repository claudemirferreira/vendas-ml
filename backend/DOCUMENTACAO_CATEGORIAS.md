# 📋 API de Categorias do Mercado Livre

## 🌐 Endpoints Implementados

### 1. Listar Categorias de um Site

**Endpoint:** `GET /api/mercadolivre/categorias?siteId={siteId}`

**Descrição:** Lista todas as categorias principais disponíveis em um site específico do Mercado Livre.

**Parâmetros:**
- `siteId` (query parameter, obrigatório): ID do site
  - `MLB` - Brasil
  - `MLA` - Argentina  
  - `MLM` - México
  - `MCO` - Colômbia
  - `MLC` - Chile
  - [Mais sites disponíveis](https://api.mercadolibre.com/sites)

**Exemplo de Requisição:**
```bash
curl -X GET "http://localhost:8080/api/mercadolivre/categorias?siteId=MLB"
```

**Exemplo de Resposta:**
```json
[
  {
    "id": "MLB5672",
    "name": "Acessórios para Veículos"
  },
  {
    "id": "MLB1499",
    "name": "Agro"
  },
  {
    "id": "MLB1403",
    "name": "Alimentos e Bebidas"
  },
  {
    "id": "MLB1144",
    "name": "Eletrônicos, Áudio e Vídeo"
  }
]
```

**Swagger:** Disponível em http://localhost:8080/swagger-ui.html

---

### 2. Obter Detalhes de uma Categoria

**Endpoint:** `GET /api/mercadolivre/categorias/{categoryId}`

**Descrição:** Obtém os detalhes completos de uma categoria específica, incluindo subcategorias (children_categories) e configurações.

**Parâmetros:**
- `categoryId` (path parameter, obrigatório): ID da categoria (ex: `MLB5672`)

**Exemplo de Requisição:**
```bash
curl -X GET "http://localhost:8080/api/mercadolivre/categorias/MLB5672"
```

**Exemplo de Resposta:**
```json
{
  "id": "MLB5672",
  "name": "Acessórios para Veículos",
  "childrenCategories": [
    {
      "id": "MLB1743",
      "name": "Peças Automotivas"
    },
    {
      "id": "MLB5673",
      "name": "Som Automotivo"
    },
    {
      "id": "MLB1744",
      "name": "Pneus e Rodas"
    }
  ],
  "settings": {
    "buyingAllowed": true,
    "listingAllowed": true,
    "maxPicturesPerItem": 10,
    "maxTitleLength": 60,
    "maxDescriptionLength": 10000,
    "buyingModes": ["buy_it_now", "auction"],
    "itemConditions": ["new", "used", "not_specified"],
    "shippingModes": ["custom", "not_specified"],
    "currencies": ["BRL"],
    "immediatePayment": "optional",
    "stock": "required"
  }
}
```

---

## 🔍 Sites Disponíveis

Para descobrir todos os sites disponíveis:

```bash
curl -X GET "https://api.mercadolibre.com/sites"
```

Principais sites:
- **MLB** - Brasil
- **MLA** - Argentina
- **MLM** - México
- **MCO** - Colômbia
- **MLC** - Chile
- **MLU** - Uruguai
- **MPE** - Peru

---

## 📝 Exemplos de Uso

### Exemplo 1: Listar todas as categorias do Brasil
```bash
GET /api/mercadolivre/categorias?siteId=MLB
```

### Exemplo 2: Obter subcategorias de "Acessórios para Veículos"
```bash
GET /api/mercadolivre/categorias/MLB5672
```

### Exemplo 3: Buscar categoria recursivamente (Java)
```java
// Listar categorias principais
List<CategoryResponse> mainCategories = mercadoLivreService.getCategories("MLB");

// Para cada categoria, buscar subcategorias
for (CategoryResponse category : mainCategories) {
    CategoryResponse details = mercadoLivreService.getCategory(category.getId());
    if (details.getChildrenCategories() != null) {
        System.out.println("Subcategorias de " + category.getName() + ":");
        details.getChildrenCategories().forEach(sub -> {
            System.out.println("  - " + sub.getName() + " (" + sub.getId() + ")");
        });
    }
}
```

---

## 🚀 Endpoints da API Original do Mercado Livre

A implementação utiliza os seguintes endpoints da API do Mercado Livre:

1. **Listar categorias:** 
   ```
   GET https://api.mercadolibre.com/sites/{site_id}/categories
   ```

2. **Detalhes da categoria:**
   ```
   GET https://api.mercadolibre.com/categories/{category_id}
   ```

**Importante:** Estes endpoints são **públicos** e **não requerem autenticação**. Você pode testá-los diretamente no navegador ou com curl.

---

## ✅ Testando no Swagger

1. Acesse: http://localhost:8080/swagger-ui.html
2. Procure pela seção **"Mercado Livre"**
3. Encontre os endpoints:
   - `GET /api/mercadolivre/categorias`
   - `GET /api/mercadolivre/categorias/{categoryId}`
4. Clique em **"Try it out"**
5. Preencha os parâmetros e execute

---

## 📚 Referências

- [Documentação Oficial - Categorias](https://developers.mercadolivre.com.br/pt_br/categorias-e-publicacoes)
- [API de Sites](https://api.mercadolibre.com/sites)
- [API de Categorias (Exemplo)](https://api.mercadolibre.com/sites/MLB/categories)

---

## 🎯 Casos de Uso

### Caso 1: Criar um seletor de categorias
```javascript
// 1. Listar categorias principais
fetch('/api/mercadolivre/categorias?siteId=MLB')
  .then(r => r.json())
  .then(categories => {
    // 2. Quando usuário selecionar, buscar subcategorias
    categories.forEach(cat => {
      fetch(`/api/mercadolivre/categorias/${cat.id}`)
        .then(r => r.json())
        .then(details => {
          console.log(`Categoria: ${cat.name}`);
          console.log(`Subcategorias:`, details.childrenCategories);
        });
    });
  });
```

### Caso 2: Validar category_id antes de criar produto
```java
// Validar se a categoria existe e está ativa
CategoryResponse category = mercadoLivreService.getCategory("MLB5672");
if (category.getSettings().getListingAllowed()) {
    // Categoria permite publicação, prosseguir...
}
```

