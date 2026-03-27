# CongDongViet — Mô tả hệ thống Backend

> Đọc file này để hiểu toàn bộ hệ thống mà không cần duyệt code.
> Cập nhật file này mỗi khi có thay đổi kiến trúc quan trọng.

---

## 1. Tổng quan

**CongDongViet** là marketplace đăng tin cho người Việt tại Đài Loan.
Các loại tin hiện tại: cho thuê nhà/phòng/căn hộ, mua bán nhà/căn hộ/đất, mua bán ô tô.
Thiết kế theo dạng **Modular Monolith** — có thể tách thành microservices sau.

---

## 2. Tech Stack

| Thành phần     | Công nghệ                          |
|----------------|------------------------------------|
| Language       | Java 17                            |
| Framework      | Spring Boot 3                      |
| Security       | Spring Security + JWT              |
| ORM            | Spring Data JPA (Hibernate)        |
| Database       | MySQL (CHAR(36) UUID làm PK)       |
| Migration      | Flyway (`db/migration/V*.sql`)     |
| Cache / OTP    | Redis (Lettuce pool)               |
| SMS (tạm off)  | Twilio (đã cài, chưa dùng)        |
| Build          | Maven                              |
| Port           | 8080                               |

---

## 3. Biến môi trường (Environment Variables)

```
DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
JWT_SECRET, JWT_EXPIRATION
REDIS_HOST, REDIS_PORT, REDIS_PASSWORD
TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN, TWILIO_PHONE_NUMBER  ← tạm không dùng
SUPER_ADMIN_USERNAME, SUPER_ADMIN_EMAIL, SUPER_ADMIN_PASSWORD, SUPER_ADMIN_PHONE
```

---

## 4. Cấu trúc module

```
com.congdongviet
├── auth/           Đăng ký, đăng nhập, JWT, OTP (tạm tắt)
├── admin/          Quản lý tài khoản admin, audit log, bootstrap SuperAdmin
├── user/           (chưa implement) Profile người dùng
├── category/       Danh mục tin đăng (cây cha/con)
├── listing/        Tin đăng (base + detail theo loại)
├── favorite/       (chưa implement) Tin yêu thích
├── common/         BaseEntity, ApiResponse, GlobalExceptionHandler, exceptions
├── config/         SecurityConfig, JwtProperties, RedisConfig, AsyncConfig
└── security/       JwtTokenProvider, JwtAuthenticationFilter, UserPrincipal
```

Mỗi module theo cấu trúc: `controller / service / repository / entity / dto / mapper`

---

## 5. Database Schema

### 5.1 Bảng `users`
| Cột          | Kiểu          | Ghi chú                                      |
|--------------|---------------|----------------------------------------------|
| id           | CHAR(36) PK   | UUID                                         |
| email        | VARCHAR(255)  | Nullable (admin system có thể không có email)|
| password     | VARCHAR(255)  | BCrypt                                       |
| full_name    | VARCHAR(255)  |                                              |
| phone        | VARCHAR(20)   | Nullable                                     |
| role         | VARCHAR(50)   | USER \| ADMIN \| SUPER_ADMIN                 |
| verified     | BOOLEAN       | true = tài khoản active                      |
| username     | VARCHAR(100)  | Nullable, dùng cho ADMIN/SUPER_ADMIN login   |
| created_by   | VARCHAR(100)  | Audit: ai tạo tài khoản này                  |
| created_at   | DATETIME      |                                              |
| updated_at   | DATETIME      |                                              |

> **Lưu ý:** User thường đăng ký bằng email hoặc phone (ít nhất 1 cái). Admin/SuperAdmin dùng username.

### 5.2 Bảng `otp_verifications`
Lưu OTP trong DB (đã có migration). Tuy nhiên OTP hiện tại chạy qua **Redis** (OtpService), không dùng bảng này.
Bảng giữ lại cho khả năng fallback sau.

