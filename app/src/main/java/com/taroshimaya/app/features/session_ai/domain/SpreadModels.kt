package com.taroshimaya.app.features.session_ai.domain

/**
 * Domain taxonomy and data contracts for Tarot spread layouts (KI-09 / F04 / D6).
 * Aligned with D4 readings schema, D6 reusable table composable, and C19 bilingual guidelines.
 */

enum class SpreadCategory {
    LOVE,
    CAREER,
    FINANCE,
    HEALING,
    QUICK_DECISION,
    CUSTOM,
}

enum class CardOrientation {
    UPRIGHT,
    REVERSED,
}

data class SpreadPosition(
    val index: Int,
    val nameVi: String,
    val nameEn: String,
    val descriptionVi: String,
    val descriptionEn: String,
    val normalizedX: Float, // 0.0f .. 1.0f on table canvas
    val normalizedY: Float, // 0.0f .. 1.0f on table canvas
    val rotationDegrees: Float = 0f,
)

data class SpreadConfig(
    val id: String,
    val category: SpreadCategory,
    val cardCount: Int,
    val titleVi: String,
    val titleEn: String,
    val descriptionVi: String,
    val descriptionEn: String,
    val positions: List<SpreadPosition>,
    val isCustom: Boolean = false,
)

data class PlacedCard(
    val positionIndex: Int,
    val cardId: String, // Matches cardId format D2 (e.g. major_00_fool, minor_cups_01_ace)
    val orientation: CardOrientation = CardOrientation.UPRIGHT,
    val isRevealed: Boolean = false,
    val revealedAtUtc: Long? = null,
)

object PresetSpreads {
    val LOVE_3 = SpreadConfig(
        id = "spread_love_3",
        category = SpreadCategory.LOVE,
        cardCount = 3,
        titleVi = "Trải bài Tình yêu (3 lá)",
        titleEn = "Love & Relationship Spread (3 cards)",
        descriptionVi = "Khám phá kết nối cảm xúc, góc nhìn của đối phương và hướng đi tiềm năng của mối quan hệ.",
        descriptionEn = "Explore emotional connection, partner perspective, and future relationship trends.",
        positions = listOf(
            SpreadPosition(
                index = 0,
                nameVi = "Bạn (Năng lượng cá nhân)",
                nameEn = "You (Personal Energy)",
                descriptionVi = "Tâm trạng, cảm xúc và góc nhìn hiện tại của bạn trong mối quan hệ.",
                descriptionEn = "Your current feelings, emotions, and mindset towards the relationship.",
                normalizedX = 0.2f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 1,
                nameVi = "Đối phương (Năng lượng đối xứng)",
                nameEn = "Partner (Their Perspective)",
                descriptionVi = "Thái độ, kỳ vọng và cảm xúc thực sự của người kia.",
                descriptionEn = "Their genuine emotions, expectations, and attitudes towards you.",
                normalizedX = 0.5f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 2,
                nameVi = "Xu hướng kết nối",
                nameEn = "Relationship Trend",
                descriptionVi = "Tiềm năng phát triển, cơ hội và bài học cần vượt qua.",
                descriptionEn = "Growth potential, mutual challenges, and outcome path.",
                normalizedX = 0.8f,
                normalizedY = 0.5f,
            ),
        ),
    )

    val CAREER_3 = SpreadConfig(
        id = "spread_career_3",
        category = SpreadCategory.CAREER,
        cardCount = 3,
        titleVi = "Trải bài Sự nghiệp (3 lá)",
        titleEn = "Career & Professional Spread (3 cards)",
        descriptionVi = "Phân tích thực trạng công việc, trở ngại đang đối mặt và hành động tối ưu.",
        descriptionEn = "Analyze workplace status, critical obstacles, and optimal action steps.",
        positions = listOf(
            SpreadPosition(
                index = 0,
                nameVi = "Hiện trạng công việc",
                nameEn = "Current Status",
                descriptionVi = "Vị trí thực tế và môi trường công tác hiện thời.",
                descriptionEn = "Real situation and current occupational environment.",
                normalizedX = 0.2f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 1,
                nameVi = "Thử thách & Trở ngại",
                nameEn = "Challenges & Obstacles",
                descriptionVi = "Điểm nghẽn năng lực, áp lực hoặc mâu thuẫn cần tháo gỡ.",
                descriptionEn = "Bottlenecks, pressure, or key friction to overcome.",
                normalizedX = 0.5f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 2,
                nameVi = "Giải pháp & Tiềm năng",
                nameEn = "Action & Opportunity",
                descriptionVi = "Lời khuyên hành động và triển vọng thăng tiến.",
                descriptionEn = "Action advice and potential career advancement path.",
                normalizedX = 0.8f,
                normalizedY = 0.5f,
            ),
        ),
    )

