# 🚀 Deploying to Render (24/7 Free Cloud Hosting)

This guide shows you how to deploy the **College Library Management System** to **Render** so it runs permanently online 24/7 with a fixed HTTPS URL.

---

## 📋 What Was Prepared

We've added everything Render needs:
1. [`Dockerfile`](file:///Users/abinpramodb/Downloads/library/Dockerfile) — Multi-stage Eclipse Temurin JDK 21 build.
2. [`render.yaml`](file:///Users/abinpramodb/Downloads/library/render.yaml) — Render configuration blueprint.
3. [`Main.java`](file:///Users/abinpramodb/Downloads/library/java-lms/src/com/library/Main.java) — Updated to automatically read Render's dynamic `$PORT` and run in headless server mode.

---

## Step 1: Push Your Code to GitHub

If you haven't already pushed this project to GitHub:

1. Open your terminal:
   ```bash
   cd /Users/abinpramodb/Downloads/library
   ```

2. Initialize and commit:
   ```bash
   git add .
   git commit -m "Configure Render Docker deployment"
   ```

3. Create a new repository on [GitHub](https://github.com/new) (e.g. `college-library`).

4. Link and push to GitHub:
   ```bash
   git branch -M main
   git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/college-library.git
   git push -u origin main
   ```

---

## Step 2: Create Web Service on Render

1. Go to [https://dashboard.render.com](https://dashboard.render.com) and sign in (you can sign in with your GitHub account).
2. Click the **"New +"** button at the top right and select **"Web Service"**.
3. Choose **"Build and deploy from a Git repository"** and click **Next**.
4. Connect and select your **`college-library`** repository.
5. Fill in the basic settings:
   - **Name**: `college-library-system` (or whatever name you like)
   - **Region**: Oregon (US West) or Singapore (pick closest to you)
   - **Branch**: `main`
   - **Runtime**: **Docker** *(Render detects Dockerfile automatically)*
   - **Instance Type**: **Free**
6. Click **"Deploy Web Service"** at the bottom.

---

## Step 3: Access Your 24/7 Live Website!

Render will build your container and deploy the app. Within 2–3 minutes, you will get a permanent public link:

```
https://college-library-system.onrender.com
```

- ✅ **Permanent Link**: Unlike temporary tunnels, this link **never changes**.
- ✅ **Runs 24/7**: Your Mac does **not** need to stay awake or turned on.
- ✅ **HTTPS Secured**: Free SSL certificate included out of the box.
- ✅ **Mobile & Tablet Friendly**: Anyone in your college can open and use it anytime.

---

## 🔑 Login Accounts for Testing

Once deployed, share these accounts with students and teachers:

| Role | Username / ID | Password |
|---|---|---|
| **Student** | `2026-CS-001` | `password123` |
| **Student** | `2026-EC-014` | `password123` |
| **Librarian (Admin)** | `LIB-001` | `password123` |
