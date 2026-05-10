package com.evenza.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// ============================================================
// USER ENTITY
// ============================================================
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_phone", columnList = "phone")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "full_name", nullable = false, length = 150) private String fullName;
    @Column(nullable = false, unique = true) private String email;
    @Column(length = 20) private String phone;
    @Column(name = "password_hash", nullable = false, length = 500) private String passwordHash;
    @Enumerated(EnumType.STRING) private UserRole role = UserRole.CUSTOMER;
    @Column(name = "avatar_url", length = 500) private String avatarUrl;
    @Column(name = "is_active") private Boolean isActive = true;
    @Column(name = "is_verified") private Boolean isVerified = false;
    @Column(name = "email_verified") private Boolean emailVerified = false;
    @Column(name = "phone_verified") private Boolean phoneVerified = false;
    @Column(name = "last_login_at") private LocalDateTime lastLoginAt;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;

    public enum UserRole { CUSTOMER, ADMIN, SUPER_ADMIN }
}

// ============================================================
// PORTFOLIO ENTITY
// ============================================================
@Entity
@Table(name = "portfolio", indexes = {
    @Index(name = "idx_portfolio_vendor", columnList = "vendor_id"),
    @Index(name = "idx_portfolio_type", columnList = "media_type")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class Portfolio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vendor_id", nullable = false) private Vendor vendor;
    @Column(length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "media_url", nullable = false, length = 500) private String mediaUrl;
    @Enumerated(EnumType.STRING) @Column(name = "media_type") private MediaType mediaType = MediaType.IMAGE;
    @Column(name = "thumbnail_url", length = 500) private String thumbnailUrl;
    @Column(length = 100) private String category;
    @Column(length = 500) private String tags;
    @Column(name = "likes_count") private Integer likesCount = 0;
    @Column(name = "views_count") private Integer viewsCount = 0;
    @Column(name = "is_featured") private Boolean isFeatured = false;
    @Column(name = "sort_order") private Integer sortOrder = 0;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;

    public enum MediaType { IMAGE, VIDEO, REEL }
}

// ============================================================
// REVIEW ENTITY
// ============================================================
@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vendor_id", nullable = false) private Vendor vendor;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "booking_id") private Long bookingId;
    @Column(nullable = false, precision = 2, scale = 1) private BigDecimal rating;
    @Column(length = 200) private String title;
    @Column(columnDefinition = "TEXT") private String comment;
    @Column(name = "is_verified") private Boolean isVerified = false;
    @Column(name = "is_featured") private Boolean isFeatured = false;
    @Column(name = "vendor_reply", columnDefinition = "TEXT") private String vendorReply;
    @Column(name = "replied_at") private LocalDateTime repliedAt;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;
}

// ============================================================
// BOOKING ENTITY
// ============================================================
@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_vendor", columnList = "vendor_id"),
    @Index(name = "idx_booking_user", columnList = "user_id"),
    @Index(name = "idx_booking_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "booking_number", nullable = false, unique = true, length = 50) private String bookingNumber;
    @Column(name = "vendor_id", nullable = false) private Long vendorId;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "event_type", length = 150) private String eventType;
    @Column(name = "event_date", nullable = false) private LocalDate eventDate;
    @Column(name = "event_time") private LocalTime eventTime;
    @Column(name = "event_location", length = 500) private String eventLocation;
    @Column(name = "guest_count") private Integer guestCount;
    @Column(name = "special_requirements", columnDefinition = "TEXT") private String specialRequirements;
    @Column(name = "total_amount", precision = 12, scale = 2) private BigDecimal totalAmount;
    @Column(name = "advance_paid", precision = 12, scale = 2) private BigDecimal advancePaid = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) private BookingStatus status = BookingStatus.PENDING;
    @Column(name = "cancellation_reason", columnDefinition = "TEXT") private String cancellationReason;
    @Column(columnDefinition = "TEXT") private String notes;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
    @Column(name = "deleted_at") private LocalDateTime deletedAt;

    public enum BookingStatus { PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, REJECTED }
}

