# MyFuture lifecycle email categories

Each lifecycle stage now writes its own free-form `sender_service` category:

- `LifecycleWelcome`
- `LifecycleFirstQuizCompleted`
- `LifecycleFirstQuizReminder`
- `LifecycleInactive3Days`
- `LifecycleInactive7Days`
- `LifecycleInactiveMonthly`
- `LifecycleSubscriptionNudge`
- `LifecycleSubscriptionExpiry`
- `LifecycleSubscriptionConfirmed`
- `LifecycleWeeklyRevisionPlan`

The per-stage `campaignKey` remains separate and is used only for logging/deduplication context. Categories are passed as strings to `SchedulerService`, so adding a lifecycle stage does not require adding a value to `SenderService`.

The admin email page now obtains category filter options from distinct values already stored in `scheduled_emails.sender_service`, so both existing categories and future categories appear automatically.

## Database requirement

`scheduled_emails.sender_service` must be a string column (not an ENUM). For MariaDB, run once if this has not already been applied:

```sql
ALTER TABLE commonauth.scheduled_emails
MODIFY COLUMN sender_service VARCHAR(100) NOT NULL;
```

Existing categories such as `WeeklyScore`, `PersonalBroadcast`, `Groups`, and `Broadcast` are preserved. The legacy `SenderService` enum remains for existing callers that still use it; lifecycle email stages no longer use the shared `StudentLifecycle` category.

## Compatibility notes

- Existing records categorized as `StudentLifecycle` remain unchanged.
- The category filter is populated from database values, so historic and future category names remain searchable.
- This package contains source changes only; run your normal project build and test in UAT before production deployment.
