# VoiceMe Permissions

This document tracks every Android permission VoiceMe requests and why. Keep it current with each release.

## Planned permissions

| Permission / capability | Required for | Notes |
| --- | --- | --- |
| `RECORD_AUDIO` | Capturing speech while dictation is active | Request only when the user starts setup or first dictation. |
| Foreground microphone service capability | Long-running/visible microphone capture while listening | Use a visible notification when required by Android. |
| Notifications | Foreground service status on Android versions that require notification permission | Ask only when needed and explain why. |
| Accessibility service | Floating dictation control with the user's normal keyboard and best-effort text insertion | Primary UX mode. Must include explicit in-app disclosure and avoid non-dictation use. |
| Input method service | Optional VoiceMe keyboard fallback | More reliable text insertion, but user must switch keyboards. |
| Internet/network | Explicit model downloads or optional update metadata | Avoid in offline/F-Droid flavor if practical; never use for cloud transcription by default. |

## Permissions intentionally avoided

- Contacts, calendar, location, SMS, call log, camera, and account permissions are not part of the product scope.
- Overlay permission (`SYSTEM_ALERT_WINDOW`) should not be needed for the MVP if the accessibility overlay is sufficient.

## Sensitive-field policy

VoiceMe should not dictate into password fields or other fields Android marks as sensitive. The app should hide or disable the dictation button when such a field is detected.
