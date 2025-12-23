#!/bin/bash
set -e

bash orchestrator/run_flow.sh
bash emulator/run_emulator.sh

echo "PIPELINE: SUCCESS"