#!/usr/bin/env bash
set -e

# Automatically detect Java JDK
if [ -z "$JAVA_HOME" ]; then
    if [ -d "/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
    elif [ -d "/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home"
    elif [ -d "/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home" ]; then
        export JAVA_HOME="/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home"
    fi
fi

if [ -n "$JAVA_HOME" ]; then
    export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "========================================================"
echo " Building College Library Management System (Java OOP) "
echo " Using Java: $(java -version 2>&1 | head -n 1)"
echo "========================================================"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

mkdir -p bin
cp -r resources bin/ 2>/dev/null || true

echo "Compiling Java source files..."
javac -d bin -sourcepath src $(find src -name "*.java")
jar cfe ../LibraryManagementSystem.jar com.library.Main -C bin . 2>/dev/null || true

echo "Compilation successful!"
echo "Starting Native Java Desktop Application (GUI)..."
exec java -ea -cp bin com.library.Main "$@"
