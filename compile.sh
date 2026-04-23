#!/usr/bin/env bash
# ============================================================
# compile.sh — build the SpringAuto compiler and run it
# ============================================================
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="$SCRIPT_DIR/target/spring-auto-compiler-1.0.0.jar"

# ---- defaults ----
SPEC=""
OUTPUT="./generated"
PACKAGE="com.generated.api"

usage() {
  echo "Usage: $0 --spec <openapi.yaml> [--output <dir>] [--package <pkg>]"
  echo ""
  echo "  --spec    <file>    OpenAPI 3.0 YAML file (required)"
  echo "  --output  <dir>     Output directory       (default: ./generated)"
  echo "  --package <pkg>     Base Java package      (default: com.generated.api)"
  echo ""
  echo "Examples:"
  echo "  $0 --spec openapi.yaml"
  echo "  $0 --spec openapi.yaml --output ./my-service --package com.example.myservice"
  exit 1
}

# ---- parse args ----
while [[ $# -gt 0 ]]; do
  case "$1" in
    --spec)    SPEC="$2";    shift 2 ;;
    --output)  OUTPUT="$2";  shift 2 ;;
    --package) PACKAGE="$2"; shift 2 ;;
    --help|-h) usage ;;
    *) echo "Unknown argument: $1"; usage ;;
  esac
done

if [[ -z "$SPEC" ]]; then
  echo "ERROR: --spec is required"
  usage
fi

# ---- build compiler if needed ----
if [[ ! -f "$JAR" ]]; then
  echo "[compile.sh] Building SpringAuto compiler..."
  cd "$SCRIPT_DIR"
  mvn clean package -q
  echo "[compile.sh] Build complete."
fi

# ---- run ----
java -jar "$JAR" --spec "$SPEC" --output "$OUTPUT" --package "$PACKAGE"
