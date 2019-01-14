@echo off
rem set properties.dir=classpath:
rem java -jar WebControl\target\WebControl.jar
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar WebControl\target\WebControl.jar