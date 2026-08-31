# Security Setup

This project does not store runtime secrets in `application.properties`.
Set the following values as environment variables in local, CI, and production environments.

## Required Environment Variables

| Variable | Purpose |
| --- | --- |
| `SPRING_DATASOURCE_URL` | MySQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | MySQL application account |
| `SPRING_DATASOURCE_PASSWORD` | MySQL application account password |
| `JWT_SECRET` | Secret key used to sign access and refresh tokens |
| `MAIL_USERNAME` | SMTP sender account |
| `MAIL_PASSWORD` | SMTP app password |
| `CRISIS_DETECTION_API_KEY` | n8n crisis-detection webhook `x-api-key` value |

## Immediate Rotation Checklist

- Revoke the previously committed Gmail app password and issue a new app password.
- Replace the JWT signing secret with a new random value of at least 32 bytes.
- Rotate the database password if the repository has been shared outside the team.
- Configure the production server with environment variables instead of committed files.
- Keep `.env`, `.env.*`, and `application-local.properties` out of Git.

## Local Development

Copy `.env.example` to `.env` and fill in local-only values, or set the variables directly in your shell or IDE run configuration.
