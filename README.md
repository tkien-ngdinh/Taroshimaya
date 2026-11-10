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
├── data/
├── feature/
│   ├── auth/
│   ├── decks/
│   └── cards/
└── ui/
    ├── theme/
    ├── navigation/
    └── components/
```

| Thư mục | Nội dung |
|---|---|
| `MainActivity.kt` | Điểm vào của app, chỉ dựng Compose và điều hướng gốc. |
| `core/` | Tiện ích dùng chung: hằng số, extension, helper không phụ thuộc màn hình nào. |
| `data/` | Tầng dữ liệu: model, repository, truy cập Firestore / Firebase Auth. |
| `feature/` | Mỗi chức năng một thư mục con (`auth`, `decks`, `cards`…), chứa màn hình, ViewModel và logic riêng của chức năng đó. |
| `ui/theme/` | Design tokens: màu, kiểu chữ, hình dạng, theme của app. |
| `ui/navigation/` | Khai báo route và đồ thị điều hướng giữa các màn hình. |
| `ui/components/` | Composable dùng chung cho nhiều màn hình (nút, thẻ bài, hộp thoại…). |

Quy ước: code của một chức năng nằm trong `feature/<tên>/`; thứ gì hai chức năng trở lên cùng dùng thì chuyển sang `core/`, `data/` hoặc `ui/components/`.

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
2. **Làm và commit** trên nhánh đó, commit nhỏ và rõ nghĩa.
3. **Mở PR vào `main`**, điền theo mẫu PR (`.github/pull_request_template.md`).
4. **Gắn reviewer** đúng người ghi ở cột N (Reviewer) của task trong Tracking Sheet, và đổi trạng thái task sang "Chờ review".
5. **Reviewer duyệt** theo đợt review cố định (Thu/CN). Duyệt xong thì merge vào `main`, rồi đổi task sang "Xong" và cập nhật Doc, Sheet.

Không bao giờ commit `google-services.json`, `local.properties`, file khóa ký (`*.jks`, `*.keystore`) hay bất kỳ secret nào.
