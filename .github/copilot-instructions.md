# COPILOT – LOCAL KUBERNETES WORKFLOW (WINDOWS + WSL2)

You are operating in a LOCAL-FIRST environment.

## PLATFORM
- OS: Windows 10/11
- Kubernetes: LOCAL only (no cloud)
- Runtime: WSL2 (Ubuntu)
- Container Engine: Docker Desktop (with Kubernetes enabled)
- Target: Backend services for Flutter applications

## STRICT RULES
- DO NOT use cloud resources
- DO NOT reference AKS, GKE, EKS
- DO NOT add telemetry or tracking
- DO NOT add external SaaS dependencies
- DO NOT modify Flutter frontend code

## ALLOWED TOOLS
- Docker Desktop
- kubectl
- Helm
- Minikube (optional)
- kind (optional)
- bash scripts
- YAML manifests

## KUBERNETES CONSTRAINTS
- Single-node cluster
- Linux containers only
- Local networking only
- No ingress controller unless explicitly requested

## ARCHITECTURE
- Flutter app runs outside Kubernetes
- Kubernetes hosts:
  - API services
  - Auth services
  - Background workers
  - Databases (for local dev only)

## REQUIRED OUTPUT FORMAT
When generating scripts:
- Use bash
- Make scripts idempotent
- Use set -e
- Provide COPY-PASTE ready output

When generating Kubernetes manifests:
- Use apiVersion v1 / apps/v1
- Avoid deprecated APIs
- Use minimal permissions
- Use ClusterIP services only

## SECURITY
- No privileged containers
- No hostPath volumes unless requested
- No root user inside containers

## VERSIONING (PLACEHOLDERS)
- Kubernetes version: {{K8S_VERSION}}
- Docker image tag: {{IMAGE_TAG}}

## WORKFLOW ORDER
1. Validate Docker Desktop & Kubernetes
2. Validate kubectl context (local only)
3. Build Docker images
4. Deploy to Kubernetes
5. Verify pods and services
6. Provide rollback commands

## FINAL RULE
If any step violates LOCAL-FIRST or Microsoft-supported tooling, STOP and explain why.
