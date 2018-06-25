#!/usr/bin/env bash

# do integration-test install and test data setup

cd /opt/dv
unzip dvinstall.zip
cd /opt/dv/testdata
./scripts/deploy/phoenix.dataverse.org/prep
./db.sh
# FIXME: Remove the line below after it is being run in `docker build`
#./install # modified from phoenix
/usr/local/glassfish4/glassfish/bin/asadmin deploy /opt/dv/dvinstall/dataverse.war
./post # modified from phoenix

