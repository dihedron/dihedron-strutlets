#!/bin/bash
mvn install
if [ $? == 0 ]; then
	mv target/strutlets-demo-portlet-0.55.0.war /opt/liferay-6.2.b3/portal/deploy/
fi
	