    val FINANCE_3 = SpreadConfig(
        id = "spread_finance_3",
        category = SpreadCategory.FINANCE,
        cardCount = 3,
        titleVi = "Trải bài Tài chính (3 lá)",
        titleEn = "Finance & Wealth Spread (3 cards)",
        descriptionVi = "Đánh giá dòng tiền, phát hiện rủi ro thất thoát và chiến lược gia tăng tài sản.",
        descriptionEn = "Evaluate cash flow, uncover risk factors, and optimize wealth strategy.",
        positions = listOf(
            SpreadPosition(
                index = 0,
                nameVi = "Nguồn lực & Thu nhập",
                nameEn = "Income & Assets",
                descriptionVi = "Dòng thu nhập và nguồn lực tài chính sẵn có.",
                descriptionEn = "Existing income streams and available financial resources.",
                normalizedX = 0.2f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 1,
                nameVi = "Thất thoát & Rủi ro",
                nameEn = "Risks & Expenditures",
                descriptionVi = "Thói quen tiêu xài thiếu kiểm soát hoặc rủi ro đầu tư.",
                descriptionEn = "Uncontrolled spending habits or hidden financial pitfalls.",
                normalizedX = 0.5f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 2,
                nameVi = "Chiến lược tối ưu",
                nameEn = "Optimization Advice",
                descriptionVi = "Hướng đi an toàn và gia tăng giá trị bền vững.",
                descriptionEn = "Prudent pathway to sustainable prosperity.",
                normalizedX = 0.8f,
                normalizedY = 0.5f,
            ),
        ),
    )

    val HEALING_3 = SpreadConfig(
        id = "spread_healing_3",
        category = SpreadCategory.HEALING,
        cardCount = 3,
        titleVi = "Trải bài Chữa lành (3 lá)",
        titleEn = "Mindfulness & Healing Spread (3 cards)",
        descriptionVi = "Nhận diện căn nguyên bất an, chấp nhận bài học và đón nhận năng lượng phục hồi.",
        descriptionEn = "Acknowledge emotional wounds, embrace personal lessons, and restore balance.",
        positions = listOf(
            SpreadPosition(
                index = 0,
                nameVi = "Tổn thương tiềm thức",
                nameEn = "Inner Wound",
                descriptionVi = "Căn nguyên của lo âu hoặc cảm xúc dồn nén.",
                descriptionEn = "The root cause of subconscious distress or bottled emotion.",
                normalizedX = 0.2f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 1,
                nameVi = "Bài học nhận thức",
                nameEn = "Lesson & Acceptance",
                descriptionVi = "Góc nhìn cần soi chiếu để buông bỏ chấp niệm.",
                descriptionEn = "The conscious perspective required for acceptance and letting go.",
                normalizedX = 0.5f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 2,
                nameVi = "Năng lượng chuyển hóa",
                nameEn = "Restorative Energy",
                descriptionVi = "Nguồn động lực và sự bao dung với chính mình.",
                descriptionEn = "Inner compassion and regenerative vitality.",
                normalizedX = 0.8f,
                normalizedY = 0.5f,
            ),
        ),
    )

