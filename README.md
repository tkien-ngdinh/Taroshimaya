# Taroshimaya

Ứng dụng Android (Kotlin + Jetpack Compose + Material 3) của nhóm **Glueck** cho dự án **Taroshimaya**.

- Package / `applicationId`: `com.taroshimaya.app`
- Gradle module duy nhất (MVP): `:app` (`compileSdk = 37`, `targetSdk = 37`, `minSdk = 24`)
- Hướng dẫn cài môi trường chi tiết (Firebase, SHA-1, thiết bị): xem [docs/SETUP.md](docs/SETUP.md).

---

## 1. Cấu trúc thư mục & kiến trúc (đồng bộ DI-01 + UY-01 + KI-03)

Toàn bộ mã nguồn nằm trong `app/src/main/java/com/taroshimaya/app/`:

```text
com/taroshimaya/app/
├── TaroshimayaApplication.kt        # Khởi tạo DefaultAppContainer (Fake / Firebase)
├── MainActivity.kt                  # AppCompatActivity (hỗ trợ AppCompat per-app locales)
├── TaroshimayaApp.kt                # App shell v1 (TopAppBar + LanguagePicker + Bottom NavigationBar)
├── core/
│   ├── auth/                        # Hợp đồng xác thực & phiên (UserRole, UserSession, AuthRepository, FakeAuthRepository)
│   ├── data/                        # Công tắc DataSourceMode (FAKE / FIREBASE), C19 AppLanguagePreference & LanguageManager
│   ├── designsystem/                # D10 tokens (DI-03): 4 theme F14, màu, chữ, khoảng cách, shape, motion + TaroshimayaTheme
│   ├── di/                          # AppContainer & DefaultAppContainer (DI thủ công tại điểm khởi tạo app)
│   ├── navigation/                  # AppRoute & AppNavHost tập trung cho 5 gói
│   └── ui/                          # UiState (Loading/Empty/Error/Success), DisclaimerBanner (S06), LanguagePickerDialog, ModulePlaceholderScreen
└── features/
    ├── auth_deck/                   # Gói 1 (Uyên · D1, D2, D3, D8: F01, F02, F03, F07, F08) -> {ui, domain, data}
    ├── session_ai/                  # Gói 2 (Kiên · D4, D5, D6: F04, F05, F06, F13, F25b) -> {ui, domain, data}
    ├── journal_insights/            # Gói 3 (Diệp · D10 tokens/UI chung: F09–F12, F14, F24) -> {ui, domain, data}
    ├── reader_community/            # Gói 4 (Tân · D9 + TA-05 điều hướng: F15–F19, F25a) -> {ui, domain, data}
    └── economy_shop/                # Gói 5 (Quỳnh · D7, D11 Functions: F20–F23, F25c) -> {ui, domain, data}
```

Bên trong mỗi gói `features/<gói>/` chia theo 3 lớp chuẩn `ui/`, `domain/`, `data/`; thư mục `viewmodel/` sẽ được bổ sung từ GĐ1. Không tự tạo thêm subpackage ngoài chuẩn này. Không import trực tiếp tầng `data/` của feature khác, luôn gọi qua interface D1–D11.

