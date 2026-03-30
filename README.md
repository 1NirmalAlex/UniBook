# UniBook

Java **Servlet + JSP** web app for a campus social network: profiles, friends, feed, events, kuppiya (study sessions), and communities. Built with **MVC**, **DAO + JDBC** (MySQL), **Ant** (NetBeans), deployed on **Apache Tomcat**. This is **not** Spring Boot or Maven.

## Cloning on another computer (NetBeans)

If the project shows errors or “cannot be opened” after `git clone`, it is usually one of these:

1. **Wrong Tomcat paths in Git** — Do not commit `nbproject/private/private.properties`. It stores *your* PC’s Tomcat folder. Teammates should open the project in NetBeans, then set **Tools → Servers** (or project **Properties → Run**) and choose their own Tomcat install.
2. **Missing MySQL JAR** — The driver must be in `web/WEB-INF/lib/` (it is tracked in this repo). If someone still has an old clone, run **Clean and Build** after `git pull`.
3. **JDK** — Use **JDK 17** (matches `javac.source` / `javac.target` in `project.properties`).

Also create/configure `src/java/com/unibook/config/db.properties` and import `sql/unibook_schema.sql` into MySQL (see below).

## Requirements

- JDK 17+
- Apache Tomcat 9+ (or 10+ with `javax` compatibility as needed)
- MySQL 8+ (or compatible)
- NetBeans with Java Web / Ant (optional; you can build with Ant from the project root)

## Database setup

1. Create the database and run the schema:

   ```sql
   CREATE DATABASE unibook CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE unibook;
   ```

2. Execute `sql/unibook_schema.sql` in MySQL (Workbench, CLI, etc.).

3. If you created the DB before **`user_achievements`** existed, also run `sql/002_user_achievements.sql`.

## Configure JDBC

Edit `src/java/com/unibook/config/db.properties`:

- `db.url` — JDBC URL (default `localhost:3306/unibook`)
- `db.user` / `db.password` — MySQL credentials

NetBeans **Clean and Build** copies this file into the webapp classes (see `build.xml` `-post-compile`).

## MySQL driver

`mysql-connector-j` is in `web/WEB-INF/lib/` and referenced in `nbproject/project.properties` / `project.xml`.

## Run the app

1. **Build** the project in NetBeans (or run `ant` from the project directory if Ant is on your PATH).
2. **Deploy** the generated WAR to Tomcat (or use NetBeans “Run” with Tomcat registered).
3. Open `http://localhost:8080/UniBook/` (context path may match `web/META-INF/context.xml`).

Sign up requires a campus email ending in **`edu.lk`**.

## Project layout (high level)

| Layer | Location |
|--------|-----------|
| Models | `src/java/com/unibook/model/` |
| DAOs | `src/java/com/unibook/dao/` |
| Servlets (controllers) | `src/java/com/unibook/controller/` |
| Utilities | `src/java/com/unibook/util/` |
| Views | `web/*.jsp`, `web/includes/` |
| SQL | `sql/` |

## Main URLs

| Path | Purpose |
|------|---------|
| `/` → `index.jsp` | Landing |
| `/signup`, `/loginUser` | Registration and login |
| `/feed` | Post feed (friends + you) |
| `/profile` | Profile, achievements, photo, **change password** |
| `/friends` | Find friends, requests |
| `/events` | Events (create, join, host delete) |
| `/kuppiya` | Teaching sessions |
| `/communities` | Groups and members |
| `/logout` | End session |

## Authentication filter

`com.unibook.filter.AuthFilter` is mapped to `/*` and sends unauthenticated users to `login.jsp`. A `next` query parameter (same-app path only) is preserved through login via `LoginRedirectUtil` (open redirects are rejected).

Public paths include: `/`, `index.jsp`, `login.jsp`, `signup.jsp`, `error.jsp`, `campusRegistration.jsp`, auth servlets (`/loginUser`, `/signup`, `/logout`, `/registerCampus`), and static prefixes `/css/` and `/uploads/`.

## Security notes (course / demo)

- Passwords are stored with **PBKDF2** (`PasswordUtil`).
- SQL uses **PreparedStatement** throughout.
- Post content is **HTML-escaped** on the feed.
- You can only **delete your own posts**, **events you created**, **kuppiya sessions you host**, and **communities you own**.

For production you would add HTTPS, stronger session settings, CSRF tokens, file-upload hardening, and rate limiting.

## License

Use and modify for your course / portfolio as needed.
