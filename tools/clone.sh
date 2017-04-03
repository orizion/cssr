if [ -d cssr ]; then
   cd cssr
   git reset --hard HEAD
   git pull
else
   git clone --depth=1 https://github.com/orizion/cssr
   cd cssr
fi

tools/deploy.sh