| Thư mục | Nội dung |
|---|---|
| `MainActivity.kt` | Điểm vào của app, nạp app shell Compose và điều hướng gốc. |
| `core/designsystem/` | Design tokens và theme (D10, DI-03): 4 theme F14, màu Material 3 + màu mở rộng, kiểu chữ, khoảng cách, kích thước, shape, motion; truy cập qua `TaroshimayaTheme.*`. |
| `core/ui/` | Composable dùng chung cho nhiều màn hình (UiStateHost, ModulePlaceholderScreen), gồm câu miễn trừ S06. |
| `core/auth/` | Interface đăng nhập dùng chung giữa các gói, gồm `UserSession` và `AuthRepository` (D1). |
| `core/data/` | Model lõi, `DataSourceMode` (FAKE / FIREBASE) và quản lý song ngữ `LanguageManager`. |
| `core/di/` | `AppContainer` và `DefaultAppContainer` (DI thủ công tại điểm khởi tạo app). |
| `core/navigation/` | `AppRoute` và `AppNavHost` nối 5 tab cấp cao theo DI-01 (TA-05). |
| `features/auth_deck/` | Gói 1: danh tính và kho nội dung (Uyên · D1–D3, D8). |
| `features/session_ai/` | Gói 2: phiên trải bài và AI (Kiên · D4–D6). |
| `features/journal_insights/` | Gói 3: hành trình và giao diện (Diệp · D10). |
| `features/reader_community/` | Gói 4: reader và cộng đồng (Tân · D9). |
| `features/economy_shop/` | Gói 5: xu, học tập và cửa hàng (Quỳnh · D7, D11). |

Khung v1 mở ở tab **Trải bài** (`session_ai`). Chọn lại tab đang mở giữ nguyên màn hình. Back từ tab cấp cao khác quay về Trải bài.

`AppNavHost` quản lý graph và xử lý điều hướng. Shell nhận tab đang chọn và callback. Composable dùng lại trong các gói nhận dữ liệu hiển thị và callback. Route chi tiết chỉ truyền ID hoặc tham số nhỏ.

Khi nối màn hình Reader trong `features/reader_community`, `ReaderList` phát `onReaderClick(readerId)`. `AppNavHost` mở `ReaderDetail` với `readerId`, rồi ViewModel tải dữ liệu qua `ReaderRepository`. `AppContainer` chọn data source Fake hoặc Firebase cho luồng dữ liệu.

### Quy ước kiến trúc & song ngữ (C19)

- **Luồng chuẩn**: `Compose Screen -> ViewModel -> Repository Contract (domain) -> Data Source (data: Fake / Firebase)`. Composable không gọi Firebase trực tiếp và không rẽ nhánh `FAKE`/`FIREBASE` trong UI.
- **Cô lập theo gói (R01)**: mỗi gói chỉ làm trong `features/<gói>/{ui, domain, data}`; giao tiếp giữa các gói chỉ đi qua `core/navigation` (truyền ID/tham số nhỏ) hoặc interface `D1–D11`.
- **Song ngữ Việt – Anh (C19)**:
  - Mặc định **Tiếng Việt** (đồng thời là fallback khi máy dùng ngôn ngữ khác Việt/Anh) tại `app/src/main/res/values/strings.xml`.
  - **Tiếng Anh** tại `app/src/main/res/values-en/strings.xml`.
  - Khai báo `android:localeConfig="@xml/locales_config"` + `AppLocalesMetadataHolderService` (`autoStoreLocales = true`). Trong Cài đặt / app shell hỗ trợ 3 lựa chọn: `Theo thiết bị` / `Tiếng Việt` / `English`. Mọi chữ giao diện (kể cả Loading/Empty/Error và miễn trừ `S06`) phải dùng `stringResource(...)`.

### Design tokens (D10, DI-03)

`TaroshimayaTheme { ... }` đã bọc toàn bộ app một lần ở `TaroshimayaApp`; màn hình trong các gói **không bọc lại** (chỉ bọc trong `@Preview` và test) và lấy giá trị qua object `TaroshimayaTheme`, **không đặt cứng màu, cỡ chữ hay `dp`** (G02, G08, T12):

