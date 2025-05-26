package ke.co.myfuture.Myfuture.Treasury.Account;

public enum AccountStatus {
    ACTIVE,           // Account is in use
    INACTIVE,         // Temporarily disabled or not used
    PENDING,          // Awaiting activation or approval
    CLOSED,           // Permanently closed
    DORMANT,          // No activity for a long period
    FROZEN,           // Blocked due to legal or other issues
    BLOCKED,          // Blocked temporarily, maybe by user or admin
    ARCHIVED,         // Historical account, kept for record purposes
    DELETED           // Marked for deletion (if using soft deletes)
}
