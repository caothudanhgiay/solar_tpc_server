# Solar TPC Server - AI Coding Rules

> Spring Boot 4.0 · Java 21 · Gradle · MySQL · JPA · Lombok · OAuth2 Resource Server

Đọc file `../AGENTS.md` (root) để hiểu tổng quan dự án.

---

## 1. Kiến trúc Ứng dụng (Architecture)

```
src/main/java/com/example/solar_tpc_server/
├── config/          # Cấu hình Spring (Security, i18n, Tracing...)
├── controller/      # REST Controller - xử lý HTTP request
├── dto/             # Data Transfer Object - dữ liệu gửi/nhận từ client
├── entity/          # JPA Entity - ánh xạ bảng database
├── exception/       # Custom exception + GlobalExceptionHandler
├── repository/      # JPA Repository - truy vấn database
├── response/        # API Response wrapper (TsoApiResponse)
├── service/         # Business logic
├── util/            # Utility/Helper classes + Constants
└── validation/      # Custom validation logic
```

## 2. Naming Convention

### Prefix bắt buộc: `Tso`
Mọi class đều phải bắt đầu bằng `Tso`:
- Controller: `TsoXxxController` → `TsoUserController`
- Service: `TsoXxxService` → `TsoUserService`  
- Repository: `TsoXxxRepository` → `TsoUserRepository`
- Entity: `TsoXxx` → `TsoUser`, `TsoMenu`
- DTO: `TsoXxxDto` → `TsoUserDto`, `TsoLoginRequestDto`
- Exception: `TsoXxxException` → `TsoAppException`
- Config: `TsoXxxConfig` → `TsoSecurityConfig`
- Utility: `TsoXxxUtil` / `TsoXxxConstant` → `TsoMessageUtil`, `TsoApiConstant`
- Validation: `TSOXxxValidation` → `TSOCustomerRequestValidation`

### Database
- Table name: `tso_` prefix + `snake_case` → `tso_user`, `tso_menu`
- Column name: `snake_case` → `customer_name`, `created_date`
- ID column: `xxx_id` → `request_id`, `user_id`

## 3. Coding Patterns

### 3.1. Entity
```java
@Entity
@Table(name = "tso_xxx")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TsoXxx extends TsoMetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "xxx_id")
    private Long xxxId;
    
    @Column(name = "column_name", nullable = false)
    private String columnName;
}
```

**Quy tắc Entity:**
- Luôn kế thừa `TsoMetaData` (chứa `createdAt`, `createdDate`, `updatedDate`)
- Dùng `@Data`, `@EqualsAndHashCode(callSuper = true)`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Map column name rõ ràng bằng `@Column(name = "...")`

### 3.2. DTO
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TsoXxxDto {
    @NotBlank(message = "Field không được để trống")
    private String fieldName;
}
```

**Quy tắc DTO:**
- Dùng Lombok `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Dùng Jakarta Validation annotations (`@NotBlank`, `@Pattern`, `@NotNull`...) trên các field cần validate
- KHÔNG kế thừa `TsoMetaData`

### 3.3. Repository
```java
@Repository
public interface TsoXxxRepository extends JpaRepository<TsoXxx, Long> {
    Optional<TsoXxx> findByFieldName(String fieldName);
}
```

**Quy tắc Repository:**
- Kế thừa `JpaRepository<Entity, IdType>`
- Annotate `@Repository`
- Ưu tiên dùng derived query methods. Chỉ dùng `@Query` khi query phức tạp

### 3.4. Service
```java
@Service
@RequiredArgsConstructor
public class TsoXxxService {
    private final TsoXxxRepository repository;
    
    public void saveXxx(TsoXxxDto dto) {
        TsoXxx entity = new TsoXxx();
        // Map fields từ DTO → Entity
        entity.setCreatedAt(TsoConstant.SYSTEM);
        entity.setCreatedDate(TSODateUtil.datetimeNow());
        entity.setUpdatedDate(null);
        repository.save(entity);
    }
}
```

