# Petstore Java REST CRUD Framework

Clean, production-grade API testing framework for the Swagger **Petstore**. It uses **Java 21**, **REST Assured**, **TestNG**, **Allure**, strict **Checkstyle**, and a layered design (POJOs ➜ Builders/DTOs ➜ Steps ➜ Tests).


## ✨ Highlights

- **POJOs** with Lombok `@Builder` (body-only fields)
- **Builders** used only by **Steps** (no builders in tests)
- **Orders** flow via a **DTO** (no Builder) to illustrate mixed styles
- **Steps** execute HTTP + perform **SoftAssert** checks; **tests have no assertions**
- **Enums & constants** for paths, headers, media types, status codes, query keys, test data (**no magic literals**)
- **HttpRequest** core with retry/backoff, masking, Allure attachments, multipart, query params
- **Unified request API**: `RequestOptions` + `ResponseHandling` + `RequestOptionsFactory` (authorized JSON helpers)
- **Per-request retry tuning** via `RetryOptions` (e.g., retry `404 Not Found` a few times for eventual consistency)
- **BaseApiTest** adds pre-/post-conditions (suite healthcheck, per-test cleanup registry, Allure env info)
- **DataProviders** for matrixed coverage (e.g., pet status transitions; login/logout profiles)
- **Checkstyle**: blocks magic literals & single-letter names (with sane exceptions), constant-case for `static final`
- **Docker** & **GitHub Actions** ready


## 📦 Project structure

```
.
├── .github/
│   └── workflows/
│       └── ci.yml                # JDK 21, Checkstyle, tests, artifacts
├── checkstyle/
│   └── checkstyle.xml            # Rules: no magic literals; naming; etc.
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── api
│   │   │   │   ├── pojo          # POJOs (Lombok @Builder; body-only)
│   │   │   │   │   ├── user/User.java
│   │   │   │   │   ├── pet/{Pet.java, Category.java, Tag.java}
│   │   │   │   │   └── store/Order.java
│   │   │   │   ├── builder       # Builders (used only by Steps)
│   │   │   │   │   ├── user/UserBuilder.java
│   │   │   │   │   ├── pet/PetBuilder.java
│   │   │   │   │   └── store/OrderBuilder.java   # (kept/optional; Orders uses DTO in Steps)
│   │   │   │   └── steps         # Reusable Steps (HTTP + SoftAssert + assertions inside)
│   │   │   │       ├── UserSteps.java
│   │   │   │       ├── PetSteps.java
│   │   │   │       └── OrderSteps.java           # Uses OrderDto
│   │   │   ├── api/dto
│   │   │   │   └── store/OrderDto.java           # DTO for Orders flow
│   │   │   ├── config
│   │   │   │   └── Config.java                   # Env/property loading via SystemVar
│   │   │   └── utils
│   │   │       ├── assertions/
│   │   │       │   └── BaseSoftAssert.java       # Shared SoftAssert helpers
│   │   │       ├── constants/
│   │   │       │   └── TestData.java             # Test constants
│   │   │       ├── enums/
│   │   │       │   ├── ApiPath.java
│   │   │       │   ├── SystemVar.java
│   │   │       │   ├── HttpHeader.java
│   │   │       │   ├── MediaType.java
│   │   │       │   ├── HttpMethod.java
│   │   │       │   ├── HttpStatusCode.java
│   │   │       │   ├── HttpStatusGroup.java
│   │   │       │   ├── PetStatus.java
│   │   │       │   ├── OrderStatus.java
│   │   │       │   └── QueryParamKey.java
│   │   │       ├── helpers/
│   │   │       │   ├── JsonHelper.java
│   │   │       │   └── QueryParams.java
│   │   │       ├── request/
│   │   │       │   ├── HttpRequest.java          # Core client (retry/backoff + Allure attachments)
│   │   │       │   ├── RequestOptions.java       # Unified request descriptor
│   │   │       │   ├── RequestOptionsFactory.java# Authorized JSON helpers; strict/lenient toggles
│   │   │       │   ├── RetryOptions.java         # Per-request retry tuning (e.g., 404 retry)
│   │   │       │   ├── Headers.java
│   │   │       │   ├── path/IPath.java
│   │   │       │   └── exception/HttpsException.java
│   │   │       ├── BaseApiTest.java              # Healthcheck + per-test cleanup + Allure env + logStep()
│   │   │       └── AllureUtils.java              # Attachments
│   │   └── resources
│   │       └── application.properties            # Defaults for env vars
│   └── test
│       ├── java
│       │   └── smokeTests                         # Smoke flows (assertions live in Steps)
│       │       ├── UserFlowTest.java
│       │       ├── PetFlowTest.java
│       │       ├── OrderFlowTest.java
│       │       ├── PetStatusMatrixTest.java       # DataProvider example
│       │       ├── OrderQuantityAndNegativeTest.java
│       │       └── UserLoginLogoutDataProviderTest.java
│       └── resources
│           └── testng.xml                        # TestNG suite
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```