### 5.3 Bảng `categories`
| Cột         | Kiểu         | Ghi chú                                            |
|-------------|--------------|--------------------------------------------------- |
| id          | CHAR(36) PK  |                                                    |
| code        | VARCHAR(50)  | Unique. Ví dụ: HOUSE_RENT, CAR_SALE                |
| name        | VARCHAR(100) | Tên hiển thị tiếng Việt                            |
| parent_id   | CHAR(36)     | FK tự tham chiếu. NULL = danh mục gốc              |
| detail_type | VARCHAR(30)  | REAL_ESTATE \| CAR \| NULL (node cha thuần)         |
| is_active   | BOOLEAN      |                                                    |
| sort_order  | INT          |                                                    |

**Seed data (V12):**
```
REAL_ESTATE_RENT (Cho thuê BĐS)
  ├── HOUSE_RENT      (Cho thuê nhà)
  ├── ROOM_RENT       (Cho thuê phòng)
  └── APARTMENT_RENT  (Cho thuê căn hộ)

REAL_ESTATE_SALE (Mua bán BĐS)
  ├── HOUSE_SALE      (Bán nhà)
  ├── APARTMENT_SALE  (Bán căn hộ)
  └── LAND_SALE       (Bán đất)

CAR_SALE (Mua bán ô tô)   ← leaf category luôn
```

### 5.4 Bảng `listings`
| Cột          | Kiểu          | Ghi chú                              |
|--------------|---------------|--------------------------------------|
| id           | CHAR(36) PK   |                                      |
| category_id  | CHAR(36) FK   | → categories.id                      |
| title        | VARCHAR(255)  |                                      |
| description  | TEXT          |                                      |
| price        | DECIMAL(15,2) |                                      |
| price_unit   | VARCHAR(20)   | MONTHLY \| TOTAL \| NEGOTIABLE       |
| status       | VARCHAR(20)   | ACTIVE \| INACTIVE \| SOLD \| RENTED |
| created_by   | CHAR(36) FK   | → users.id                           |
| created_at   | DATETIME      |                                      |
| updated_at   | DATETIME      |                                      |

### 5.5 Bảng `listing_real_estate_details`
| Cột            | Kiểu          | Ghi chú                          |
|----------------|---------------|----------------------------------|
| listing_id     | CHAR(36) PK   | FK → listings.id (ON DELETE CASCADE)|
| city           | VARCHAR(100)  | Bắt buộc                        |
| district       | VARCHAR(100)  |                                  |
| address        | VARCHAR(255)  |                                  |
| area_sqm       | DECIMAL(8,2)  |                                  |
| bedrooms       | INT           |                                  |
| bathrooms      | INT           |                                  |
| floors         | INT           |                                  |
| is_furnished   | BOOLEAN       |                                  |
| available_from | DATE          | Chỉ cho cho thuê                 |
| deposit        | DECIMAL(15,2) | Tiền cọc — chỉ cho cho thuê      |

### 5.6 Bảng `listing_car_details`
| Cột              | Kiểu         | Ghi chú                         |
|------------------|--------------|---------------------------------|
| listing_id       | CHAR(36) PK  | FK → listings.id (ON DELETE CASCADE)|
| brand            | VARCHAR(100) | Bắt buộc                       |
| model            | VARCHAR(100) | Bắt buộc                       |
| manufacture_year | INT          | Bắt buộc                       |
| mileage_km       | INT          |                                 |
| fuel_type        | VARCHAR(20)  | GASOLINE \| DIESEL \| ELECTRIC \| HYBRID |
| transmission     | VARCHAR(20)  | MANUAL \| AUTOMATIC             |
| color            | VARCHAR(50)  |                                 |

### 5.7 Bảng `listing_images`
| Cột        | Kiểu         | Ghi chú                              |
|------------|--------------|--------------------------------------|
| id         | CHAR(36) PK  |                                      |
| listing_id | CHAR(36) FK  | → listings.id (ON DELETE CASCADE)    |
| url        | VARCHAR(500) |                                      |
| is_cover   | BOOLEAN      | Ảnh bìa                              |
| sort_order | INT          |                                      |
| created_at | DATETIME     |                                      |