**Quy tắc Service:**
- Dùng `@Service` + `@RequiredArgsConstructor` (inject qua constructor)
- KHÔNG dùng `@Autowired` trên field
- Set metadata (`createdAt`, `createdDate`, `updatedDate`) khi tạo mới entity
- Business logic chỉ nằm trong Service, KHÔNG nằm trong Controller

### 3.5. Controller
```java
@RestController
@RequestMapping(TsoApiConstant.API_XXX)
@RequiredArgsConstructor
public class TsoXxxController {
    private final TsoXxxService service;

    @GetMapping
    public ResponseEntity<TsoApiResponse<Object>> getAll() {
        var data = service.getAll();
        return ResponseEntity.ok(TsoApiResponse.success(data, TsoMessageUtil.getMessage("xxx.success")));
    }

    @PostMapping
    public ResponseEntity<TsoApiResponse<Object>> create(@RequestBody TsoXxxDto dto) {
        // Validation nếu cần
        service.save(dto);
        return ResponseEntity.ok(TsoApiResponse.success(dto, TsoMessageUtil.getMessage("xxx.created")));
    }
}
```

**Quy tắc Controller:**
- Dùng `@RestController` + `@RequestMapping(TsoApiConstant.API_XXX)`
- URL endpoint khai báo trong `TsoApiConstant`
- Response **luôn** bọc trong `TsoApiResponse<T>` với format: `{ statusCode, message, data }`
- Dùng `TsoMessageUtil.getMessage("key")` cho message (i18n)
- Dùng `@RequiredArgsConstructor` cho dependency injection
- Controller chỉ xử lý request/response, KHÔNG chứa business logic

### 3.6. Exception Handling
```java
// Tạo error code mới trong TsoErrorCode enum:
USER_ALREADY_EXISTS(409, "error.user_already_exists"),

// Quăng lỗi ở bất kỳ đâu:
throw new TsoAppException(TsoErrorCode.USER_ALREADY_EXISTS);
// Hoặc với message chi tiết:
throw new TsoAppException(TsoErrorCode.BAD_REQUEST, "Chi tiết lỗi ở đây");
```

**Quy tắc Exception:**
- Định nghĩa mã lỗi trong `TsoErrorCode` enum (có messageKey cho i18n)
- Quăng `TsoAppException` — sẽ được `TsoGlobalExceptionHandler` bắt và trả về `TsoApiResponse`
- KHÔNG dùng `try-catch` generic trong Controller — để GlobalExceptionHandler xử lý

### 3.7. API Constants
```java
// Thêm endpoint mới trong TsoApiConstant:
public static final String API_XXX = API_BASE + "/xxx";
```

## 4. Quy tắc API Design

- Base URL: `/api`
- RESTful: `GET /api/users`, `POST /api/users`, `PUT /api/users/{id}`, `DELETE /api/users/{id}`
- Response format thống nhất:
```json
{
  "statusCode": 200,
  "message": "Thành công",
  "data": { ... }
}
```
- Error response:
```json
{
  "statusCode": 400,
  "message": "Dữ liệu không hợp lệ",
  "data": { "field": "Lỗi chi tiết" }
}
```

## 5. Quy tắc Khác

- **Lombok**: Luôn dùng Lombok cho getter/setter/constructor. KHÔNG viết tay.
- **i18n**: Message trả về client luôn qua `TsoMessageUtil.getMessage("key")`. Thêm key mới vào file messages properties.
- **Date/Time**: Dùng `TSODateUtil.datetimeNow()` thay vì `LocalDateTime.now()` trực tiếp.
- **Security**: Server dùng OAuth2 Resource Server (JWT). Cấu hình trong `TsoSecurityConfig`.
- **Monitoring**: Đã tích hợp Actuator + OpenTelemetry + Zipkin.
- **Build**: `./gradlew build` hoặc `./gradlew bootRun` để chạy.
