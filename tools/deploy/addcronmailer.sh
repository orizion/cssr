# Execute whole script as root
set -e


appdir="/var/cssr/mailer"
appuser="cssr" 

if [ ! -d "$appdir" ]; then
    mkdir "$appdir"
fi


cp -f ./cssr/server/mailer/target/mailer*.jar $appdir/mailer.jar
cp -f ./cssr/tools/deploy/startmail.sh $appdir/startmail.sh
chown -R $appuser $appdir
chgrp -R $appuser $appdir
chmod -R 555 $appdir # The user can only read

chmod 555 $appdir/mailer.jar # The user can only read
chmod 555 $appdir/startmail.sh # The user can only read


grep 'startmail.sh' /etc/crontab || echo '*/5 * * * * $appdir/startmail.sh' >> /etc/crontab

