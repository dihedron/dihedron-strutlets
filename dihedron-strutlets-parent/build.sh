#!/bin/bash
WAR_FILE=strutlets-demo-portlet-0.55.0.war
mvn install
if [ $? == 0 ]; then
	mv ../dihedron-strutlets-demo-portlet/target/$WAR_FILE /opt/liferay-6.2.b3/portal/deploy/
	echo ""
	echo "**********************************************************************************"
	echo "*"
	echo "*        file $WAR_FILE moved to deploy directory"
	echo "*"
	echo "**********************************************************************************"
fi
