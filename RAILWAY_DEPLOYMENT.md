# Railway Deployment Guide for Spring ATS

## Prerequisites
1. A GitHub account
2. A Railway account (sign up at [railway.app](https://railway.app))

## Step-by-Step Deployment Instructions

### 1. Push Code to GitHub
```bash
# Add the latest changes
git add .
git commit -m "Fix Railway build configuration with multi-stage Dockerfile"
git push origin master
```

> **Note**: If you haven't pushed to GitHub yet, create a new repository first and push all your code.

### 2. Set Up Railway Project
1. Go to [railway.app](https://railway.app) and sign in
2. Click "New Project"
3. Select "Deploy from GitHub repo"
4. Choose your `spring-ats` repository
5. Railway will automatically detect it's a Java project

### 3. Add PostgreSQL Database
1. In your Railway project dashboard, click "New Service"
2. Select "Database" → "PostgreSQL"
3. Railway will provision a PostgreSQL database

### 4. Configure Environment Variables
In your Railway project settings, add these environment variables:

**Required Variables:**
- `SPRING_PROFILES_ACTIVE` = `prod`
- `DATABASE_URL` = (automatically set by Railway PostgreSQL service)
- `PGUSER` = (automatically set by Railway PostgreSQL service)  
- `PGPASSWORD` = (automatically set by Railway PostgreSQL service)

**Optional Variables:**
- `UPLOAD_PATH` = `/tmp/ats-uploads` (default)
- `DEFAULT_PASSWORD` = `Ats@ABC` (default admin password)

### 5. Initialize Database
1. Connect to your Railway PostgreSQL database using the provided credentials
2. Run the SQL script from `database-init.sql` to create tables and initial data

### 6. Deploy
1. Railway will automatically build and deploy your application
2. Once deployed, you'll get a public URL like: `https://your-app-name.railway.app`

### 7. Access Your Application
- Admin URL: `https://your-app-name.railway.app/ats/admin`
- Login with: `admin@spring.ats` / `Ats@ABC`

## Environment Variables Reference

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` |
| `DATABASE_URL` | PostgreSQL connection URL | Auto-set by Railway |
| `PGUSER` | Database username | Auto-set by Railway |
| `PGPASSWORD` | Database password | Auto-set by Railway |
| `UPLOAD_PATH` | File upload directory | `/tmp/ats-uploads` |
| `DEFAULT_PASSWORD` | Default user password | `Ats@ABC` |
| `PORT` | Application port | `8080` |

## Troubleshooting

### Build Issues
- Check that Java 11 is being used
- Verify all dependencies are available
- Check Railway build logs

### Database Connection Issues
- Verify PostgreSQL service is running
- Check environment variables are set correctly
- Ensure database initialization script was run

### Application Not Starting
- Check Railway deployment logs
- Verify all required environment variables are set
- Ensure the application port matches Railway's expectations

## Post-Deployment Setup
1. Test all functionality (login, file upload, interview scheduling)
2. Create additional admin/recruiter users as needed
3. Configure any additional settings

## Monitoring
- Use Railway's built-in monitoring and logs
- Monitor database usage and performance
- Set up alerts for application health

## Scaling
Railway automatically handles:
- HTTPS certificates
- Load balancing
- Basic scaling

For high traffic, consider upgrading to Railway Pro for more resources.