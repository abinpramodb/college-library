package com.library;

import com.library.ui.ConsoleUI;
import com.library.ui.TestRunner;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   COLLEGE LIBRARY MANAGEMENT SYSTEM — DESKTOP APPLICATION        ");
        System.out.println("==================================================================");

        // 1. Initialize OOP Domain Facade
        LibrarySystem system = new LibrarySystem();

        // 2. Resolve Frontend UI Root
        Path currentDir = Paths.get("").toAbsolutePath();
        Path webRoot = currentDir;
        if (currentDir.endsWith("java-lms")) {
            webRoot = currentDir.getParent();
        }

        int port = 8080;
        boolean enableCli = false;
        boolean serverOnly = java.awt.GraphicsEnvironment.isHeadless()
                || System.getProperty("os.name", "").toLowerCase().contains("linux");
        boolean runTests = false;

        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
                serverOnly = true;
            } catch (NumberFormatException ignored) {}
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--cli".equalsIgnoreCase(arg)) {
                enableCli = true;
            } else if ("--test".equalsIgnoreCase(arg) || "--tests".equalsIgnoreCase(arg)) {
                runTests = true;
            } else if ("--server".equalsIgnoreCase(arg) || "--headless".equalsIgnoreCase(arg)) {
                serverOnly = true;
            } else if ("--port".equalsIgnoreCase(arg) && i + 1 < args.length) {
                try {
                    port = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ignored) {}
            } else {
                try {
                    port = Integer.parseInt(arg);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (runTests) {
            TestRunner.runAllTests(system);
        }

        // 4. Start Embedded Zero-Dependency HTTP & REST Server
        try {
            LibraryHttpServer server = new LibraryHttpServer(system, port, webRoot);
            server.start();

            int activePort = server.getActivePort();
            String appUrl = "http://localhost:" + activePort + "/";
            System.out.println("\n🌐 Serving Exact Library UI at: " + appUrl);
            System.out.println("👥 System Portals:");
            System.out.println("   • Student:           Student Portal (ID: 2026CE045)");
            System.out.println("   • Librarian:         Librarian Console (Station #102 · LIB-001)\n");

            if (enableCli) {
                ConsoleUI console = new ConsoleUI(system);
                console.start();
                server.stop();
                System.exit(0);
            } else if (serverOnly) {
                System.out.println("🚀 Running in Production Headless Server mode.");
                System.out.println("📡 Ready for incoming production traffic on port " + activePort);
                System.out.println("  (Press Ctrl+C to terminate server)");
                Thread.currentThread().join();
            } else {
                // Launch Dedicated Standalone Desktop Application Window (Exact UI)
                launchDesktopAppWindow(appUrl);
                System.out.println("✓ Dedicated Desktop Application Window is active.");
                System.out.println("  (Press Ctrl+C in terminal to stop)");
                Thread.currentThread().join();
            }

        } catch (Exception e) {
            System.err.println("Fatal error starting Library Application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void launchDesktopAppWindow(String url) {
        String tmpProfile = System.getProperty("java.io.tmpdir") + "/lms_desktop_app_profile";
        String[] chromiumBrowsers = {
            "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome",
            "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser",
            "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge"
        };

        boolean launched = false;
        for (String execPath : chromiumBrowsers) {
            File bin = new File(execPath);
            if (bin.exists()) {
                try {
                    ProcessBuilder pb = new ProcessBuilder(
                            execPath,
                            "--app=" + url,
                            "--user-data-dir=" + tmpProfile,
                            "--no-first-run",
                            "--no-default-browser-check",
                            "--window-size=1260,840"
                    );
                    pb.start();
                    System.out.println("🚀 Launched Dedicated Desktop Application Window via " + bin.getName());
                    launched = true;

                    // Activate and bring to front
                    try {
                        new ProcessBuilder("osascript", "-e", "tell application \"" + bin.getName().replace(".app", "") + "\" to activate").start();
                    } catch (Exception ignored) {}
                    break;
                } catch (Exception ignored) {}
            }
        }

        // Fallback open command
        if (!launched) {
            try {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI(url));
                    System.out.println("🌐 Opened application window at: " + url);
                } else {
                    new ProcessBuilder("open", url).start();
                }
            } catch (Exception ignored) {}
        }
    }
}
