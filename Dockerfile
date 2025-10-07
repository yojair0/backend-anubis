# Etapa de construcción
FROM maven:3.9.4-openjdk-17-slim AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Descargar dependencias (aprovechando cache de Docker)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM openjdk:17-jdk-slim

# Crear usuario no root para seguridad
RUN addgroup --system spring && adduser --system spring --ingroup spring

# Establecer directorio de trabajo
WORKDIR /app

# Crear directorio para uploads
RUN mkdir -p /app/uploads && chown spring:spring /app/uploads

# Copiar el JAR construido
COPY --from=build /app/target/*.jar app.jar

# Cambiar propietario del archivo
RUN chown spring:spring app.jar

# Cambiar a usuario no root
USER spring:spring

# Exponer puerto
EXPOSE 8081

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando de inicio con opciones de JVM
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]