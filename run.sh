#!/bin/bash

# export MAVEN_OPTS="-Djava.library.path=/usr/lib/jni/ -Dproperties.dir=classpath:"
# mvn spring-boot:run

java -Djava.library.path=/usr/lib/jni/ -Dproperties.dir=classpath: -jar WebControl/target/WebControl.jar