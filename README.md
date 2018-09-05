# PaVoS-source
**PaVoS** (**P**rocessing **a**nd **V**isualization **o**f **S**ensordata Streams) is a program for processing sensordata supplied by an Apache Kafka Stream and simultaneous visualization of said data as a web service. See [PaVoS](https://github.com/PaVoS-TECO/pavos-documents) for non-code elements of the project.

The program and surrounding elements are written for the [TECO Research Group](teco.edu) as part of the SS2018 PSE project *"Visualizing & Mining of Geospatial Sensorstreams with Apache Kafka"*.

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
To install both docker and docker-compose, you can either follow the instructions on the subpages of the docker documentation (https://docs.docker.com/install/ and https://docs.docker.com/compose/install/) or use this script: https://github.com/BowenWang29/smartaqnet-dev/blob/master/sbin/setupDocker.sh.

### Environment variables
The variable `smartaqnethome` must be set to the IP or the FQDN of the machine(s) that run(s) FROST and Kafka, either temporarily via executing `export smartaqnethome=vm.example.com` in the shell or appending that line to your shell-specific rc file (e.g. `~/.bashrc` with bash or `~/.zshrc` with zsh)

### FROST
See https://github.com/image357/docker-SensorThingsServer/

### Kafka
See https://github.com/BowenWang29/smartaqnet-dev/blob/master/sbin/startLandoop.sh

### Memcached
See https://github.com/PaVoS-TECO/pavos-source/wiki/Memcached-Server

### Graphite & Grafana
See https://github.com/PaVoS-TECO/pavos-source/wiki/Data-transfer

## Starting the modules
### Import

### Bridge
See https://github.com/PaVoS-TECO/java-mqtt-kafka-bridge/blob/master/README.md

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

### Export
