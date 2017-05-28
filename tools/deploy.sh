set -e

cd ../client
sudo cp -r node_modules /var/www/html
sudo cp -r dist /var/www/html
sudo cp index.html /var/www/html
cd ../server/domain
mvn install
cd ../mailer
mvn package
cd ../webserver.ressource/
mvn package
cd ../../

sudo ./cssr/tools/deploy/systemd.sh
