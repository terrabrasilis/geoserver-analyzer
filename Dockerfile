FROM maven:3.8.5-openjdk-17 as builder

ENV INSTALL_PATH /usr/local/geoserver-analyzer
RUN mkdir ${INSTALL_PATH}

WORKDIR ${INSTALL_PATH}
COPY pom.xml .

COPY src/ ./src/
#RUN mvn install

ADD docker/docker-entrypoint.sh ${INSTALL_PATH}/docker-entrypoint.sh
RUN chmod +x ${INSTALL_PATH}/docker-entrypoint.sh

ENTRYPOINT [ "/usr/local/geoserver-analyzer/docker-entrypoint.sh" ]

