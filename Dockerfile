FROM openjdk:8-alpine

#RUN wget https://www.yourkit.com/download/docker/YourKit-JavaProfiler-2019.8-docker.zip -P /tmp/ && \
#  unzip /tmp/YourKit-JavaProfiler-2019.8-docker.zip -d /usr/local && \
#  rm /tmp/YourKit-JavaProfiler-2019.8-docker.zip

RUN apk add --no-cache libc6-compat
ENV LD_LIBRARY_PATH=/lib64

COPY target /build
COPY conf /app/config

#RUN mv /build/artifacts/com_dematis_hive_jar/com.dematis.hive.jar /app/hive.jar && rm -r /build
RUN mv /build/com.renaud.test-1.0-SNAPSHOT-jar-with-dependencies.jar  /app/hive.jar && rm -r ./build

EXPOSE 8000

#CMD java  -agentpath:/usr/local/YourKit-JavaProfiler-2019.8/bin/linux-x86-64/libyjpagent.so=port=10001,listen=all -Xms256m -Xmx1024m -jar /app/hive.jar -config /app/config/config.ini
CMD java -Xms400m -Xmx800m -jar /app/hive.jar -config /app/config/config.ini