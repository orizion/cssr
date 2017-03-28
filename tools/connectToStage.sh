
# You should copy cloud.key to this directory in order to make this work

cd "$(dirname "$0")"
ssh -i "./cssrkey.pem"  -L 3306:86.119.37.179:3306 ubuntu@86.119.37.179
read -t 200
