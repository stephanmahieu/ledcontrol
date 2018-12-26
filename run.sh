#!/bin/bash
cd WebControl

export MAVEN_OPTS="-Djava.library.path=/usr/lib/jni/ -Dproperties.dir=classpath:"

mvn jetty:run
