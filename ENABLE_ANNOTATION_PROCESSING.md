# Enable Annotation Processing in IntelliJ IDEA

## Steps:

1. **Open Preferences**
   - Mac: `Cmd + ,`
   - Windows/Linux: `Ctrl + Alt + S`

2. **Navigate to Annotation Processors**
   - Go to: `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`

3. **Enable Processing**
   - ✅ Check: **"Enable annotation processing"**
   - Click: **Apply** → **OK**

4. **Rebuild Project**
   - Menu: `Build` → `Rebuild Project`

5. **Run Application**
   - Open: `src/main/java/com/spring/getready/GetreadyApplication.java`
   - Right-click → `Run 'GetreadyApplication'`

6. **Access Application**
   - URL: http://localhost:8080/ats
   - Login: `admin@spring.ats` / `Admin@ABC`

## Quick Path:
```
Preferences → Build, Execution, Deployment → Compiler → Annotation Processors
→ ✅ Enable annotation processing
```

## After Starting:
- Test recruitment: http://localhost:8080/ats/recruitment/jobs
- View applications: http://localhost:8080/ats/recruitment/job/1/applications
