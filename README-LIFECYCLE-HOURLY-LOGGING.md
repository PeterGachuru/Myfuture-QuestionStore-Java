# MyFuture lifecycle email scan frequency and logging

## Changes
- Lifecycle email eligibility scan defaults to once per hour (`3600000` ms).
- Scan logs are reduced: one start and one completion summary per scan, plus INFO for successfully queued emails and ERROR for student processing failures.
- Per-student eligibility/trigger details, duplicate suppression, frequency-cap suppression, and routine missing-parent/email skips are DEBUG.

## External production application.yml
Add these top-level properties to the external `application.yml` used by the deployed application (merge with existing settings; do not replace the whole file):

```yaml
student:
  lifecycle:
    email:
      enabled: true
      scan-delay-ms: 3600000

logging:
  level:
    ke.co.myfuture.Myfuture.UserManagement.ReportsAndMarketing.StudentLifecycle.StudentLifecycleEmailService: INFO
```

`3600000` milliseconds is 60 minutes. Restart the application after updating the external config. The first scan runs immediately due to `initialDelay = 0`, and later scans run one hour after the previous scan completes.

For detailed per-student diagnostics temporarily change the service logging level to `DEBUG`. Return it to `INFO` afterwards to avoid large logs.

This setting controls creation of scheduled email records only. It does not change the independent email delivery worker's schedule or enable/disable actual delivery.
