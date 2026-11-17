# Đặc tả Thiết kế Schema Firestore D4 (Mẫu T07 · KI-05)

- **Mã Task:** `KI-05`
- **Mã Interface:** `D4 – Schema readings & daily_draws`
- **Chủ sở hữu (Owner):** Kiên (Nguyễn Đình Trung Kiên) – PM / Lead Gói 2 (*features/session_ai*)
- **Bên tiêu thụ (Consumer):** Diệp (Gói 3 – F09, F10, F11, F12, F24), Quỳnh (Gói 5 / BE Lead – QU-07 Core Schema)
- **Hạn chốt:** 17/11/2026 (Đợt 1 GĐ1)

---

## 1. Collection `readings` (Phiên trải bài Tarot)

### 1. Tên collection:
`readings` *(Tuân thủ quy ước: Chữ thường, số nhiều, snake_case)*

### 2. Chủ sở hữu (Owner):
Kiên (Lead Gói 2 · features/session_ai)

### 3. Cấu trúc các trường (Fields):

| Tên trường (Field) | Kiểu dữ liệu (Type) | Bắt buộc? | Ý nghĩa / Ghi chú |
| :--- | :--- | :---: | :--- |
| `id` | `String` | Có | Document ID (Firestore auto-generated, lưu lại bên trong document) |
| `userId` | `String` | Có | Foreign Key `users.uid` (camelCase theo quy ước) |
| `question` | `String` | Có | Câu hỏi người dùng nhập (5–200 ký tự theo Guard F04 FSM) |
| `deckId` | `String` | Có | ID bộ bài Tarot sử dụng (ví dụ: `rider_waite_standard` theo D2) |
| `spreadId` | `String` | Có | ID kiểu trải bài (`spread_love_3`, `spread_career_3`, `spread_finance_3`, `spread_healing_3`, `spread_decision_3`, `spread_yes_no_1`, `custom_spread_3`) |
| `spreadNameVi` | `String` | Có | Tên kiểu trải bài tiếng Việt (ví dụ: "Tình yêu & Gắn kết") |
| `spreadNameEn` | `String` | Có | Tên kiểu trải bài tiếng Anh (ví dụ: "Love & Relationship") |
| `cards` | `Array<Map>` | Có | Danh sách các lá bài đã rút trong phiên |
| `cards[].positionIndex` | `Int` | Có | Thứ tự vị trí lá trong sơ đồ (0, 1, 2...) |
| `cards[].positionNameVi` | `String` | Có | Ý nghĩa vị trí tiếng Việt |
| `cards[].positionNameEn` | `String` | Có | Ý nghĩa vị trí tiếng Anh |
| `cards[].cardId` | `String` | Có | Mã lá bài theo D2 (ví dụ: `major_00_the_fool`, `cups_02`) |
| `cards[].cardNameVi` | `String` | Có | Tên lá bài tiếng Việt |
| `cards[].cardNameEn` | `String` | Có | Tên lá bài tiếng Anh |
| `cards[].arcanaType` | `String` | Có | `"MAJOR"` hoặc `"MINOR"` (hỗ trợ Diệp tính tỷ lệ F12) |
| `cards[].suit` | `String?` | Không | `"WANDS"`, `"CUPS"`, `"SWORDS"`, `"PENTACLES"`, hoặc `null` (hỗ trợ Diệp F12) |
| `cards[].isReversed` | `Boolean` | Có | `true` nếu ngược, `false` nếu xuôi |
| `cards[].orientation` | `String` | Có | `"UPRIGHT"` hoặc `"REVERSED"` |
| `cards[].drawnAtUtc` | `Timestamp` | Có | Thời điểm rút lá bài |
| `cards[].normalizedX` | `Float` | Có | Tọa độ $X$ tương đối ($0.0 \dots 1.0$) cho Composable bàn trải D6 |
| `cards[].normalizedY` | `Float` | Có | Tọa độ $Y$ tương đối ($0.0 \dots 1.0$) cho Composable bàn trải D6 |
| `aiInterpretation` | `Map?` | Không | Kết quả phân tích từ AI (F06 / D11). `null` nếu phiên chưa gọi AI |
| `aiInterpretation.provider` | `String` | Có | Nhà cung cấp AI (mặc định: `"GEMINI_API"`) |
| `aiInterpretation.model` | `String` | Có | Tên model đã chốt tại C13 (`gemini-2.5-flash`) |
| `aiInterpretation.generatedAtUtc`| `Timestamp` | Có | Thời điểm tạo xong luận giải AI |
| `aiInterpretation.overallMessageVi`| `String` | Có | Thông điệp tổng quan tiếng Việt |
| `aiInterpretation.overallMessageEn`| `String` | Có | Thông điệp tổng quan tiếng Anh |
| `aiInterpretation.cardInterpretations`| `Array<Map>` | Có | Luận giải chi tiết từng lá tương ứng `positionIndex` |
| `aiInterpretation.adviceVi` | `String` | Có | Lời khuyên hành động tiếng Việt |
| `aiInterpretation.adviceEn` | `String` | Có | Lời khuyên hành động tiếng Anh |
| `aiInterpretation.disclaimerText` | `String` | Có | Tuyên bố miễn trừ trách nhiệm theo S06 |
| `userNotes` | `String?` | Không | Ghi chú phản tư của người dùng sau khi trải nghiệm (F09) |
| `tags` | `Array<String>` | Có | Thẻ chủ đề (`"tinh_yeu"`, `"cong_viec"`...) phục vụ lọc F11 |
| `moodTag` | `String?` | Không | Nhãn tâm trạng khi rút bài (F10) |
| `accuracyRating` | `Int?` | Không | Đánh giá độ ứng nghiệm 1-5 sao, cập nhật sau 1 tuần/tháng (F10) |
| `ratedAtUtc` | `Timestamp?` | Không | Thời điểm đánh giá độ ứng nghiệm |
| `isFavorite` | `Boolean` | Có | Đánh dấu yêu thích (mặc định: `false`) |
| `isOfflineSaved` | `Boolean` | Có | Cờ đánh dấu phiên lưu tạm offline khi mất mạng |
| `createdAtUtc` | `Timestamp` | Có | Thời điểm tạo phiên (chuẩn UTC) |
| `updatedAtUtc` | `Timestamp` | Có | Thời điểm cập nhật cuối (chuẩn UTC) |
| `isDeleted` | `Boolean` | Có | Cờ xóa mềm (mặc định: `false`) |
| `deletedAtUtc` | `Timestamp?` | Không | Thời điểm xóa mềm |

