# PaVoS-source
**PaVoS** (**P**rocessing **a**nd **V**isualization **o**f **S**ensordata Streams) is a program for processing sensordata supplied by an Apache Kafka Stream and simultaneous visualization of said data as a web service. See [PaVoS](https://github.com/PaVoS-TECO/pavos-documents) for non-code elements of the project.

The program and surrounding elements are written for the [TECO Research Group](teco.edu) as part of the SS2018 PSE project *"Visualizing & Mining of Geospatial Sensorstreams with Apache Kafka"*.

## Quality assurance
![quality-gate](https://sonarcloud.io/api/project_badges/quality_gate?project=pavos-source)  

### Statistics
![coverage](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=coverage)  
![lines of code](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=ncloc)  
![duplicated lines](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=duplicated_lines_density)  
![maintainability](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=sqale_rating)  
![reliability](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=reliability_rating)  
![security](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=security_rating)  
![bugs](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=bugs)  
![code smells](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=code_smells)  
![vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=vulnerabilities)  
![technical debt](https://sonarcloud.io/api/project_badges/measure?project=pavos-source&metric=sqale_index)  
  
[Click here to view PaVoS-Core on Sonarcloud!](https://sonarcloud.io/dashboard?id=pavos-source)
## Authors
The authors of this project are (alphabetically sorted):
- [Erik Wessel](https://github.com/erikwessel)
- [Jean Baumgarten](https://github.com/Jelumar)
- [Oliver Liu](https://github.com/olivermliu)
- [Patrick Ries](https://github.com/masterries)
- [Thomas Frank](https://github.com/thomas475)

## License
PaVoS is licensed under the [MIT License](LICENSE).

## Overview
The PaVoS project is composed of multiple modules. In order of dataflow, these are:
- [Import](https://github.com/PaVoS-TECO/FROST-import)
- [Bridge](https://github.com/PaVoS-TECO/java-mqtt-kafka-bridge)
- [Core with Database](https://github.com/PaVoS-TECO/pavos-source)
- [Webinterface](https://github.com/PaVoS-TECO/webinterface)
- [Export](https://github.com/PaVoS-TECO/Kafka-export)

## Dependencies
Each module requires some services to be running. These services **must** be started before starting the modules to guarantee proper function.

| Module             | FROST | Kafka | Memcached | Grafana | Graphite |
| :----------------- | :---: | :---: | :-------: | :-----: | :------: |
| Import             | ✔     |       |           |         |          |
| Bridge             | ✔     | ✔     |           |         |          |
| Core with Database |       | ✔     | ✔         | ✔       | ✔        |
| Webinterface       |       |       |           |         |          |
| Export             |       | ✔     |           |         |          |

## Starting the services
### Docker
All services require docker to be installed. Some services require docker-compose to be installed as well.
To install both docker and docker-compose, you can either follow the instructions on the subpages of the docker documentation ([docker](https://docs.docker.com/install/) and [docker-compose](https://docs.docker.com/compose/install/)) or use this script: [setupDocker.sh](https://github.com/BowenWang29/smartaqnet-dev/blob/master/sbin/setupDocker.sh).

### Environment variables
The variable `smartaqnethome` **must be set** to the IP or the FQDN of the machine(s) that run(s) **FROST** and **Kafka**, either temporarily via executing `export smartaqnethome=vm.example.com` in the shell or appending that line to your shell-specific rc file (e.g. `~/.bashrc` with bash or `~/.zshrc` with zsh)

### FROST

See [image357/docker-SensorThingsServer](https://github.com/image357/docker-SensorThingsServer/).

Clone that repository, change the `serviceRootUrl` parameter in the `docker-compose.yml` file to `http://machine_address:8080/FROST-Server`, execute `sudo docker-compose up --build -d`, wait a while (around 3 minutes), execute `sudo ./docker-setup.sh`, then go to `http://machine_address:8080/FROST-Server/DatabaseStatus` and press on "Do Update".

### Kafka
From [BowenWang29/smartaqnet-dev](https://github.com/BowenWang29/smartaqnet-dev/blob/master/sbin/startLandoop.sh):

`sudo docker run --rm --net=host --name="kafka_landoop" -d -e CONNECT_HEAP=3G -e ADV_HOST=$smartaqnethome -e RUNTESTS=0 landoop/fast-data-dev`

### Memcached
From [Wiki: Memcached Server](https://github.com/PaVoS-TECO/pavos-source/wiki/Memcached-Server):

`sudo docker run --name memcached-server -d -p 11211:11211 memcached`

### Graphite & Grafana
See [Wiki: Data transfer](https://github.com/PaVoS-TECO/pavos-source/wiki/Data-transfer)

## Starting the modules
### Import
Note that this is a standalone desktop application and cannot be run in a text-only environment.
1. `git clone https://github.com/PaVoS-TECO/FROST-import.git`
2. `cd FROST-import`
3. `mvn clean install`
4. Run the jar in the target folder.

### Bridge
See README at [PaVoS-TECO/java-mqtt-kafka-bridge](https://github.com/PaVoS-TECO/java-mqtt-kafka-bridge/)

### Core
1. `git clone https://github.com/PaVoS-TECO/pavos-source.git`
2. `cd pavos-source`
3. `mvn clean install`
4. `java -jar target/*.jar`

To pull new changes:
- `git pull origin master`

To log console output into a logfile:
- `java -jar target/*.jar 2>&1 | tee logfile`

### Webinterface
The webinterface can be called as is via `index.jsp` of [the repo](https://github.com/PaVoS-TECO/webinterface). You may also generate a .war file via `mvn clean install` which can be used as a webapp with a Tomcat server (for example).

### Export
1. `git clone https://github.com/PaVoS-TECO/Export-docker.git`
2. `cd Export-docker`
3. `sudo docker-compose -f dcomp.yml up --build -d`
4. The export docker is now started and can be accessed from the webinterface to start and manage exports.

## Data-Management
### Core-Internal
To ensure a fast and simple way to access all data that is currently managed by the core (especially the grid), PaVoS uses a uniform data-class, called "ObservationData".  
#### Structure
An ObservationData object consists of the following variables and appropriate methods to access them:
- sensorID - the unique string identificator of the sensor  
- clusterID - the unique string identificator of the polygon, which contains the sensor (will be set by the system)  
- observationDate - the date-time of the observation (format-string: "yyyy-MM-dd'T'HH:mm:ss'Z'") (needs to be parsed via TimeUtil class)  
- singleObservations - the list of observed properties with a single dimension of data (e.g.: int, float, double, char, string)  
- vectorObservations - the list of observed properties with multiple dimensions of data (e.g.: vector, list) (no dimension-limit)  
  
For simple reconstruction of data from a database or other input methods, the ObservationData class provides getter and setter methods for all variables.  
Even more, it provides an abstraction to dimensional control and methods to add new elements to the existing dataset.  
#### Usability with Kafka
For convenience, the ObservationData class comes with a deserializer and a serializer to ensure compatibility with Kafka.
The process of serializing/deserializing is done by a mapper that creates a JSON from the contents of the ObservationData object.  
#### Special data-transformations for other software
Since Graphite and Grafana do not support the java internal data format for efficient transfer, we have to convert our ObservationData object, by the use of an converter, to python metrics.  
These metrics will then be packed with **cpickle** and sent to graphite.
Grafana simply accesses the data that was collected and stored by graphite.

## Webserver
To enable communication between core and webinterface, we decided to use a server, which accepts client connections and answers HTTP-requests.  
The WebServer runs WebWorker threads for the clients. Per default, the maximum amount of simultaneous is capped at 10'000 requests.  
A WebWorker is created for every request and not for each client.
### Structure
The WebServer uses the java.net library for the ServerSocket and the ClientSocket (Socket).  
It is executed last in the initialization of the programm and continues to run on a thread.
Since the default java.net library does not include a simple way to read HTTP-requests and send HTTP-answers, these two important steps were created manually for the WebWorker.  
Inside the WebWorkers processing of a request, we check for patterns that match a simple specified form:  
**requestType?param1=value1&param2=value2**  
- requestType stands for the name of the request we want to have answered  
- the questionmark is important! If there is no questionmark, the WebWorker will ignore the request and return a HTTP-400 bad request error.  
- parameters and values are used to inform the server of selected settings of the client. They are separated by a **&** sign.  
