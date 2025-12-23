#!/usr/bin/env node

/**
 * VALIDATOR_AGENT
 * 
 * TASKS:
 * - Ejecutar flutter analyze
 * - Ejecutar flutter test
 * - Detectar estado duplicado o no idempotente
 * 
 * RULE:
 * - Warning crítico o test fallido → FAIL
 */

import { spawn } from 'child_process';
import { existsSync } from 'fs';
import { resolve } from 'path';

class ValidatorAgent {
  constructor(projectPath = '.') {
    this.projectPath = resolve(projectPath);
    this.errors = [];
    this.warnings = [];
    this.duplicateStateIssues = [];
  }

  /**
   * Execute a command and capture output
   */
  async executeCommand(command, args = [], options = {}) {
    return new Promise((resolve, reject) => {
      const proc = spawn(command, args, {
        cwd: this.projectPath,
        ...options
      });

      let stdout = '';
      let stderr = '';

      proc.stdout?.on('data', (data) => {
        stdout += data.toString();
      });

      proc.stderr?.on('data', (data) => {
        stderr += data.toString();
      });

      proc.on('close', (code) => {
        resolve({
          code,
          stdout,
          stderr,
          success: code === 0
        });
      });

      proc.on('error', (error) => {
        reject(error);
      });
    });
  }

  /**
   * Check if Flutter is available
   */
  async checkFlutterInstalled() {
    try {
      const result = await this.executeCommand('flutter', ['--version']);
      return result.success;
    } catch (error) {
      console.error('Flutter is not installed or not in PATH');
      return false;
    }
  }

  /**
   * Check if this is a Flutter project
   */
  isFlutterProject() {
    const pubspecPath = resolve(this.projectPath, 'pubspec.yaml');
    return existsSync(pubspecPath);
  }

  /**
   * Run flutter analyze
   */
  async runFlutterAnalyze() {
    console.log('🔍 Running flutter analyze...\n');
    
    try {
      const result = await this.executeCommand('flutter', ['analyze']);
      
      console.log(result.stdout);
      if (result.stderr) {
        console.error(result.stderr);
      }

      // Parse critical warnings from output
      const criticalPatterns = [
        /error •/gi,
        /warning •/gi,
        /lint •/gi
      ];

      criticalPatterns.forEach(pattern => {
        const matches = result.stdout.match(pattern);
        if (matches && matches.length > 0) {
          this.warnings.push(`Found ${matches.length} issues matching pattern: ${pattern}`);
        }
      });

      // Check for critical errors
      if (result.stdout.includes('error •')) {
        this.errors.push('Critical errors found in flutter analyze');
      }

      return result.success;
    } catch (error) {
      this.errors.push(`Failed to run flutter analyze: ${error.message}`);
      return false;
    }
  }

  /**
   * Run flutter test
   */
  async runFlutterTest() {
    console.log('\n🧪 Running flutter test...\n');
    
    try {
      const result = await this.executeCommand('flutter', ['test']);
      
      console.log(result.stdout);
      if (result.stderr) {
        console.error(result.stderr);
      }

      // Check for test failures
      if (!result.success || result.stdout.includes('FAILED') || result.stdout.includes('Failed')) {
        this.errors.push('Tests failed');
      }

      return result.success;
    } catch (error) {
      this.errors.push(`Failed to run flutter test: ${error.message}`);
      return false;
    }
  }

