# BariLog — Server

A Spring Boot 4.1.2 REST API backend for the BariLog application.

---

## Tech Stack

| Layer      | Technology                      |
| ---------- | ------------------------------- |
| Language   | Java 21                         |
| Framework  | Spring Boot 4.1.2               |
| Build Tool | Maven (via `mvnw` wrapper)      |
| Database   | H2 (in-memory, auto-configured) |
| ORM        | Spring Data JPA / Hibernate     |
| Email      | Spring Mail                     |
| Utilities  | Lombok                          |

> No external database installation needed — H2 runs in-memory automatically.

---

## Prerequisites

You only need **one thing** installed before you can run this project:

### Java 21 (JDK)

#### Ubuntu / Debian / WSL

```bash
sudo apt update && sudo apt install -y openjdk-21-jdk
```

Then set `JAVA_HOME` so Maven can find it:

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.profile
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.profile
source ~/.profile
```

Verify:

```bash
java -version
# openjdk version "21.x.x" ...
```

#### macOS (using Homebrew)

```bash
brew install openjdk@21
```

Then follow the instructions Homebrew prints to link it (usually):

```bash
sudo ln -sfn $(brew --prefix)/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
```

Set `JAVA_HOME`:

```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

Verify:

```bash
java -version
```

#### Windows

1. Download **OpenJDK 21** from https://adoptium.net/
2. Run the installer — check the box **"Set JAVA_HOME variable"** during setup.
3. Open a new terminal and verify:

```cmd
java -version
```

> **No Maven installation needed.** The project includes `mvnw` / `mvnw.cmd` — a self-contained Maven wrapper that downloads Maven automatically on first run.

---

## Running the Server

### Linux / macOS

```bash
cd server
./mvnw spring-boot:run
```

### Windows

```cmd
cd server
mvnw.cmd spring-boot:run
```

> The first run will download Maven and all dependencies (~50–100 MB). This only happens once; subsequent runs are fast.

Once started you should see:

```
Tomcat started on port 8080
Started BarilogApplication in X.XXX seconds
```

---

## Application Logs

Running `./mvnw spring-boot:run` shows readable console logs with a timestamp,
level, thread, logger, and message. Application logs are also written as one
Logstash JSON object per line to `logs/barilog.json`, using Spring Boot's
[built-in structured logging](https://docs.spring.io/spring-boot/reference/features/logging.html).
No separate Logstash server is required to generate these logs.

Files roll daily or at 10 MB. Archives are retained for up to 7 days with a
100 MB archive size cap. The `logs/` directory is ignored by Git.
Set `LOG_FILE` to change the output path.

```bash
# Follow the structured logs (requires jq)
tail -f logs/barilog.json | jq .

# Use Logstash JSON on the console as well, for container log collection
LOGGING_STRUCTURED_FORMAT_CONSOLE=logstash ./mvnw spring-boot:run
```

JSON entries include `@timestamp`, `level`, `logger_name`, `thread_name`,
and `message`. MDC fields and SLF4J fluent `addKeyValue` fields are included
automatically. Maven's own build output keeps its existing format.

---

## Accessing the App

| URL                                | Description         |
| ---------------------------------- | ------------------- |
| `http://localhost:8080`            | API base URL        |
| `http://localhost:8080/h2-console` | H2 database browser |

### H2 Console Login

| Field    | Value                |
| -------- | -------------------- |
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa`                 |
| Password | _(leave blank)_      |

> The H2 database is **in-memory** — data resets every time the server restarts.

Start the application before opening the console. Enter `jdbc:h2:mem:testdb`
in the JDBC URL field, replacing any saved file URL such as
`jdbc:h2:./data/barilog`. A file URL points to a different database and can
produce error `90149` when that database does not exist.

---

## Project Structure

```
server/
├── src/
│   ├── main/
│   │   ├── java/com/cse470/barilog/   # Application source code
│   │   └── resources/
│   │       └── application.yaml       # App configuration
│   └── test/                          # Tests
├── pom.xml                            # Dependencies & build config
├── mvnw                               # Maven wrapper (Linux/macOS)
└── mvnw.cmd                           # Maven wrapper (Windows)
```

---

## Troubleshooting

### `JAVA_HOME is not defined correctly`

Java is not installed or `JAVA_HOME` is not set. Follow the [Prerequisites](#prerequisites) section for your OS.

### `Permission denied: ./mvnw`

Make the wrapper executable:

```bash
chmod +x mvnw
```

### Port 8080 already in use

Kill whatever is using port 8080:

```bash
# Linux / macOS
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

Then re-run `./mvnw spring-boot:run`.
