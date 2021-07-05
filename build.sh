#!/bin/sh


docker run -it --rm --name com.renaud.test -v "$(pwd)":/usr/src/workspace -w /usr/src/workspace maven:3.3-jdk-8 mvn clean test package && \
docker build --no-cache -f Dockerfile -t application:latest . && \
docker-compose up  --force-recreate -d && \
echo "We need to adjust some permissions : " && \
sudo chown -R "$(whoami):$(whoami)" ./target
