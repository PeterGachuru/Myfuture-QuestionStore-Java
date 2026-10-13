# Admin email recipient filter

Updated `/admin/emails` to support recipient email filtering. The search is case-insensitive and supports partial matches (for example, `gmail.com` or a full email address). It is applied server-side and preserved while navigating pages and opening email details.

The database query and count query both include the recipient filter, and email body remains excluded from list queries.