### 5.8 Bảng `audit_logs`
Ghi lại hành động quản trị. Hiện dùng cho: `SUPER_ADMIN_CREATED_ADMIN_ACCOUNT`.

---

## 6. API Endpoints

### Auth (`/api/v1/auth`) — Public
| Method | Path              | Mô tả                                         |
|--------|-------------------|-----------------------------------------------|
| POST   | `/register`       | Đăng ký bằng email hoặc phone (ít nhất 1 cái). Trả JWT ngay, không cần OTP. |
| POST   | `/login`          | Đăng nhập bằng email, phone, hoặc username + password |

**Register request:**
```json
{
  "email": "abc@gmail.com",      // optional nếu có phone
  "phone": "0912345678",         // optional nếu có email
  "password": "123456",
  "fullName": "Nguyen Van A"
}
```

**Login request:**
```json
{ "identifier": "abc@gmail.com", "password": "123456" }
// identifier có thể là email, phone, hoặc username
```

**Auth response (cả register lẫn login):**
```json
{ "success": true, "data": { "id": "uuid", "token": "jwt", "email": "...", "fullName": "...", "role": "USER" } }
```

### Categories (`/api/v1/categories`) — Public
| Method | Path  | Mô tả                         |
|--------|-------|-------------------------------|
| GET    | `/`   | Trả toàn bộ cây danh mục      |

### Listings (`/api/v1/listings`)
| Method | Path             | Auth      | Mô tả                          |
|--------|------------------|-----------|--------------------------------|
| GET    | `/?categoryCode=` | Public   | Danh sách tin theo danh mục (có phân trang) |
| GET    | `/{id}`           | Public   | Chi tiết 1 tin                 |
| POST   | `/`               | Login     | Tạo tin mới                    |
| PATCH  | `/{id}/status`    | Chủ tin   | Cập nhật trạng thái (INACTIVE/SOLD/RENTED) |

**Tạo tin BĐS:**
```json
{
  "categoryCode": "HOUSE_RENT",
  "title": "Cho thuê nhà 3 phòng ngủ Đài Bắc",
  "price": 15000,
  "priceUnit": "MONTHLY",
  "realEstateDetails": {
    "city": "Đài Bắc", "district": "Đại An",
    "areaSqm": 80, "bedrooms": 3, "bathrooms": 2,
    "isFurnished": true, "availableFrom": "2026-04-01", "deposit": 30000
  }
}
```

**Tạo tin ô tô:**
```json
{
  "categoryCode": "CAR_SALE",
  "title": "Toyota Camry 2022 ít đi",
  "price": 800000,
  "priceUnit": "TOTAL",
  "carDetails": {
    "brand": "Toyota", "model": "Camry",
    "manufactureYear": 2022, "mileageKm": 15000,
    "fuelType": "GASOLINE", "transmission": "AUTOMATIC"
  }
}
```

### Admin (`/api/v1/admin`) — Chỉ SUPER_ADMIN
| Method | Path        | Mô tả               |
|--------|-------------|---------------------|
| POST   | `/accounts` | Tạo tài khoản ADMIN |

---

## 7. Phân quyền

| Role        | Quyền                                                      |
|-------------|-------------------------------------------------------------|
| USER        | Đăng ký, đăng nhập, tạo/quản lý tin của mình              |
| ADMIN       | (Chưa implement) Quản lý tin đăng, người dùng              |
| SUPER_ADMIN | Tạo/xóa ADMIN. Tự động tạo khi khởi động nếu chưa tồn tại |

**Login identifier resolution** (CustomUserDetailsService):
- Chứa `@` → tìm theo email
- Bắt đầu bằng `+` hoặc toàn số → tìm theo phone
- Còn lại → tìm theo username (ADMIN/SUPER_ADMIN)

---

## 8. Thiết kế mở rộng Listing (Handler Pattern)

Đây là phần kiến trúc quan trọng nhất.

```
ListingDetailHandler (interface)
  ├── RealEstateDetailHandler  → xử lý detail_type = REAL_ESTATE
  └── CarDetailHandler         → xử lý detail_type = CAR
```

