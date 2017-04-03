set -e

cd server/domain
mvn install
cd ../mailer
mvn package
cd ../webserver.ressource/
mvn package

../../tools/deploy/system.sh
