# Taroshimaya

Ứng dụng Android (Kotlin + Jetpack Compose) của nhóm Taroshimaya.

- Package / applicationId: `com.taroshimaya.app`
- Hướng dẫn cài môi trường (Firebase, SHA-1, chạy app): xem `docs/SETUP.md` (bổ sung ở task UY-04 / T20).

## 1. Đề xuất cấu trúc thư mục

Toàn bộ mã nguồn nằm trong `app/src/main/java/com/taroshimaya/app/`:

```
com/taroshimaya/app/
├── MainActivity.kt
├── core/
│   ├── designsystem/
│   ├── ui/
│   ├── auth/
│   ├── data/
│   ├── di/
│   └── navigation/
└── features/
    ├── auth_deck/
    ├── session_ai/
    ├── journal_insights/
    ├── reader_community/
    └── economy_shop/
```

Bên trong mỗi gói `features/<gói>/` chia theo 3 lớp chuẩn `ui/`, `domain/`, `data/`; thư mục `viewmodel/` sẽ được bổ sung từ GĐ1. Không tự tạo thêm subpackage ngoài chuẩn này. Không import trực tiếp tầng `data/` của feature khác, luôn gọi qua interface D1–D11.

Chuỗi giao diện: `app/src/main/res/values/strings.xml` (VI, mặc định) và `values-en/strings.xml` (EN).

| Thư mục | Nội dung |
|---|---|
| `MainActivity.kt` | Điểm vào của app, chỉ dựng Compose và điều hướng gốc. |
| `core/designsystem/` | Design tokens và theme: màu, kiểu chữ, hình dạng (D10). |
| `core/ui/` | Composable dùng chung cho nhiều màn hình (nút, thẻ bài, hộp thoại…), gồm câu miễn trừ S06. |
| `core/auth/` | Interface đăng nhập dùng chung giữa các gói, gồm `UserSession` (D1). |
| `core/data/` | Model lõi và `DataSourceMode` (FAKE / FIREBASE). |
| `core/di/` | `AppContainer` và `DefaultAppContainer`. |
| `core/navigation/` | `AppRoute` và `AppNavHost`, nối 5 gói. |
| `features/auth_deck/` | Gói 1: danh tính và kho nội dung. |
| `features/session_ai/` | Gói 2. |
| `features/journal_insights/` | Gói 3. |
| `features/reader_community/` | Gói 4. |
| `features/economy_shop/` | Gói 5. |

Quy ước: code của một chức năng nằm trong `features/<gói>/`; thứ gì hai gói trở lên cùng dùng thì chuyển sang `core/`. Tên nhánh và commit theo mã task trên Tracking Sheet (ví dụ `UY-01`).

## 2. Đề xuất tên nhánh và quy trình

### Nhánh

- `main`: nhánh chính, **được bảo vệ**. Không push thẳng, chỉ merge qua Pull Request (PR) đã được duyệt.
- Nhánh làm việc đặt theo mẫu `<loại>/<MÃ-TASK>-<mô-tả-ngắn>`, trong đó `<MÃ-TASK>` là mã task trong Tracking Sheet (ví dụ `UY-01`, `KI-03`, `TA-25`, `QU-17`). Nhờ mã task, PR khớp được với đúng một dòng trong sheet.

| Loại | Dùng khi | Ví dụ |
|---|---|---|
| `feature/` | Thêm chức năng hoặc màn hình mới | `feature/UY-01-repo-setup` |
| `fix/` | Sửa lỗi | `fix/TA-25-lock-user-crash` |
| `docs/` | Chỉ sửa tài liệu | `docs/UY-04-setup-guide` |
| `chore/` | Cấu hình, CI/CD, dọn dẹp | `chore/UY-16-ci-pipeline` |

Viết `<mô-tả-ngắn>` bằng chữ thường không dấu, nối bằng dấu gạch ngang.

### Quy trình làm việc

1. **Tạo nhánh** từ `main` mới nhất, đặt tên theo mẫu trên.
2. **Làm và commit** trên nhánh đó, commit nhỏ và rõ nghĩa, mở đầu bằng mã task (ví dụ `UY-01: thêm cấu trúc thư mục`).
3. **Mở PR vào `main`**, điền theo mẫu PR (`.github/pull_request_template.md`).
4. **Gắn reviewer** đúng người ghi ở cột N (Reviewer) của task trong Tracking Sheet, và đổi trạng thái task sang "Chờ review".
5. **Reviewer duyệt** theo đợt review cố định (Thu/CN). Duyệt xong thì merge vào `main`, rồi đổi task sang "Xong" và cập nhật Doc, Sheet.

Không bao giờ commit `google-services.json`, `local.properties`, file khóa ký (`*.jks`, `*.keystore`) hay bất kỳ secret nào.