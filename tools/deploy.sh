git pull

set -e

cd ../client
yarn install
npm run build
sudo cp -r node_modules/react* /var/www/html/node_modules
sudo cp -r style /var/www/html
sudo cp -r dist /var/www/html
sudo cp index.html /var/www/html
sudo cp *.png /var/www/html
cd ../server/domain
mvn install
cd ../mailer
mvn package
cd ../webserver.ressource/
mvn package
cd ../../../

sudo ./cssr/tools/deploy/systemd.sh
sudo ./cssr/tools/deploy/addcronmailer.sh
