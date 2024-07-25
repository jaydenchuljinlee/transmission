-- 사용자 테이블
CREATE TABLE user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    del_yn CHAR(1) DEFAULT 'N'
);

-- 외부 서비스 테이블
CREATE TABLE notification_platform (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    del_yn CHAR(1) DEFAULT 'N'
);

-- 사용자-외부 서비스 관계 테이블
CREATE TABLE user_notification_platform (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    platform_id BIGINT NOT NULL,
    external_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_info(id) ON DELETE CASCADE,
    FOREIGN KEY (platform_id) REFERENCES notification_platform(id) ON DELETE CASCADE,
    UNIQUE (user_id, platform_id)
);

-- 알림 그룹 테이블
CREATE TABLE notification_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    del_yn CHAR(1) DEFAULT 'N'
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