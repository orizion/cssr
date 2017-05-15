set -e

cd ../client
cp -r dist /var/www/html
cp index.html /var/www/html
cd ../server/domain
mvn install
cd ../mailer
mvn package
cd ../webserver.ressource/
mvn package
cd ../../

sudo ./cssr/tools/deploy/systemd.sh