**ListingService** nhận tất cả handlers qua constructor injection, build thành `Map<CategoryDetailType, ListingDetailHandler>`. Khi tạo/đọc tin, service tra cứu handler theo `category.detailType` rồi gọi.

**Để thêm danh mục mới (ví dụ: xe máy):**
1. Tạo migration: bảng `listing_motorcycle_details`
2. Tạo entity `ListingMotorcycleDetail`
3. Thêm `MOTORCYCLE` vào enum `CategoryDetailType`
4. Tạo DTO request/response
5. Tạo `MotorcycleDetailHandler implements ListingDetailHandler` (đánh `@Component`)
6. Insert vào bảng `categories`
7. **Không cần sửa bất kỳ file hiện có nào.**

---

## 9. Response format chuẩn

Mọi API đều trả `ApiResponse<T>`:
```json
// Thành công
{ "success": true, "data": {...}, "message": "..." }

// Lỗi validation
{ "success": false, "data": { "field": "message" }, "message": "Validation failed" }

// Lỗi nghiệp vụ
{ "success": false, "message": "Email already in use" }
```

---

## 10. Các quyết định thiết kế quan trọng

| Quyết định | Lý do |
|------------|-------|
| OTP verify bị tắt | Tạm thời không dùng Twilio, tài khoản tạo ra `verified=true` ngay |
| UUID dạng CHAR(36) | Tương thích MySQL tốt hơn binary UUID |
| `listing_id` làm PK cho detail tables | Tránh join thừa, quan hệ 1-1 rõ ràng (`@MapsId`) |
| `categories` là DB-driven | Thêm danh mục không cần deploy code mới |
| `ddl-auto: validate` | Schema chỉ do Flyway quản lý, tránh Hibernate tự sửa DB |
| `@EnableMethodSecurity` | Phân quyền ở method level bằng `@PreAuthorize` |

---

## 11. Tiến độ

### Đã làm
- [x] Auth: đăng ký (email hoặc phone), đăng nhập, JWT
- [x] Admin: SUPER_ADMIN bootstrap, tạo ADMIN, audit log
- [x] Security: JWT filter, role-based access
- [x] Category: cây danh mục, seed data
- [x] Listing: CRUD cơ bản, handler pattern cho BĐS + ô tô, ảnh, phân trang

### Chưa làm
- [ ] User module: xem/cập nhật profile, upload avatar
- [ ] Favorite module: thêm/xóa/xem tin yêu thích
- [ ] Image upload: hiện chỉ lưu URL, chưa có endpoint upload file
- [ ] Admin panel API: duyệt tin, khóa user, thống kê
- [ ] Search/filter nâng cao: tìm theo giá, diện tích, thành phố
- [ ] Frontend: thư mục `congdongviet-client/` còn trống

---

## 12. Flyway Migration Log

| Version | File                                    | Nội dung                                     |
|---------|-----------------------------------------|----------------------------------------------|
| V1      | init_schema.sql                         | Schema rỗng ban đầu                          |
| V2      | create_users_table.sql                  | Bảng users                                   |
| V3      | create_listings_table.sql               | Bảng listings (schema cũ, bị V9 thay thế)   |
| V4      | change_id_to_uuid.sql                   | Đổi id từ BIGINT → CHAR(36)                  |
| V5      | create_otp_table.sql                    | Bảng otp_verifications + cột verified        |
| V6      | add_username_created_by_to_users.sql    | Thêm username, created_by vào users          |
| V7      | create_audit_logs_table.sql             | Bảng audit_logs                              |
| V8      | create_categories_table.sql             | Bảng categories                              |
| V9      | redesign_listings_table.sql             | Redesign listings (xóa type, thêm category_id, status) |
| V10     | create_listing_detail_tables.sql        | Bảng listing_real_estate_details, listing_car_details |
| V11     | create_listing_images_table.sql         | Bảng listing_images                          |
| V12     | seed_categories.sql                     | Seed 9 danh mục ban đầu                      |
