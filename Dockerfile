FROM gradle:8.4-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle installDist

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/install/* /app/
CMD ["./bin/ktor-login-sample"]