  /**
   * Detect duplicate or non-idempotent state
   * This checks for common patterns that indicate duplicate state or non-idempotent operations
   */
  async detectDuplicateState() {
    console.log('\n🔎 Detecting duplicate or non-idempotent state...\n');
    
    try {
      // Search for duplicate state patterns in Dart files
      const patterns = [
        // Multiple setState calls in the same method
        { pattern: 'setState', description: 'Multiple setState calls (potential non-idempotent state)' },
        // Duplicate variable declarations
        { pattern: 'final.*=.*final', description: 'Potential duplicate declarations' },
        // Non-idempotent operations
        { pattern: '\\+\\+|--', description: 'Increment/decrement operations (potential non-idempotent)' },
      ];

      // Use grep to search for patterns
      for (const { pattern, description } of patterns) {
        try {
          const result = await this.executeCommand('grep', [
            '-r',
            '-n',
            pattern,
            '--include=*.dart',
            '.'
          ]);

          if (result.stdout && result.stdout.trim()) {
            const lines = result.stdout.trim().split('\n');
            if (lines.length > 10) {
              this.duplicateStateIssues.push({
                description,
                count: lines.length,
                sample: lines.slice(0, 3).join('\n')
              });
            }
          }
        } catch (error) {
          // Grep returns non-zero when no matches found, which is fine
        }
      }

      if (this.duplicateStateIssues.length > 0) {
        console.log('⚠️  Potential duplicate or non-idempotent state issues found:');
        this.duplicateStateIssues.forEach(issue => {
          console.log(`  - ${issue.description}: ${issue.count} occurrences`);
        });
        console.log('\nNote: These are potential issues and require manual review.\n');
      } else {
        console.log('✅ No obvious duplicate or non-idempotent state issues detected.\n');
      }

      return true;
    } catch (error) {
      console.warn(`Warning: Could not complete duplicate state detection: ${error.message}`);
      return true; // Don't fail on detection issues
    }
  }

  /**
   * Run all validations
   */
  async validate() {
    console.log('🚀 Starting Validator Agent\n');
    console.log(`Project path: ${this.projectPath}\n`);

    // Check if Flutter is installed
    const flutterInstalled = await this.checkFlutterInstalled();
    if (!flutterInstalled) {
      console.error('❌ FAIL: Flutter is not installed or not in PATH');
      return false;
    }

    // Check if this is a Flutter project
    if (!this.isFlutterProject()) {
      console.error('❌ FAIL: Not a Flutter project (pubspec.yaml not found)');
      return false;
    }

    // Run flutter analyze
    const analyzeSuccess = await this.runFlutterAnalyze();
    
    // Run flutter test
    const testSuccess = await this.runFlutterTest();
    
    // Detect duplicate state
    await this.detectDuplicateState();

    // Generate final report
    console.log('\n' + '='.repeat(60));
    console.log('📊 VALIDATION REPORT');
    console.log('='.repeat(60));
    
    if (this.errors.length > 0) {
      console.log('\n❌ ERRORS:');
      this.errors.forEach(error => console.log(`  - ${error}`));
    }

    if (this.warnings.length > 0) {
      console.log('\n⚠️  WARNINGS:');
      this.warnings.forEach(warning => console.log(`  - ${warning}`));
    }

    if (this.duplicateStateIssues.length > 0) {
      console.log('\n⚠️  DUPLICATE STATE ISSUES:');
      this.duplicateStateIssues.forEach(issue => {
        console.log(`  - ${issue.description}: ${issue.count} occurrences`);
      });
    }

    const hasCriticalIssues = this.errors.length > 0 || 
                               (this.warnings.length > 0 && this.warnings.some(w => w.includes('error')));

    console.log('\n' + '='.repeat(60));
    if (hasCriticalIssues || !analyzeSuccess || !testSuccess) {
      console.log('❌ VALIDATION RESULT: FAIL');
      console.log('='.repeat(60) + '\n');
      return false;
    } else {
      console.log('✅ VALIDATION RESULT: PASS');
      console.log('='.repeat(60) + '\n');
      return true;
    }
  }
}

// CLI execution
if (import.meta.url === `file://${process.argv[1]}`) {
  const projectPath = process.argv[2] || '.';
  const validator = new ValidatorAgent(projectPath);
  
  validator.validate()
    .then(success => {
      process.exit(success ? 0 : 1);
    })
    .catch(error => {
      console.error('Fatal error:', error);
      process.exit(1);
    });
}

export default ValidatorAgent;
