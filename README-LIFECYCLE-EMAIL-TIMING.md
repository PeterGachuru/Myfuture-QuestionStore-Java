# Lifecycle email timing and UAT testing

## What changed

- Student lifecycle detection starts after 10 seconds and scans once per minute by default (`student.lifecycle.email.scan-delay-ms`).
- The existing email delivery worker processes pending emails once per minute rather than every 10 minutes.
- First-quiz emails use a 7-day lookback so a short scheduler outage does not cause the event to be missed.
- Long-term inactive students (30+ days since last recorded study/quiz activity) receive a dedicated reactivation message, with a 30-day per-recipient/subject duplicate window.
- Lifecycle email queueing is enabled by default. It only creates scheduled email rows; delivery is separately controlled.
- Actual delivery is controlled by `email.delivery.enabled`, defaulting to `production`. In UAT, set `production: false` to queue rows without sending real emails. The sender will leave those rows pending.

## Configuration examples

UAT (create records, do not send email):

```yaml
production: false
student.lifecycle.email.enabled: true
email.delivery.enabled: false
student.lifecycle.email.scan-delay-ms: 60000
```

Production (queue and send):

```yaml
production: true
student.lifecycle.email.enabled: true
email.delivery.enabled: true
student.lifecycle.email.scan-delay-ms: 60000
```

## Timing

A campaign email is queued within approximately one minute after the scheduled scan observes its eligibility. In production, the delivery worker then picks up due emails within approximately another minute, subject to queue size and the existing rate limit. This is polling-based, not a synchronous hook on account registration or quiz completion.

## Notes

- The scanner pages students in batches of 250 to bound memory usage, but still checks all student records each scan. Monitor database load and increase the scan delay if needed.
- The source archive provided did not include a Maven/Gradle build file, so a full build could not be run here.
