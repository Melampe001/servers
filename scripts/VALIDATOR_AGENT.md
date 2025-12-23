# Validator Agent

## Overview

The Validator Agent is a tool designed to validate Flutter projects by running analysis, tests, and detecting potential state management issues.

## Role

**ROLE:** `VALIDATOR_AGENT`

## Tasks

The Validator Agent performs the following tasks:

1. **Ejecutar flutter analyze** - Runs Flutter static analysis to detect code issues
2. **Ejecutar flutter test** - Executes all Flutter tests in the project
3. **Detectar estado duplicado o no idempotente** - Detects duplicate or non-idempotent state patterns

## Rules

- **Critical warning or test failure → FAIL**: The validator returns a failure status if:
  - Critical warnings are found in `flutter analyze`
  - Any tests fail in `flutter test`
  - The validation process encounters errors

## Available Implementations

### JavaScript/Node.js Version

**File:** `scripts/validator_agent.js`

**Usage:**

```bash
# From the project root
node scripts/validator_agent.js [project_path]

# Or make it executable
./scripts/validator_agent.js [project_path]

# Default project path is current directory
node scripts/validator_agent.js
```

**Requirements:**
- Node.js 14+ with ES modules support
- Flutter SDK installed and in PATH

### Python Version

**File:** `scripts/validator_agent.py`

**Usage:**

```bash
# From the project root
python3 scripts/validator_agent.py [project_path]

# Or make it executable
./scripts/validator_agent.py [project_path]

# Default project path is current directory
python3 scripts/validator_agent.py
```

**Requirements:**
- Python 3.7+
- Flutter SDK installed and in PATH

## Features

### 1. Flutter Analyze

Runs `flutter analyze` to detect:
- Syntax errors
- Code warnings
- Linting issues
- Type errors

Critical errors will cause the validation to fail.

### 2. Flutter Test

Executes `flutter test` to:
- Run all unit tests
- Run widget tests
- Run integration tests
- Report test failures

Any test failure will cause the validation to fail.

### 3. Duplicate State Detection

Analyzes Dart files to detect potential issues:
- Multiple `setState` calls (potential non-idempotent state updates)
- Increment/decrement operations (`++`, `--`)
- Other patterns that might indicate state management issues

These are reported as warnings and require manual review.

## Exit Codes

- **0**: Validation passed - no critical issues found
- **1**: Validation failed - critical issues detected or tests failed

## Output Format

The validator provides structured output:

```
🚀 Starting Validator Agent

Project path: /path/to/project

🔍 Running flutter analyze...
[flutter analyze output]

🧪 Running flutter test...
[flutter test output]

🔎 Detecting duplicate or non-idempotent state...
[detection results]

============================================================
📊 VALIDATION REPORT
============================================================

❌ ERRORS:
  - [error details]

⚠️  WARNINGS:
  - [warning details]

⚠️  DUPLICATE STATE ISSUES:
  - [issue details]

============================================================
❌ VALIDATION RESULT: FAIL
============================================================
```

## Integration with CI/CD

### GitHub Actions Example

```yaml
name: Flutter Validation

on: [push, pull_request]

jobs:
  validate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - uses: subosito/flutter-action@v2
        with:
          flutter-version: '3.x'
      
      - name: Run Validator Agent
        run: python3 scripts/validator_agent.py
```

### GitLab CI Example

```yaml
flutter:validate:
  image: cirrusci/flutter:stable
  script:
    - python3 scripts/validator_agent.py
  only:
    - merge_requests
    - main
```

## Customization

Both implementations can be easily customized:

1. **Add more validation patterns**: Edit the `detectDuplicateState()` method
2. **Adjust severity levels**: Modify the error/warning classification logic
3. **Add custom checks**: Extend the `validate()` method

## Limitations

- Requires Flutter SDK to be installed
- Requires a valid Flutter project (must have `pubspec.yaml`)
- Duplicate state detection provides suggestions, not definitive errors
- Some patterns may produce false positives

## Support

For issues or questions about the Validator Agent, please refer to the main project documentation or create an issue in the repository.
