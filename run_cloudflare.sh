#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PORT=8080

# 1. Check if Java LMS server is running on port 8080
if ! lsof -i :$PORT >/dev/null 2>&1; then
    echo "⚡ Java LMS is not running on port $PORT. Starting native server..."
    if [ -f "$SCRIPT_DIR/LibraryManagementSystem.jar" ]; then
        java -jar "$SCRIPT_DIR/LibraryManagementSystem.jar" --server $PORT &
    else
        "$SCRIPT_DIR/run_java.sh" --server $PORT &
    fi
    sleep 2
fi

echo "=========================================================="
echo "   ☁️  STARTING CLOUDFLARE TUNNEL (ZERO-TRUST HTTPS)       "
echo "=========================================================="
echo "Connecting local port $PORT to Cloudflare edge network..."
echo "A secure, free public HTTPS URL will appear below."
echo "Press Ctrl+C to terminate the tunnel."
echo "----------------------------------------------------------"

exec cloudflared tunnel --url "http://localhost:$PORT"
