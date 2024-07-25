
-- 채널 생성
INSERT INTO notification_platform (name, created_at, updated_at, del_yn) VALUES ('slack', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N');
INSERT INTO notification_platform (name, created_at, updated_at, del_yn) VALUES ('telegram', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N');


-- 사용자 100명 생성
INSERT INTO user_info (nickname, email, created_at, updated_at, del_yn)
SELECT
  LOWER(SUBSTRING(RIGHT(RANDOM_UUID(), 12), 1, 8)),
  LOWER(SUBSTRING(RIGHT(RANDOM_UUID(), 12), 1, 8)) || '@kakao.com',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  'N'
FROM SYSTEM_RANGE(1, 100) x(x);

-- 알림 그룹 10개 생성
INSERT INTO notification_group (name, created_at, updated_at, del_yn)
SELECT 'group_' || x, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'
FROM SYSTEM_RANGE(1, 10) x(x);

-- 사용자 외부 서비스 연결 (Slack)
INSERT INTO user_notification_platform (user_id, platform_id, external_id)
SELECT x, 1, 'SLACK_' || LPAD(x, 6, '0')
FROM SYSTEM_RANGE(1, 100) x(x);

-- 사용자 외부 서비스 연결 (Telegram)
INSERT INTO user_notification_platform (user_id, platform_id, external_id)
SELECT x, 2, 'TELEGRAM_' || LPAD(x, 6, '0')
FROM SYSTEM_RANGE(1, 100) x(x);


-- 사용자 알림 그룹 랜덤 할당
INSERT INTO user_notification_group (user_id, group_id)
SELECT x, FLOOR(RAND() * 10) + 1
FROM SYSTEM_RANGE(1, 100) x(x);