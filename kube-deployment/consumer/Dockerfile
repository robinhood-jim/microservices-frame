FROM openjdk:8-jdk-alpine
WORKDIR /data
COPY kube-consumer-service-1.0-SNAPSHOT.jar client.jar
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
CMD ["java","-jar","client.jar"]
