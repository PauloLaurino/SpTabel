# Tools / Debug helpers

This folder contains helper scripts to collect browser logs and snapshots for debugging the frontend.

## collect-browser-logs.js

Usage:

1. Install Playwright (once):

```powershell
npm install -D playwright
npx playwright install
```

2. Start your frontend (e.g. `npm start`) and backend if needed.

3. Run the script:

```powershell
node tools/collect-browser-logs.js --url http://localhost:3000/workspace --out debug/logs/session.json --headful
```

Output:
- `debug/logs/session-*.json` with captured console logs and network events
- `debug/logs/session-*.png` screenshot of the page
- `debug/logs/session-*.html` page HTML snapshot

Notes:
- The script requires Node and Playwright.
- The script is development-only and safe to keep in the repo.
