#!/bin/bash
PORTAL_DIR=/opt/liferay-6.2.b3/portal
WAR_FILE=strutlets-demo-portlet-0.59.0-SNAPSHOT.war

if [ "$1" = "--undeploy" -o "$2" = "--undeploy" ]; then
	echo "undeploying existing portlets"
	rm -rf $PORTAL_DIR/tomcat-7.0.40/webapps/strutlets-demo-portlet/
fi

if [ "$1" = "--clean" -o "$2" = "--clean" ]; then
	echo "cleaning previous build"
	mvn clean
fi

mvn install && cp dihedron-strutlets-demo-portlet/target/$WAR_FILE $PORTAL_DIR/deploy
