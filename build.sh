#!/bin/sh

docker info > /dev/null 2>&1
# Ensure that Docker is running...
if [ $? -ne 0 ]; then
    echo "Docker is not running."
    exit 1
fi

# Lance les tests unitaires + build le package.
docker run -it --rm --name com.renaud.test -v "$(pwd)":/app/workspace -w /app/workspace maven:3.3-jdk-8 mvn clean test package && \
# Build la doc.
docker run -it --rm --name com.renaud.test -v "$(pwd)":/app/workspace -w /app/workspace maven:3.3-jdk-8 mvn javadoc:javadoc && \

# Build l'image docker application:latest.
docker build --no-cache -f Dockerfile -t application:latest . && \
# actualise la stack/
docker-compose up  --force-recreate -d # && \
# echo "We need sudo to adjust permission " && \
# sudo chown -R "$(whoami):$(whoami)" ./target
