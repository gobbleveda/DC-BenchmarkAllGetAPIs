FROM maven:3.6.0-jdk-8-alpine
LABEL Aman Misra <amanmisra@gmail.com>

RUN apk add -Uuv python less py-pip openssl tzdata
RUN pip install awscli
RUN cp /usr/share/zoneinfo/Europe/Amsterdam /etc/localtime

RUN apk --purge -v del py-pip && \
    rm /var/cache/apk/*

WORKDIR /build

COPY pom.xml .
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.1.1:go-offline
#RUN mvn org.apache.maven.plugins:maven-dependency-plugin
COPY src/ /build/src
#COPY src .
COPY run.sh .

RUN mvn install --offline
#RUN mvn install

ENTRYPOINT ["./run.sh"]


