package com.taroshimaya.app.features.session_ai.data

import com.taroshimaya.app.features.session_ai.domain.AiInterpretation
import com.taroshimaya.app.features.session_ai.domain.ArcanaType
import com.taroshimaya.app.features.session_ai.domain.CardInterpretation
import com.taroshimaya.app.features.session_ai.domain.CardOrientation
import com.taroshimaya.app.features.session_ai.domain.CardSuit
import com.taroshimaya.app.features.session_ai.domain.ReadingCard
import com.taroshimaya.app.features.session_ai.domain.SessionReadingRepository
import com.taroshimaya.app.features.session_ai.domain.TarotReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap

/**
 * Phase 0–1 in-memory stub implementation of [SessionReadingRepository] (D4).
 * Pre-populated with 3 realistic sample sessions conforming to D4 JSON schema.
 * Allows Diệp (Package 3) to build and test F09, F10, F11, F12 UI immediately.
 */
class FakeSessionReadingRepository : SessionReadingRepository {

    private val readingsMap = ConcurrentHashMap<String, TarotReading>()
    private val readingsStateFlow = MutableStateFlow<List<TarotReading>>(emptyList())

    init {
        seedInitialReadings()
    }

    private fun seedInitialReadings() {
        val initialSessions = listOf(
            TarotReading(
                id = "reading_mock_001_love",
                userId = "usr_glueck_kien_01",
                question = "Mối quan hệ hiện tại của tôi và người ấy sẽ phát triển như thế nào trong thời gian tới?",
                deckId = "rider_waite_standard",
                spreadId = "spread_love_3",
                spreadNameVi = "Tình yêu & Gắn kết",
                spreadNameEn = "Love & Relationship",
                cards = listOf(
                    ReadingCard(
                        positionIndex = 0,
                        positionNameVi = "Năng lượng của bạn",
                        positionNameEn = "Your Energy",
                        cardId = "cups_02",
                        cardNameVi = "Hai Ly",
                        cardNameEn = "Two of Cups",
                        arcanaType = ArcanaType.MINOR,
                        suit = CardSuit.CUPS,
                        isReversed = false,
                        orientation = CardOrientation.UPRIGHT,
                        drawnAtUtcIso = "2026-11-16T08:30:15Z",
                        normalizedX = 0.2f,
                        normalizedY = 0.5f,
                    ),
                    ReadingCard(
                        positionIndex = 1,
                        positionNameVi = "Năng lượng của đối phương",
                        positionNameEn = "Partner's Energy",
                        cardId = "swords_04",
                        cardNameVi = "Bốn Kiếm",
                        cardNameEn = "Four of Swords",
                        arcanaType = ArcanaType.MINOR,
                        suit = CardSuit.SWORDS,
                        isReversed = true,
                        orientation = CardOrientation.REVERSED,
                        drawnAtUtcIso = "2026-11-16T08:30:45Z",
                        normalizedX = 0.5f,
                        normalizedY = 0.5f,
                    ),
                    ReadingCard(
                        positionIndex = 2,
                        positionNameVi = "Xu hướng tương lai",
                        positionNameEn = "Connection Outcome",
                        cardId = "major_06_the_lovers",
                        cardNameVi = "Người Tình",
                        cardNameEn = "The Lovers",
                        arcanaType = ArcanaType.MAJOR,
                        suit = null,
                        isReversed = false,
                        orientation = CardOrientation.UPRIGHT,
                        drawnAtUtcIso = "2026-11-16T08:31:10Z",
                        normalizedX = 0.8f,
                        normalizedY = 0.5f,
                    ),
                ),
                aiInterpretation = AiInterpretation(
                    provider = "GEMINI_API",
                    model = "gemini-2.5-flash",
                    generatedAtUtcIso = "2026-11-16T08:31:30Z",
                    overallMessageVi = "Trải bài cho thấy một liên kết sâu sắc và có tiềm năng cam kết lâu dài. Bạn đang tràn đầy thiện chí kết nối, trong khi đối phương đang dần bước ra khỏi giai đoạn khép kín để đón nhận tình cảm.",
                    overallMessageEn = "The spread reveals a profound bond with potential for lasting commitment. You bring harmonious empathy, while your partner is emerging from mental withdrawal to embrace mutual affection.",
                    cardInterpretations = listOf(
                        CardInterpretation(
                            positionIndex = 0,
                            cardId = "cups_02",
                            interpretationVi = "Hai Ly đại diện cho sự hòa hợp cảm xúc chân thành, bạn đang sẵn lòng lắng nghe và chia sẻ chân thật với người ấy.",
                            interpretationEn = "Two of Cups signifies genuine emotional harmony and openness to heartfelt mutual sharing.",
                        ),
                        CardInterpretation(
                            positionIndex = 1,
                            cardId = "swords_04",
                            interpretationVi = "Bốn Kiếm ngược cho thấy đối phương đã trải qua giai đoạn mỏi mệt tâm trí và đang sẵn sàng quay trở lại đối thoại thay vì trốn tránh.",
                            interpretationEn = "Four of Swords reversed indicates that your partner is concluding their mental retreat and is ready to re-engage in open dialogue.",
                        ),
                        CardInterpretation(
                            positionIndex = 2,
                            cardId = "major_06_the_lovers",
                            interpretationVi = "The Lovers là biểu tượng tối thượng của sự lựa chọn từ trái tim và sự đồng điệu giá trị sống, báo hiệu bước tiến mới trong mối quan hệ.",
                            interpretationEn = "The Lovers marks mutual trust, shared values, and an affirmative choice toward meaningful union.",
                        ),
                    ),
                    adviceVi = "Hãy kiên nhẫn tạo không gian an toàn cho đối phương chia sẻ; tránh tạo áp lực quyết định quá vội vàng.",
                    adviceEn = "Maintain a patient, safe environment for vulnerable conversations without rushing declarations.",
                    disclaimerText = "Thông điệp Tarot và luận giải AI chỉ mang tính chất tham khảo, chiêm nghiệm và giải trí cá nhân; không thay thế các lời khuyên chuyên môn về tâm lý hay pháp lý.",
                ),
                userNotes = "Người ấy đã chủ động nhắn tin sau 3 ngày im lặng. Cảm thấy rất đúng với trạng thái hiện tại.",
                tags = listOf("tinh_yeu", "chua_lanh", "cam_ket"),
                moodTag = "Bình yên",
                accuracyRating = null,
                ratedAtUtcIso = null,
                isFavorite = true,
                isOfflineSaved = false,
                createdAtUtcIso = "2026-11-16T08:30:00Z",
                updatedAtUtcIso = "2026-11-16T08:35:00Z",
            ),
            TarotReading(
                id = "reading_mock_002_career",
                userId = "usr_glueck_kien_01",
                question = "Tôi có nên chuyển hướng sang phụ trách mảng kiến trúc hệ thống backend trong quý tới không?",
                deckId = "rider_waite_standard",
                spreadId = "spread_career_3",
                spreadNameVi = "Sự nghiệp & Tiềm năng",
                spreadNameEn = "Career & Professional",
                cards = listOf(
                    ReadingCard(
                        positionIndex = 0,
                        positionNameVi = "Hiện trạng công việc",
                        positionNameEn = "Current Career State",
                        cardId = "pentacles_08",
                        cardNameVi = "Tám Xu",
                        cardNameEn = "Eight of Pentacles",
                        arcanaType = ArcanaType.MINOR,
                        suit = CardSuit.PENTACLES,
                        isReversed = false,
                        orientation = CardOrientation.UPRIGHT,
                        drawnAtUtcIso = "2026-11-17T03:15:10Z",
                        normalizedX = 0.2f,
                        normalizedY = 0.5f,
                    ),
                    ReadingCard(
                        positionIndex = 1,
                        positionNameVi = "Thử thách & Trở ngại",
                        positionNameEn = "Obstacles & Challenges",
                        cardId = "wands_10",
                        cardNameVi = "Mười Gậy",
                        cardNameEn = "Ten of Wands",
                        arcanaType = ArcanaType.MINOR,
                        suit = CardSuit.WANDS,
                        isReversed = true,
                        orientation = CardOrientation.REVERSED,
                        drawnAtUtcIso = "2026-11-17T03:15:35Z",
                        normalizedX = 0.5f,
                        normalizedY = 0.5f,
                    ),
                    ReadingCard(
                        positionIndex = 2,
                        positionNameVi = "Giải pháp & Tiềm năng",
                        positionNameEn = "Solution & Growth",
                        cardId = "major_01_the_magician",
                        cardNameVi = "Nhà Ảo Thuật",
                        cardNameEn = "The Magician",
                        arcanaType = ArcanaType.MAJOR,
                        suit = null,
                        isReversed = false,
                        orientation = CardOrientation.UPRIGHT,
                        drawnAtUtcIso = "2026-11-17T03:16:00Z",
                        normalizedX = 0.8f,
                        normalizedY = 0.5f,
                    ),
                ),
                aiInterpretation = null,
                userNotes = "Cần cân nhắc khối lượng công việc hiện tại trước khi nhận thêm trách nhiệm mới.",
                tags = listOf("su_nghiep", "cong_viec", "thang_tien"),
                moodTag = "Quyết tâm",
                accuracyRating = null,
                ratedAtUtcIso = null,
                isFavorite = false,
                isOfflineSaved = false,
                createdAtUtcIso = "2026-11-17T03:15:00Z",
                updatedAtUtcIso = "2026-11-17T03:16:30Z",
            ),
            TarotReading(
                id = "reading_mock_003_decision",
                userId = "usr_glueck_kien_01",
                question = "Tôi có nên đầu tư vào khóa học chuyên sâu chứng chỉ Cloud Architect tuần này không?",
                deckId = "rider_waite_standard",
                spreadId = "spread_yes_no_1",
                spreadNameVi = "Quyết định nhanh Yes/No",
                spreadNameEn = "Quick Decision",
                cards = listOf(
                    ReadingCard(
                        positionIndex = 0,
                        positionNameVi = "Thông điệp cốt lõi",
                        positionNameEn = "Direct Answer",
                        cardId = "major_19_the_sun",
                        cardNameVi = "Mặt Trời",
                        cardNameEn = "The Sun",
                        arcanaType = ArcanaType.MAJOR,
                        suit = null,
                        isReversed = false,
                        orientation = CardOrientation.UPRIGHT,
                        drawnAtUtcIso = "2026-11-10T14:20:10Z",
                        normalizedX = 0.5f,
                        normalizedY = 0.5f,
                    ),
                ),
                aiInterpretation = AiInterpretation(
                    provider = "GEMINI_API",
                    model = "gemini-2.5-flash",
                    generatedAtUtcIso = "2026-11-10T14:20:30Z",
                    overallMessageVi = "Câu trả lời là một tiếng 'Có' dứt khoát và rực rỡ. Lá bài Mặt Trời tượng trưng cho sự thành công, thấu suốt tri thức và giá trị mang lại lâu dài cho sự nghiệp.",
                    overallMessageEn = "The answer is an unequivocal 'Yes'. The Sun brings clarity, vitality, and genuine career dividends.",
                    cardInterpretations = listOf(
                        CardInterpretation(
                            positionIndex = 0,
                            cardId = "major_19_the_sun",
                            interpretationVi = "Mặt Trời khẳng định năng lực học tập và sự bứt phá; việc đầu tư này đem lại hiệu quả tức thì và niềm hứng khởi lớn.",
                            interpretationEn = "The Sun confirms optimal cognitive momentum and high return on educational investment.",
                        ),
                    ),
                    adviceVi = "Hãy bắt đầu ngay mà không cần do dự; lập kế hoạch học tập đều đặn mỗi tối.",
                    adviceEn = "Commit promptly and maintain consistent daily study blocks.",
                    disclaimerText = "Thông điệp Tarot và luận giải AI chỉ mang tính chất tham khảo, chiêm nghiệm và giải trí cá nhân; không thay thế các lời khuyên chuyên môn.",
                ),
                userNotes = "Đã đăng ký khóa học, nội dung thực sự rất chất lượng!",
                tags = listOf("hoc_tap", "quyet_dinh", "yes_no"),
                moodTag = "Hào hứng",
                accuracyRating = 5,
                ratedAtUtcIso = "2026-11-17T04:00:00Z",
                isFavorite = true,
                isOfflineSaved = false,
                createdAtUtcIso = "2026-11-10T14:20:00Z",
                updatedAtUtcIso = "2026-11-17T04:00:00Z",
            ),
        )

        for (reading in initialSessions) {
            readingsMap[reading.id] = reading
        }
        syncStateFlow()
    }

