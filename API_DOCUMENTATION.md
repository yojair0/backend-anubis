# API Backend Anubis - Documentación

Estado: Funcionando correctamente - 371 tests pasando

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
  "fullName": "Juan Pérez",
  "email": "juan@email.com",
  "password": "password123",
  "phone": "555-1234"
}
```

**Response Exitoso:**
```json
{
  "message": "Código de verificación enviado. Revisa tu email."
}
```

### 2. Verificar Email con Código
**POST** `/api/auth/verify-email`

**Request:**
```json
{
  "email": "juan@email.com",
  "code": "123456"
}
```

**Response Exitoso:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": "66de123456789abcdef01234",
  "email": "juan@email.com",
  "fullName": "Juan Pérez",
  "role": "USER"
}
```

### 3. Iniciar Sesión
**POST** `/api/auth/login`

**Request:**
```json
{
  "email": "juan@email.com",
  "password": "password123"
}
```

**Response Exitoso:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": "66de123456789abcdef01234",
  "email": "juan@email.com",
  "fullName": "Juan Pérez",
  "role": "USER"
}
```

### 4. Reenviar Código de Verificación
**POST** `/api/auth/resend-verification?email=juan@email.com`

**Response:**
```json
{
  "message": "Email de verificación enviado"
}
```

### 5. Solicitar Restablecimiento de Contraseña
**POST** `/api/auth/forgot-password`

**Request:**
```json
{
  "email": "juan@email.com"
}
```

### 6. Restablecer Contraseña
**POST** `/api/auth/reset-password`

**Request:**
```json
{
  "token": "reset-token-received-by-email",
  "newPassword": "newPassword123"
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
**POST** `/api/auth/verify-email`

**Request:**
```json
{
  "email": "juan@email.com",
  "code": "111111"
}
```

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

### 3. Crear Mascota (Fundación)
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
  "gender": "Macho",
  "description": "Un perro muy cariñoso y jugueton",
  "imageUrl": "https://example.com/buddy.jpg"
}
```

### 4. Actualizar Mascota (Administrador y Fundación)
**PUT** `/api/pets/{id}`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:** Mismo formato que crear

### 5. Cambiar Estado de Mascota (Administrador y Fundación)
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

### 1. Crear Postulación (Usuario)
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
    "foundationResponse": null,
    "createdAt": "2025-09-08T15:30:00.000Z",
    "updatedAt": "2025-09-08T15:30:00.000Z"
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
  "adminNotes": "Postulación aprobada"
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

### 2. Obtener Mi Rol
**GET** `/api/users/role`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "role": "USER"
}
```

**Roles posibles:** `USER`, `FOUNDATION`, `ADMIN`

**Ejemplo de uso:**
```javascript
const getUserRole = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('http://localhost:8081/api/users/role', {
    headers: {'Authorization': `Bearer ${token}`}
  });
  const data = await response.json();
  return data.role; // "USER", "FOUNDATION", o "ADMIN"
};
```

### 3. Actualizar Mi Perfil
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

### 4. Listar Todos los Usuarios (Solo Administrador)
**GET** `/api/users`

**Headers:**
```
Authorization: Bearer {token}
```

## Administración

### 1. Obtener Todos los Usuarios (Solo Administrador)
**GET** `/api/admin/users`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "id": "66de123456789abcdef01234",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@email.com",
    "phone": "555-1234",
    "role": "USER",
    "emailVerified": true,
    "createdAt": "2024-09-15T10:30:00Z"
  }
]
```

### 2. Eliminar Usuario (Solo Administrador)
**DELETE** `/api/admin/users/{userId}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response Exitoso:**
```json
{
  "message": "Usuario eliminado exitosamente"
}
```

**Response Error:**
```json
{
  "message": "Usuario no encontrado"
}
```

