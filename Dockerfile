FROM maven:3.8.5-openjdk-17 as builder

# #Creating container environment
ENV INSTALL_PATH /usr/local/geoserver-analyser
RUN mkdir ${INSTALL_PATH}

WORKDIR ${INSTALL_PATH}
COPY pom.xml .
#RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn clean compile assembly:single


FROM eclipse-temurin:17-jdk-alpine as prod

ENV INSTALL_PATH /usr/local/geoserver-analyser 
WORKDIR ${INSTALL_PATH}
COPY --from=builder /usr/local/geoserver-analyser/target/geoserver-analyser.jar /usr/local/geoserver-analyser/geoserver-analyser.jar

ADD docker/docker-entrypoint.sh ${INSTALL_PATH}/docker-entrypoint.sh
RUN chmod +x ${INSTALL_PATH}/docker-entrypoint.sh

ENTRYPOINT [ "/usr/local/geoserver-analyser/docker-entrypoint.sh" ]
