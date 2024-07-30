-- 사용자 100명 생성
INSERT INTO user_info (nickname, email, created_at, updated_at, version, del_yn)
SELECT
  LOWER(SUBSTRING(RIGHT(RANDOM_UUID(), 12), 1, 8)),
  LOWER(SUBSTRING(RIGHT(RANDOM_UUID(), 12), 1, 8)) || '@kakao.com',
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  1,
  'N'
FROM SYSTEM_RANGE(1, 100) x(x);

-- [슬랙 쿼리 START] --

-- Slack 채널 생성
INSERT INTO notification_platform (name, created_at, updated_at) VALUES ('slack', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO notification_channel (name, url, platform_id, created_at, updated_at)
SELECT 'slack_channel_' || x, 'slack_url_' || x, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM SYSTEM_RANGE(1, 100) x(x);

-- 사용자와 Slack 채널 연결
INSERT INTO user_channel (user_id, channel_id, created_at, updated_at)
SELECT u.id, nc.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM user_info u
JOIN notification_channel nc ON nc.name = 'slack_channel_' || u.id
WHERE nc.platform_id = 1;

-- [슬랙 쿼리 END] --

-- [텔레그램 쿼리 START] --

-- Telegram 채널 생성
--INSERT INTO notification_platform (name, created_at, updated_at, del_yn) VALUES ('telegram', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N');
--
--INSERT INTO notification_channel (name, platform_id, created_at, updated_at, del_yn)
--SELECT 'telegram_channel_' || x, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'
--FROM SYSTEM_RANGE(1, 100) x(x);

-- 사용자와 Telegram 채널 연결
--INSERT INTO user_channel (user_id, channel_id, created_at, updated_at, del_yn)
--SELECT u.id, nc.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'
--FROM user_info u
--JOIN notification_channel nc ON nc.name = 'telegram_channel_' || u.id
--WHERE nc.platform_id = 2;

-- [텔레그램 쿼리 END] --

-- [사용자-알림 그룹 생성 및 연결 쿼리 START] --

-- 알림 그룹 10개 생성
--INSERT INTO notification_group (name, created_at, updated_at, del_yn)
--SELECT 'group_' || x, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'N'
--FROM SYSTEM_RANGE(1, 10) x(x);

-- 사용자 알림 그룹 랜덤 할당
--INSERT INTO user_notification_group (user_id, group_id)
--SELECT x, FLOOR(RAND() * 10) + 1
--FROM SYSTEM_RANGE(1, 100) x(x);

-- [사용자-알림 그룹 생성 및 연결 쿼리 END] --