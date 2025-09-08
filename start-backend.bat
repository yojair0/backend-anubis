@echo off
set MONGO_URI=mongodb+srv://anubis_db:3xlXFBRmxaCQC16I@anubiscluster.7p2cyvr.mongodb.net/anubis_db?retryWrites=true^&w=majority^&appName=anubisCluster
set JWT_SECRET=AnubisSecretKeyForAdoptionPlatform2024SuperSecretKeyForJWTHS512AlgorithmRequires512BitsMinimum
set MAIL_HOST=sandbox.smtp.mailtrap.io
set MAIL_PORT=587
set MAILTRAP_USERNAME=141d3d3aca55df
set MAILTRAP_PASSWORD=776490a2cb6a98
set CORS_ORIGINS=http://localhost:3000

echo Iniciando backend con MongoDB Atlas y Mailtrap...
java -jar target\backend-anubis-1.0.0.jar
set MONGO_URI=mongodb+srv://anubis_db:3xlXFBRmxaCQC16I@anubiscluster.7p2cyvr.mongodb.net/anubis_db?retryWrites=true^&w=majority^&appName=anubisCluster
set JWT_SECRET=AnubisSecretKeyForAdoptionPlatform2024SuperSecretKeyForJWTHS512AlgorithmRequires512BitsMinimum
set MAILTRAP_USERNAME=141d3d3aca55df
set MAILTRAP_PASSWORD=****6a98
set CORS_ORIGINS=http://localhost:3000

echo Iniciando backend con MongoDB Atlas y Mailtrap...
java -jar target\backend-anubis-1.0.0.jar