### 3. Estadísticas del Sistema (Solo Administrador)
**GET** `/api/admin/statistics`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "message": "Estadísticas del Sistema:\n Usuarios: 150\n Postulaciones: 75\n Mascotas: 25\n"
}
```

### 4. Contar Datos del Sistema (Solo Administrador)
**GET** `/api/admin/count-data`

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "message": "Datos en base:\n Usuarios: 150\n Postulaciones: 75\n"
}
```

### 5. Crear Usuario con Rol Específico (Solo Administrador)
**POST** `/api/admin/create-user`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request:**
```json
{
  "email": "fundacion@email.com",
  "password": "password123",
  "fullName": "Fundación Amor Animal",
  "phone": "555-9999",
  "role": "FOUNDATION"
}
```

**Roles válidos:** `USER`, `FOUNDATION`, `ADMIN`

**Response Exitoso:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": "66de123456789abcdef01238",
  "email": "fundacion@email.com",
  "fullName": "Fundación Amor Animal",
  "role": "FOUNDATION"
}
```

**Importante:** Este endpoint crea usuarios sin verificación de email. Los usuarios creados por admin están pre-verificados.

### 6. Cambiar Rol de Usuario (Solo Administrador)
**PUT** `/api/admin/users/{userId}/role`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Ejemplo:** `PUT /api/admin/users/66de123456789abcdef01234/role`

**Request:**
```json
{
  "role": "FOUNDATION"
}
```

**Roles válidos:** `USER`, `FOUNDATION`, `ADMIN`

**Response Exitoso:**
```json
{
  "message": "Rol actualizado exitosamente. Usuario: juan@email.com ahora tiene rol: FOUNDATION"
}
```

**Response Error:**
```json
{
  "message": "Usuario no encontrado"
}
```

### 7. Estadísticas del Sistema (Solo Administrador)
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

## 🧪 PRUEBAS FINALES - SPRINT 5 (JAIRO)

### Endpoints de Login - Casos de Prueba

#### 1. Login Usuario Exitoso
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@test.com",
    "password": "password123"
  }'
```
**Esperado**: Token JWT + datos usuario

#### 2. Login con Credenciales Incorrectas
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "wrong@test.com", 
    "password": "wrongpass"
  }'
```
**Esperado**: Error 401

#### 3. Registrar Usuario
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User", 
    "email": "nuevo@test.com",
    "password": "password123",
    "phone": "555-0123"
  }'
```
**Esperado**: Token + usuario creado

### Endpoints de Postulaciones - Casos de Prueba

#### 4. Crear Postulación (Usuario)
```bash
curl -X POST http://localhost:8081/api/applications/create \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "petId": "{petId}",
    "contactNumber": "555-0123",
    "reason": "Quiero adoptar esta mascota",
    "experienceWithPets": "He tenido perros toda mi vida"
  }'
```

#### 5. Mis Postulaciones (Usuario)
```bash
curl -X GET http://localhost:8081/api/applications/user/my-applications \
  -H "Authorization: Bearer {token}"
```

#### 6. Cambiar Estado Postulación (Admin y Fundación)
```bash
curl -X PUT http://localhost:8081/api/applications/{id}/status \
  -H "Authorization: Bearer {admin-token}" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "ACCEPTED",
    "response": "¡Felicitaciones! Tu postulación ha sido aceptada"
  }'
```

### Endpoints de Administración - Casos de Prueba

#### 7. Eliminar Usuario (Admin)
```bash
curl -X DELETE http://localhost:8081/api/admin/users/{userId} \
  -H "Authorization: Bearer {admin-token}"
```

#### 8. Estadísticas del Sistema (Admin)
```bash
curl -X GET http://localhost:8081/api/admin/statistics \
  -H "Authorization: Bearer {admin-token}"
```

### Lista de Verificación Final

- [ ] Login funciona correctamente
- [ ] Register crea usuarios sin errores
- [ ] Validaciones de email/password funcionan
- [ ] Postulaciones se crean correctamente
- [ ] Emails se envían al cambiar estado
- [ ] Admin puede eliminar usuarios
- [ ] Todas las validaciones de roles funcionan
- [ ] Manejo de errores es consistente

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

