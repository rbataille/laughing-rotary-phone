FROM openjdk:8-alpine

COPY target /build
COPY conf /app/config
COPY public /public
RUN mv /build/com.renaud.larp-1.0-SNAPSHOT-jar-with-dependencies.jar  /app/laughing-rotary-phone.jar && rm -r ./build
EXPOSE 8000

CMD java -Xms400m -Xmx800m -jar /app/laughing-rotary-phone.jar -config /app/config/config.ini