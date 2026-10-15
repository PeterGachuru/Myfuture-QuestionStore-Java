# MyFuture student lifecycle email changes

## Changes
- Added `StudentLifecycleEmailService`, an hourly production-only scan that queues lifecycle emails through the existing `ScheduledEmails` sender pipeline.
- Added the `StudentLifecycle` sender enum value.
- Added repository methods for campaign deduplication, per-recipient volume checks, non-deleted quiz lookups, and subscription lookups.
- Added a global cap of at most 2 newly queued emails per recipient in a rolling 7-day period. The cap is recipient-level, so it applies across campaigns and across multiple student accounts sharing one parent email address.
- Added long-term reactivation for accounts with no recorded activity for 30 or more days. It can include students who never completed a quiz and queues at most one same-subject monthly reactivation per shared inbox, with a 30-day duplicate window.
- Short-term inactivity messages are staged at 1, 3, 7, and 14 days of inactivity for students with a completed quiz; accounts at 30+ days use the monthly reactivation path.
- Students without a completed quiz receive first-quiz prompts at account ages 1, 3, 7, and 14 days; the monthly reactivation path begins at 30 days.
- Subscription prompts use the parent username/email mapping and point to the profile for M-Pesa subscription; emails also link to the Android app.

## Trigger behaviour
- `StudentLifecycleEmailService.processStudentLifecycleEmails()` runs at minute 12 of every hour, only when `production=true`.
- The service queues messages in `ScheduledEmails`; the existing `EmailSchedulerService` processes pending mail every 10 minutes.
- “Immediate” lifecycle emails therefore mean queued on the next hourly scan, then sent by the existing sender when due.

## Important deployment notes
- The uploaded source archive did not include a Maven/Gradle build file, so a full project compile/test could not be run here.
- Validate repository-derived queries and the subscription status/expiry assumptions against your production schema in UAT before deployment.
- The cap counts emails recorded in `ScheduledEmails` in the last seven days, including queued and previously sent items. This is intentionally conservative and may suppress lifecycle messages if other email campaigns already used the recipient's quota.
- This implementation does not add an email opt-out field because no existing preference mechanism was identified in the supplied source. Add one before treating these as promotional campaigns at scale.
