# T20 – Hướng dẫn dựng môi trường và chạy app Taroshimaya

> Bản dùng chung cho **Doc tab 6.20** và **GitHub (`docs/SETUP.md`, README trỏ về đây)**. Đổi công cụ/phiên bản phải sửa cả 2 nơi (R21).
> **Tuyệt đối không ghi khóa thật, mật khẩu, `google-services.json`, keystore vào tài liệu hay repo (S01).**

## 1. Công cụ cần cài

| Công cụ | Phiên bản | Ghi chú |
|---|---|---|
| Git | bản mới | Windows: Git for Windows |
| Android Studio | bản ổn định mới nhất hỗ trợ AGP của repo | Có sẵn JDK (JBR) đi kèm, không cần cài JDK riêng |
| JDK | 17 trở lên (dùng JBR của Android Studio) | Gradle JDK: Settings > Build Tools > Gradle |
| Android SDK | `compileSdk = 37`, `targetSdk = 37`, `minSdk = 24` (theo README); cài SDK Platform 37 + Platform-Tools + Emulator | Tải qua SDK Manager; đặt `ANDROID_HOME` nếu chạy Gradle bằng dòng lệnh |
| Thiết bị chạy | Emulator (AVD) hoặc máy Android thật | Nhóm đã test thật trên AVD Pixel 8, API 37 |
| Node.js LTS + Firebase CLI | Node LTS; `npm i -g firebase-tools` | Chỉ cần khi chạy Firebase Emulator hoặc deploy rules/functions (Quỳnh chủ trì). Firestore Emulator cần Java: chạy `java -version` trong chính cửa sổ PowerShell sẽ chạy emulator, kết quả phải ra phiên bản Java tương thích với bản `firebase-tools` đang dùng; nếu không tìm thấy Java thì thêm thư mục `bin` của JDK phù hợp vào PATH rồi mở lại PowerShell |

> Phiên bản chính xác của Gradle, AGP, Kotlin, Compose: xem `gradle/libs.versions.toml` và `gradle/wrapper/gradle-wrapper.properties` (nguồn duy nhất, không chép lại vào đây để khỏi lệch).

## 2. Lấy mã và chạy

1. Clone: `git clone https://github.com/tkien-ngdinh/Taroshimaya.git` (đã được thêm collaborator).
2. Mở thư mục gốc bằng Android Studio, chờ Gradle sync xong.
3. Tạo `local.properties` ở thư mục gốc (file này nằm trong `.gitignore`, không commit). Android Studio thường tự tạo dòng `sdk.dir`. Các khóa khác (nếu có) xin ở mục 4.
4. Tạo AVD (Device Manager > Create Device > Pixel 8, system image Google Play/Google APIs) hoặc bật USB debugging trên máy thật.
5. Chọn cấu hình `app`, bấm Run. Hoặc dòng lệnh: `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`).
6. Kiểm tra trước khi mở PR: `./gradlew testDebugUnitTest lint`.
7. Làm việc theo nhánh và commit theo mã task trên Tracking Sheet (ví dụ `UY-04`), mở PR theo mẫu T16.

Cấu trúc: 1 module `:app` (`com.taroshimaya.app`), mỗi gói làm trong `features/<gói>` riêng, không sửa file gói khác.

## 3. Cấu hình Firebase

