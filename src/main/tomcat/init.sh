#!/bin/sh -x

if [ -z "$1" ]
then
  echo "usage: $0 dir-path" >&2
  exit 1
fi

cd /usr/local/tomcat/webapps
if [ -e ROOT ] ; then
  echo "ROOT webapp already exists, will not overwrite"
else
  mkdir ROOT
  cd ROOT
  jar xvf /root/war/*.war
  ln -v -s $1
fi
cd ../..

exec bin/catalina.sh run
