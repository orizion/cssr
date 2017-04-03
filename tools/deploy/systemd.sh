# Deployment as seen on

# https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html

$appdir = "/var/cssr/web"
$user = "cssr" 

if [ ! -d "$appdir" ]; then
   mkdir "$appdir"
   chown $appdir $user
   chgrp $appdir $user 
   chmod 0400 $appdir # The user can only read
   
   cp ./cssr/tools/deploy/cssr.service /etc/systemd/system 
fi

cp -f ./cssr/server/webserver.ressource/target/webserver.*.jar $appdir/webserver.ressource.jar
