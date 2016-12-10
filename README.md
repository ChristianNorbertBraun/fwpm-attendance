Attendance Microservice
====================================

This microservice is part of our project FWPM Election. It handles the presentation of
the assignment results.
The project is developed for a docker-environment.

You can find our Dockerfiles under:

* [Dockerfiles OSX][osx]

System Requirements
-------------------

* Running docker-environment described in the Dockerfile-Repos 
* [Maven][mvn]

Running
-------

* Add a Server 'tomcat-localhost' with the same User and Password you configured in your Tomcat
* $ mvn clean install
* $ mvn tomcat:redeploy

[osx]: https://github.com/marcelgross90/Tomcat-MYSQL-Docker
[mvn]: https://maven.apache.org/