## 🧰 Technology stack

- **Java 21**, **Maven**
- **REST Assured** (HTTP client)
- **TestNG** (runner, DataProviders, suite via `testng.xml`)
- **Allure** (reporting; request/response attachments baked in)
- **Jackson** (JSON parse helpers)
- **Lombok** (`@Builder` on POJOs)
- **Checkstyle** (no magic numbers/strings; no single-letter identifiers; constant-case for `static final`)
- **JavaFaker** (test data)
- **Docker / Docker Compose**
- **GitHub Actions** (CI on push/PR)


## 🔐 Configuration

Use environment variables or `src/main/resources/application.properties`. Keys are centralized in `utils.enums.SystemVar`:

| Env var           | Property key         | Default                           |
|-------------------|----------------------|-----------------------------------|
| `BASE_URL`        | `api.base.url`       | `https://petstore.swagger.io/v2`  |
| `FILES_BASE_URL`  | `files.base.url`     | `https://petstore.swagger.io/v2`  |
| `API_CONSOLE_LOG` | `api.console.log`    | `false`                           |
| `API_RETRY_MAX`   | `api.retry.max`      | `2`                               |
| `ACCEPT_LANG`     | `accept.lang`        | `en-US`                           |
| `API_BEARER`      | `api.bearer`         | *(empty)*                         |

Example:
```bash
export BASE_URL="https://petstore.swagger.io/v2"
export API_CONSOLE_LOG="true"
mvn -ntp verify
```
```bash
# dev (default)
mvn -ntp -Pdev verify

# stage
mvn -ntp -Pstage verify

# prod
mvn -ntp -Pprod verify

# CLI proo
mvn -ntp -Denv=stage verify
```


## 🧪 Assertions model

- **Tests contain no assertions**. They call the **Steps** layer only.
- Each **Step** extends `BaseSoftAssert`, validates response payload (e.g., `"code" == 200`, field equality), and calls `assertAll()` inside the step.
- Negative overloads in Steps accept an expected `HttpStatusCode` and send requests with **`ResponseHandling.LENIENT`** to assert non-2xx statuses at the step level.


## 🚦 Unified HTTP

- `RequestOptions` describes a request (method, path, body, query, headers, handling).
- `RequestOptionsFactory` gives concise JSON GET/POST/PUT/DELETE builders (authorized or not) and strict/lenient toggles.
- `RetryOptions` enables per-request retry, e.g., retry **404** a few times for eventual consistency.


## 🧱 Patterns

- **Users & Pets**: POJOs + Builders (builders live in `api/builder/**`, called only from Steps).
- **Orders**: **DTO** (`api/dto/store/OrderDto.java`) used from Steps (no Builder) to illustrate DTO style.
- **QueryParams**: helper builds typed maps (`username`, `password`) for login.
- **Enums/constants**: `ApiPath`, `HttpHeader`, `MediaType`, `HttpMethod`, `HttpStatusCode`, `HttpStatusGroup`, `QueryParamKey`, `PetStatus`, `OrderStatus`, `TestData`.


## 🧹 BaseApiTest (pre-/post-conditions)

- **Preconditions**: suite-level health check (`/store/inventory`) + Allure environment attachment.
- **Post-conditions**: per-test cleanup registry (users/pets/orders), executed in `@AfterMethod` with an Allure summary.
- **Helper**: `logStep("...")` for readable steps in reports.


## 🚀 Quick start

### Full pipeline (Checkstyle + tests)
```bash
mvn -ntp verify
```

### Run tests only (TestNG suite)
```bash
mvn -ntp test
```

### Run a single test class
```bash
mvn -ntp -Dtest=smokeTests.PetFlowTest test
mvn -ntp -Dtest=smokeTests.UserFlowTest test
mvn -ntp -Dtest=smokeTests.OrderFlowTest test
```

### Allure report
```bash
mvn -ntp allure:serve      # Serve locally (opens browser)
mvn -ntp allure:report     # Generate static report in target/site/allure-maven-plugin
```

### Checkstyle
```bash
mvn -ntp checkstyle:check
```


## 🐳 Docker

Run the pipeline inside a container:
```bash
docker compose up --build --abort-on-container-exit
```


## 🤖 CI (GitHub Actions)

Workflow: `.github/workflows/ci.yml`
- JDK 21 with Maven cache
- `mvn verify` (Checkstyle + tests)
- Uploads **Surefire** and **Allure** artifacts


## 🛠 Troubleshooting

- **`java.lang.instrument ASSERTION FAILED`**: remove incompatible `-javaagent` flags (AspectJ/JaCoCo/APM). Prefer Allure TestNG (no agent) or update to JDK-22–compatible agents.
- **Checkstyle name errors**: constants should be `UPPER_SNAKE_CASE`; locals/params lowerCamelCase (single `_` allowed for placeholders if configured). Extract string/number literals to enums/constants.

---

Happy testing! 🚀
