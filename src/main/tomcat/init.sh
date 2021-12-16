#!/bin/sh

cd /usr/local/tomcat/webapps
mkdir ROOT
cd ROOT
jar xvf /root/ROOT.war
ln -s /srv test
cd ../..
exec bin/catalina.sh run
