# Architect Agent MCP Server

An MCP server that acts as an architecture validation agent for Flutter projects. It validates that Flutter projects follow the required clean architecture structure by checking for mandatory folders.

## Role

**ROLE=ARCHITECT_AGENT**

This server enforces Flutter architecture standards by:
- Validating the presence of required architecture folders
- Blocking projects that don't follow the correct structure
- Providing read-only validation (no modification capabilities)

## Features

### Tools

- **validate_flutter_structure**
  - Validates Flutter project architecture
  - Checks for required folders: `core`, `data`, `domain`, `presentation`
  - Returns FAIL if any required folder is missing
  - No suggestions for changes (read-only validation)

## Installation

### Using npx

```bash
npx @modelcontextprotocol/server-architect
```

### Installing via npm

```bash
npm install -g @modelcontextprotocol/server-architect
```

## Usage

### Configuration

Add this server to your MCP settings:

```json
{
  "mcpServers": {
    "architect": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-architect"]
    }
  }
}
```

Or if installed globally:

```json
{
  "mcpServers": {
    "architect": {
      "command": "mcp-server-architect"
    }
  }
}
```

### Example Usage

The server provides one tool: `validate_flutter_structure`

**Input:**
```json
{
  "projectPath": "/path/to/flutter/project"
}
```

**Success Response:**
```json
{
  "valid": true,
  "message": "SUCCESS: Flutter architecture is valid. All required folders found: core, data, domain, presentation",
  "foundFolders": ["core", "data", "domain", "presentation"]
}
```

**Failure Response:**
```json
{
  "valid": false,
  "message": "FAIL: Flutter architecture validation failed. Missing required folders: domain, presentation",
  "missingFolders": ["domain", "presentation"],
  "foundFolders": ["core", "data"]
}
```

## Architecture Rules

The server validates the following structure:

```
flutter_project/
├── lib/
│   ├── core/         # Required: Core utilities and base classes
│   ├── data/         # Required: Data layer (repositories, data sources)
│   ├── domain/       # Required: Domain layer (entities, use cases)
│   └── presentation/ # Required: Presentation layer (UI, widgets)
```

### Validation Rules

1. **Required Folders**: `core`, `data`, `domain`, `presentation`
2. **If any folder is missing** → FAIL
3. **No modification suggestions** - validation only
4. **Read-only operation** - does not modify the project

## Development

### Building from source

```bash
cd src/architect
npm install
npm run build
```

### Running locally

```bash
npm run watch  # Development mode with auto-rebuild
```

## License

MIT - See LICENSE file for details
