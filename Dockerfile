FROM maven:3.8.5-openjdk-17 as builder

# #Creating container environment
ENV INSTALL_PATH /usr/local/geoserver-analyser 
RUN mkdir ${INSTALL_PATH}

WORKDIR ${INSTALL_PATH}
COPY pom.xml .
#RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn clean package -DskipTests=true


FROM eclipse-temurin:17-jdk-alpine as prod

ENV INSTALL_PATH /usr/local/geoserver-analyser 
WORKDIR ${INSTALL_PATH}
COPY --from=builder /usr/local/geoserver-analyser/target/geoserver-analyser.jar /usr/local/geoserver-analyser/geoserver-analyser.jar

ADD docker/docker-entrypoint.sh ${INSTALL_PATH}/docker-entrypoint.sh
RUN chmod +x ${INSTALL_PATH}/docker-entrypoint.sh

ENTRYPOINT [ "docker-entrypoint.sh" ]

# FROM openjdk:8-jdk-alpine

# LABEL Claudio Bogossian<claudio.bogossian@gmail.com>


# #Creating container environment
# ENV INSTALL_PATH /usr/local/geoserver-analyser 
# RUN mkdir ${INSTALL_PATH}
# ARG JAR_FILE
# COPY ${JAR_FILE} ${INSTALL_PATH}/geoserver-analyser.jar

# ARG GEOSERVER_URL
# ARG BUSINESSAPI_URL

# RUN apk update \
#   && apk add --no-cache --update curl \
#   && rm -rf /var/cache/apk/*



# #ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/geoserver-analyser.jar", ]

# WORKDIR $INSTALL_PATH

# ENTRYPOINT [ "/docker-entrypoint.sh" ]
