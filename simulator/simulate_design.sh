#!/bin/bash
set -e

echo "SIMULATOR: Analyzing Flutter intent (no execution)"

mkdir -p simulator/output

cat <<EOF > simulator/output/design_model.json
{
  "source": "flutter",
  "mode": "simulation",
  "ui_intent": "semantic",
  "states": ["idle", "loading", "success", "error"],
  "navigation": "abstract",
  "responsive": true,
  "accessibility": true
}
EOF

echo "SIMULATOR: Design model generated"