- **Dùng chung 1 project Firebase: `taroshimaya-app`** cho cả GĐ0–GĐ2 (chốt C12a). Không tách dev/prod bằng project; khi cần phân tách môi trường thì dùng tiền tố collection (C12a, S01).
- Bật: Authentication (Email/Password + Google), Firestore (region `asia-southeast1`). Rules hiện tại: yêu cầu `request.auth != null`; rules theo vai trò do Quỳnh làm ở QU-15/QU-17.
- Cả nhóm đã có quyền Editor, mở console bằng email đã được mời: https://console.firebase.google.com/project/taroshimaya-app
- **File `google-services.json`:** tải ở Project settings > Your apps > Android app (`com.taroshimaya.app`), đặt vào `app/google-services.json`. File này đã bị gitignore, **không commit, không gửi vào group chat**. **Khi nào cần:** app khung hiện chạy bằng dữ liệu Fake nên build, chạy và lấy SHA-1 debug được ngay, chưa cần file này. Chỉ tải và đặt vào `app/` khi nhánh bạn làm đã bật plugin Google Services; lúc đó thiếu file sẽ báo `google-services.json is missing`. Chỉ thêm plugin Google Services vào Gradle khi đã có file cấu hình hợp lệ.
- **SHA-1 debug (cần cho Google Sign-in):** chạy `./gradlew signingReport` và lấy dòng SHA1 của biến thể `debug` (hoặc `keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android`). Gửi SHA-1 cho Quỳnh để đăng ký (QU-06, hạn 14/11); ghi thiết bị và SHA-1 ở tab QA & Kiểm thử (T24). SHA-1 không phải bí mật nhưng chỉ gửi cho Quỳnh.
- **Test an toàn:** vì cả nhóm dùng chung 1 project, **không test trực tiếp trên dữ liệu chung**. Dùng Firebase Emulator Suite (`firebase emulators:start`, Auth cổng 9099, Firestore cổng 8080, UI cổng 4000; từ AVD gọi qua `10.0.2.2`) và chỉ dùng tài khoản, dữ liệu giả (S02). Không thử nghiệm trực tiếp trên Firestore cloud chung (S01) để tránh ghi đè dữ liệu của nhau; cần kiểm tra trên dữ liệu thật thì báo Quỳnh (BE lead) trước.
- **Lưu ý khi dùng Emulator:** terminal báo Emulator đã chạy chưa có nghĩa là app đang dùng nó. Nếu app chưa được trỏ sang Emulator thì bản có gọi Firebase vẫn có thể gọi Firestore cloud mà không báo lỗi. Bản app hiện tại dùng Fake và chưa hỗ trợ kết nối Firebase Emulator; khi cần thử Firebase, liên hệ Quỳnh để dùng cấu hình phù hợp. Khi nhóm tích hợp Emulator, sẽ ghi rõ cách bật trên bản debug và cách xác nhận dữ liệu thử xuất hiện trong Emulator UI (cổng 4000).

## 4. Khóa bí mật lấy ở đâu (không ghi key thật)

| Loại | Đặt ở đâu | Xin ai |
|---|---|---|
| Khóa máy (Android) | `local.properties` (gitignored) | Quỳnh (BE lead) |
| `google-services.json` | `app/` (gitignored), tải từ Firebase console | Tự tải (Editor) hoặc hỏi Quỳnh |
| Khóa server (AI, Agora, thanh toán) | `functions/.env` hoặc Firebase Secrets Manager, **không bao giờ vào app** | Quỳnh |
| Keystore (`*.jks`, `*.keystore`) | Không commit | — |

Lỡ commit nhầm khóa: báo Quỳnh ngay để thu hồi/đổi khóa; xóa khỏi lịch sử chứ không chỉ xóa file.

## 5. Lỗi thường gặp

| Lỗi | Cách xử lý |
|---|---|
| Gradle sync lỗi JDK / "Unsupported class file version" | Đặt Gradle JDK về JBR 17+ của Android Studio |
| `google-services.json is missing` | Chỉ gặp khi nhánh đã bật plugin Google Services: làm mục 3; kiểm tra đúng đường dẫn `app/` và đúng package `com.taroshimaya.app` |
| Google Sign-in lỗi `DEVELOPER_ERROR` / code 10 | SHA-1 máy chưa được đăng ký (chờ QU-06) hoặc `google-services.json` cũ; tải lại sau khi Quỳnh đăng ký |
| `PERMISSION_DENIED` khi đọc/ghi Firestore | Chưa đăng nhập (`request.auth != null`) hoặc rules chưa cho vai trò đó |
| `firebase emulators:start` không chạy, báo `Could not spawn java -version` hoặc Java quá cũ | Chạy `java -version` trong cùng cửa sổ PowerShell; cài/chọn JDK phù hợp với `firebase-tools`, thêm thư mục `bin` của JDK vào PATH rồi mở lại PowerShell |
| Emulator không kết nối Firebase Emulator | Dùng `10.0.2.2` thay `localhost`; kiểm tra cổng 9099/8080 |
| AVD chậm hoặc không khởi động | Bật ảo hóa (VT-x/Hyper-V/WHPX), cấp đủ RAM, thử system image khác |
| `adb` không thấy máy thật | Bật USB debugging, đổi cáp/cổng, `adb devices` |
| Lỗi lint/test khi mở PR | Chạy `./gradlew testDebugUnitTest lint` cục bộ và sửa trước khi push |

## 6. Cập nhật tài liệu này

Đổi công cụ, phiên bản hoặc cấu hình Firebase thì sửa **ngay cả Doc tab 6.20 và `docs/SETUP.md`**, ghi 1 dòng ở Tab 8 – Log thay đổi và cập nhật Tracking Sheet.
