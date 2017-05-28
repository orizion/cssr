# Deployment as seen on
# https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html
#
# Execute whole script as root
set -e


appdir="/var/cssr/web"
appuser="cssr" 

if [ ! -d "$appdir" ]; then
    mkdir "$appdir"
   
    cp ./cssr/tools/deploy/cssr.service /etc/systemd/system 
else 
    service cssr stop
    systemctl disable cssr.service
fi

cp -f ./cssr/server/webserver.ressource/target/webserver.*.jar $appdir/webserver.ressource.jar
cp -f ./cssr/tools/deploy/start.sh $appdir/start.sh
chown -R $appuser $appdir
chgrp -R $appuser $appdir
chmod -R 0400 $appdir # The user can only read
chmod 755 $appdir/start.sh # The user can only read
chmod 755 $appdir/webserver.ressource.jar # The user can only read

# Enable Systemd service
systemctl enable cssr.service

# Start service
service cssr start
       