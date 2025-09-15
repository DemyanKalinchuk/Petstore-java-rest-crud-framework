# REST Assured CRUD Framework (Java 21 + TestNG + Allure)

A clean, strongly-typed API testing framework for the Swagger Petstore with:

- **POJOs** (Lombok `@Builder`, body-only fields)
- **Builders** in `api/builder/**` (used **only** by Steps)
- **Steps** that perform **HTTP calls + SoftAssert checks** (smokeTests have **no assertions**)
- **Enums/constants** for paths, headers, media types, status codes, query keys, test data (no magic literals)
- **HttpRequest** with retry/backoff, masking, Allure attachments, multipart upload, GET-with-query
- **Config** via env vars or `application.properties` (centralized in `SystemVar`)
- **Checkstyle** rules (blocks magic numbers/strings & single-letter variable names)
- **GitHub Actions** CI workflow
- **Docker** & Compose to run the pipeline in containers

---

## Project structure
.
├── .github/
│   └── workflows/
│       └── ci.yml                       # GitHub Actions (JDK 21, Checkstyle, smokeTests, artifacts)
├── checkstyle/
│   └── checkstyle.xml                   # Checkstyle rules (no magic literals, no 1-letter names, etc.)
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── api
│   │   │   │   ├── pojo                 # POJOs (Lombok @Builder)
│   │   │   │   │   ├── user/User.java
│   │   │   │   │   ├── pet/{Pet.java, Category.java, Tag.java}
│   │   │   │   │   └── store/Order.java
│   │   │   │   ├── builder              # Builders for POJOs (used only by Steps)
│   │   │   │   │   ├── user/UserBuilder.java
│   │   │   │   │   ├── pet/PetBuilder.java
│   │   │   │   │   └── store/OrderBuilder.java
│   │   │   │   └── steps                # Reusable step classes (HTTP + SoftAssert)
│   │   │   │       ├── UserSteps.java
│   │   │   │       ├── PetSteps.java
│   │   │   │       └── OrderSteps.java
│   │   │   ├── config
│   │   │   │   └── Config.java          # Env/property loading via SystemVar enum
│   │   │   └── utils
│   │   │       ├── assertions/
│   │   │       │   └── BaseSoftAssert.java
│   │   │       ├── constants/
│   │   │       │   └── TestData.java    # Test constants (no magic literals in smokeTests)
│   │   │       ├── enums/               # Centralized enums
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
│   │   │       │   ├── JsonHelper.java  # Safe JSON parse/get helpers
│   │   │       │   └── QueryParams.java # Enum-driven query param builders
│   │   │       ├── request/
│   │   │       │   ├── HttpRequest.java # Retry/backoff + Allure + multipart + GET with query
│   │   │       │   ├── Headers.java
│   │   │       │   ├── path/IPath.java
│   │   │       │   └── exception/HttpsException.java
│   │   │       ├── BaseApiTest.java     # Pre-conditions (healthcheck), post-conditions (cleanup), Allure env
│   │   │       └── AllureUtils.java     # Allure attachment helpers
│   │   └── resources
│   │       └── application.properties   # Defaults for environment vars
│   └── test
│       ├── java
│       │   └── smokeTests                    # Smoke flows (no asserts; Steps do SoftAssert)
│       │       ├── UserFlowTest.java
│       │       ├── PetFlowTest.java
│       │       └── OrderFlowTest.java
│       └── resources
│           └── testng.xml               # TestNG suite
├── Dockerfile                           # Containerized build & test
├── docker-compose.yml                   # Compose to run mvn verify in a container
├── pom.xml                              # Java 21, Rest Assured, TestNG, Allure, Checkstyle, Faker
└── README.md                            # This file

---

## Technology stack

- **Java 21**, **Maven**
- **REST Assured** (HTTP client)
- **TestNG** (runner; suite via `testng.xml`)
- **Allure** (reporting; request/response attachments baked in)
- **Jackson** (JSON parsing; small helper `JsonHelper`)
- **Lombok** (`@Builder` on POJOs)
- **Checkstyle** (no magic numbers/strings; no single-letter variable/parameter names)
- **JavaFaker** (test data generation in smokeTests)
- **Docker / Docker Compose**
- **GitHub Actions** (CI on push/PR)

---

## Assertions model

- **smokeTests contain no assertions.** They call **Steps** only.
- Each **Step** extends `BaseSoftAssert`, performs **SoftAssert** checks on the response (e.g., `"code" == 200`, field equality), and calls `assertAll()` **inside the step**.
- Keeps smokeTests concise and pushes validation closer to the reusable logic.

---

## Configuration

Use **environment variables** (preferred) or `src/main/resources/application.properties`. Keys are centralized in `utils.enums.SystemVar`.

| Env var            | Property key          | Default                         |
|--------------------|-----------------------|---------------------------------|
| `BASE_URL`         | `api.base.url`        | `https://petstore.swagger.io/v2`|
| `FILES_BASE_URL`   | `files.base.url`      | `https://petstore.swagger.io/v2`|
| `API_CONSOLE_LOG`  | `api.console.log`     | `false`                         |
| `API_RETRY_MAX`    | `api.retry.max`       | `2`                             |
| `ACCEPT_LANG`      | `accept.lang`         | `en-US`                         |
| `API_BEARER`       | `api.bearer`          | `special-key`_

---
## Quick start
 Checkstyle + smokeTests
```bash
 export BASE_URL="https://petstore.swagger.io/v2"
 export API_CONSOLE_LOG="true"
 mvn -ntp verify

- Run smokeTests only (TestNG suite)
 mvn -ntp test

- Run a single test class
 mvn -ntp -Dtest=smokeTests.orders.OrderFlowTest test
 mvn -ntp -Dtest=smokeTests.PetFlowTest test
 mvn -ntp -Dtest=smokeTests.users.UserFlowTest test

## Allure report
mvn -ntp allure:serve
mvn -ntp allure:report

Checkstyle (explicit)
mvn -ntp checkstyle:check



