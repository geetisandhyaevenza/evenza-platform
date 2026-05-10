package com.evenza.vendor.controller;

import com.evenza.vendor.controller.AuthController.ApiResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/vendors")
@Slf4j
@CrossOrigin(origins = "*")
public class VendorController {

    // ============================================================
    // PUBLIC: Search & List Vendors
    // GET /api/vendors/public/search
    // ============================================================
    @GetMapping("/public/search")
    public ResponseEntity<ApiResponse<PagedResponse<VendorSummaryDto>>> searchVendors(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "rating") String sortBy) {

        log.info("Vendor search: query={}, city={}, category={}", query, city, categoryId);

        // Demo data
        List<VendorSummaryDto> vendors = generateDemoVendors();

        PagedResponse<VendorSummaryDto> paged = new PagedResponse<>(
            vendors, 0, 12, vendors.size(), 1, false, false, true, true
        );

        return ResponseEntity.ok(ApiResponse.success("Vendors fetched successfully", paged));
    }

    // ============================================================
    // PUBLIC: Get Single Vendor Profile
    // GET /api/vendors/public/{id}
    // ============================================================
    @GetMapping("/public/{id}")
    public ResponseEntity<ApiResponse<VendorDetailDto>> getVendorProfile(@PathVariable Long id) {
        log.info("Fetching vendor profile: {}", id);

        VendorDetailDto vendor = buildDemoVendorDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Vendor profile fetched", vendor));
    }

    // ============================================================
    // PUBLIC: Featured Vendors
    // GET /api/vendors/public/featured
    // ============================================================
    @GetMapping("/public/featured")
    public ResponseEntity<ApiResponse<List<VendorSummaryDto>>> getFeaturedVendors(
            @RequestParam(defaultValue = "8") int limit) {
        List<VendorSummaryDto> vendors = generateDemoVendors().subList(0, Math.min(limit, 8));
        return ResponseEntity.ok(ApiResponse.success("Featured vendors", vendors));
    }

    // ============================================================
    // PUBLIC: Trending Vendors by Category
    // GET /api/vendors/public/trending
    // ============================================================
    @GetMapping("/public/trending")
    public ResponseEntity<ApiResponse<Map<String, List<VendorSummaryDto>>>> getTrendingVendors() {
        Map<String, List<VendorSummaryDto>> trending = new HashMap<>();
        trending.put("photographers", generateDemoVendors().subList(0, 3));
        trending.put("decorators", generateDemoVendors().subList(1, 4));
        trending.put("caterers", generateDemoVendors().subList(2, 5));
        return ResponseEntity.ok(ApiResponse.success("Trending vendors", trending));
    }

    // ============================================================
    // VENDOR: Get Own Profile
    // GET /api/vendors/me
    // ============================================================
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<VendorDetailDto>> getMyProfile() {
        VendorDetailDto vendor = buildDemoVendorDetail(1001L);
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", vendor));
    }

    // ============================================================
    // VENDOR: Update Profile
    // PUT /api/vendors/me
    // ============================================================
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<VendorDetailDto>> updateMyProfile(
            @RequestBody UpdateProfileRequest request) {
        log.info("Updating vendor profile");
        VendorDetailDto updated = buildDemoVendorDetail(1001L);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    // ============================================================
    // VENDOR: Upload Profile Picture
    // POST /api/vendors/me/profile-picture
    // ============================================================
    @PostMapping("/me/profile-picture")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file) {
        log.info("Uploading profile picture: {}", file.getOriginalFilename());

        // In production: save to S3/local storage
        Map<String, String> response = new HashMap<>();
        response.put("url", "https://evenza-uploads.s3.amazonaws.com/profiles/vendor_1001.jpg");
        response.put("message", "Profile picture uploaded successfully");

        return ResponseEntity.ok(ApiResponse.success("Upload successful", response));
    }

    // ============================================================
    // VENDOR: Upload Business Logo
    // POST /api/vendors/me/logo
    // ============================================================
    @PostMapping("/me/logo")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadBusinessLogo(
            @RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        response.put("url", "https://evenza-uploads.s3.amazonaws.com/logos/vendor_1001_logo.png");
        return ResponseEntity.ok(ApiResponse.success("Logo uploaded", response));
    }

    // ============================================================
    // VENDOR: Get Dashboard Stats
    // GET /api/vendors/me/dashboard
    // ============================================================
    @GetMapping("/me/dashboard")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboardStats() {
        DashboardStats stats = DashboardStats.builder()
                .totalBookings(48).pendingBookings(3).confirmedBookings(42).completedBookings(38)
                .totalInquiries(124).newInquiries(7).avgRating(BigDecimal.valueOf(4.8))
                .totalReviews(96).profileCompletion(85).profileViews(1240)
                .totalRevenue(BigDecimal.valueOf(245000)).monthlyRevenue(BigDecimal.valueOf(42000))
                .wishlistedBy(89).build();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats", stats));
    }

    // ============================================================
    // VENDOR: Update Availability
    // PATCH /api/vendors/me/availability
    // ============================================================
    @PatchMapping("/me/availability")
    public ResponseEntity<ApiResponse<String>> updateAvailability(
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return ResponseEntity.ok(ApiResponse.success("Availability updated to: " + status, null));
    }

    // ============================================================
    // Admin: Get All Vendors
    // GET /api/vendors/admin/all
    // ============================================================
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<PagedResponse<VendorSummaryDto>>> getAllVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        List<VendorSummaryDto> vendors = generateDemoVendors();
        PagedResponse<VendorSummaryDto> paged = new PagedResponse<>(
            vendors, page, size, 150, 13, false, false, true, false
        );
        return ResponseEntity.ok(ApiResponse.success("All vendors", paged));
    }

    // ============================================================
    // Admin: Approve/Reject Vendor
    // PATCH /api/vendors/admin/{id}/approval
    // ============================================================
    @PatchMapping("/admin/{id}/approval")
    public ResponseEntity<ApiResponse<String>> updateApprovalStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String action = request.get("action");
        String reason = request.get("reason");
        log.info("Admin {} vendor {}: {}", action, id, reason);
        return ResponseEntity.ok(ApiResponse.success(
            "Vendor " + action + " successfully", null));
    }

    // ============================================================
    // DEMO DATA GENERATORS
    // ============================================================
    private List<VendorSummaryDto> generateDemoVendors() {
        String[] names = {"Kapoor Photography", "Royal Decorations", "Divine Caterers",
                          "Glamour Makeup Studio", "Mehendi Magic", "BeatDrop DJ",
                          "Dream Wedding Planners", "Grand Pavilion Venue", "Swarovski Jewelry", "Event Extras"};
        String[] categories = {"Photographer", "Decorator", "Caterer", "Makeup Artist",
                               "Mehendi Artist", "DJ", "Wedding Planner", "Venue", "Jewelry", "Others"};
        String[] cities = {"Mumbai", "Delhi", "Bangalore", "Chennai", "Hyderabad", "Pune", "Kolkata", "Jaipur"};

        List<VendorSummaryDto> list = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            list.add(VendorSummaryDto.builder()
                .id((long)(i + 1))
                .businessName(names[i])
                .category(categories[i])
                .city(cities[i % cities.length])
                .avgRating(BigDecimal.valueOf(4.0 + (i % 10) * 0.1))
                .totalReviews(50 + i * 12)
                .startingPrice(BigDecimal.valueOf(15000 + i * 5000))
                .profilePictureUrl("https://ui-avatars.com/api/?name=" + names[i] + "&background=C9973A&color=fff&size=200")
                .isVerified(i % 3 == 0)
                .isFeatured(i < 4)
                .approvalStatus("APPROVED")
                .build());
        }
        return list;
    }

    private VendorDetailDto buildDemoVendorDetail(Long id) {
        return VendorDetailDto.builder()
            .id(id)
            .fullName("Rajesh Kapoor")
            .businessName("Kapoor Photography")
            .email("rajesh@kapoor.photography")
            .phone("+91-9876543210")
            .category("Photographer")
            .categoryId(1L)
            .bio("Award-winning wedding photographer with 10+ years of experience capturing love stories across India. Specializing in candid, traditional, and pre-wedding shoots.")
            .tagline("Every frame tells your love story")
            .city("Mumbai")
            .state("Maharashtra")
            .country("India")
            .websiteUrl("https://kapoor.photography")
            .startingPrice(BigDecimal.valueOf(45000))
            .maxPrice(BigDecimal.valueOf(250000))
            .priceUnit("per event")
            .yearsOfExperience(10)
            .avgRating(BigDecimal.valueOf(4.9))
            .totalReviews(156)
            .totalBookings(284)
            .isVerified(true)
            .isFeatured(true)
            .isPremium(true)
            .availabilityStatus("AVAILABLE")
            .approvalStatus("APPROVED")
            .profileCompletion(92)
            .profilePictureUrl("https://ui-avatars.com/api/?name=Rajesh+Kapoor&background=C9973A&color=fff&size=400")
            .instagramUrl("https://instagram.com/kapoor.photography")
            .facebookUrl("https://facebook.com/kapoor.photography")
            .whatsappNumber("+91-9876543210")
            .build();
    }

    // ============================================================
    // DTOs
    // ============================================================
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VendorSummaryDto {
        private Long id;
        private String businessName;
        private String category;
        private String city;
        private BigDecimal avgRating;
        private Integer totalReviews;
        private BigDecimal startingPrice;
        private String profilePictureUrl;
        private Boolean isVerified;
        private Boolean isFeatured;
        private String approvalStatus;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VendorDetailDto {
        private Long id;
        private String fullName;
        private String businessName;
        private String email;
        private String phone;
        private String category;
        private Long categoryId;
        private String bio;
        private String tagline;
        private String city;
        private String state;
        private String country;
        private String websiteUrl;
        private BigDecimal startingPrice;
        private BigDecimal maxPrice;
        private String priceUnit;
        private Integer yearsOfExperience;
        private BigDecimal avgRating;
        private Integer totalReviews;
        private Integer totalBookings;
        private Boolean isVerified;
        private Boolean isFeatured;
        private Boolean isPremium;
        private String availabilityStatus;
        private String approvalStatus;
        private Integer profileCompletion;
        private String profilePictureUrl;
        private String businessLogoUrl;
        private String instagramUrl;
        private String facebookUrl;
        private String youtubeUrl;
        private String whatsappNumber;
        private List<ServiceDto> services;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ServiceDto {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String priceUnit;
        private String duration;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class DashboardStats {
        private Integer totalBookings;
        private Integer pendingBookings;
        private Integer confirmedBookings;
        private Integer completedBookings;
        private Integer totalInquiries;
        private Integer newInquiries;
        private BigDecimal avgRating;
        private Integer totalReviews;
        private Integer profileCompletion;
        private Integer profileViews;
        private BigDecimal totalRevenue;
        private BigDecimal monthlyRevenue;
        private Integer wishlistedBy;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String fullName;
        private String businessName;
        private String phone;
        private String bio;
        private String tagline;
        private String city;
        private String state;
        private String websiteUrl;
        private BigDecimal startingPrice;
        private BigDecimal maxPrice;
        private String instagramUrl;
        private String facebookUrl;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PagedResponse<T> {
        private List<T> content;
        private int currentPage;
        private int pageSize;
        private long totalElements;
        private int totalPages;
        private boolean hasPrevious;
        private boolean hasNext;
        private boolean isFirst;
        private boolean isLast;
    }
}
