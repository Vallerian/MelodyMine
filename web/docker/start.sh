#!/bin/bash

if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

if lsof -Pi :3477 -sTCP:LISTEN -t ; then
    echo "port 3477 is in use. Stopping service using port 3477..."
    PID=$(lsof -t -i:3477)
    kill -9 $PID
fi

if ! command -v docker
then
    echo "Docker is not installed. Installing Docker..."

    sudo apt-get update
    sudo apt-get install apt-transport-https ca-certificates curl software-properties-common

    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

    sudo apt-get update
    sudo apt-get install docker-ce
else
    echo "Docker is installed."
fi

if ! command -v docker-compose
then
    echo "Docker Compose is not installed. Installing Docker Compose..."

    sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

    sudo chmod +x /usr/local/bin/docker-compose
else
    echo "Docker Compose is installed."
fi

. ./.env

[ -f nginx.conf ] && rm nginx.conf
[ -f turnserver.conf ] && rm turnserver.conf

cp nginx.conf.init nginx.conf
cp turnserver.conf.init turnserver.conf

sed -i "s/\$DOMAIN/$DOMAIN/g; s/\$SERVER_IP/$SERVER_IP/g" turnserver.conf

sed -i "s/\$DOMAIN/$DOMAIN/g; s/\$SERVER_IP/$SERVER_IP/g" nginx.conf


if [ "$(docker-compose ps -q)" ]; then
    echo "Docker Compose is running. Stopping Docker Compose..."
    docker-compose down
else
    echo "Docker Compose is not running. Start Docker Compose..."
fi

CERTBOT_DOMAIN_FILE="./certbot/conf/live/$DOMAIN/fullchain.pem"

if [ -f "$CERTBOT_DOMAIN_FILE" ]; then

    echo "Certbot domain file exists. Running all services except nginx_ssl and certbot..."
    docker compose up --scale nginx_ssl=0 --scale certbot=0 -d

else
  if lsof -Pi :80 -sTCP:LISTEN -t ; then
      echo "port 80 is in use. Stopping service using port 80..."
      PID=$(lsof -t -i:80)
      kill -9 $PID
    fi
  echo "Certbot domain file does not exist. Running all services..."
  docker compose up -d

fi