    private fun syncStateFlow() {
        readingsStateFlow.value = readingsMap.values
            .filter { !it.isDeleted }
            .sortedByDescending { it.createdAtUtcIso }
    }

    override suspend fun getReadingById(readingId: String): Result<TarotReading> {
        val reading = readingsMap[readingId]
        return if (reading != null && !reading.isDeleted) {
            Result.success(reading)
        } else {
            Result.failure(NoSuchElementException("Reading with id '$readingId' not found or deleted."))
        }
    }

    override fun getReadingsStream(userId: String): Flow<List<TarotReading>> {
        return readingsStateFlow.map { list ->
            list.filter { it.userId == userId && !it.isDeleted }
        }
    }

    override suspend fun getRecentReadings(userId: String, limit: Int): Result<List<TarotReading>> {
        val list = readingsMap.values
            .filter { it.userId == userId && !it.isDeleted }
            .sortedByDescending { it.createdAtUtcIso }
            .take(limit)
        return Result.success(list)
    }

    override suspend fun getReadingsFiltered(
        userId: String,
        startDateUtcIso: String?,
        endDateUtcIso: String?,
        deckId: String?,
        spreadId: String?,
        tag: String?,
    ): Result<List<TarotReading>> {
        val filtered = readingsMap.values.filter { reading ->
            if (reading.userId != userId || reading.isDeleted) return@filter false
            if (startDateUtcIso != null && reading.createdAtUtcIso < startDateUtcIso) return@filter false
            if (endDateUtcIso != null && reading.createdAtUtcIso > endDateUtcIso) return@filter false
            if (deckId != null && reading.deckId != deckId) return@filter false
            if (spreadId != null && reading.spreadId != spreadId) return@filter false
            if (tag != null && !reading.tags.contains(tag)) return@filter false
            true
        }.sortedByDescending { it.createdAtUtcIso }

        return Result.success(filtered)
    }

