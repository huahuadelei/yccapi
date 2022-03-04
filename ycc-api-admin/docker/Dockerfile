FROM openjdk:8u312-jre-slim-bullseye
MAINTAINER "chaoqunyu9311@163.com"

ENV JAVA_VERSION "java1.8_312"
WORKDIR /app
EXPOSE 8099

RUN sh -c "mkdir -p /app/data && mkdir -p /app/logs"

COPY target/ycc-api-admin.jar ./target/
COPY config/ ./config
COPY bin/ ./bin

CMD ["/bin/bash" ,"-c","sh ./bin/startup.sh&&tail -f ./bin/console.out"]

