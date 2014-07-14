#!/bin/bash
PORTAL_DIR=/opt/liferay-6.2.0-ce-ga1/portal/tomcat-7.0.42
WAR_FILE=dihedron-strutlets-demo-portlet-1.0.5-SNAPSHOT.war

if [ "$1" = "--undeploy" -o "$2" = "--undeploy" ]; then
	echo "undeploying existing portlets"
	rm -rf $PORTAL_DIR/webapps/strutlets-demo-portlet/
fi

if [ "$1" = "--clean" -o "$2" = "--clean" ]; then
	echo "cleaning previous build"
	mvn clean
fi

mvn install && cp dihedron-strutlets-demo-portlet/target/$WAR_FILE $PORTAL_DIR/../deploy

if [ $? ]; then
	echo "[INFO] FILE COPY SUCCESS"
else
	echo "[ERROR] FILE COPY FAILED"
fi

echo "[INFO] ------------------------------------------------------------------------"
