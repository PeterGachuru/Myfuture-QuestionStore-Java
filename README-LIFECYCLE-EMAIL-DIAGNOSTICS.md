# MyFuture lifecycle email diagnostics and scheduling

## What changed

- Lifecycle queue creation now uses `student.lifecycle.email.enabled` (default `true`) instead of being blocked whenever `production: false`. This allows UAT to create email rows while delivery remains disabled.
- The lifecycle scan runs on `student.lifecycle.email.scan-delay-ms` (default 60,000 ms), instead of a hard-coded hourly cron.
- The email delivery worker honors `email.delivery.enabled` (default `true` for compatibility). Set it to `false` in UAT to prevent real delivery while keeping queued records visible in `/admin/emails`.
- Added SLF4J traces for scan start/page/end, parent/account validation, quiz and activity timestamps, campaign triggers, queue attempts, duplicate/frequency-cap skips, queue save results, delivery scans and send results/errors. Recipient addresses are masked in logs.
- Delivery scan interval is configurable with `email.delivery.scan-delay-ms` (default 60,000 ms).

## Suggested UAT configuration

```yaml
production: false
student:
  lifecycle:
    email:
      enabled: true
      scan-delay-ms: 60000
email:
  delivery:
    enabled: false
    scan-delay-ms: 60000
logging:
  level:
    ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.StudentLifecycle: INFO
    ke.co.myfuture.Myfuture.Commonauth.ScheduledEmails: INFO
```

## How to diagnose

1. Restart the backend and look for `Student lifecycle email scan START`.
2. Look for `Student lifecycle email scan page ...` and `studentsScanned=...`.
3. For older student accounts, inspect `Lifecycle eligibility ... lastStudy=...`. Students inactive for 30+ days should log `Lifecycle trigger INACTIVE_MONTHLY` if parent and email checks pass.
4. Look for `Lifecycle email CREATE attempt`, then either `QUEUED` or `NOT queued` with the duplicate or weekly-cap reason.
5. With delivery disabled, the queued email should remain pending and appear in `/admin/emails`; the delivery worker logs that it is disabled.

## Important eligibility notes

- Students must have a valid `parent` ID that resolves to a `User` with a valid email address. Skips are logged.
- `lastStudy` is the latest of `recentActivity`, last non-deleted quiz creation time and account creation time. If `recentActivity` is recent, the student will not be considered inactive even if the account was created months ago.
- The global cap of two emails per recipient in seven days still applies across children sharing one parent email. When reached, the log explains that the email was not queued.
- A full Maven/Gradle build could not be run because the supplied archive does not contain a build descriptor. Compile and test in UAT before production deployment.
