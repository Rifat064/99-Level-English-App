# Google Play Data Safety Form Guide

Use this guide to fill out the Data Safety section in your Google Play Console for **ShobdoDaily**.

## Data Collection and Security
- **Does your app collect or share any of the required user data types?**
  Yes.
- **Is all of the user data collected by your app encrypted in transit?**
  Yes. (Supabase uses HTTPS for all communication)
- **Do you provide a way for users to request that their data be deleted?**
  Yes. (Users can delete via Settings, and you provide an account deletion URL).

## Data Types Collected

### 1. Personal Info
- **Name**: Collected.
  - **Is this data collected, shared, or both?** Collected.
  - **Is this data processed ephemerally?** No.
  - **Is this data required for your app, or can users choose whether it's collected?** Required (for Google sign-in).
  - **Why is this user data collected?** App functionality, Account management.
- **Email address**: Collected.
  - **Is this data collected, shared, or both?** Collected.
  - **Is this data processed ephemerally?** No.
  - **Is this data required for your app, or can users choose whether it's collected?** Required.
  - **Why is this user data collected?** App functionality, Account management.

### 2. App Activity
- **Other user-generated content** (e.g., Bookmarks, Quiz Answers, Self-ratings): Collected.
  - **Is this data collected, shared, or both?** Collected.
  - **Is this data processed ephemerally?** No.
  - **Is this data required for your app, or can users choose whether it's collected?** Required.
  - **Why is this user data collected?** App functionality (syncing progress).

### 3. App Info and Performance
- **Crash logs**: Collected.
  - **Is this data collected, shared, or both?** Collected.
  - **Is this data processed ephemerally?** No.
  - **Is this data required for your app, or can users choose whether it's collected?** Required (or Optional depending on Sentry settings, but generally required for stability).
  - **Why is this user data collected?** Analytics.
- **Diagnostics** (Performance/Tracing): Collected.
  - **Is this data collected, shared, or both?** Collected.
  - **Is this data processed ephemerally?** No.
  - **Is this data required for your app, or can users choose whether it's collected?** Required.
  - **Why is this user data collected?** Analytics.

### 4. Device or Other IDs
- **Device or other IDs**: Collected (Sentry unique installations).
  - **Is this data collected, shared, or both?** Collected.
  - **Is this data processed ephemerally?** No.
  - **Is this data required for your app, or can users choose whether it's collected?** Required.
  - **Why is this user data collected?** Analytics.

## URLs required for Play Console
1. **Privacy Policy URL**: Host `docs/privacy_policy.html` (e.g. on GitHub Pages) and paste the link.
2. **Account Deletion URL**: Host `docs/account_deletion.html` and paste the link.
