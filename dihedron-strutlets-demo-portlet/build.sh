#!/bin/bash
mvn install
if [ $? == 0 ]; then
	mv target/strutlets-demo-portlet-*.war /opt/liferay-6.2.0-ce-ga1/portal/deploy/
fi
	