// ============================================================
// INQUIRY ENTITY
// ============================================================
@Entity
@Table(name = "inquiries")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class Inquiry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "vendor_id", nullable = false) private Long vendorId;
    @Column(name = "user_id") private Long userId;
    @Column(nullable = false, length = 150) private String name;
    @Column(nullable = false, length = 255) private String email;
    @Column(length = 20) private String phone;
    @Column(name = "event_type", length = 150) private String eventType;
    @Column(name = "event_date") private LocalDate eventDate;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Column(length = 100) private String budget;
    @Enumerated(EnumType.STRING) private InquiryStatus status = InquiryStatus.NEW;
    @Column(columnDefinition = "TEXT") private String reply;
    @Column(name = "replied_at") private LocalDateTime repliedAt;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;

    public enum InquiryStatus { NEW, READ, REPLIED, CLOSED }
}

// ============================================================
// NOTIFICATION ENTITY
// ============================================================
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notif_recipient", columnList = "recipient_type, recipient_id"),
    @Index(name = "idx_notif_read", columnList = "is_read")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) @Column(name = "recipient_type", nullable = false) private RecipientType recipientType;
    @Column(name = "recipient_id", nullable = false) private Long recipientId;
    @Column(nullable = false, length = 300) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String message;
    @Enumerated(EnumType.STRING) private NotificationType type = NotificationType.SYSTEM;
    @Column(name = "action_url", length = 500) private String actionUrl;
    @Column(name = "is_read") private Boolean isRead = false;
    @Column(name = "read_at") private LocalDateTime readAt;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();

    public enum RecipientType { VENDOR, USER, ADMIN }
    public enum NotificationType { BOOKING, INQUIRY, REVIEW, SYSTEM, PAYMENT, VERIFICATION }
}

// ============================================================
// VISITING CARD ENTITY
// ============================================================
@Entity
@Table(name = "visiting_cards", indexes = {
    @Index(name = "idx_vc_vendor", columnList = "vendor_id"),
    @Index(name = "idx_vc_token", columnList = "share_token")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class VisitingCard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vendor_id", nullable = false) private Vendor vendor;
    @Column(name = "template_id", length = 50) private String templateId = "classic";
    @Column(name = "card_name", length = 200) private String cardName;
    @Column(name = "primary_color", length = 20) private String primaryColor = "#C9973A";
    @Column(name = "secondary_color", length = 20) private String secondaryColor = "#6B1A2A";
    @Column(name = "font_family", length = 100) private String fontFamily = "Cormorant Garamond";
    @Column(name = "card_data", columnDefinition = "JSON") private String cardData;
    @Column(name = "image_url", length = 500) private String imageUrl;
    @Column(name = "pdf_url", length = 500) private String pdfUrl;
    @Column(name = "qr_code_url", length = 500) private String qrCodeUrl;
    @Column(name = "share_token", unique = true, length = 100) private String shareToken;
    @Column(name = "views_count") private Integer viewsCount = 0;
    @Column(name = "downloads_count") private Integer downloadsCount = 0;
    @Column(name = "is_active") private Boolean isActive = true;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
}

// ============================================================
// VENDOR SERVICE ENTITY
// ============================================================
@Entity
@Table(name = "vendor_services")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class VendorService {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "vendor_id", nullable = false) private Vendor vendor;
    @Column(nullable = false, length = 200) private String name;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(precision = 12, scale = 2) private BigDecimal price;
    @Column(name = "price_unit", length = 50) private String priceUnit;
    @Column(length = 100) private String duration;
    @Column(name = "is_active") private Boolean isActive = true;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
}

// ============================================================
// OTP VERIFICATION ENTITY
// ============================================================
@Entity
@Table(name = "otp_verifications", indexes = {
    @Index(name = "idx_otp_identifier", columnList = "identifier, type")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class OtpVerification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 255) private String identifier;
    @Column(name = "otp_code", nullable = false, length = 10) private String otpCode;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OtpType type;
    @Column(name = "is_used") private Boolean isUsed = false;
    @Column(name = "expires_at", nullable = false) private LocalDateTime expiresAt;
    @Column(name = "created_at") private LocalDateTime createdAt = LocalDateTime.now();

    public enum OtpType { EMAIL, PHONE, PASSWORD_RESET }
    public boolean isExpired() { return LocalDateTime.now().isAfter(expiresAt); }
}
