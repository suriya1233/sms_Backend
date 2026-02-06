# Render Environment Variables Configuration

## Database Configuration (Railway MySQL)

Copy these values into your Render dashboard under Environment Variables:

### Required Variables

| Variable Name | Value |
|--------------|-------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://switchback.proxy.rlwy.net:13270/railway?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `SPRING_DATASOURCE_USERNAME` | `root` |
| `SPRING_DATASOURCE_PASSWORD` | `uptYibevkuuPLoNjthBNueBYRgcotkkW` |
| `JWT_SECRET` | *Let Render generate this or use:* `openssl rand -base64 64` |
| `JWT_EXPIRATION` | `86400000` |

### Optional Variables (Recommended)

| Variable Name | Value | Description |
|--------------|-------|-------------|
| `SPRING_JPA_DDL_AUTO` | `update` | Auto-update database schema |
| `SPRING_JPA_SHOW_SQL` | `false` | Disable SQL logging in production |

---

## How to Set Environment Variables in Render

### Method 1: During Initial Setup

1. When creating the web service, scroll to "Environment Variables"
2. Click "Add Environment Variable" for each variable above
3. Copy and paste the exact values
4. Click "Create Web Service"

### Method 2: After Service is Created

1. Go to your service in Render Dashboard
2. Click "Environment" in the left sidebar
3. Add each variable:
   - Click "Add Environment Variable"
   - Enter key name (e.g., `SPRING_DATASOURCE_URL`)
   - Enter value (copy from table above)
   - Click "Save Changes"
4. Render will automatically redeploy with new variables

---

## Testing Database Connection Locally (Optional)

If you have Docker installed, test the connection before deploying:

```bash
cd c:\Users\SivaSuriyan\OneDrive\Desktop\Studentt\StudentLoginBackend

docker build -t student-backend .

docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://switchback.proxy.rlwy.net:13270/railway?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="uptYibevkuuPLoNjthBNueBYRgcotkkW" \
  -e JWT_SECRET="test-secret-key-minimum-256-bits-for-development-only" \
  student-backend
```

Then test:
```bash
curl http://localhost:8081/actuator/health
```

Expected: `{"status":"UP"}`

---

## Quick Deploy Checklist

- [ ] Push code to GitHub
  ```bash
  git add .
  git commit -m "Add Docker configuration"
  git push origin main
  ```
- [ ] Go to [Render Dashboard](https://dashboard.render.com)
- [ ] Click "New +" → "Blueprint" (or "Web Service")
- [ ] Connect your GitHub repository
- [ ] Add all environment variables listed above
- [ ] Click "Create Web Service" / "Apply"
- [ ] Wait for deployment (5-10 minutes)
- [ ] Test health endpoint: `https://your-app.onrender.com/actuator/health`
- [ ] Update frontend API URL to point to Render

---

## Security Warning

> [!CAUTION]
> **Do NOT commit these credentials to Git!** This file is for reference only. Always use environment variables in Render.

> [!TIP]
> Add this file to `.gitignore` if you want to keep it locally:
> ```bash
> echo "RENDER_ENV_VARS.md" >> .gitignore
> ```

---

## Troubleshooting

### Connection Timeout
- Check that Railway database is running
- Verify the host and port are correct
- Ensure Railway allows external connections

### Authentication Failed
- Double-check username and password
- Ensure no extra spaces in environment variables

### Database Not Found
- Verify database name is `railway`
- Check Railway dashboard for actual database name

---

## Next Steps

1. Set these environment variables in Render
2. Deploy your application
3. Monitor logs for any connection issues
4. Test all API endpoints

For complete deployment instructions, see [DEPLOYMENT.md](file:///c:/Users/SivaSuriyan/OneDrive/Desktop/Studentt/StudentLoginBackend/DEPLOYMENT.md)
