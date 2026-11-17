# Phiếu Bàn Giao Interface D4 (Mẫu T06 · KI-05)

- **Mã Task:** `KI-05`
- **Mã Interface:** `D4 – Schema readings & daily_draws`
- **Tên mẫu:** T06 – Phiếu bàn giao interface
- **Hạn chốt bản mẫu:** 17/11/2026 (Đợt 1 GĐ1)

---

## 1. Mã và Tên interface:
`D4 – Schema readings & daily_draws (Phiên trải bài và Lá bài hằng ngày)`

## 2. Các bên liên quan:
- **Bên cung cấp:** Kiên (Nguyễn Đình Trung Kiên) – PM / Lead Gói 2 (*features/session_ai*)
- **Bên dùng chính:** Diệp (Dương Thị Bích Diệp) – FE Lead / Lead Gói 3 (*features/journal_insights*: F09, F10, F11, F12, F24)
- **Bên phối hợp liên quan:** Tân (Gói 4 – F19), Quỳnh (Gói 5 / BE Lead – QU-07 Core Schema)
- **Người liên hệ khi có lỗi:** Kiên

## 3. Đặc tả Kotlin Signature / Hợp đồng dữ liệu (Contract):
- **Vị trí Interface:**
  - `app/src/main/java/com/taroshimaya/app/features/session_ai/domain/SessionReadingRepository.kt`
  - `app/src/main/java/com/taroshimaya/app/features/session_ai/domain/DailyDrawRepository.kt`
- **Domain Models:**
  - `TarotReading.kt`, `ReadingCard.kt`, `AiInterpretation.kt`, `DailyDraw.kt`
- **Cấu hình DI:** Được cung cấp toàn cục qua `AppContainer` (`core/di/AppContainer.kt`):
  - `appContainer.sessionReadingRepository`
  - `appContainer.dailyDrawRepository`

```kotlin
interface SessionReadingRepository {
    suspend fun getReadingById(readingId: String): Result<TarotReading>
    fun getReadingsStream(userId: String): Flow<List<TarotReading>>
    suspend fun getRecentReadings(userId: String, limit: Int = 20): Result<List<TarotReading>>
    suspend fun getReadingsFiltered(
        userId: String,
        startDateUtcIso: String? = null,
        endDateUtcIso: String? = null,
        deckId: String? = null,
        spreadId: String? = null,
        tag: String? = null
    ): Result<List<TarotReading>>
    suspend fun saveReading(reading: TarotReading): Result<String>
    suspend fun updateReadingReflection(
        readingId: String,
        userNotes: String?,
        tags: List<String>?,
        moodTag: String?,
        accuracyRating: Int?
    ): Result<Unit>
    suspend fun toggleFavorite(readingId: String, isFavorite: Boolean): Result<Unit>
}

interface DailyDrawRepository {
    suspend fun getTodayDailyDraw(userId: String, dateLocalKey: String): Result<DailyDraw?>
    fun getTodayDailyDrawStream(userId: String, dateLocalKey: String): Flow<DailyDraw?>
    suspend fun saveDailyDraw(dailyDraw: DailyDraw): Result<Unit>
}
```

## 4. Trạng thái bàn giao & Cơ chế Toggle:
- `[x] Bản Fake (Mock Repository - Dữ liệu mẫu)`: Đã cài đặt `FakeSessionReadingRepository` và `FakeDailyDrawRepository`, nạp sẵn 3 phiên mẫu JSON và 1 daily draw mẫu trong `app/src/main/assets/mock/`. Gói 3 có thể sử dụng ngay mà không cần kết nối Firestore.
- `[ ] Bản Thật (Firebase Real Repository)`: Sẽ kích hoạt tại task `KI-12` (hạn 22/11/2026).
- **Cơ chế chuyển đổi (Toggle):** Thông qua `DataSourceMode` trong `DefaultAppContainer`.

## 5. Ví dụ dữ liệu / Cách gọi hàm (Example):

```kotlin
// 1. Diệp lấy danh sách lịch sử phiên trải bài (F11)
val recentReadings = sessionReadingRepository.getRecentReadings(userId = currentUserId).getOrNull()

// 2. Diệp cập nhật phản tư, ghi chú & độ ứng nghiệm 5 sao (F09, F10)
sessionReadingRepository.updateReadingReflection(
    readingId = "reading_mock_001_love",
    userNotes = "Kết quả rất chính xác với thực tế tuần qua",
    tags = listOf("tinh_yeu", "ung_nghiem"),
    moodTag = "Bình yên",
    accuracyRating = 5
)

// 3. Diệp lấy lá bài hôm nay hiển thị lên Glance Widget (F24)
val todayDraw = dailyDrawRepository.getTodayDailyDraw(
    userId = currentUserId,
    dateLocalKey = "2026-11-17"
).getOrNull()
```

## 6. Lịch sử thay đổi & Git:
| Ngày | Branch / PR | Nội dung thay đổi | Breaking Change? | Người cập nhật |
| :---: | :--- | :--- | :---: | :---: |
| 17/11/2026 | `feat/KI-05-d4-readings-daily-draws-schema` | Bàn giao Hợp đồng D4, Schema T07, 3 phiên mẫu JSON và Fake Repositories | Không (Bản ban đầu) | Kiên |
| 17/11/2026 | `feat/KI-05-d4-readings-daily-draws-schema` | Đồng bộ mock `userId` (`fake-user-001`), `deckId` (`rider_waite`) và `cardId` theo hợp đồng D1 (`UY-06`) & D2 (`UY-07`) | Không | Kiên |

## 7. Xác nhận từ bên dùng (Sign-off):
- Giao cho Diệp nghiệm thu tích hợp tại task `DI-07` (*Xác nhận D4 đủ cho F09, F11, F12*).

## 8. Checklist hoàn thành:
- [x] Đã thiết kế đầy đủ Schema Firestore theo chuẩn T07 tại `docs/D4_FIRESTORE_SCHEMA_T07.md`.
- [x] Đã cung cấp 3 phiên mẫu JSON và 1 bản ghi daily draw mẫu tại `app/src/main/assets/mock/`.
- [x] Đã triển khai Kotlin Interface Contracts và Fake Repositories trong `features/session_ai`.
- [x] Đã viết Unit Test bao phủ 100% và chạy lint sạch.
- [x] Đã mở PR trên GitHub và gán Reviewer Quỳnh.
- [x] Đã cập nhật lên Doc chính (Tab 6.07, 6.06, 3.3, 3.4, Tab 8) và Tracking Sheet (Tab Kiên, Diệp, Bàn giao).
