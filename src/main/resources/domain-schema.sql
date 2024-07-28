-- 사용자 테이블
CREATE TABLE user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    del_yn CHAR(1) DEFAULT 'N'
);

-- 외부 플랫폼 테이블
CREATE TABLE notification_platform (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    del_yn CHAR(1) DEFAULT 'N'
);

-- 사용자-외부 플랫폼 관계 테이블
CREATE TABLE user_notification_platform (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    platform_id BIGINT NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_info(id) ON DELETE CASCADE,
    FOREIGN KEY (platform_id) REFERENCES notification_platform(id) ON DELETE CASCADE,
    UNIQUE (user_id, platform_id)
);

CREATE TABLE notification_channel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    platform_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (platform_id) REFERENCES notification_platform(id) ON DELETE CASCADE
);

CREATE TABLE user_channel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    channel_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user_info(id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES channel(id) ON DELETE CASCADE,
    UNIQUE (user_id, channel_id)
);

-- 알림 그룹 테이블
CREATE TABLE notification_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);

-- 사용자-알림 그룹 관계
CREATE TABLE user_notification_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_info(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES notification_group(id) ON DELETE CASCADE,
    UNIQUE (user_id, group_id)
);

-- 알림 전송 기록
CREATE TABLE notification_delivery_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    channel VARCHAR(255) NOT NULL,
    platform VARCHAR(255) NOT NULL,
    batch_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',  -- 'PENDING', 'RETRYING', 'FAILED', 'SUCCESS'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
