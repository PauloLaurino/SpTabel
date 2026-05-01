#!/bin/bash
set -euo pipefail

# Detect service user (fallback to 'tomcat')
user=$(systemctl show -p User --value spdealer.service || true)
if [ -z "$user" ]; then user=tomcat; fi

echo "Detected service user: $user"

# Backup existing unit
if [ -f /etc/systemd/system/spdealer.service ]; then
  cp /etc/systemd/system/spdealer.service /etc/systemd/system/spdealer.service.bak.$(date +%s)
  echo "Backed up existing unit to /etc/systemd/system/spdealer.service.bak.*"
fi

# Write corrected unit file
cat > /etc/systemd/system/spdealer.service <<UNIT
[Unit]
Description=Apache Tomcat spdealer
After=network.target

[Service]
Type=simple
User=$user
Group=$user
Environment="JAVA_OPTS=-Xms1024M -Xmx1024M --add-opens=java.base/java.lang=ALL-UNNAMED"
Environment="CATALINA_BASE=/usr/local/tomcat10"
Environment="CATALINA_HOME=/usr/local/tomcat10"
ExecStart=/usr/local/tomcat10/bin/catalina.sh run
ExecStop=/usr/local/tomcat10/bin/shutdown.sh
Restart=on-failure
RestartSec=10
UMask=0027

[Install]
WantedBy=multi-user.target
UNIT

echo "Wrote corrected systemd unit to /etc/systemd/system/spdealer.service"

# Reload and restart
systemctl daemon-reload
systemctl restart spdealer || true
systemctl status spdealer -l --no-pager || true

# Show recent journal lines
journalctl -u spdealer -n 200 --no-pager || true

# Show current owners of critical dirs
ls -ld /usr/local/tomcat10/{webapps,work,temp,logs} || true

# Apply ownership if the user exists
if id -u "$user" >/dev/null 2>&1; then
  chown -R "$user":"$user" /usr/local/tomcat10/webapps /usr/local/tomcat10/work /usr/local/tomcat10/temp /usr/local/tomcat10/logs || true
  echo "Applied chown to $user on tomcat dirs"
else
  echo "User $user not found, skipping chown"
fi

# Apply safe perms
find /usr/local/tomcat10 -type d -exec chmod 750 {} \; || true
find /usr/local/tomcat10 -type f -exec chmod 640 {} \; || true
chmod +x /usr/local/tomcat10/bin/*.sh || true

echo "--- tail catalina.out ---"
tail -n 200 /usr/local/tomcat10/logs/catalina.out || true

echo "--- smoke test (local curl) ---"
if command -v curl >/dev/null 2>&1; then
  curl -i -f http://127.0.0.1:8080/spdealer/api/v2/dashboards/1 || true
else
  echo "curl not found on remote host"
fi
