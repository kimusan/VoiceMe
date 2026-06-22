# VoiceMe Build and Release Plan

## Versioning

Use semantic versions for `versionName` and a monotonically increasing integer `versionCode`. Suggest a version bump when a user-visible feature set is complete, privacy behavior changes, ASR model compatibility changes, or release artifacts are ready.

## Release channels

- GitHub Releases: signed APK, checksums, changelog, source tag.
- F-Droid: FOSS-compatible flavor once dependencies and model delivery are acceptable.
- Play Store: optional later; may require an IME-focused or carefully justified accessibility implementation.

## Required release documents

Keep these tracked documents current:

- `README.md` - overview, build/install instructions, screenshots when available.
- `PRIVACY.md` - data flow and privacy behavior.
- `docs/PERMISSIONS.md` - permission table and justification.
- `docs/THREAT_MODEL.md` - assets, threats, mitigations.
- `docs/ARCHITECTURE.md` - current technical design.
- `docs/UX_ONBOARDING.md` - onboarding, usage, settings UX.
- `CHANGELOG.md` - user-facing release changes.
- `SECURITY.md` - vulnerability reporting and supported versions.
- `LICENSE` - project license.
- `NOTICE` or third-party license inventory before first binary release.
- `fastlane/metadata/android/...` before F-Droid/Play submission.

## CI goals

When the Android project is bootstrapped, CI should run:

- Gradle wrapper validation.
- Kotlin formatting/lint.
- Unit tests.
- Android lint.
- Debug APK build.
- Release APK build for release branches/tags.
- Dependency/license report.
- SBOM generation if practical.
- Checksum generation for release artifacts.

## Signing

- Keep release keystores out of git.
- Keep signing credentials in local files or CI secrets.
- Publish APK SHA-256 checksums and signing certificate fingerprint.
- Build releases from clean tags.

## F-Droid readiness

- Use only FOSS dependencies in the F-Droid flavor.
- Avoid proprietary SDKs and analytics.
- Ensure model licenses are documented.
- Avoid opaque binary downloads; if model downloads are required, make them explicit and policy-compliant.
- Provide Fastlane metadata and changelog snippets.
