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
