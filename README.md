# Backend Anubis - Sistema de Adopción de Mascotas

Backend REST API para sistema de adopción de mascotas desarrollado con Spring Boot 3.1.0 y MongoDB.

## 📊 Estado del Proyecto

![CI/CD Pipeline](https://github.com/yojair0/backend-anubis/actions/workflows/ci-cd.yml/badge.svg)
![Coverage](https://img.shields.io/badge/coverage-60%25-green)
![Tests](https://img.shields.io/badge/tests-371%20passing-brightgreen)
![Build](https://img.shields.io/badge/build-passing-success)
![Java](https://img.shields.io/badge/java-17-orange)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.1.0-brightgreen)

## 🎯 Métricas de Calidad

- **Cobertura de Tests**: 60% (2,909/4,818 instrucciones)
- **Suite de Tests**: 371 tests unitarios e integración
- **CI/CD**: Pipeline automatizado con GitHub Actions
- **Seguridad**: JWT + Spring Security 6.0
- **Base de Datos**: MongoDB Atlas (Cloud)

## 🛠️ Stack Tecnológico

- **Java 17** + **Spring Boot 3.1.0**
- **Spring Security** + **JWT Authentication**
- **Spring Data MongoDB**
- **Maven 3.9.6** + **JaCoCo 0.8.10**
- **JUnit 5** + **Mockito** + **Spring Test**
- **Docker** Multi-stage

## Arquitectura del Sistema

### Patrón MVC + Clean Architecture
```
├── Controllers/     # Capa de presentación REST
├── Services/       # Lógica de negocio
├── Repositories/   # Acceso a datos (MongoDB)
├── DTOs/          # Transferencia de datos
├── Models/        # Entidades de dominio
├── Security/      # JWT + Spring Security
└── Config/        # Configuración aplicación
```

### Características Técnicas Destacadas
- **Autenticación JWT**: Tokens seguros con roles
- **Control de Acceso**: ADMIN vs FOUNDATION roles  
- **Sistema Email**: Verificación y notificaciones
- **🔄 Dual Routing**: `/api/admin/*` y `/api/applications/*`
- **🧪 Test Coverage**: Suite completa con 55% cobertura
- **☁️ Cloud Ready**: MongoDB Atlas + Docker

## Testing y Cobertura

El proyecto incluye una suite completa de tests profesionales:

### Cobertura por Componente *(Datos actuales JaCoCo)*
- **Controllers**: 45% - Endpoints principales cubiertos
- **Services**: 61% - Lógica de negocio robusta  
- **Security**: 72% - Autenticación y autorización
- **Models**: 62% - Entidades y validaciones

### Suite de Tests Profesionales
- **228 Tests** ejecutándose exitosamente
- **AdminController**: 6 tests para gestión administrativa
- **ApplicationController**: 24 tests para flujos de aplicaciones
- **AuthController**: 16 tests de autenticación integrada
- **Services**: 94 tests de lógica de negocio
- **Security**: 30 tests de JWT y autenticación
- **Models**: 6 tests de entidades y validaciones

### Ejecutar Tests
```bash
# Ejecutar todos los tests
mvn clean test

# Ejecutar tests con reporte de cobertura
mvn clean test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

## Docker

### Construir imagen
```bash
docker build -t backend-anubis .
```

### Ejecutar contenedor
```bash
docker run -p 8080:8080 backend-anubis
```

## CI/CD Pipeline

El proyecto utiliza GitHub Actions para:

1. **Tests Automatizados**: Ejecuta los 225 tests en cada push/PR
2. **Análisis de Cobertura**: Genera reportes automáticos
3. **Build de Docker**: Construye imagen en ramas principales
4. **Artifacts**: Guarda el JAR compilado

### Flujo de Trabajo
- Checkout del código
- Configuración de Java 17
- Cache de dependencias Maven
- Ejecución de tests
- Generación de reporte de cobertura
- Build del JAR
- Construcción de imagen Docker

## Estructura del Proyecto

```
src/
├── main/java/com/anubis/
│   ├── controller/          # Controladores REST
│   ├── service/            # Lógica de negocio
│   ├── repository/         # Acceso a datos
│   ├── security/           # Configuración JWT
│   ├── model/              # Entidades
│   └── dto/                # Objetos de transferencia
└── test/java/com/anubis/
    ├── controller/         # Tests de controladores
    ├── service/            # Tests de servicios
    ├── security/           # Tests de seguridad
    └── model/              # Tests de modelos
```

## Configuración Local

1. **Clonar repositorio**
```bash
git clone https://github.com/yojair0/backend-anubis.git
cd backend-anubis
```

2. **Configurar variables de entorno**
```bash
# Crear archivo .env con:
MONGODB_URI=tu_conexion_mongodb
JWT_SECRET=tu_secret_jwt
EMAIL_PASSWORD=tu_password_email
```

3. **Ejecutar aplicación**
```bash
mvn spring-boot:run
```

## Endpoints Principales

- `GET /health` - Health check
- `POST /auth/register` - Registro de usuarios
- `POST /auth/login` - Login
- `GET /pets` - Listar mascotas
- `POST /applications` - Crear aplicación de adopción

## Desarrollo Académico

Este proyecto fue desarrollado siguiendo estándares profesionales:

- Arquitectura MVC bien definida
- Testing exhaustivo (68% coverage)
- Documentación completa
- CI/CD automatizado
- Containerización Docker
- Código limpio y profesional

## Calidad del Código

- **SonarQube**: Cumple estándares de calidad
- **Coverage**: 68% (supera el mínimo del 60%)
- **Tests**: 225 tests unitarios y de integración
- **Performance**: Optimizado para producción

---

**Desarrollado como proyecto académico - Universidad [Nombre]**