    override suspend fun saveReading(reading: TarotReading): Result<String> {
        readingsMap[reading.id] = reading
        syncStateFlow()
        return Result.success(reading.id)
    }

    override suspend fun updateReadingReflection(
        readingId: String,
        userNotes: String?,
        tags: List<String>?,
        moodTag: String?,
        accuracyRating: Int?,
    ): Result<Unit> {
        val existing = readingsMap[readingId]
            ?: return Result.failure(NoSuchElementException("Reading '$readingId' not found"))

        val updated = existing.copy(
            userNotes = userNotes ?: existing.userNotes,
            tags = tags ?: existing.tags,
            moodTag = moodTag ?: existing.moodTag,
            accuracyRating = accuracyRating ?: existing.accuracyRating,
            ratedAtUtcIso = if (accuracyRating != null) "2026-11-17T14:00:00Z" else existing.ratedAtUtcIso,
            updatedAtUtcIso = "2026-11-17T14:00:00Z",
        )
        readingsMap[readingId] = updated
        syncStateFlow()
        return Result.success(Unit)
    }

    override suspend fun toggleFavorite(readingId: String, isFavorite: Boolean): Result<Unit> {
        val existing = readingsMap[readingId]
            ?: return Result.failure(NoSuchElementException("Reading '$readingId' not found"))

        readingsMap[readingId] = existing.copy(
            isFavorite = isFavorite,
            updatedAtUtcIso = "2026-11-17T14:00:00Z",
        )
        syncStateFlow()
        return Result.success(Unit)
    }
}