| Token | Truy cập | Ví dụ |
|---|---|---|
| Màu Material 3 | `TaroshimayaTheme.colors` | `colors.primary`, `colors.onSurface`, `colors.surfaceContainer` |
| Màu mở rộng | `TaroshimayaTheme.extendedColors` | `cardBack`, `cardFace`, `cardInk` (chữ trên mặt lá bài), `cardArt` (hình trên mặt lá bài), `glow` (màu ánh sáng), `nebula` (màu tinh vân), `elementFire`, `success`, `warning`, `chartColor(i)`, `backgroundBrush` |
| Kiểu chữ | `TaroshimayaTheme.typography`, `TaroshimayaTheme.eyebrow` | Tiêu đề (`display*`, `headline*`, `titleLarge`) dùng **Fraunces** (display 400, headline và titleLarge 600); chữ chức năng (nút, chip, ô nhập, mô tả, nhãn) dùng **Be Vietnam Pro**. `eyebrow` là nhãn mục nhỏ viết HOA giãn chữ, người gọi tự `uppercase()` |
| Khoảng cách | `TaroshimayaTheme.spacing` | `xs 4`, `sm 8`, `md 12`, `lg 16`, `xl 24`, `xxl 32`; `screenHorizontal`, `cardPadding`, `sectionGap` |
| Kích thước | `TaroshimayaTheme.sizing` | `minTouchTarget 48`, `iconMedium`, `tarotCardMedium`, `tarotCardAspectRatio` |
| Shape | `TaroshimayaTheme.shapes` / `.taroShapes` | `shapes.medium`, `taroShapes.tarotCard`, `taroShapes.pill` |
| Elevation | `TaroshimayaTheme.elevation` | `level1` … `level5` |
| Motion | `TaroshimayaTheme.motion` | `durationPress 120`, `Short 150`, `Medium 220`, `Long 300`, `easingOut` (vào/ra), `easingInOut` (di chuyển trên màn hình), `pressScale 0.97`, `staggerDelay 50`; khi người dùng tắt hiệu ứng thì Compose kết thúc mọi animation tức thì (kể cả fade); `TaroMotion.Reduced` bỏ scale khi nhấn/xuất hiện và stagger để không bị giật |

| Nền trời đêm | `TaroBackground { ... }` | Đã đặt **một lần ở gốc app** (`TaroshimayaApp`), Scaffold và thanh trên trong suốt. Các gói **không tự thêm** nền; màn hình để nền trong suốt là thấy |
| Viền phát sáng | `Modifier.taroGlow(enabled, shape)` | Lá bài đang chọn: quầng sáng `glow` và viền `cardBorder`, mặc định theo `taroShapes.tarotCard`. Chỉ vẽ, không tự chuyển động; hiệu ứng mờ dần hay nâng lá bài làm ở DI-06 |

Font nhúng trong `res/font`, chạy offline. Cả Fraunces và Be Vietnam Pro đều theo giấy phép SIL OFL 1.1, được dùng miễn phí trong app (giấy phép nằm ở `assets/licenses/`). Đổi font chỉ cần thay file trong `res/font` và `Type.kt`.

Quy tắc chữ: không nối từ bằng dấu chấm giữa (·) hay gạch dài (—) trong chữ giao diện; viết thành câu hoặc tách dòng.

4 theme (`TaroThemeVariant`): **Mystic Night** (tối, mặc định khi máy bật dark mode; nền xanh coban, sao trắng băng, ánh cyan, tinh vân tím), **Moonlight** (sáng, mặc định khi máy ở light mode; theo video giới thiệu: nền trắng sương, xanh hoàng gia, san hô), **Classic Tarot** (sáng; kem, đỏ thẫm, xanh lam), **Celestial Aurora** (tối; theo ảnh widget: tím chàm, ánh oải hương, nét vàng). Lưu lựa chọn bằng `storageKey`, khôi phục bằng `TaroThemeVariant.fromStorageKey(...)`; bộ chuyển theme trong app làm ở DI-14. Xem trước cả 4 theme trong `core/designsystem/DesignSystemCatalog.kt` (Android Studio Preview). Mọi cặp chữ/nền đạt WCAG AA (≥ 4.5:1), được kiểm tra tự động trong `DesignTokensTest`.

---

## 2. Kiểm tra build & kiểm thử cục bộ

Yêu cầu JDK 17+ / JDK 25 (khớp Android Studio / Gradle Wrapper trong dự án) và Android SDK (`ANDROID_HOME`).

