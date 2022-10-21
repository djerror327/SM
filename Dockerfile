FROM openjdk:8-jdk-alpine
WORKDIR /app
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
COPY . .
RUN ls -l
ENTRYPOINT ["sh","start.sh"]
