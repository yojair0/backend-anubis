# API Backend Anubis - Documentación para Frontend

## URL Base
```
http://localhost:8081
```

## Configuración del Backend

### Variables de Entorno
Ver archivo `.env.example` para las variables requeridas.

### Iniciar Backend
```bash
./mvnw.cmd spring-boot:run
```

## Autenticación

### 1. Registrar Usuario
**POST** `/api/auth/register`

**Request:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@email.com",
  "password": "password123",
  "phone": "555-1234"
}
```

**Response Exitoso:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.example",
  "type": "Bearer",
  "id": "66de123456789abcdef01234",
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@email.com",
  "phone": "555-1234",
  "roles": ["ROLE_USER"],
  "emailVerified": false
}
```

**Response Error:**
```json
{
  "message": "El email ya está registrado"
}
```

### 2. Iniciar Sesión
**POST** `/api/auth/login`

**Request:**
```json
{
  "email": "juan@email.com",
  "password": "password123"
}
```

**Response:** Mismo formato que registro

### 3. Verificar Email
**POST** `/api/auth/verify-code?email=juan@email.com&code=123456`

**Response Exitoso:**
```json
{
  "message": "Email verificado exitosamente"
}
```

### 4. Recuperar Contraseña
**POST** `/api/auth/forgot-password`

**Request:**
```json
{
  "email": "juan@email.com"
}
```

**Response:**
```json
{
  "message": "Código de recuperación enviado al email"
}
```

### 5. Confirmar Nueva Contraseña
**POST** `/api/auth/reset-password`

**Request:**
```json
{
  "email": "juan@email.com",
  "code": "123456",
  "newPassword": "newpassword123"
}
```

## Mascotas

### 1. Listar Mascotas Disponibles (Público)
**GET** `/api/pets`

**Response:**
```json
[
  {
    "id": "66de123456789abcdef01235",
    "name": "Max",
    "species": "Perro",
    "breed": "Golden Retriever",
    "age": 3,
    "description": "Un perro muy amigable y energico",
    "status": "AVAILABLE",
    "imageUrl": "https://images.unsplash.com/photo-1552053831-71594a27632d?w=500"
  },
  {
    "id": "66de123456789abcdef01236",
    "name": "Luna",
    "species": "Gato",
    "breed": "Persa",
    "age": 2,
    "description": "Una gata muy tranquila y cariñosa",
    "status": "AVAILABLE",
    "imageUrl": "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=500"
  }
]
```

### 2. Obtener Mascota Específica
**GET** `/api/pets/{id}`

**Ejemplo:** `GET /api/pets/66de123456789abcdef01235`

**Response:**
```json
{
  "id": "66de123456789abcdef01235",
  "name": "Max",
  "species": "Perro",
  "breed": "Golden Retriever",
  "age": 3,
  "description": "Un perro muy amigable y energico",
  "status": "AVAILABLE",
  "imageUrl": "https://images.unsplash.com/photo-1552053831-71594a27632d?w=500"
}
```

### 3. Crear Mascota (Solo Administrador)
**POST** `/api/pets`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "name": "Buddy",
  "species": "Perro",
  "breed": "Labrador",
  "age": 2,
  "description": "Un perro muy cariñoso y jugueton",
  "imageUrl": "https://example.com/buddy.jpg"
}
```

### 4. Actualizar Mascota (Solo Administrador)
**PUT** `/api/pets/{id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:** Mismo formato que crear

### 5. Cambiar Estado de Mascota (Solo Administrador)
**PUT** `/api/pets/{id}/status`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "status": "ADOPTED"
}
```

**Estados válidos:** AVAILABLE, ADOPTED, PENDING

### 6. Eliminar Mascota (Solo Administrador)
**DELETE** `/api/pets/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

## Postulaciones de Adopción

### 1. Crear Postulación
**POST** `/api/applications/create`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "petId": "66de123456789abcdef01235",
  "reason": "Quiero adoptar esta mascota porque tengo mucho amor para dar",
  "experience": "He tenido perros toda mi vida y conozco sus necesidades",
  "livingSpace": "Casa con jardín grande y seguro",
  "hasOtherPets": true,
  "workSchedule": "Trabajo medio tiempo desde casa"
}
```

**Response:**
```json
{
  "id": "66de123456789abcdef01237",
  "userId": "66de123456789abcdef01234",
  "petId": "66de123456789abcdef01235",
  "reason": "Quiero adoptar esta mascota porque tengo mucho amor para dar",
  "experience": "He tenido perros toda mi vida y conozco sus necesidades",
  "livingSpace": "Casa con jardín grande y seguro",
  "hasOtherPets": true,
  "workSchedule": "Trabajo medio tiempo desde casa",
  "status": "PENDING",
  "createdAt": "2025-09-08T15:30:00.000Z"
}
```

### 2. Mis Postulaciones
**GET** `/api/applications/user/my-applications`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": "66de123456789abcdef01237",
    "userId": "66de123456789abcdef01234",
    "petId": "66de123456789abcdef01235",
    "reason": "Quiero adoptar esta mascota porque tengo mucho amor para dar",
    "experience": "He tenido perros toda mi vida y conozco sus necesidades",
    "livingSpace": "Casa con jardín grande y seguro",
    "hasOtherPets": true,
    "workSchedule": "Trabajo medio tiempo desde casa",
    "status": "PENDING",
    "createdAt": "2025-09-08T15:30:00.000Z"
  }
]
```

### 3. Todas las Postulaciones (Solo Administrador)
**GET** `/api/applications`

