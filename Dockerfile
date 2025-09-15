# Etapa de build do backend
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa final com Java 19 + Node 20.5.1 + npm 9.8.0
FROM eclipse-temurin:19-jdk-jammy

# Instala Node.js 20.5.1 e npm 9.8.0
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    npm install -g npm@9.8.0 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copia o backend (arquivo .jar gerado)
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Copia o frontend
COPY src/main/resources/templates/projectLdwFront ./frontend
WORKDIR /app/frontend
RUN npm install && npm run build

COPY wait-for.sh /wait-for.sh
RUN chmod +x /wait-for.sh


# Volta para a raiz e define entrypoint
WORKDIR /app
ENTRYPOINT ["/wait-for.sh", "mysql", "java", "-jar", "/app/app.jar"]
