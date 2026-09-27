# ☁️ Cloudflare Deployment Guide for College Library Management System

This guide outlines two proven methods to deploy your Java Library Management System using **Cloudflare**:

---

## 🚀 Option 1: Cloudflare Tunnel (`cloudflared`) — **(Recommended for Java Backend)**

Because this application runs an embedded **Java HTTP backend** (`LibraryHttpServer.java`), the most powerful, free, and secure way to deploy it to the world with Cloudflare is via **Cloudflare Tunnel (Zero Trust)**.

### Why this is best:
- ✅ Keeps your full Java OOP backend, in-memory state, and REST API fully functional.
- ✅ Gives you a free public HTTPS URL (`https://your-library.trycloudflare.com` or custom domain `https://library.yourcollege.edu`).
- ✅ **No port forwarding or public IP required** — Cloudflare creates an encrypted outbound tunnel from your computer/server to Cloudflare's global edge network.
- ✅ DDoS protection, global SSL/TLS certificates, and web application firewall (WAF) included automatically.

---

### Step-by-Step Setup:

#### 1. Install `cloudflared` CLI

On **macOS** (via Homebrew):
```bash
brew install cloudflared
```

On **Linux (Ubuntu/Debian)**:
```bash
curl -L --output cloudflared.deb https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb
sudo dpkg -i cloudflared.deb
```

---

#### 2. Start Your Java Library Application
Run the application locally so the server is listening on port `8080`:
```bash
./College
# or
./run_java.sh
```

---

#### 3. Launch an Instant Quick Tunnel (No Domain or Login Needed!)
To get an instant live public HTTPS URL right away for testing and sharing:
```bash
cloudflared tunnel --url http://localhost:8080
```

Cloudflare will output a public URL like:
```text
https://random-words-1234.trycloudflare.com
```
👉 Anyone in the world (or your mobile phone on cellular 4G/5G) can open that link, browse books, log into student/librarian profiles, and scan barcodes over secure HTTPS!

---

#### 4. (Optional) Production Tunnel with Your Own Domain (e.g., `library.yourcollege.edu`)

If you own a domain registered or proxied through Cloudflare:

1. **Login to Cloudflare**:
   ```bash
   cloudflared tunnel login
   ```
2. **Create the tunnel**:
   ```bash
   cloudflared tunnel create college-library
   ```
3. **Route DNS to the tunnel**:
   ```bash
   cloudflared tunnel route dns college-library library.yourcollege.edu
   ```
4. **Create config file `~/.cloudflared/config.yml`**:
   ```yaml
   tunnel: <TUNNEL_UUID_FROM_STEP_2>
   credentials-file: /Users/<your-user>/.cloudflared/<TUNNEL_UUID>.json

   ingress:
     - hostname: library.yourcollege.edu
       service: http://localhost:8080
     - service: http_status:404
   ```
5. **Run the tunnel as a persistent service**:
   ```bash
   cloudflared tunnel run college-library
   ```

---

## ⚡ Option 2: Cloudflare Pages (Frontend Client UI Only)

If you only want to host the web/mobile client frontend on Cloudflare Pages edge network without running a 24/7 Java host machine:

### 1. Requirements:
- A GitHub repository with the static frontend (`java-lms/resources/web/index.html`).

### 2. Deploy Steps:
1. Log into your [Cloudflare Dashboard](https://dash.cloudflare.com/).
2. Navigate to **Workers & Pages** ➔ **Create application** ➔ **Pages** ➔ **Connect to Git**.
3. Select your GitHub repository.
4. Set Build Settings:
   - **Framework preset**: None
   - **Build output directory**: `java-lms/resources/web`
5. Click **Save and Deploy**.
6. Cloudflare Pages will give you a lightning-fast edge URL: `https://college-library.pages.dev`.

> [!NOTE]
> If deploying static frontend on Cloudflare Pages, set up the API URL in JavaScript to point to your Java backend tunnel URL from **Option 1**.

---

## 🛠️ Quick Comparison

| Feature | Option 1: Cloudflare Tunnel | Option 2: Cloudflare Pages |
| :--- | :--- | :--- |
| **Java Backend Execution** | Full Java OOP Engine Active | Frontend only (Needs external API) |
| **Setup Time** | 2 minutes (`cloudflared --url ...`) | 5 minutes |
| **Free SSL Certificate** | Automated by Cloudflare | Automated by Cloudflare |
| **Custom Domain Support** | Yes | Yes |
| **Recommended For** | Complete System (Desktop, Mobile, API) | Static UI Demos |
