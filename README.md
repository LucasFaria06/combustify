# 🚗 Combustify — SaaS de Preços de Combustível em Tempo Real

**Combustify** é uma plataforma que mostra preços de combustíveis em tempo real em postos de gasolina, permitindo que motoristas encontrem as melhores ofertas.

## 🎯 MVP Scope

- **Região:** Cuiabá, MT (inicialmente)
- **Combustíveis:** Gasolina, Diesel, Etanol, GNV
- **Modelo:** SaaS por assinatura
- **Coleta de dados:** Crowdsourcing de usuários

## 🛠️ Stack

### Backend
- **Java 21 LTS + Spring Boot 3.3+**
- **PostgreSQL 16** para persistência
- **Redis** para cache (opcional)
- **JWT** para autenticação
- **REST API**

### Frontend
- **Next.js 15+ (TypeScript)**
- **Tailwind CSS**
- **Zustand** para state management
- **Leaflet/Google Maps** para geolocalização

### Infrastructure
- **Docker Compose** para desenvolvimento
- **AWS/Render/Railway** para produção
- **GitHub Actions** para CI/CD

## 📁 Estrutura do Projeto

```
combustify/
├── backend/              # Spring Boot app
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/             # Next.js app
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml    # Dev environment
├── README.md
└── .gitignore
```

## 🚀 Como Começar

### Pré-requisitos
- Docker & Docker Compose
- Java 21+
- Node.js 18+
- PostgreSQL 16

### Setup Local

```bash
# Clone o repositório
git clone https://github.com/LucasFaria06/combustify.git
cd combustify

# Inicie os serviços
docker-compose up -d

# Backend (terminal 1)
cd backend
./mvnw spring-boot:run

# Frontend (terminal 2)
cd frontend
npm install
npm run dev
```

Acesse:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Database: localhost:5432

## 📊 Database Schema (MVP)

```sql
-- Users (Subscribers)
CREATE TABLE users (
  id UUID PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  password_hash VARCHAR(255),
  subscription_plan VARCHAR(50),
  created_at TIMESTAMP
);

-- Gas Stations (Postos)
CREATE TABLE gas_stations (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  latitude DECIMAL(10, 8),
  longitude DECIMAL(11, 8),
  city VARCHAR(100),
  created_at TIMESTAMP
);

-- Prices (Histórico de Preços)
CREATE TABLE prices (
  id UUID PRIMARY KEY,
  station_id UUID REFERENCES gas_stations(id),
  fuel_type VARCHAR(50), -- gasolina, diesel, etanol, gnv
  price DECIMAL(10, 2),
  reported_by UUID REFERENCES users(id),
  reported_at TIMESTAMP
);

-- Subscriptions
CREATE TABLE subscriptions (
  id UUID PRIMARY KEY,
  user_id UUID REFERENCES users(id),
  plan_name VARCHAR(50),
  status VARCHAR(20),
  starts_at TIMESTAMP,
  ends_at TIMESTAMP
);
```

## 💰 Pricing

| Plano | Preço | Consultas/dia | Histórico | Alertas | Suporte |
|-------|-------|---------------|-----------|---------|---------|
| **Free** | R$ 0 | 5 | 7 dias | 2/dia | — |
| **Basic** | R$ 12,90/mês | 200 | 30 dias | 10/dia | Email |
| **Pro** | R$ 24,90/mês | Ilimitado | 6 meses | Ilimitado | Prioritário |
| **Business** | Custom | Ilimitado + API | Completo | Ilimitado | Dedicado |

## 🤝 Contribuindo

1. Fork o repositório
2. Crie uma branch: `git checkout -b feature/sua-feature`
3. Commit: `git commit -m "feat: descrição"`
4. Push: `git push origin feature/sua-feature`
5. Abra um Pull Request

## 📝 Roadmap

### v0.1 (MVP — Agosto 2026)
- [ ] Backend básico (CRUD)
- [ ] Frontend básico (listagem de postos)
- [ ] Coleta crowdsourcing

### v0.2 (Setembro 2026)
- [ ] Dashboard admin
- [ ] Sistema de notificações
- [ ] Histórico de preços

### v0.3 (Outubro 2026)
- [ ] App mobile (PWA)
- [ ] Integração com pagamento
- [ ] Expansão para mais cidades

## 📄 Licença

MIT

## 👨‍💻 Autor

**Lucas Augusto** (LucasFaria06)

---

**Última atualização:** Agosto 2026
