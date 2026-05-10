package com.evenza.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors",
    indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_category", columnList = "category_id"),
        @Index(name = "idx_approval", columnList = "approval_status")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "alternate_phone", length = 20)
    private String alternatePhone;

    @Column(name = "password_hash", nullable = false, length = 500)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "business_logo_url", length = 500)
    private String businessLogoUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 300)
    private String tagline;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "address_line1", length = 300)
    private String addressLine1;

    @Column(name = "address_line2", length = 300)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(length = 100)
    private String country = "India";

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience = 0;

    @Column(name = "starting_price", precision = 12, scale = 2)
    private BigDecimal startingPrice;

    @Column(name = "max_price", precision = 12, scale = 2)
    private BigDecimal maxPrice;

    @Column(name = "price_unit", length = 50)
    private String priceUnit = "per event";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "is_premium")
    private Boolean isPremium = false;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status")
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "profile_completion")
    private Integer profileCompletion = 0;

    @Column(name = "total_bookings")
    private Integer totalBookings = 0;

    @Column(name = "total_revenue", precision = 14, scale = 2)
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Column(name = "facebook_url", length = 500)
    private String facebookUrl;

    @Column(name = "youtube_url", length = 500)
    private String youtubeUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "pinterest_url", length = 500)
    private String pinterestUrl;

    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relationships
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VendorService> services = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Portfolio> portfolioItems = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VisitingCard> visitingCards = new ArrayList<>();

    // Enums
    public enum AvailabilityStatus { AVAILABLE, BUSY, ON_LEAVE }
    public enum ApprovalStatus { PENDING, APPROVED, REJECTED, SUSPENDED }

    // Soft delete helper
    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.isActive = false;
    }

    // Calculate profile completion
    public void recalculateProfileCompletion() {
        int score = 0;
        if (fullName != null && !fullName.isBlank()) score += 5;
        if (businessName != null && !businessName.isBlank()) score += 5;
        if (email != null && !email.isBlank()) score += 5;
        if (phone != null && !phone.isBlank()) score += 5;
        if (profilePictureUrl != null) score += 10;
        if (businessLogoUrl != null) score += 10;
        if (bio != null && bio.length() > 50) score += 10;
        if (tagline != null && !tagline.isBlank()) score += 5;
        if (city != null && !city.isBlank()) score += 5;
        if (addressLine1 != null && !addressLine1.isBlank()) score += 5;
        if (startingPrice != null) score += 10;
        if (instagramUrl != null || facebookUrl != null) score += 5;
        if (websiteUrl != null) score += 5;
        if (emailVerified) score += 10;
        if (!services.isEmpty()) score += 5;
        this.profileCompletion = Math.min(score, 100);
    }
}
