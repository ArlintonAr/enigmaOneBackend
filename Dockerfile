# imagen base con Java 17 (por ejemplo, la versión ligera 'jdk-alpine')
FROM eclipse-temurin:17-jdk

# Expón el puerto donde escucha Spring Boot
EXPOSE 8080

# Define el nombre del archivo JAR generado por Maven o Gradle
# Asegúrate de que el path sea correcto (ej: target/mi-app.jar o build/libs/mi-app.jar)
ARG JAR_FILE=target/*.jar

# Copia el JAR ejecutable al contenedor
COPY ${JAR_FILE} app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","/app.jar"]