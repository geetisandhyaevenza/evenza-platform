-- ============================================================
-- EVENZA VENDOR - Complete MySQL Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS evenza_vendor
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE evenza_vendor;

-- ============================================================
-- CATEGORIES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(100) NOT NULL UNIQUE,
    icon        VARCHAR(255),
    description TEXT,
    is_active   BOOLEAN DEFAULT TRUE,
    sort_order  INT DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME NULL
);

-- ============================================================
-- USERS TABLE (Customers / Admins)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(150) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    phone           VARCHAR(20),
    password_hash   VARCHAR(500) NOT NULL,
    role            ENUM('CUSTOMER','ADMIN','SUPER_ADMIN') DEFAULT 'CUSTOMER',
    avatar_url      VARCHAR(500),
    is_active       BOOLEAN DEFAULT TRUE,
    is_verified     BOOLEAN DEFAULT FALSE,
    email_verified  BOOLEAN DEFAULT FALSE,
    phone_verified  BOOLEAN DEFAULT FALSE,
    last_login_at   DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME NULL,
    INDEX idx_email (email),
    INDEX idx_phone (phone)
);

-- ============================================================
-- VENDORS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS vendors (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name           VARCHAR(150) NOT NULL,
    business_name       VARCHAR(200) NOT NULL,
    email               VARCHAR(255) NOT NULL UNIQUE,
    phone               VARCHAR(20) NOT NULL,
    alternate_phone     VARCHAR(20),
    password_hash       VARCHAR(500) NOT NULL,
    category_id         BIGINT NOT NULL,
    profile_picture_url VARCHAR(500),
    business_logo_url   VARCHAR(500),
    bio                 TEXT,
    tagline             VARCHAR(300),
    website_url         VARCHAR(500),
    address_line1       VARCHAR(300),
    address_line2       VARCHAR(300),
    city                VARCHAR(100),
    state               VARCHAR(100),
    pincode             VARCHAR(10),
    country             VARCHAR(100) DEFAULT 'India',
    latitude            DECIMAL(10,8),
    longitude           DECIMAL(11,8),
    years_of_experience INT DEFAULT 0,
    starting_price      DECIMAL(12,2),
    max_price           DECIMAL(12,2),
    price_unit          VARCHAR(50) DEFAULT 'per event',
    is_active           BOOLEAN DEFAULT TRUE,
    is_verified         BOOLEAN DEFAULT FALSE,
    is_featured         BOOLEAN DEFAULT FALSE,
    is_premium          BOOLEAN DEFAULT FALSE,
    email_verified      BOOLEAN DEFAULT FALSE,
    phone_verified      BOOLEAN DEFAULT FALSE,
    availability_status ENUM('AVAILABLE','BUSY','ON_LEAVE') DEFAULT 'AVAILABLE',
    approval_status     ENUM('PENDING','APPROVED','REJECTED','SUSPENDED') DEFAULT 'PENDING',
    rejection_reason    TEXT,
    profile_completion  INT DEFAULT 0,
    total_bookings      INT DEFAULT 0,
    total_revenue       DECIMAL(14,2) DEFAULT 0,
    avg_rating          DECIMAL(3,2) DEFAULT 0.00,
    total_reviews       INT DEFAULT 0,
    instagram_url       VARCHAR(500),
    facebook_url        VARCHAR(500),
    youtube_url         VARCHAR(500),
    linkedin_url        VARCHAR(500),
    pinterest_url       VARCHAR(500),
    whatsapp_number     VARCHAR(20),
    last_login_at       DATETIME,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          DATETIME NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_email (email),
    INDEX idx_city (city),
    INDEX idx_category (category_id),
    INDEX idx_approval_status (approval_status),
    FULLTEXT INDEX ft_search (business_name, bio, tagline)
);

-- ============================================================
-- VENDOR SERVICES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS vendor_services (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id   BIGINT NOT NULL,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    price       DECIMAL(12,2),
    price_unit  VARCHAR(50),
    duration    VARCHAR(100),
    is_active   BOOLEAN DEFAULT TRUE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE
);

-- ============================================================
-- PORTFOLIO TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS portfolio (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id   BIGINT NOT NULL,
    title       VARCHAR(200),
    description TEXT,
    media_url   VARCHAR(500) NOT NULL,
    media_type  ENUM('IMAGE','VIDEO','REEL') DEFAULT 'IMAGE',
    thumbnail_url VARCHAR(500),
    category    VARCHAR(100),
    tags        VARCHAR(500),
    likes_count INT DEFAULT 0,
    views_count INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    sort_order  INT DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME NULL,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    INDEX idx_vendor (vendor_id),
    INDEX idx_media_type (media_type)
);

-- ============================================================
-- REVIEWS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS reviews (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id   BIGINT NOT NULL,
    user_id     BIGINT NOT NULL,
    booking_id  BIGINT,
    rating      DECIMAL(2,1) NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title       VARCHAR(200),
    comment     TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    vendor_reply TEXT,
    replied_at  DATETIME,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME NULL,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_review (vendor_id, user_id, booking_id)
);

