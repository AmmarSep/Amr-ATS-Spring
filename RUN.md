# How to Run the Application

## Prerequisites
- Java 1.8 or higher
- Maven 3.x
- PostgreSQL 10 or higher
- IDE (IntelliJ IDEA, Eclipse, or STS recommended)

## Step 1: Clone the Repository
```bash
git clone <repository-url>
cd spring-getready-placements
```

## Step 2: Database Setup

### Create Database
```bash
# Login to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE spring-ats;

# Exit psql
\q
```

### Run Migration Scripts
```bash
# Option 1: Run migration scripts from MIGRATION_GUIDE.md
# Follow the SQL scripts in MIGRATION_GUIDE.md to create tables

# Option 2: Restore from backup (if available)
psql -U postgres -d spring-ats -f db/spring-ats.sql
```

## Step 3: Configure Application

### Update `src/main/resources/application.properties`

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/spring-ats
spring.datasource.username=postgres
spring.datasource.password=<your-postgres-password>

# File Upload Path (create this directory)
file.upload-path=/path/to/resume/uploads
```

### Create Resume Upload Directory
```bash
# Linux/Mac
mkdir -p /path/to/resume/uploads

# Windows
mkdir C:\resume\uploads
```

## Step 4: Build the Project

```bash
# Clean and install dependencies
mvn clean install

# Or if using IDE, update Maven dependencies
```

## Step 5: Run the Application

### Option A: Using IDE
1. Open the project in your IDE
2. Locate `src/main/java/com/spring/getready/GetreadyApplication.java`
3. Right-click and select "Run"

### Option B: Using Maven
```bash
mvn spring-boot:run
```

### Option C: Using JAR
```bash
mvn clean package
java -jar target/getready-0.0.1-SNAPSHOT.jar
```

## Step 6: Access the Application

Open your browser and navigate to:
```
http://localhost:8080/ats
```

## Default Credentials

### Admin Account
- **Email:** `admin@ats.com`
- **Password:** `Admin@ABC`
- **Access:** Full system access, user management, system configuration

### Candidate Accounts
- **Ana:** `ana@ats.com` / `Ats@ABC`
- **Charles:** `charles@ats.com` / `Ats@ABC`
- **Peter:** `peter@ats.com` / `Ats@ABC`
- **Sep:** `sep@ats.com` / `Ats@ABC`
- **Ice:** `ice@ats.com` / `Ats@ABC`
- **Mark:** `mark@ats.com` / `Ats@ABC`
- **Fred:** `fred@ats.com` / `Ats@ABC`
- **Victoria:** `victoria@ats.com` / `Ats@ABC`

### Creating Additional Users
After logging in as Admin:
1. Navigate to User Management
2. Create Recruiter accounts
3. Create Candidate accounts

Or users can self-register as Candidates through the registration page.

## User Roles & Access

### Admin
- Manage all users (Recruiters and Candidates)
- View system-wide analytics
- Configure system settings
- Full access to all features

### Recruiter
- Create and manage job postings
- Review applications and AI screening results
- Schedule interviews
- Update application status
- Access recruitment analytics

### Candidate
- Register and create profile
- Browse available job postings
- Apply for jobs with resume upload
- Track application status
- View interview schedules

## Troubleshooting

### Database Connection Issues
- Verify PostgreSQL is running: `sudo service postgresql status`
- Check database exists: `psql -U postgres -l`
- Verify credentials in `application.properties`

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### File Upload Issues
- Ensure upload directory exists and has write permissions
- Check `file.upload-path` in `application.properties`

### Maven Dependency Issues
```bash
mvn clean install -U
```

## Application URLs

- **Home:** http://localhost:8080/ats
- **Login:** http://localhost:8080/ats/login
- **Register:** http://localhost:8080/ats/register
- **Admin Dashboard:** http://localhost:8080/ats/admin
- **Recruiter Dashboard:** http://localhost:8080/ats/recruiter
- **Candidate Dashboard:** http://localhost:8080/ats/candidate

## Testing the Application

1. **Login as Admin** using default credentials
2. **Create a Recruiter account** from admin panel
3. **Login as Recruiter** and create a job posting
4. **Register as Candidate** (or create from admin panel)
5. **Apply for a job** with resume upload
6. **View AI screening results** as Recruiter
7. **Schedule interview** and update application status

## Support

For issues or questions:
- Check **MIGRATION_GUIDE.md** for database schema
- Review **Synopsis.md** for project requirements
- See **README.md** for feature documentation

---
**Note:** This is an academic project. Ensure all dependencies are properly configured before running.