```bash
# Chạy unit test + lint + build APK debug (Windows PowerShell dùng .\gradlew.bat)
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

> Lưu ý: `google-services.json` chưa có ở GĐ0 trước khi hoàn tất `QU-01` & `QU-06` (thu SHA-1); không thêm plugin Google Services trước khi có cấu hình Firebase hợp lệ.

---

## 3. Quy ước nhánh, commit và Pull Request

### Nhánh

- `main`: nhánh chính, **được bảo vệ**. Không push thẳng, chỉ merge qua Pull Request (PR) đã được duyệt.
- Nhánh gắn task đặt theo mẫu `<loại>/<MÃ-TASK>-<mô-tả-ngắn>` (chữ thường không dấu, nối bằng `-`, `<MÃ-TASK>` viết hoa khớp Tracking Sheet như `UY-01`, `KI-03`, `TA-25`, `QU-17`):

| Loại | Dùng khi | Ví dụ |
|---|---|---|
| `feature/` | Thêm chức năng hoặc màn hình mới | `feature/F06-ai-analysis`, `feature/UY-01-repo-setup` |
| `fix/` | Sửa lỗi thường | `fix/TA-25-lock-user-crash` |
| `hotfix/` | Sửa lỗi nghiêm trọng cần gấp (nhất là sau 28/12) | `hotfix/F20-double-coin` |
| `refactor/` | Tái cấu trúc không đổi hành vi | `refactor/F12-stats-repository` |
| `design/` | Giao diện, theme, tokens, tài nguyên hình không đổi logic | `design/DI-03-design-tokens` |
| `data/` | Script nạp dữ liệu, dữ liệu mẫu, Security Rules, index Firestore | `data/UY-09-seed-cards` |
| `test/` | Thêm hoặc sửa kiểm thử | `test/KI-15-reminder-tests` |
| `docs/` | Chỉ sửa tài liệu / README | `docs/UY-04-setup-guide` |
| `chore/` | Cấu hình dự án, khung v0, thư viện, build, CI/CD | `chore/KI-03-app-skeleton-v0` |

- Nhánh không gắn task: `release/` (vd `release/m1` cho QA/Release lead) và `spike/` (vd `spike/agora-token`, không merge vào `main`).

### Commit

- Đặt theo mẫu: `<type>: <mô tả ngắn> (<MÃ-TASK>[ · Fxx])` (ví dụ: `chore: scaffold Android Compose v0 shell (KI-03)` hoặc `fix: handle empty card draw (KI-16 · F04)`).
- Commit nhỏ, mỗi commit một việc rõ ràng, mở đầu bằng type và mã task.

### Quy trình PR

1. **Tạo nhánh** từ `main` mới nhất (hoặc nhánh nền khi cần PR chồng chờ review) theo đúng mẫu trên.
2. **Làm, chạy test/lint/build và commit** trên nhánh đó, commit nhỏ và rõ nghĩa, gắn mã task trong thông điệp.
3. **Mở PR vào `main`** (hoặc nhánh nền chờ merge), điền đủ mẫu `.github/pull_request_template.md` (`T16`).
4. **Gắn đúng Reviewer** theo cột N Tracking Sheet, tuân thủ 2 đợt review Thu/CN (`R22`), đổi trạng thái sang `Chờ review`.
5. **Reviewer duyệt** theo đợt review cố định (Thu/CN). Duyệt xong thì merge vào `main`, rồi đổi task sang `Xong` và cập nhật Doc, Sheet.
6. **Cập nhật Doc gốc + Tab 8 (Log thay đổi) + Tracking Sheet ngay (`R20`, `R21`, `R24`)** trước khi đánh `Xong`.

**Bảo mật bắt buộc (S01):** Không bao giờ commit `google-services.json`, `local.properties`, file khóa ký (`*.jks`, `*.keystore`), API key AI / Agora / thanh toán hay bất kỳ secret nào.
