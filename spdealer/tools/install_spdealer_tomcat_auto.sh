#!/usr/bin/env bash
set -euo pipefail

# install_spdealer_tomcat_auto.sh
# Detects Spring Boot version (from pom.xml) and installs a compatible Tomcat
# Defaults:
#  - Spring Boot 3.x -> Tomcat 10.x (Jakarta)
#  - Install dir: /usr/local/tomcat10
#  - Default HTTP port: 8089
# Usage:
# sudo bash install_spdealer_tomcat_auto.sh [INSTALL_DIR] [SERVICE_NAME] [TOMCAT_USER] [JAVA_HOME] [PORT]

USER_SET_INSTALL_DIR=0
if [ "$#" -ge 1 ]; then
  USER_SET_INSTALL_DIR=1
fi

INSTALL_DIR=${1:-/usr/local/tomcat10}
SERVICE_NAME=${2:-spdealer}
TOMCAT_USER=${3:-tomcat-spdealer}
JAVA_HOME=${4:-/usr/lib64/jvm/java-17-openjdk}
TOMCAT_PORT=${5:-8089}

echo "Detecting Spring Boot version from pom.xml (if available) to choose Tomcat major..."
SPRING_BOOT_VERSION=""
if [ -f pom.xml ]; then
  SPRING_BOOT_VERSION=$(grep -m1 "<version>" -n pom.xml | sed -n '1p' || true)
fi

# Simple detection: if pom.xml parent artifact spring-boot-starter-parent present, read its version
if grep -q "spring-boot-starter-parent" pom.xml 2>/dev/null; then
  SPRING_BOOT_VERSION=$(sed -n '1,120p' pom.xml | grep -A2 "spring-boot-starter-parent" | grep '<version>' | sed -E 's/.*<version>(.*)<\/version>.*/\1/' | head -1 || true)
fi

echo "Spring Boot version detected: ${SPRING_BOOT_VERSION:-(none)}"

TOMCAT_MAJOR=10
if [ -z "${SPRING_BOOT_VERSION}" ]; then
  echo "No Spring Boot version detected, defaulting to Tomcat 10.x (Jakarta)";
else
  # If major version of Spring Boot is 2.x or less, use Tomcat 9
  SB_MAJOR=$(echo "${SPRING_BOOT_VERSION}" | cut -d. -f1 || echo "3")
  if [ "${SB_MAJOR}" -lt 3 ]; then
    TOMCAT_MAJOR=9
  else
    TOMCAT_MAJOR=10
  fi
fi

echo "Selected Tomcat major: ${TOMCAT_MAJOR}"

# If user did not pass an install dir, set it to /usr/local/tomcat${TOMCAT_MAJOR}
if [ "${USER_SET_INSTALL_DIR}" -eq 0 ]; then
  INSTALL_DIR="/usr/local/tomcat${TOMCAT_MAJOR}"
  echo "No install dir provided; using ${INSTALL_DIR} based on Tomcat major"
fi

# Determine latest version in the selected Tomcat major series (prefer series 10.1 for Tomcat 10)
if [ "${TOMCAT_MAJOR}" -eq 10 ]; then
  TOMCAT_DIR="tomcat-10"
else
  TOMCAT_DIR="tomcat-9"
fi

BASE_URL="https://dlcdn.apache.org/tomcat/${TOMCAT_DIR}/"
echo "Querying ${BASE_URL} for latest version..."

# Fetch directory listing and extract v* entries, sort version numbers, pick last
LATEST_VERSION=$(curl -sS "${BASE_URL}" | grep -oE 'v[0-9]+\.[0-9]+\.[0-9]+' | sed 's/^v//' | sort -V | tail -n1 || true)
if [ -z "${LATEST_VERSION}" ]; then
  echo "Failed to detect latest Tomcat version from ${BASE_URL}. Falling back to defaults." >&2
  if [ "${TOMCAT_MAJOR}" -eq 10 ]; then
    LATEST_VERSION="10.1.18"
  else
    LATEST_VERSION="9.0.79"
  fi