-- ============================================================
-- BOOKINGS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS bookings (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_number      VARCHAR(50) NOT NULL UNIQUE,
    vendor_id           BIGINT NOT NULL,
    user_id             BIGINT NOT NULL,
    event_type          VARCHAR(150),
    event_date          DATE NOT NULL,
    event_time          TIME,
    event_location      VARCHAR(500),
    guest_count         INT,
    special_requirements TEXT,
    total_amount        DECIMAL(12,2),
    advance_paid        DECIMAL(12,2) DEFAULT 0,
    status              ENUM('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED','REJECTED') DEFAULT 'PENDING',
    cancellation_reason TEXT,
    notes               TEXT,
    created_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          DATETIME NULL,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_vendor (vendor_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_event_date (event_date)
);

-- ============================================================
-- INQUIRIES TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS inquiries (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id   BIGINT NOT NULL,
    user_id     BIGINT,
    name        VARCHAR(150) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    phone       VARCHAR(20),
    event_type  VARCHAR(150),
    event_date  DATE,
    message     TEXT NOT NULL,
    budget      VARCHAR(100),
    status      ENUM('NEW','READ','REPLIED','CLOSED') DEFAULT 'NEW',
    reply       TEXT,
    replied_at  DATETIME,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_vendor (vendor_id),
    INDEX idx_status (status)
);

-- ============================================================
-- NOTIFICATIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_type ENUM('VENDOR','USER','ADMIN') NOT NULL,
    recipient_id   BIGINT NOT NULL,
    title       VARCHAR(300) NOT NULL,
    message     TEXT NOT NULL,
    type        ENUM('BOOKING','INQUIRY','REVIEW','SYSTEM','PAYMENT','VERIFICATION') DEFAULT 'SYSTEM',
    action_url  VARCHAR(500),
    is_read     BOOLEAN DEFAULT FALSE,
    read_at     DATETIME,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_recipient (recipient_type, recipient_id),
    INDEX idx_is_read (is_read)
);

-- ============================================================
-- VISITING CARDS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS visiting_cards (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id       BIGINT NOT NULL,
    template_id     VARCHAR(50) NOT NULL DEFAULT 'classic',
    card_name       VARCHAR(200),
    primary_color   VARCHAR(20) DEFAULT '#C9973A',
    secondary_color VARCHAR(20) DEFAULT '#6B1A2A',
    font_family     VARCHAR(100) DEFAULT 'Cormorant Garamond',
    card_data       JSON,
    image_url       VARCHAR(500),
    pdf_url         VARCHAR(500),
    qr_code_url     VARCHAR(500),
    share_token     VARCHAR(100) UNIQUE,
    views_count     INT DEFAULT 0,
    downloads_count INT DEFAULT 0,
    is_active       BOOLEAN DEFAULT TRUE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    INDEX idx_vendor (vendor_id),
    INDEX idx_share_token (share_token)
);

-- ============================================================
-- OTP VERIFICATIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS otp_verifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier  VARCHAR(255) NOT NULL,
    otp_code    VARCHAR(10) NOT NULL,
    type        ENUM('EMAIL','PHONE','PASSWORD_RESET') NOT NULL,
    is_used     BOOLEAN DEFAULT FALSE,
    expires_at  DATETIME NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_identifier (identifier, type)
);

-- ============================================================
-- SUBSCRIPTIONS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS subscriptions (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_id       BIGINT NOT NULL,
    plan_name       ENUM('FREE','BASIC','PREMIUM','ENTERPRISE') DEFAULT 'FREE',
    starts_at       DATE NOT NULL,
    ends_at         DATE,
    amount_paid     DECIMAL(10,2) DEFAULT 0,
    payment_ref     VARCHAR(200),
    is_active       BOOLEAN DEFAULT TRUE,
    auto_renew      BOOLEAN DEFAULT FALSE,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id)
);

-- ============================================================
-- WISHLIST TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS wishlists (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    vendor_id   BIGINT NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vendor_id) REFERENCES vendors(id) ON DELETE CASCADE,
    UNIQUE KEY unique_wishlist (user_id, vendor_id)
);

-- ============================================================
-- REFRESH TOKENS TABLE
-- ============================================================
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    token       VARCHAR(500) NOT NULL UNIQUE,
    vendor_id   BIGINT,
    user_id     BIGINT,
    expires_at  DATETIME NOT NULL,
    is_revoked  BOOLEAN DEFAULT FALSE,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- SEED DATA - Categories
-- ============================================================
INSERT INTO categories (name, slug, icon, description, sort_order) VALUES
('Photographer', 'photographer', 'camera', 'Professional wedding and event photographers', 1),
('Makeup Artist', 'makeup-artist', 'brush', 'Bridal and event makeup artists', 2),
('Mehendi Artist', 'mehendi-artist', 'palette', 'Traditional and contemporary mehendi artists', 3),
('Caterer', 'caterer', 'utensils', 'Wedding and event catering services', 4),
('Decorator', 'decorator', 'star', 'Wedding and event decoration specialists', 5),
('DJ', 'dj', 'music', 'DJs and sound system providers', 6),
('Wedding Planner', 'wedding-planner', 'calendar', 'Complete wedding planning services', 7),
('Venue', 'venue', 'building', 'Wedding venues and banquet halls', 8),
('Jewelry', 'jewelry', 'gem', 'Bridal jewelry and accessories', 9),
('Others', 'others', 'grid', 'Other wedding and event services', 10);

-- ============================================================
-- SEED DATA - Admin User
-- ============================================================
INSERT INTO users (full_name, email, phone, password_hash, role, is_active, is_verified, email_verified)
VALUES ('Evenza Admin', 'admin@evenza.com', '+91-9999999999',
        '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQyCgKMCYPaWNmASlf9sLFa.2', -- password: Admin@123
        'SUPER_ADMIN', TRUE, TRUE, TRUE);
