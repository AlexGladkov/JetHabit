# Security Policy

## Supported Versions

JetHabit is a Compose Multiplatform demo application. The table below lists the platforms and versions currently supported:

| Platform | Version | Support Status |
|----------|---------|----------------|
| Android | API 23+ (SDK 34) | Supported |
| iOS | 14.0+ | Supported |
| JVM/Desktop | Java 21 | Supported |
| macOS | Current | Supported |
| Web | Experimental | Limited Support |

Current release: **1.0**

Note: This table should be updated when new versions are released.

## Reporting a Vulnerability

If you discover a security vulnerability in JetHabit, please report it responsibly using one of the following methods:

### Private Disclosure (Recommended)

Use GitHub's private vulnerability reporting feature:
1. Navigate to the repository's Security tab
2. Click "Report a vulnerability"
3. Fill out the advisory form with details about the issue

Note: The repository owner must enable GitHub Security Advisories in the repository settings for this feature to be available.

### Alternative Contact

For non-sensitive security matters, you may open a GitHub issue. However, for actual vulnerabilities, always prefer private disclosure to avoid exposing users to risk before a fix is available.

## What to Report

Please report security issues relevant to this project, including but not limited to:

- Vulnerabilities in project dependencies (Kotlin, Compose, Room, Ktor, etc.)
- Data leakage or insecure data storage
- Credential exposure in build configuration or code
- Insecure network communication
- Authentication or authorization bypasses

Note: JetHabit is a demo/educational application. Security issues in upstream dependencies should also be reported directly to those projects (Kotlin, Compose Multiplatform, Room, Ktor).

## Response Process

As a community-maintained demo project, we follow this best-effort process:

1. **Acknowledgment** - We aim to acknowledge receipt of vulnerability reports within 5 business days.
2. **Triage** - We will assess the severity and impact of the reported issue.
3. **Fix** - We will work on a fix and may collaborate with you for verification.
4. **Disclosure** - Once a fix is available, we will coordinate disclosure timing and publish a security advisory if appropriate.

Please note: Response times are best-effort goals, not guarantees. This is a community project maintained primarily by a single developer, not a commercial product with SLA obligations.

## Security Best Practices for Contributors

When contributing to JetHabit, please follow these security guidelines:

- Never commit secrets, API keys, or credentials to the repository
- Keep dependencies up to date and review security advisories for libraries used
- Do not include sensitive data in example configurations or test files
- Ensure signing configurations and keystore files are properly excluded from version control
- Review changes for common vulnerabilities (SQL injection, XSS, insecure data storage)
- Use secure defaults in configuration and code examples

## Security Update Process

Security fixes are delivered through the following channels:

- **Patch Releases** - Critical security issues will be addressed in patch releases when feasible
- **Dependency Updates** - Security updates to dependencies will be applied and tested
- **Security Advisories** - Significant vulnerabilities will be documented in GitHub Security Advisories
- **Release Notes** - Security-related changes will be noted in release documentation

Users are encouraged to keep their dependencies updated and watch the repository for security advisories.
