#!/usr/bin/env python3

"""
VALIDATOR_AGENT

TASKS:
- Ejecutar flutter analyze
- Ejecutar flutter test
- Detectar estado duplicado o no idempotente

RULE:
- Warning crítico o test fallido → FAIL
"""

import subprocess
import sys
import os
import re
from pathlib import Path
from typing import List, Dict, Tuple


class ValidatorAgent:
    """Flutter project validator agent"""
    
    def __init__(self, project_path: str = '.'):
        self.project_path = Path(project_path).resolve()
        self.errors: List[str] = []
        self.warnings: List[str] = []
        self.duplicate_state_issues: List[Dict[str, any]] = []
    
    def execute_command(self, command: List[str], **kwargs) -> Tuple[int, str, str]:
        """Execute a command and capture output"""
        try:
            result = subprocess.run(
                command,
                cwd=self.project_path,
                capture_output=True,
                text=True,
                **kwargs
            )
            return result.returncode, result.stdout, result.stderr
        except FileNotFoundError:
            return 1, '', f"Command not found: {command[0]}"
        except Exception as e:
            return 1, '', str(e)
    
    def check_flutter_installed(self) -> bool:
        """Check if Flutter is available"""
        returncode, stdout, stderr = self.execute_command(['flutter', '--version'])
        return returncode == 0
    
    def is_flutter_project(self) -> bool:
        """Check if this is a Flutter project"""
        pubspec_path = self.project_path / 'pubspec.yaml'
        return pubspec_path.exists()
    
    def run_flutter_analyze(self) -> bool:
        """Run flutter analyze"""
        print('🔍 Running flutter analyze...\n')
        
        returncode, stdout, stderr = self.execute_command(['flutter', 'analyze'])
        
        print(stdout)
        if stderr:
            print(stderr, file=sys.stderr)
        
        # Parse critical warnings from output
        critical_patterns = [
            (r'error •', 'errors'),
            (r'warning •', 'warnings'),
            (r'lint •', 'lints')
        ]
        
        for pattern, name in critical_patterns:
            matches = re.findall(pattern, stdout, re.IGNORECASE)
            if matches:
                self.warnings.append(f'Found {len(matches)} {name}')
        
        # Check for critical errors
        if 'error •' in stdout:
            self.errors.append('Critical errors found in flutter analyze')
        
        return returncode == 0
    
    def run_flutter_test(self) -> bool:
        """Run flutter test"""
        print('\n🧪 Running flutter test...\n')
        
        returncode, stdout, stderr = self.execute_command(['flutter', 'test'])
        
        print(stdout)
        if stderr:
            print(stderr, file=sys.stderr)
        
        # Check for test failures
        if returncode != 0 or 'FAILED' in stdout or 'Failed' in stdout:
            self.errors.append('Tests failed')
        
        return returncode == 0
    
    def detect_duplicate_state(self) -> bool:
        """Detect duplicate or non-idempotent state"""
        print('\n🔎 Detecting duplicate or non-idempotent state...\n')
        
        patterns = [
            {
                'pattern': r'setState',
                'description': 'Multiple setState calls (potential non-idempotent state)'
            },
            {
                'pattern': r'\+\+|--',
                'description': 'Increment/decrement operations (potential non-idempotent)'
            }
        ]
        
        for pattern_info in patterns:
            pattern = pattern_info['pattern']
            description = pattern_info['description']
            
            # Search for Dart files
            dart_files = list(self.project_path.glob('**/*.dart'))
            
            occurrences = []
            for dart_file in dart_files:
                if 'test' in str(dart_file) or '.dart_tool' in str(dart_file):
                    continue
                
                try:
                    with open(dart_file, 'r', encoding='utf-8') as f:
                        content = f.read()
                        matches = re.findall(pattern, content)
                        if len(matches) > 5:  # Threshold for reporting
                            occurrences.append({
                                'file': str(dart_file.relative_to(self.project_path)),
                                'count': len(matches)
                            })
                except Exception as e:
                    pass
            
            if occurrences:
                total_count = sum(occ['count'] for occ in occurrences)
                self.duplicate_state_issues.append({
                    'description': description,
                    'count': total_count,
                    'files': occurrences[:3]  # Sample first 3 files
                })
        
        if self.duplicate_state_issues:
            print('⚠️  Potential duplicate or non-idempotent state issues found:')
            for issue in self.duplicate_state_issues:
                print(f"  - {issue['description']}: {issue['count']} occurrences")
            print('\nNote: These are potential issues and require manual review.\n')
        else:
            print('✅ No obvious duplicate or non-idempotent state issues detected.\n')
        
        return True
    
    def validate(self) -> bool:
        """Run all validations"""
        print('🚀 Starting Validator Agent\n')
        print(f'Project path: {self.project_path}\n')
        
        # Check if Flutter is installed
        flutter_installed = self.check_flutter_installed()
        if not flutter_installed:
            print('❌ FAIL: Flutter is not installed or not in PATH', file=sys.stderr)
            return False
        
        # Check if this is a Flutter project
        if not self.is_flutter_project():
            print('❌ FAIL: Not a Flutter project (pubspec.yaml not found)', file=sys.stderr)
            return False
        
        # Run flutter analyze
        analyze_success = self.run_flutter_analyze()
        
        # Run flutter test
        test_success = self.run_flutter_test()
        
        # Detect duplicate state
        self.detect_duplicate_state()
        
        # Generate final report
        print('\n' + '=' * 60)
        print('📊 VALIDATION REPORT')
        print('=' * 60)
        
        if self.errors:
            print('\n❌ ERRORS:')
            for error in self.errors:
                print(f'  - {error}')
        
        if self.warnings:
            print('\n⚠️  WARNINGS:')
            for warning in self.warnings:
                print(f'  - {warning}')
        
        if self.duplicate_state_issues:
            print('\n⚠️  DUPLICATE STATE ISSUES:')
            for issue in self.duplicate_state_issues:
                print(f"  - {issue['description']}: {issue['count']} occurrences")
        
        has_critical_issues = (
            len(self.errors) > 0 or
            any('error' in w for w in self.warnings)
        )
        
        print('\n' + '=' * 60)
        if has_critical_issues or not analyze_success or not test_success:
            print('❌ VALIDATION RESULT: FAIL')
            print('=' * 60 + '\n')
            return False
        else:
            print('✅ VALIDATION RESULT: PASS')
            print('=' * 60 + '\n')
            return True


def main():
    """CLI entry point"""
    project_path = sys.argv[1] if len(sys.argv) > 1 else '.'
    validator = ValidatorAgent(project_path)
    
    try:
        success = validator.validate()
        sys.exit(0 if success else 1)
    except Exception as e:
        print(f'Fatal error: {e}', file=sys.stderr)
        sys.exit(1)


if __name__ == '__main__':
    main()
