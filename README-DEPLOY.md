# ANUBIS BACKEND - GUÍA DE DESPLIEGUE

## REQUISITOS PREVIOS
- Docker y Docker Compose instalados
- Git instalado

## INSTALACIÓN RÁPIDA (UN SOLO COMANDO)

```bash
# Clonar repositorio
git clone https://github.com/yojair0/backend-anubis.git
cd backend-anubis

# Configurar variables de entorno
cp .env.template .env

# Construir y ejecutar
docker-compose up --build -d
```

## COMANDOS DISPONIBLES

### Iniciar aplicación
```bash
docker-compose up -d
```

### Ver logs
```bash
docker-compose logs -f backend
```

### Parar aplicación
```bash
docker-compose down
```

### Rebuild completo
```bash
docker-compose down
docker-compose up --build -d
```

### Limpiar todo (contenedores, imágenes, volúmenes)
```bash
docker-compose down -v --rmi all
docker system prune -af
```

## ENDPOINTS DISPONIBLES

### Registro de usuario
POST http://localhost:8081/api/auth/register
Content-Type: application/json

```json
{
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Juan Perez",
    "phone": "123456789"
}
```

### Verificación de email
POST http://localhost:8081/api/auth/verify-email?email=test@example.com&code=123456

### Health check
GET http://localhost:8081/health

## CONFIGURACIÓN

### Variables de entorno (.env)
- MONGO_URI: URI de conexión a MongoDB Atlas
- JWT_SECRET: Clave secreta para JWT
- GMAIL_USER: Email de Gmail para envío de correos
- GMAIL_APP_PASSWORD: App Password de Gmail

### Puertos
- Backend: http://localhost:8081

## SOLUCIÓN DE PROBLEMAS

### Backend no inicia
```bash
docker-compose logs backend
```

### Problemas de conexión MongoDB
- Verificar MONGO_URI en .env
- Verificar conectividad a internet

### Problemas de email
- Verificar GMAIL_USER y GMAIL_APP_PASSWORD
- Verificar que Gmail tenga App Passwords habilitado

## ESTRUCTURA DEL PROYECTO
```
backend-anubis/
├── src/main/java/com/anubis/
├── Dockerfile
├── docker-compose.yml
├── .env.template
├── pom.xml
└── README.md
```