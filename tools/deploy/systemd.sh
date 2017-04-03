# Deployment as seen on

# https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html

appdir = "/var/cssr/web"
appuser = "cssr" 

if [ ! -d "$appdir" ]; then
   mkdir "$appdir"
   chown $appdir $appuser
   chgrp $appdir $appuser 
   chmod 0400 $appdir # The user can only read
   
   sudo cp ./cssr/tools/deploy/cssr.service /etc/systemd/system 
fi

sudo cp -f ./cssr/server/webserver.ressource/target/webserver.*.jar $appdir/webserver.ressource.jar