### 4. Khóa và Index (Keys & Indexes):
- **Khóa chính:** Tự sinh bởi Firestore (`auto-generated`), luôn chứa field `id` bằng Document ID.
- **Index cần tạo trên Firestore (DL07):**
  1. `userId` (ASC) + `isDeleted` (ASC) + `createdAtUtc` (DESC)
  2. `userId` (ASC) + `deckId` (ASC) + `createdAtUtc` (DESC)
  3. `userId` (ASC) + `spreadId` (ASC) + `createdAtUtc` (DESC)
  4. `userId` (ASC) + `tags` (ARRAY_CONTAINS) + `createdAtUtc` (DESC)

### 5. Phân quyền (Security Rules - DL03):
- **Read:** Chỉ User chủ sở hữu (`request.auth.uid == resource.data.userId`), hoặc Reader trong phiên phòng trải bài F19 được cấp quyền, hoặc Admin duyệt báo cáo vi phạm F25b.
- **Write:** `request.auth.uid == request.resource.data.userId`. Gói 3 (Diệp) cập nhật `userNotes`, `tags`, `moodTag`, `accuracyRating` qua interface D4.

---

## 2. Collection `daily_draws` (Rút bài hằng ngày & Streak)

### 1. Tên collection:
`daily_draws` *(Tuân thủ quy ước: Chữ thường, số nhiều, snake_case)*

### 2. Chủ sở hữu (Owner):
Kiên (Lead Gói 2 · features/session_ai)

### 3. Cấu trúc các trường (Fields):

| Tên trường (Field) | Kiểu dữ liệu (Type) | Bắt buộc? | Ý nghĩa / Ghi chú |
| :--- | :--- | :---: | :--- |
| `id` | `String` | Có | Khóa chính xác định: `{userId}_{dateLocalKey}` |
| `userId` | `String` | Có | Foreign Key `users.uid` |
| `dateLocalKey` | `String` | Có | Ngày rút theo múi giờ máy người dùng (`YYYY-MM-DD`, DL05) |
| `deckId` | `String` | Có | Bộ bài sử dụng (`rider_waite_standard`) |
| `cardId` | `String` | Có | Mã lá bài rút được (D2) |
| `cardNameVi` | `String` | Có | Tên lá bài tiếng Việt |
| `cardNameEn` | `String` | Có | Tên lá bài tiếng Anh |
| `arcanaType` | `String` | Có | `"MAJOR"` hoặc `"MINOR"` |
| `suit` | `String?` | Không | `"WANDS"`, `"CUPS"`, `"SWORDS"`, `"PENTACLES"`, hoặc `null` |
| `isReversed` | `Boolean` | Có | `true` nếu ngược, `false` nếu xuôi |
| `orientation` | `String` | Có | `"UPRIGHT"` hoặc `"REVERSED"` |
| `dailyMessageVi` | `String` | Có | Thông điệp ngắn trong ngày tiếng Việt |
| `dailyMessageEn` | `String` | Có | Thông điệp ngắn trong ngày tiếng Anh |
| `streakCount` | `Int` | Có | Số ngày liên tục duy trì rút bài |
| `coinReward` | `Int` | Có | Số Xu thưởng nhận được (gọi qua D7) |
| `coinTransactionId`| `String?` | Không | Idempotency Key gửi Gói 5: `{userId}_daily_{dateLocalKey}` |
| `shareImageUrl` | `String?` | Không | URL ảnh chia sẻ lên mạng xã hội |
| `drawnAtUtc` | `Timestamp` | Có | Thời điểm rút bài (chuẩn UTC) |
| `createdAtUtc` | `Timestamp` | Có | Thời điểm tạo bản ghi (chuẩn UTC) |
| `updatedAtUtc` | `Timestamp` | Có | Thời điểm cập nhật cuối (chuẩn UTC) |
| `isDeleted` | `Boolean` | Có | Cờ xóa mềm (mặc định: `false`) |
| `deletedAtUtc` | `Timestamp?` | Không | Thời điểm xóa mềm |

### 4. Khóa và Index (Keys & Indexes):
- **Khóa chính:** Xác định theo công thức `{userId}_{dateLocalKey}` chống rút 2 lần trong ngày (DL04/DL05).
- **Index cần tạo trên Firestore:**
  - `userId` (ASC) + `dateLocalKey` (DESC)

### 5. Phân quyền (Security Rules - DL03):
- **Read:** `request.auth.uid == resource.data.userId`
- **Write:** `request.auth.uid == request.resource.data.userId && request.resource.id == request.auth.uid + '_' + request.resource.data.dateLocalKey`
