#!/bin/bash
set -e

DOMAIN="congdongviet.duckdns.org"
EMAIL="stevewangdpc@gmail.com"
COMPOSE_FILE="/home/ubuntu/app/docker-compose.prod.yml"
NGINX_CONTAINER="app-nginx-1"

echo "================================================"
echo "  SSL Setup for $DOMAIN"
echo "================================================"

# Install certbot if not already installed
if ! command -v certbot &> /dev/null; then
    echo "[1/5] Installing certbot..."
    sudo apt-get update -qq
    sudo apt-get install -y certbot
else
    echo "[1/5] Certbot already installed."
fi

# Stop nginx to free port 80
echo "[2/5] Stopping nginx..."
docker compose -f $COMPOSE_FILE stop nginx 2>/dev/null || true

# Get certificate
echo "[3/5] Obtaining SSL certificate..."
sudo certbot certonly \
    --standalone \
    -d $DOMAIN \
    --email $EMAIL \
    --agree-tos \
    --non-interactive \
    --keep-until-expiring

echo "[4/5] Starting all services..."
docker compose -f $COMPOSE_FILE up -d

# Wait for nginx to start
sleep 5

# Verify nginx is running
if docker ps | grep -q $NGINX_CONTAINER; then
    echo "[5/5] Nginx is running with SSL."
else
    echo "[5/5] WARNING: Nginx may not be running. Check logs:"
    docker logs $NGINX_CONTAINER --tail 20
fi

# Setup auto-renewal cron job
CRON_JOB="0 3 * * * certbot renew --quiet && docker exec $NGINX_CONTAINER nginx -s reload"
if crontab -l 2>/dev/null | grep -q "certbot renew"; then
    echo "Auto-renewal cron job already exists."
else
    (crontab -l 2>/dev/null; echo "$CRON_JOB") | crontab -
    echo "Auto-renewal cron job added (runs daily at 3am)."
fi

echo ""
echo "================================================"
echo "  Done! HTTPS is active at https://$DOMAIN"
echo "  Cert auto-renews daily at 3:00 AM"
echo "================================================"
