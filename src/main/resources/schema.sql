CREATE TABLE IF NOT EXISTS toilets
(
    id             BIGINT          NOT NULL AUTO_INCREMENT,
    name           VARCHAR(200)    NOT NULL,
    address        VARCHAR(500)    NOT NULL,
    lat            DECIMAL(10, 7)  NOT NULL,
    lng            DECIMAL(10, 7)  NOT NULL,
    location       POINT           NOT NULL SRID 4326,
    open_status    VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    is_disabled    TINYINT(1)      NOT NULL DEFAULT 0,
    is_gender_sep  TINYINT(1)      NOT NULL DEFAULT 1,
    open_hours     VARCHAR(100),
    source         VARCHAR(20)     NOT NULL DEFAULT 'PUBLIC_DATA',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    SPATIAL INDEX idx_location (location)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS crowd_votes
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    toilet_id   BIGINT       NOT NULL,
    device_id   VARCHAR(100) NOT NULL,
    level       VARCHAR(20)  NOT NULL,
    voted_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_crowd_device_toilet (device_id, toilet_id),
    INDEX idx_crowd_toilet_expires (toilet_id, expires_at),
    CONSTRAINT fk_crowd_toilet FOREIGN KEY (toilet_id) REFERENCES toilets (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS reviews
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    toilet_id   BIGINT       NOT NULL,
    device_id   VARCHAR(100) NOT NULL,
    rating      TINYINT      NOT NULL,
    content     VARCHAR(500),
    image_url   VARCHAR(500),
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_review_device_toilet (device_id, toilet_id),
    INDEX idx_review_toilet (toilet_id),
    CONSTRAINT fk_review_toilet FOREIGN KEY (toilet_id) REFERENCES toilets (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;


CREATE TABLE IF NOT EXISTS paper_requests
(
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    toilet_id             BIGINT        NOT NULL,
    device_id             VARCHAR(100)  NOT NULL,
    gender                VARCHAR(10)   NOT NULL COMMENT 'MALE | FEMALE',
    status                VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE | RESCUED | EXPIRED',
    requested_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at            DATETIME      NOT NULL COMMENT '요청 후 7분',
    rescued_at            DATETIME      NULL,
    rescue_display_until  DATETIME      NULL     COMMENT '구조 완료 후 3분간 *구조* 마커 표시',
    PRIMARY KEY (id),
    INDEX idx_paper_status_expires (status, expires_at),
    INDEX idx_paper_device (device_id),
    INDEX idx_paper_toilet (toilet_id),
    CONSTRAINT fk_paper_toilet FOREIGN KEY (toilet_id) REFERENCES toilets (id) ON DELETE CASCADE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '긴급 휴지 요청';

CREATE TABLE IF NOT EXISTS fcm_tokens
(
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    device_id   VARCHAR(100)  NOT NULL,
    fcm_token   VARCHAR(500)  NOT NULL,
    last_lat    DOUBLE NULL     COMMENT '마지막 위도',
    last_lng    DOUBLE NULL     COMMENT '마지막 경도',
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_fcm_device (device_id),
    INDEX idx_fcm_location (last_lat, last_lng)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '기기별 FCM 토큰';


CREATE TABLE IF NOT EXISTS toilet_reports
(
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    device_id           VARCHAR(100)  NOT NULL,
    name                VARCHAR(200)  NOT NULL,
    address             VARCHAR(500)  NOT NULL,
    lat                 DECIMAL(10,7) NOT NULL,
    lng                 DECIMAL(10,7) NOT NULL,
    open_status         VARCHAR(20),
    is_disabled         TINYINT(1),
    is_gender_sep       TINYINT(1),
    open_hours          VARCHAR(100),
    memo                VARCHAR(500),
    image_url           VARCHAR(500),
    status              VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    approved_toilet_id  BIGINT,
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_report_status (status),
    INDEX idx_report_device (device_id),
    CONSTRAINT fk_report_approved_toilet FOREIGN KEY (approved_toilet_id) REFERENCES toilets (id) ON DELETE SET NULL
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;