    val QUICK_YES_NO_1 = SpreadConfig(
        id = "spread_yes_no_1",
        category = SpreadCategory.QUICK_DECISION,
        cardCount = 1,
        titleVi = "Quyết định nhanh Yes/No (1 lá)",
        titleEn = "Quick Yes/No Decision (1 card)",
        descriptionVi = "Một lá duy nhất định hướng rõ ràng cho thắc mắc tức thời.",
        descriptionEn = "A single focused card delivering straightforward directional guidance.",
        positions = listOf(
            SpreadPosition(
                index = 0,
                nameVi = "Câu trả lời & Hướng đi",
                nameEn = "Answer & Guidance",
                descriptionVi = "Thông điệp cốt lõi định hướng lựa chọn của bạn.",
                descriptionEn = "Core indication highlighting the leaning outcome.",
                normalizedX = 0.5f,
                normalizedY = 0.5f,
            ),
        ),
    )

    val QUICK_DECISION_3 = SpreadConfig(
        id = "spread_decision_3",
        category = SpreadCategory.QUICK_DECISION,
        cardCount = 3,
        titleVi = "Quyết định đa chiều (3 lá)",
        titleEn = "Comprehensive Decision Spread (3 cards)",
        descriptionVi = "Cân nhắc yếu tố thuận lợi, trở ngại và kết quả nghiêng về.",
        descriptionEn = "Evaluate supporting forces, contrary blocks, and outcome inclination.",
        positions = listOf(
            SpreadPosition(
                index = 0,
                nameVi = "Điểm thuận (Yes)",
                nameEn = "Favorable Factors (Yes)",
                descriptionVi = "Các điều kiện hậu thuẫn nếu bạn tiến tới.",
                descriptionEn = "Supportive conditions if you choose to proceed.",
                normalizedX = 0.2f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 1,
                nameVi = "Điểm cản (No)",
                nameEn = "Obstacles & Risks (No)",
                descriptionVi = "Các thách thức hoặc chi phí cơ hội cần cân nhắc.",
                descriptionEn = "Trade-offs and risks that caution against hasty action.",
                normalizedX = 0.5f,
                normalizedY = 0.5f,
            ),
            SpreadPosition(
                index = 2,
                nameVi = "Kết luận xu hướng",
                nameEn = "Outcome Tendency",
                descriptionVi = "Xu hướng kết quả khả dĩ nhất theo bối cảnh hiện tại.",
                descriptionEn = "The most probable outcome inclination.",
                normalizedX = 0.8f,
                normalizedY = 0.5f,
            ),
        ),
    )

    val allPresets: List<SpreadConfig> = listOf(
        LOVE_3,
        CAREER_3,
        FINANCE_3,
        HEALING_3,
        QUICK_YES_NO_1,
        QUICK_DECISION_3,
    )

    fun findById(spreadId: String): SpreadConfig? =
        allPresets.firstOrNull { it.id == spreadId }

