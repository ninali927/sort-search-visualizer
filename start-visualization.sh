#!/bin/bash

cd "$(dirname "$0")"

# Announce
echo "INFO: Starting the visualization app using the algorithms in the 'core' folder."

# Kill any existing process on port 7001 to prevent errors
echo "INFO: Attempting to free up port 7001..."
pkill -f "gradle.*run" 2>/dev/null || true

# Ensure gradlew has execute permissions
echo "INFO: Setting execute permissions for gradlew..."
chmod +x app/backend/gradlew

# Run
cd app/backend
./gradlew run --quiet &

# Instruct
echo "INFO: Visualization is ready. Open http://localhost:7001 in your browser."
