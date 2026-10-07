#!/usr/bin/env bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Detect Java 21/17 runtime
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

exec java LibraryManagementSystem.java "$@"