    /**
     * Builds a custom spread with 1, 3, or 5 cards and user-assigned labels.
     */
    fun createCustomSpread(
        cardCount: Int,
        customLabelsVi: List<String> = emptyList(),
        customLabelsEn: List<String> = emptyList(),
    ): SpreadConfig {
        require(cardCount in listOf(1, 3, 5)) { "Custom spread card count must be 1, 3, or 5." }

        val positions = when (cardCount) {
            1 -> listOf(
                SpreadPosition(
                    index = 0,
                    nameVi = customLabelsVi.getOrNull(0) ?: "Trọng tâm vấn đề",
                    nameEn = customLabelsEn.getOrNull(0) ?: "Core Focus",
                    descriptionVi = "Vị trí duy nhất đại diện cho câu hỏi cá nhân.",
                    descriptionEn = "Single focal point addressing your inquiry.",
                    normalizedX = 0.5f,
                    normalizedY = 0.5f,
                ),
            )
            3 -> listOf(
                SpreadPosition(
                    index = 0,
                    nameVi = customLabelsVi.getOrNull(0) ?: "Vị trí 1 (Quá khứ / Căn nguyên)",
                    nameEn = customLabelsEn.getOrNull(0) ?: "Position 1 (Past / Root)",
                    descriptionVi = "Gốc rễ hình thành nên tình huống hiện tại.",
                    descriptionEn = "Foundation leading to current situation.",
                    normalizedX = 0.2f,
                    normalizedY = 0.5f,
                ),
                SpreadPosition(
                    index = 1,
                    nameVi = customLabelsVi.getOrNull(1) ?: "Vị trí 2 (Hiện tại / Thử thách)",
                    nameEn = customLabelsEn.getOrNull(1) ?: "Position 2 (Present / Challenge)",
                    descriptionVi = "Trạng thái và mâu thuẫn cần đối diện.",
                    descriptionEn = "Present dynamic and active conflict.",
                    normalizedX = 0.5f,
                    normalizedY = 0.5f,
                ),
                SpreadPosition(
                    index = 2,
                    nameVi = customLabelsVi.getOrNull(2) ?: "Vị trí 3 (Tương lai / Hướng đi)",
                    nameEn = customLabelsEn.getOrNull(2) ?: "Position 3 (Future / Direction)",
                    descriptionVi = "Kết quả khả dĩ và bài học phát triển.",
                    descriptionEn = "Anticipated evolution and resolution.",
                    normalizedX = 0.8f,
                    normalizedY = 0.5f,
                ),
            )
            5 -> listOf(
                SpreadPosition(
                    index = 0,
                    nameVi = customLabelsVi.getOrNull(0) ?: "1. Thực trạng trung tâm",
                    nameEn = customLabelsEn.getOrNull(0) ?: "1. Central Situation",
                    descriptionVi = "Trọng tâm của câu hỏi tại thời điểm hiện tại.",
                    descriptionEn = "Heart of the question right now.",
                    normalizedX = 0.5f,
                    normalizedY = 0.5f,
                ),
                SpreadPosition(
                    index = 1,
                    nameVi = customLabelsVi.getOrNull(1) ?: "2. Nguồn gốc quá khứ",
                    nameEn = customLabelsEn.getOrNull(1) ?: "2. Past Influences",
                    descriptionVi = "Các nhân tố quá khứ tác động đến hiện tại.",
                    descriptionEn = "Events shaping the current crossroad.",
                    normalizedX = 0.2f,
                    normalizedY = 0.5f,
                ),
                SpreadPosition(
                    index = 2,
                    nameVi = customLabelsVi.getOrNull(2) ?: "3. Kỳ vọng tương lai",
                    nameEn = customLabelsEn.getOrNull(2) ?: "3. Potential Future",
                    descriptionVi = "Xu hướng tự nhiên nếu mọi sự tiếp diễn.",
                    descriptionEn = "Likely course if no action is altered.",
                    normalizedX = 0.8f,
                    normalizedY = 0.5f,
                ),
                SpreadPosition(
                    index = 3,
                    nameVi = customLabelsVi.getOrNull(3) ?: "4. Tiềm thức & Nỗi sợ",
                    nameEn = customLabelsEn.getOrNull(3) ?: "4. Subconscious / Fears",
                    descriptionVi = "Nỗi sợ sâu kín hoặc động cơ ẩn giấu.",
                    descriptionEn = "Underlying fears or hidden subconscious drivers.",
                    normalizedX = 0.5f,
                    normalizedY = 0.2f,
                ),
                SpreadPosition(
                    index = 4,
                    nameVi = customLabelsVi.getOrNull(4) ?: "5. Lời khuyên tối hậu",
                    nameEn = customLabelsEn.getOrNull(4) ?: "5. Final Advice",
                    descriptionVi = "Thông điệp soi chiếu hành động quyết định.",
                    descriptionEn = "Ultimate guiding principle for optimal decision.",
                    normalizedX = 0.5f,
                    normalizedY = 0.8f,
                ),
            )
            else -> emptyList()
        }

        return SpreadConfig(
            id = "custom_spread_${cardCount}_cards",
            category = SpreadCategory.CUSTOM,
            cardCount = cardCount,
            titleVi = "Trải bài tùy chỉnh ($cardCount lá)",
            titleEn = "Custom Spread ($cardCount cards)",
            descriptionVi = "Sơ đồ tự thiết kế với $cardCount lá bài và ý nghĩa do bạn gán cho từng vị trí.",
            descriptionEn = "Personalized spread with $cardCount cards and user-defined position meanings.",
            positions = positions,
            isCustom = true,
        )
    }
}