fi

echo "Latest Tomcat ${TOMCAT_MAJOR} version: ${LATEST_VERSION}"

DOWNLOAD_URL="https://dlcdn.apache.org/tomcat/${TOMCAT_DIR}/v${LATEST_VERSION}/bin/apache-tomcat-${LATEST_VERSION}.tar.gz"
echo "Download URL chosen: ${DOWNLOAD_URL}"

# Reuse logic from other installers: download, extract to staging, create user, move to install dir, update server.xml port, create systemd unit
TMP_TGZ="/tmp/apache-tomcat-${LATEST_VERSION}.tar.gz"
STAGING_DIR="${INSTALL_DIR}.staging"

echo "Downloading Tomcat ${LATEST_VERSION}..."
rm -f "${TMP_TGZ}"
curl -sSfL -o "${TMP_TGZ}" "${DOWNLOAD_URL}"

echo "Extracting to staging ${STAGING_DIR}"
rm -rf "${STAGING_DIR}"
mkdir -p "${STAGING_DIR}"
tar -xzf "${TMP_TGZ}" -C /tmp
EXTRACTED_DIR="/tmp/apache-tomcat-${LATEST_VERSION}"
mv "${EXTRACTED_DIR}" "${STAGING_DIR}" || true

# create service user
if ! id -u "${TOMCAT_USER}" >/dev/null 2>&1; then
  useradd -r -s /sbin/nologin -d "${INSTALL_DIR}" "${TOMCAT_USER}" || true
fi

if [ -d "${INSTALL_DIR}" ]; then
  echo "Existing ${INSTALL_DIR} found. Backing up to ${INSTALL_DIR}.bak_$(date +%F_%H%M%S)"
  mv "${INSTALL_DIR}" "${INSTALL_DIR}.bak_$(date +%F_%H%M%S)"
fi
mv "${STAGING_DIR}" "${INSTALL_DIR}"
chown -R "${TOMCAT_USER}":"${TOMCAT_USER}" "${INSTALL_DIR}" || true
chmod -R 755 "${INSTALL_DIR}"

# Update server.xml port
SERVER_XML="${INSTALL_DIR}/conf/server.xml"
if [ -f "${SERVER_XML}" ]; then
  echo "Updating HTTP Connector port to ${TOMCAT_PORT} in ${SERVER_XML}"
  sed -i "s/port=\"[0-9]\+\"\s*protocol=\"HTTP\/1.1\"/port=\"${TOMCAT_PORT}\" protocol=\"HTTP\/1.1\"/" "${SERVER_XML}" || true
fi

# Create systemd unit
UNIT_FILE="/etc/systemd/system/${SERVICE_NAME}.service"
cat > "${UNIT_FILE}" <<EOF
[Unit]
Description=Apache Tomcat ${SERVICE_NAME}
After=network.target

[Service]
Type=simple
Environment=JAVA_HOME=${JAVA_HOME}
Environment=CATALINA_HOME=${INSTALL_DIR}
Environment=CATALINA_BASE=${INSTALL_DIR}
Environment=CATALINA_OPTS=-Xms256M -Xmx1024M
ExecStart=${INSTALL_DIR}/bin/catalina.sh run
ExecStop=${INSTALL_DIR}/bin/shutdown.sh
User=${TOMCAT_USER}
Group=${TOMCAT_USER}
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

chmod 644 "${UNIT_FILE}"
systemctl daemon-reload
systemctl enable "${SERVICE_NAME}"
systemctl start "${SERVICE_NAME}"

echo "Tomcat ${LATEST_VERSION} installed at ${INSTALL_DIR} and service ${SERVICE_NAME} started on port ${TOMCAT_PORT}."
echo "Check status: systemctl status ${SERVICE_NAME} -l --no-pager"

exit 0
