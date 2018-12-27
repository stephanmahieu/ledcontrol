#!/bin/bash

# export MAVEN_OPTS="-Djava.library.path=/usr/lib/jni/"
# mvn spring-boot:run

java -Djava.library.path=/usr/lib/jni/ -jar WebControl.jar