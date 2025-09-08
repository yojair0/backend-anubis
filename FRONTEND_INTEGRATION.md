# Frontend Integration Guide

## Backend Status
- **URL**: `http://localhost:8081`
- **Database**: MongoDB Atlas (configured)
- **Email**: Mailtrap (configured for verification codes)
- **Authentication**: JWT tokens

## API Endpoints

### Authentication

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
    "fullName": "John Doe",
    "email": "user@example.com",
    "password": "password123",
    "phone": "+56912345678",
    "role": "USER"
}

Response: 201 Created
{
    "message": "Usuario registrado exitosamente. Revisa tu email para el código de verificación."
}
```

#### Verify Email with Code
```
POST /api/auth/verify-code
Content-Type: application/json

{
    "email": "user@example.com",
    "verificationCode": "123456"
}

Response: 200 OK
{
    "message": "Email verificado exitosamente",
    "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}

Response: 200 OK
{
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "id": "user_id",
    "email": "user@example.com",
    "name": "John Doe",
    "verified": true
}
```

### User Management

#### Get User Profile
```
GET /api/users/profile
Authorization: Bearer {token}

Response: 200 OK
{
    "id": "user_id",
    "name": "John Doe",
    "email": "user@example.com",
    "verified": true,
    "createdAt": "2024-01-01T00:00:00Z"
}
```

#### Update User Profile
```
PUT /api/users/profile
Authorization: Bearer {token}
Content-Type: application/json

{
    "name": "John Updated",
    "email": "newemail@example.com"
}

Response: 200 OK
{
    "id": "user_id",
    "name": "John Updated",
    "email": "newemail@example.com",
    "verified": true
}
```

### Animal Applications

#### Submit Application
```
POST /api/applications
Authorization: Bearer {token}
Content-Type: application/json

{
    "animalName": "Buddy",
    "animalType": "DOG",
    "motivacion": "I love dogs and have experience...",
    "experiencia": "5 years with pets",
    "vivienda": "House with yard",
    "disponibilidad": "Full time"
}

Response: 201 Created
{
    "id": "application_id",
    "userId": "user_id",
    "animalName": "Buddy",
    "animalType": "DOG",
    "status": "PENDING",
    "submittedAt": "2024-01-01T00:00:00Z"
}
```

#### Get User Applications
```
GET /api/applications/my-applications
Authorization: Bearer {token}

Response: 200 OK
[
    {
        "id": "application_id",
        "animalName": "Buddy",
        "animalType": "DOG",
        "status": "PENDING",
        "submittedAt": "2024-01-01T00:00:00Z"
    }
]
```

## Authentication Flow

1. **Registration**: User submits email/password
2. **Email Verification**: 6-digit code sent to email
3. **Code Verification**: User enters code to activate account
4. **Login**: User can login with verified credentials
5. **Protected Routes**: Include JWT token in Authorization header

## Important Notes

- All protected endpoints require `Authorization: Bearer {token}` header
- Email verification is mandatory before login
- Verification codes expire in 1 hour
- JWT tokens contain user information and permissions
- CORS is configured for `http://localhost:3000`

## Error Responses

All endpoints return consistent error format:
```json
{
    "timestamp": "2024-01-01T00:00:00Z",
    "status": 400,
    "error": "Bad Request",
    "message": "Specific error description",
    "path": "/api/endpoint"
}
```

## Frontend Setup Requirements

1. Configure API base URL: `http://localhost:8081`
2. Handle JWT token storage (localStorage/sessionStorage)
3. Implement token refresh logic
4. Add Authorization header to protected requests
5. Handle email verification flow with 6-digit codes
