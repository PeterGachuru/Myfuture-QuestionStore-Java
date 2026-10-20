# Google Play RTDN backend setup

The backend exposes `POST /subscriptions/googleplay/rtdn/push` for authenticated Pub/Sub push delivery. It stores each Pub/Sub message before acknowledging it, retries messages whose purchase token has not yet been associated with a MyFuture user, queries the Google Play Developer API for current state, and records a renewal only once per package and verified order ID.

## Runtime configuration

Set the following environment-backed Spring properties in each deployed backend instance:

```properties
google.play.package-name=ke.co.myfuture
google.play.rtdn.push-audience=<the exact audience configured for the Pub/Sub push subscription>
google.play.rtdn.push-service-account=<the Pub/Sub push authentication service account email>
google.play.rtdn.retry-delay-ms=60000
```

Provide Application Default Credentials for a service identity that has Google Play Android Developer API access to this app. Grant that identity the required Play Console permissions and enable the Android Publisher API in its Google Cloud project. Do not put service-account keys or OAuth tokens in source control. The backend requests the `androidpublisher` OAuth scope at runtime.

Configure a Pub/Sub topic for package `ke.co.myfuture`, grant the Google Play developer notification publisher permission to publish to that topic, then configure an authenticated push subscription targeting the endpoint above. The push OIDC token audience must exactly match `google.play.rtdn.push-audience`, and its service account must match `google.play.rtdn.push-service-account`.

## Processing behavior and operations

- Push authentication, audience, expected service identity, package name, and Pub/Sub envelope are checked before acceptance.
- `SUBSCRIPTION_RENEWED` is counted only when `purchases.subscriptionsv2.get` returns an active subscription, a future expiry, and a successful order different from the initially recorded order. Duplicate/replayed order IDs do not create another renewal row.
- Other subscription lifecycle notifications refresh access from the verified state; cancellation preserves paid time, while expired, on-hold, paused, and pending states do not extend access. Test and unsupported notification families are durably acknowledged without changing access.
- Notifications arriving before the Android purchase record remain pending and are retried. An RTDN message never creates its own account association.
- The initial `/record` endpoint binds the token to the authenticated bearer-token account and verifies the token with Play before granting access. Client amount/currency/order/time/duration values are not payment proof. The verified Subscriptions API response does not expose an authoritative price, so the recorded Android subscription amount is zero until a separate trusted price source is integrated.
- Inspect `google_play_rtdn_events` for pending events, attempt counts, and the latest error. `google_play_renewals` is the idempotent verified renewal ledger. The ledger records verified order identity and expiry; the Subscriptions API response is not treated as proof of an amount paid.

Deployments must also configure Play Console RTDN and Pub/Sub. Merely deploying this endpoint does not enable notifications. Use Play license testers and Pub/Sub test notifications before relying on live renewal processing.
