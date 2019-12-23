FROM openjdk:8-jdk-alpine
RUN mkdir /data && mkdir /data/config && mkdir /data/config/queryConfig
WORKDIR /data
COPY kube-product-service-1.0-SNAPSHOT.jar service.jar
COPY application.yaml /data/
COPY query_rest.xml /data/queryConfig
EXPOSE 9092
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
CMD ["java","-jar","service.jar"]
