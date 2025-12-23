#!/bin/bash
set -e

echo "ORCHESTRATOR: Starting workflow"

bash simulator/simulate_design.sh

echo "ORCHESTRATOR: Running Brand Agent"
cat agents/brand.agent

echo "ORCHESTRATOR: Running UX Agent"
cat agents/ux.agent

echo "ORCHESTRATOR: Running Bridge Agent"
cat agents/bridge.agent

echo "ORCHESTRATOR: Running AutoDev Agent"
cat agents/autodev.agent

echo "ORCHESTRATOR: Flow completed"orchestrator/run_flow.sh