# 🛡️ Common Auth Service 

> A reusable authentication and authorization library for Spring Boot microservices.

This library provides shared components for JWT validation, role-based access control (RBAC), DTO models, and Feign client communication with the centralized `auth-service`.

---

## 📦 Features

- ✅ DTOs for SignUp, SignIn, JWT validation, and Login response
- ✅ JWT generation and validation logic
- ✅ Enum-based Role (RBAC)
- ✅ Feign client for centralized token validation via `auth-service`
- ✅ Custom exception handling for token and auth failures

---

## 🧱 Modules

### DTOs
- `SignUpRequest`
- `LoginRequest`
- `LoginResponse`
- `JwtValidationResponse`

### JWT Utility
- `JwtUtil` for generating and validating JWT tokens
- Methods: `generateToken`, `validateToken`, `extractUsername`, `extractRole`

### Feign Client
- `AuthClient`: connects to `auth-service` to validate tokens
  ```java
  @FeignClient(name = "auth-service")
  public interface AuthClient {
      @PostMapping("/auth/validate-token")
      JwtValidationResponse validateToken(@RequestHeader("Authorization") String token);
  }
  ```

### Enums
- `Role`: Enum for `USER`, `ADMIN`

### Exceptions
- `InvalidTokenException`
- `UnauthorizedException`

---

## 🛠️ Installation

### Maven
```xml
<dependency>
  <groupId>com.common</groupId>
  <artifactId>auth-common-lib</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
implementation 'com.common:auth-common-lib:1.0.0'
```

---

## 🧪 Usage

### ✅ Validate Token in Microservice
```java
@Autowired
private AuthClient authClient;

public void secureAction(String authHeader) {
    JwtValidationResponse user = authClient.validateToken(authHeader);
    if (!user.isValid()) throw new UnauthorizedException("Invalid token");
}
```

### ✅ RBAC Role Check
```java
if (!"ADMIN".equals(user.getRole())) {
    throw new UnauthorizedException("Admin access required.");
}
```

---

## ⚙️ Configuration

Ensure Feign and Eureka are enabled in consuming services:
```java
@EnableFeignClients(basePackages = "com.common.auth.feign")
@EnableDiscoveryClient
```

And include proper token forwarding logic in your service filters/controllers.

---

## 🧾 Dependencies

```xml
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt</artifactId>
  <version>0.9.1</version>
</dependency>

<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

Also include:
- Spring Boot Starter (core)
- Optional: Web Starter for exception mapping

---

## 🏁 License

This library is distributed under the MIT License.

---

## 🤝 Contributing

Feel free to submit issues or PRs for improvements, bug fixes, or feature extensions.
