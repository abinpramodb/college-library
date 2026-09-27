# 🏭 Production Deployment Guide (Native Java & Cloudflare)

This guide explains how to deploy the **College Library Management System** natively using **Cloudflare Zero Trust Tunnel** and **Linux systemd**.

---

## ☁️ Deployment Method 1: Cloudflare Tunnel (Recommended)

Cloudflare Tunnel creates an encrypted outbound tunnel from your local Java server to Cloudflare's global edge network. It gives you a live public HTTPS URL accessible from smartphones, laptops, and remote users without port forwarding.

### 1. Launch Instant Cloudflare Tunnel
From the project root directory, run:
```bash
./run_cloudflare.sh
```

Or run the CLI directly:
```bash
cloudflared tunnel --url http://localhost:8080
```

### 2. Access Public HTTPS URL
Cloudflare will output a secure live link:
```text
https://annex-outlet-placing-municipality.trycloudflare.com
```
👉 Anyone with this URL can access the system over global edge SSL/TLS.

### 3. Native Background Server Mode
To run the server in 24/7 background mode on port `8080`:
```bash
java -jar LibraryManagementSystem.jar --server 8080 &
```

---

## 🐧 Deployment Method 2: Linux systemd Service (Bare Metal / VPS)

If hosting directly on an Ubuntu/Debian/RHEL Linux VPS:

### 1. Install Java 21 Runtime
```bash
sudo apt update
sudo apt install -y openjdk-21-jre-headless
```

### 2. Copy Executable JAR
```bash
sudo mkdir -p /opt/college-library
sudo cp LibraryManagementSystem.jar /opt/college-library/
```

### 3. Create Service User
```bash
sudo useradd -r -s /bin/false libraryuser
sudo chown -R libraryuser:libraryuser /opt/college-library
```

### 4. Install systemd Unit
```bash
sudo cp college-library.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now college-library
```

### 5. Check Service Status
```bash
sudo systemctl status college-library
```

---

## 🔒 Security Best Practices
- Keep your Java runtime updated (JDK 21 LTS).
- Cloudflare Tunnel provides automatic DDoS shielding, TLS 1.3 encryption, and WAF filtering.
- Immutable audit trail logs every administrative and circulation transaction in real time.
