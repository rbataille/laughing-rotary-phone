# Serveur de graph Hive.

## Présentation.
Le serveur de graph Hive permet de charger et garder en mémoire les formulaires des clients.

Pour chaques formulaires, un graph est créé; Ce graph est une copie de celui du formulaire type correspondant.

## Installation
Initiliser le docker en mode swarm (docker swarm init)

Créer le reseau [apps-network](https://docs.docker.com/network/overlay/) :

docker network create -d overlay apps-network --attachable

Pour installer hive en local, il suffit de lancer le script :     
``./build_local.sh``

Il est necessaire d'installer [Apache Maven](https://maven.apache.org/) ainsi que Java 11 (La version 8 n'a pas été testée), ainsi que docker (en mode swarm) et docker-compose.

Le script de build local va créer la stack *hive*.
Pour des details sur la configuration de la stack, se référer au fichier [local-docker-compose.yml](./local-docker-compose.yml).

Le docker-compose attache la stack aux reseau externes suivant : apps-network, bridge.


## Configuration.
Le fichier de configuration est spécifié au lancement de l'executable;

``java -jar /app/hive.jar -config /app/config/config.ini``

Voici les différentes options disponibles:
 * **mysql.host** (valeur par defaut : 127.0.0.1) adresse ip du serveur mysql qui contient la base des formulaires.
 * **mysql.user** (valeur par defaut : root) utilisateur avec lequel se connecter au serveur mysql.
 * **mysql.password** (valeur par defaut : dematis) mot de passe de connection au serveur mysql.
 * **mysql.db** (valeur par defaut : forms-app) nom de la base de donnée.
 * **forms.ttl** (valeur par defaut : 60000) durée de vie d'un formulaire (en millisecondes).
 * **server.port** (valeur par defaut : 8001) port d'écoute du serveur hive.
 * **kafka.bootstrap.servers** (valeur par defaut : PLAINTEXT://192.168.2.185:9092) url vers le serveur kafka.
 * **kafka.validation.topic** (valeur par defaut : forms-validation-topic) topic vers lequel envoyer les données de validations.
 * **debug** (valeur par defaut : false **! ne pas mettre à true en prod !**) mis à true, cette option permet d'afficher plus de logs.


## Liens utiles  

 * [/swarm/init](https://docs.docker.com/engine/swarm/swarm-tutorial/create-swarm/)
 * [/stack/deploy](https://docs.docker.com/engine/reference/commandline/stack_deploy/)
 * [/network/create](https://docs.docker.com/network/overlay/)