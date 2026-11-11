## Mã task

<!-- Mã task trong Tracking Sheet, ví dụ: UY-01, KI-03. Tên nhánh theo mẫu <loại>/<MÃ-TASK>-<mô-tả-ngắn> (<loại> gồm: feature, fix, hotfix, refactor, design, data, test, docs, chore; ngoài ra có release/, spike/). -->
- Mã task:
- Link dòng task trong Sheet (nếu có):

## Mô tả thay đổi

<!-- Làm gì và vì sao, viết ngắn gọn 2–5 dòng. -->

-

## Đối chiếu "Xong khi"

<!-- Chép nguyên văn cột "Xong khi" (cột C) của task, rồi tick những ý đã đạt. -->

- [ ] (ý 1 của "Xong khi")
- [ ] (ý 2 của "Xong khi")

## Cách kiểm tra

<!-- Reviewer làm theo các bước này để tự kiểm tra lại. Ghi rõ thiết bị / bản Android đã thử. -->

1.
2.

- Thiết bị đã thử:

## Ảnh chụp màn hình / bằng chứng

<!-- Với thay đổi giao diện: ảnh trước/sau. Với thay đổi khác: ảnh log, kết quả build, link tài liệu. Nếu không có, ghi "Không áp dụng". -->

## Reviewer

<!-- Đúng người ghi ở cột N (Reviewer) của task trong Sheet. Hạn review theo cột O (đợt Thu/CN). -->
- Reviewer:
- Hạn review:

## Checklist trước khi gửi review

- [ ] Nhánh đặt đúng mẫu `<loại>/<MÃ-TASK>-<mô-tả-ngắn>` và PR trỏ vào `main`
- [ ] Build chạy được, app mở được (không lỗi biên dịch)
- [ ] Không có secret trong thay đổi: không `google-services.json`, `local.properties`, `*.jks`, `*.keystore`, mật khẩu hoặc khóa API
- [ ] Không commit file sinh ra (`build/`, `.idea/`, `.gradle/`, `.kotlin/`)
- [ ] Đã gắn đúng Reviewer và đổi trạng thái task trong Sheet sang "Chờ review"
- [ ] Đã cập nhật Doc gốc và Tracking Sheet theo R20–R21 (làm ngay, trước khi đánh "Xong")
- [ ] Nếu task có mẫu liên quan (T16–T20…), đã cập nhật đồng bộ CẢ GitHub lẫn Doc

## Ghi chú thêm

<!-- Rủi ro, việc còn dở, câu hỏi cho reviewer. Nếu không có, ghi "Không". -->
