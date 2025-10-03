# PWM – Composable Service (Angular SPA + Spring Boot)

- **Backend (Spring Boot)**: JWT + (Demo)TOTP-Flow, AES-GCM-Encrypt für Vault, Postgres, GraphQL hello, RabbitMQ publish.
- **SPA (Angular)**: Login → TOTP → Vault-CRUD (einfach), eigene Pipeline (Docker build → Nginx).
- **MPA (Express+EJS)**: SSR Touchpoint (Statusseite).
- **Worker (Node)**: Queue Consumer, HIBP Range API Check.
- **RabbitMQ**: Queue Service.

## Deploy (Render Blueprint)
1. Repo pushen.
2. In Render: **New → Blueprint**, Repo auswählen.
3. Env Vars werden per `render.yaml` gesetzt (JWT_SECRET, APP_ENCRYPTION_KEY etc.).
4. Öffne `pwm-spa-angular` URL (Frontend).

> Sicherheit/Validation sind minimal für Demo. Für Produktion: echte TOTP-Validierung, Refresh Tokens, Rate Limits, CSP, Secrets Rotation, etc.
