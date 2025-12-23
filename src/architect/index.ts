#!/usr/bin/env node

import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import {
  CallToolRequestSchema,
  ListToolsRequestSchema,
} from "@modelcontextprotocol/sdk/types.js";
import { promises as fs } from 'fs';
import path from 'path';

// Interface for validation result
interface ValidationResult {
  valid: boolean;
  message: string;
  missingFolders?: string[];
  foundFolders?: string[];
}

// Required Flutter architecture folders
const REQUIRED_FOLDERS = ['core', 'data', 'domain', 'presentation'];

/**
 * Validates Flutter project architecture by checking for required folders
 * @param projectPath - Path to the Flutter project root
 * @returns ValidationResult with status and details
 */
async function validateFlutterStructure(projectPath: string): Promise<ValidationResult> {
  try {
    // Resolve the path to absolute
    const absolutePath = path.resolve(projectPath);
    
    // Check if the path exists
    try {
      const stats = await fs.stat(absolutePath);
      if (!stats.isDirectory()) {
        return {
          valid: false,
          message: `FAIL: Provided path is not a directory: ${absolutePath}`,
        };
      }
    } catch (error) {
      return {
        valid: false,
        message: `FAIL: Directory does not exist: ${absolutePath}`,
      };
    }

    // Check for lib directory (typical Flutter structure)
    const libPath = path.join(absolutePath, 'lib');
    let targetPath = absolutePath;
    
    try {
      const libStats = await fs.stat(libPath);
      if (libStats.isDirectory()) {
        targetPath = libPath;
      }
    } catch {
      // lib directory doesn't exist, use root directory
    }

    // Read directory contents
    const entries = await fs.readdir(targetPath, { withFileTypes: true });
    const foundFolders = entries
      .filter(entry => entry.isDirectory())
      .map(entry => entry.name);

    // Check which required folders are present
    const missingFolders = REQUIRED_FOLDERS.filter(
      folder => !foundFolders.includes(folder)
    );

    const presentRequiredFolders = REQUIRED_FOLDERS.filter(
      folder => foundFolders.includes(folder)
    );

    if (missingFolders.length > 0) {
      return {
        valid: false,
        message: `FAIL: Flutter architecture validation failed. Missing required folders: ${missingFolders.join(', ')}`,
        missingFolders,
        foundFolders: presentRequiredFolders,
      };
    }

    return {
      valid: true,
      message: `SUCCESS: Flutter architecture is valid. All required folders found: ${REQUIRED_FOLDERS.join(', ')}`,
      foundFolders: REQUIRED_FOLDERS,
    };
  } catch (error) {
    return {
      valid: false,
      message: `FAIL: Error during validation: ${error instanceof Error ? error.message : String(error)}`,
    };
  }
}

// The server instance
const server = new Server(
  {
    name: "architect-agent",
    version: "0.1.0",
  },
  {
    capabilities: {
      tools: {},
    },
  }
);

server.setRequestHandler(ListToolsRequestSchema, async () => {
  return {
    tools: [
      {
        name: "validate_flutter_structure",
        description: "Validates Flutter project architecture by checking for required folders (core, data, domain, presentation). Returns FAIL if any required folder is missing. This is a read-only validation tool that blocks architecture changes.",
        inputSchema: {
          type: "object",
          properties: {
            projectPath: {
              type: "string",
              description: "Path to the Flutter project root directory or lib directory",
            },
          },
          required: ["projectPath"],
          additionalProperties: false,
        },
      },
    ],
  };
});

server.setRequestHandler(CallToolRequestSchema, async (request) => {
  const { name, arguments: args } = request.params;

  if (!args) {
    throw new Error(`No arguments provided for tool: ${name}`);
  }

  switch (name) {
    case "validate_flutter_structure": {
      const projectPath = args.projectPath as string;
      if (!projectPath) {
        throw new Error("projectPath is required");
      }

      const result = await validateFlutterStructure(projectPath);
      
      return {
        content: [
          {
            type: "text",
            text: JSON.stringify(result, null, 2),
          },
        ],
      };
    }
    default:
      throw new Error(`Unknown tool: ${name}`);
  }
});

async function main() {
  const transport = new StdioServerTransport();
  await server.connect(transport);
  console.error("Architect Agent MCP Server running on stdio");
}

main().catch((error) => {
  console.error("Fatal error in main():", error);
  process.exit(1);
});
