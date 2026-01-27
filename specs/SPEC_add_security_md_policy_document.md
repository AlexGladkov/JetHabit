# Spec: Add SECURITY.md Policy Document

## Summary

Create a `SECURITY.md` file at the repository root that documents the project's security policy. The document will cover how to report vulnerabilities, which versions are supported, and how security updates are handled. This is a standard open-source practice that GitHub recognizes and surfaces in the repository's Security tab.

JetHabit is a Compose Multiplatform demo application targeting Android, iOS, JVM/Desktop, macOS, and Web. The project is maintained primarily by Alex Gladkov and is MIT-licensed. The security policy should reflect that this is a demo/educational project while still providing responsible disclosure guidance.

## Files to Create/Modify

| File | Action | Purpose |
|------|--------|---------|
| `SECURITY.md` | **Create** | Root-level security policy document |

No existing files need modification. The file is placed at the repository root alongside `README.md` and `AGENTS.md`, following standard GitHub conventions.

## Implementation Approach

### Document Structure

The `SECURITY.md` file will contain the following sections:

1. **Security Policy** (H1 title)
2. **Supported Versions** — Table listing platforms and their current support status. Platforms: Android (API 23+, SDK 34), iOS (14.0+), JVM/Desktop (Java 21), macOS, Web (experimental). Version `1.0` is the current release.
3. **Reporting a Vulnerability** — Instructions for responsible disclosure via GitHub private vulnerability reporting or direct email to the maintainer. Emphasize private reporting over public issues.
4. **What to Report** — Examples of security concerns relevant to this project (dependency vulnerabilities, data leakage, insecure storage, credential exposure in build config).
5. **Response Process** — Expected timeline and steps: acknowledgment, triage, fix, and disclosure. Provide realistic expectations for a community-maintained demo project.
6. **Security Best Practices for Contributors** — Brief guidance on avoiding common issues: no secrets in code, dependency updates, signing configuration hygiene.
7. **Security Update Process** — How security fixes are delivered (patch releases, dependency bumps, advisory notices).

### Style and Conventions

- Match the existing documentation tone: technical but approachable, concise.
- Use H1 (`#`) for the document title, H2 (`##`) for main sections, consistent with how `AGENTS.md` is structured.
- Use tables for structured data (supported versions).
- Use bullet points for lists.
- Keep line length readable (no hard wrapping requirement, matching existing docs).
- No emojis.

### Contact Information

- Primary contact: GitHub Issues (for non-sensitive matters) and GitHub Security Advisories / private reporting for vulnerabilities.
- The maintainer's email will not be hardcoded to avoid spam; instead, direct users to GitHub's built-in private vulnerability reporting feature.

### GitHub Integration

- GitHub automatically detects `SECURITY.md` at the repository root and displays it in the repository's "Security" tab and when users create security-related issues.

## Acceptance Criteria

1. A `SECURITY.md` file exists at the repository root.
2. The file contains a **Supported Versions** section with a table listing platforms/versions and their support status.
3. The file contains a **Reporting a Vulnerability** section with clear instructions for private disclosure (GitHub Security Advisories).
4. The file contains a **Response Process** section describing acknowledgment, triage, fix, and disclosure steps.
5. The file contains a **Security Update Process** section explaining how fixes are delivered.
6. The file contains guidance for contributors on security best practices.
7. The document tone and formatting are consistent with existing project documentation (`README.md`, `AGENTS.md`).
8. The file is valid Markdown and renders correctly on GitHub.
9. The file is committed on the `feature/add-security-md-policy-document-902e50e9` branch.

## Edge Cases and Risks

| Risk / Edge Case | Mitigation |
|-------------------|------------|
| **Maintainer email exposure** | Use GitHub's private vulnerability reporting feature instead of listing a personal email. This avoids spam and keeps contact info centralized. |
| **Unrealistic response time promises** | Frame response timelines as best-effort goals, not guarantees. This is a community/demo project, not a commercial product with SLA obligations. |
| **Scope mismatch** | Clearly state that JetHabit is a demo/educational application. Security issues in upstream dependencies (Kotlin, Compose, Room, Ktor) should be reported to those projects directly. |
| **Stale supported versions table** | Keep the table simple (current version only). Note that the table should be updated when new versions are released. |
| **Over-engineering for a demo project** | Keep the policy proportional to the project's scope. Avoid enterprise-level processes that would be unrealistic for a single-maintainer demo project. |
| **Missing GitHub Security Advisories setup** | The document can reference the feature, but the repository owner must separately enable it in GitHub settings. Include a note about this. |
| **Web platform marked experimental** | Clearly mark web support status in the versions table to avoid confusion about what is and isn't in scope. |