**Headers:**
```
Authorization: Bearer {token}
```

### 4. Actualizar Estado de Postulación (Solo Administrador)
**PUT** `/api/applications/{id}/status`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "status": "APPROVED",
  "adminNotes": "Postulación aprobada tras entrevista satisfactoria"
}
```

**Estados válidos:** PENDING, APPROVED, REJECTED

## Gestión de Usuarios

### 1. Ver Mi Perfil
**GET** `/api/users/profile`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": "66de123456789abcdef01234",
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@email.com",
  "phone": "555-1234",
  "emailVerified": true,
  "roles": ["ROLE_USER"],
  "createdAt": "2025-09-08T10:00:00.000Z"
}
```

### 2. Actualizar Mi Perfil
**PUT** `/api/users/profile`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "firstName": "Juan Carlos",
  "lastName": "Pérez González",
  "phone": "555-5678"
}
```

### 3. Listar Todos los Usuarios (Solo Administrador)
**GET** `/api/users`

**Headers:**
```
Authorization: Bearer {token}
```

## Administración

### 1. Estadísticas del Sistema (Solo Administrador)
**GET** `/api/admin/stats`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "totalUsers": 150,
  "totalPets": 25,
  "totalApplications": 75,
  "pendingApplications": 12,
  "availablePets": 18,
  "adoptedPets": 7
}
```

## Códigos de Estado HTTP

- **200**: Operación exitosa
- **201**: Recurso creado exitosamente
- **400**: Datos de entrada inválidos
- **401**: Token no válido o usuario no autenticado
- **403**: Sin permisos para realizar esta acción
- **404**: Recurso no encontrado
- **500**: Error interno del servidor

## Manejo de Errores

Todos los errores se devuelven en formato JSON:

```json
{
  "message": "Descripción específica del error"
}
```

**Ejemplos de errores comunes:**
- "El email ya está registrado"
- "Credenciales inválidas"
- "Token JWT expirado"
- "Mascota no encontrada"
- "No tiene permisos para esta acción"

## Autenticación JWT

### Incluir Token en Requests
Todos los endpoints protegidos requieren el token JWT en el header:

```javascript
headers: {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}
```

### Duración del Token
- **Expiración**: 24 horas
- **Renovación**: Debe hacer login nuevamente cuando expire

### Roles de Usuario
- **ROLE_USER**: Usuario regular (puede crear postulaciones, ver su perfil)
- **ROLE_ADMIN**: Administrador (puede gestionar mascotas y postulaciones)

## Datos de Prueba

El backend incluye 6 mascotas de ejemplo:

1. **Max** - Perro Golden Retriever, 3 años
2. **Luna** - Gato Persa, 2 años
3. **Rocky** - Perro Bulldog Francés, 4 años
4. **Mia** - Gato Siamés, 1 año
5. **Bruno** - Perro Pastor Alemán, 5 años
6. **Cleo** - Gato Bengalí, 3 años

Todas están disponibles para adopción y tienen imágenes de Unsplash.

## Configuración CORS

El backend acepta requests desde:
- **Desarrollo**: `http://localhost:3000`
- **Producción**: Configurar en variable `CORS_ORIGINS`

## Ejemplos de Implementación

### Registro de Usuario (React/JavaScript)
```javascript
const register = async (userData) => {
  try {
    const response = await fetch('http://localhost:8081/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(userData)
    });
    
    const data = await response.json();
    
    if (response.ok) {
      // Guardar token
      localStorage.setItem('token', data.token);
      return data;
    } else {
      throw new Error(data.message);
    }
  } catch (error) {
    console.error('Error en registro:', error);
    throw error;
  }
};
```

### Obtener Mascotas Disponibles
```javascript
const getPets = async () => {
  try {
    const response = await fetch('http://localhost:8081/api/pets');
    const pets = await response.json();
    return pets;
  } catch (error) {
    console.error('Error obteniendo mascotas:', error);
    throw error;
  }
};
```

### Crear Postulación
```javascript
const createApplication = async (applicationData) => {
  const token = localStorage.getItem('token');
  
  try {
    const response = await fetch('http://localhost:8081/api/applications/create', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(applicationData)
    });
    
    const data = await response.json();
    
    if (response.ok) {
      return data;
    } else {
      throw new Error(data.message);
    }
  } catch (error) {
    console.error('Error creando postulación:', error);
    throw error;
  }
};
```

## Flujo de Trabajo Recomendado

### Para Usuario Regular:
1. Registrarse → `POST /api/auth/register`
2. Verificar email → `POST /api/auth/verify-code`
3. Ver mascotas disponibles → `GET /api/pets`
4. Crear postulación → `POST /api/applications/create`
5. Verificar estado de postulaciones → `GET /api/applications/user/my-applications`

### Para Administrador:
1. Iniciar sesión → `POST /api/auth/login`
2. Ver estadísticas → `GET /api/admin/stats`
3. Gestionar mascotas → `POST/PUT/DELETE /api/pets`
4. Revisar postulaciones → `GET /api/applications`
5. Aprobar/rechazar postulaciones → `PUT /api/applications/{id}/status`

## Notas Importantes

- Los endpoints públicos NO requieren autenticación (GET /api/pets)
- Los códigos de verificación aparecen en la consola del backend durante desarrollo
- Las imágenes de mascotas son URLs externas (Unsplash)
- El sistema envía emails reales a través de Mailtrap en desarrollo
- Todos los IDs son ObjectIds de MongoDB (24 caracteres hexadecimales)
- Las fechas están en formato ISO 8601 UTC
