@echo off
set properties.dir=classpath:
cd WebControl
mvn jetty:run
cd ..