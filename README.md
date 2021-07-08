# Test T03.
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=rbataille_laughing-rotary-phone&metric=alert_status)](https://sonarcloud.io/dashboard?id=rbataille_laughing-rotary-phone)

## Intro
Ce serveur expose deux endpoints (+2).

- ***/app*** accepte 5 parametres, **int1**, **int2**, **limit**, **str1**, **str2**, et renvois une liste de strings avec les nombres de 1 à **limit**, ou :  tous les multiples de **int1** sont remplacés par **str1**, tous les multiples de **int2** sont remplacés par **str2** et tous les multiples de **int1** et **int2** sont remplacés par **str1str2**.
- ***/metrics*** permet d'obtenir des statistiques sur les requetes les plus utilisé lors de l'appel à **/app**

## Installation
Il est nécessaire de posséder [docker](https://docs.docker.com/get-docker/) && [docker-compose](https://docs.docker.com/compose/install/).

Pour executer les tests unitaires, génerer la doc et lancer le serveur il faut executer la commande ``./build.sh``

Si votre utilisateur [n'est pas dans le groupe docker](https://docs.docker.com/engine/install/linux-postinstall/), il faut préfixer la commande par sudo

Si le build c'est bien déroulé, les routes suivantes sont disponibles:
 - [La Javadoc](http://localhost:8001/index.html)
 - [Le endpoint "métier" ](http://localhost:8000/app?int1=5&int2=8&limit=1000&str1=foo&str2=bar)
 - [les metrics](http://localhost:8000/metrics)
 - [La page de stats](http://localhost:8000/stats) Affiche une page de stats qui utilise la lib CanvasJS pour afficher un graph.
 - Autre : [Page sonarcloud](https://sonarcloud.io/dashboard?id=rbataille_laughing-rotary-phone)


Pour lancer le jar a la main ``mvn clean package && java -jar target/com.renaud.larp-1.0-SNAPSHOT-jar-with-dependencies.jar -config /app/config/config.ini``

Pour supprimer les containers : ``docker-compose down``.