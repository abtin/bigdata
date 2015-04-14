#!/usr/bin/env bash
cwd=`pwd`

cp=$cwd/lib/bd-gemfire-server-dependencies-@deployVersion@.jar

# server-cache.xml has higher priority than command line parameter for server port. Investigation required!
#serverPort=$(( 20000 + $1 ))
#echo "starting server on port $serverPort"

#export JAVA_ARGS="-Dlogback.configurationFile=file:config/logback.xml -DDEPLOY_ROOT=$cwd"

gfsh -e "start server --name=server$1 --use-cluster-configuration=false --log-level=config --locators=127.0.0.1[10334] --bind-address=localhost --classpath=$cp --spring-xml-location=file://$cwd/config/server-cache.xml"
