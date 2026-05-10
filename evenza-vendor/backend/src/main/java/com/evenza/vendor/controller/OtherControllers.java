package com.evenza.vendor.controller;

import com.evenza.vendor.controller.AuthController.ApiResponse;
import com.evenza.vendor.controller.VendorController.PagedResponse;
import com.evenza.vendor.controller.VendorController.VendorSummaryDto;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

// ============================================================
// PORTFOLIO CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api/portfolio")
@Slf4j
@CrossOrigin(origins = "*")
class PortfolioController {

    @GetMapping("/public/vendor/{vendorId}")
    public ResponseEntity<ApiResponse<List<PortfolioItemDto>>> getVendorPortfolio(
            @PathVariable Long vendorId,
            @RequestParam(required = false) String type) {

        List<PortfolioItemDto> items = generateDemoPortfolio(vendorId);
        return ResponseEntity.ok(ApiResponse.success("Portfolio fetched", items));
    }

    @PostMapping("/me/upload")
    public ResponseEntity<ApiResponse<PortfolioItemDto>> uploadPortfolioItem(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "IMAGE") String mediaType) {

        log.info("Uploading portfolio item: {}", file.getOriginalFilename());

        PortfolioItemDto item = PortfolioItemDto.builder()
                .id(new Random().nextLong(1000, 9999))
                .title(title)
                .description(description)
                .mediaUrl("https://evenza-uploads.s3.amazonaws.com/portfolio/item_" + System.currentTimeMillis() + ".jpg")
                .mediaType(mediaType)
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Portfolio item uploaded", item));
    }

    @DeleteMapping("/me/{itemId}")
    public ResponseEntity<ApiResponse<String>> deletePortfolioItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success("Portfolio item deleted", null));
    }

    @PatchMapping("/me/{itemId}/featured")
    public ResponseEntity<ApiResponse<String>> toggleFeatured(@PathVariable Long itemId) {
        return ResponseEntity.ok(ApiResponse.success("Featured status toggled", null));
    }

    private List<PortfolioItemDto> generateDemoPortfolio(Long vendorId) {
        List<PortfolioItemDto> items = new ArrayList<>();
        String[] types = {"IMAGE", "IMAGE", "IMAGE", "VIDEO", "IMAGE", "REEL"};
        for (int i = 0; i < 6; i++) {
            items.add(PortfolioItemDto.builder()
                .id((long)(i + 1))
                .title("Wedding at " + (i == 0 ? "Taj Mumbai" : i == 1 ? "Grand Hyatt" : "Royal Palace " + i))
                .description("Beautiful wedding ceremony captured in stunning detail")
                .mediaUrl("https://picsum.photos/seed/" + (vendorId + i) + "/800/600")
                .thumbnailUrl("https://picsum.photos/seed/" + (vendorId + i) + "/400/300")
                .mediaType(types[i])
                .likesCount(20 + i * 15)
                .viewsCount(200 + i * 80)
                .isFeatured(i < 2)
                .createdAt(LocalDateTime.now().minusDays(i * 7))
                .build());
        }
        return items;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class PortfolioItemDto {
        private Long id;
        private String title;
        private String description;
        private String mediaUrl;
        private String thumbnailUrl;
        private String mediaType;
        private Integer likesCount;
        private Integer viewsCount;
        private Boolean isFeatured;
        private LocalDateTime createdAt;
    }
}

// ============================================================
// VISITING CARD CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api/visiting-cards")
@Slf4j
@CrossOrigin(origins = "*")
class VisitingCardController {

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<VisitingCardDto>>> getMyCards() {
        List<VisitingCardDto> cards = generateDemoCards();
        return ResponseEntity.ok(ApiResponse.success("Visiting cards fetched", cards));
    }

    @PostMapping("/me/generate")
    public ResponseEntity<ApiResponse<VisitingCardDto>> generateCard(
            @RequestBody GenerateCardRequest request) {
        log.info("Generating visiting card with template: {}", request.getTemplateId());

        VisitingCardDto card = VisitingCardDto.builder()
                .id(new Random().nextLong(1, 999))
                .templateId(request.getTemplateId())
                .cardName(request.getCardName())
                .primaryColor(request.getPrimaryColor())
                .secondaryColor(request.getSecondaryColor())
                .fontFamily(request.getFontFamily())
                .shareToken(UUID.randomUUID().toString().replace("-", "").substring(0, 12))
                .shareUrl("https://evenza.com/card/" + "abc123xyz")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Visiting card generated!", card));
    }

    @GetMapping("/public/{shareToken}")
    public ResponseEntity<ApiResponse<VisitingCardDto>> getPublicCard(
            @PathVariable String shareToken) {
        List<VisitingCardDto> cards = generateDemoCards();
        return ResponseEntity.ok(ApiResponse.success("Card fetched", cards.get(0)));
    }

    @GetMapping("/me/{id}/download")
    public ResponseEntity<ApiResponse<Map<String, String>>> downloadCard(
            @PathVariable Long id,
            @RequestParam(defaultValue = "IMAGE") String format) {
        Map<String, String> response = new HashMap<>();
        response.put("downloadUrl", "https://evenza.com/api/visiting-cards/download/" + id + "." + format.toLowerCase());
        response.put("format", format);
        return ResponseEntity.ok(ApiResponse.success("Download URL generated", response));
    }

    @DeleteMapping("/me/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCard(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Card deleted", null));
    }

    private List<VisitingCardDto> generateDemoCards() {
        return List.of(
            VisitingCardDto.builder().id(1L).templateId("elegant-gold").cardName("My Gold Card")
                .primaryColor("#C9973A").secondaryColor("#6B1A2A").fontFamily("Cormorant Garamond")
                .shareToken("abc123xyz").shareUrl("https://evenza.com/card/abc123xyz")
                .viewsCount(45).downloadsCount(12).isActive(true).createdAt(LocalDateTime.now()).build(),
            VisitingCardDto.builder().id(2L).templateId("modern-minimal").cardName("Minimal Card")
                .primaryColor("#2A1A10").secondaryColor("#C9973A").fontFamily("Playfair Display")
                .shareToken("def456uvw").shareUrl("https://evenza.com/card/def456uvw")
                .viewsCount(22).downloadsCount(5).isActive(true).createdAt(LocalDateTime.now()).build()
        );
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class VisitingCardDto {
        private Long id;
        private String templateId;
        private String cardName;
        private String primaryColor;
        private String secondaryColor;
        private String fontFamily;
        private String shareToken;
        private String shareUrl;
        private String qrCodeUrl;
        private Integer viewsCount;
        private Integer downloadsCount;
        private Boolean isActive;
        private LocalDateTime createdAt;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class GenerateCardRequest {
        private String templateId = "elegant-gold";
        private String cardName;
        private String primaryColor = "#C9973A";
        private String secondaryColor = "#6B1A2A";
        private String fontFamily = "Cormorant Garamond";
    }
}

// ============================================================
// ADMIN CONTROLLER
// ============================================================
@RestController
@RequestMapping("/api/admin")
@Slf4j
@CrossOrigin(origins = "*")
class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getAdminDashboard() {
        AdminDashboardDto stats = AdminDashboardDto.builder()
                .totalVendors(2847).pendingApprovals(23).approvedVendors(2710).rejectedVendors(114)
                .totalUsers(18420).totalBookings(12650).completedBookings(11230)
                .totalRevenue(BigDecimal.valueOf(48500000)).monthlyRevenue(BigDecimal.valueOf(4200000))
                .totalInquiries(28940).activeSubscriptions(1240)
                .premiumVendors(340).featuredVendors(89)
                .build();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard stats", stats));
    }

    @GetMapping("/vendors/pending")
    public ResponseEntity<ApiResponse<List<VendorSummaryDto>>> getPendingVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<VendorSummaryDto> vendors = new ArrayList<>();
        return ResponseEntity.ok(ApiResponse.success("Pending vendors", vendors));
    }

    @GetMapping("/analytics/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("period", days + " days");
        analytics.put("newVendors", 145);
        analytics.put("newUsers", 892);
        analytics.put("newBookings", 1240);
        analytics.put("revenue", 4200000);

        List<Map<String, Object>> dailyStats = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", LocalDateTime.now().minusDays(i).toLocalDate().toString());
            day.put("vendors", (int)(Math.random() * 10 + 2));
            day.put("bookings", (int)(Math.random() * 50 + 20));
            day.put("revenue", (int)(Math.random() * 200000 + 50000));
            dailyStats.add(day);
        }
        analytics.put("dailyStats", dailyStats);

        return ResponseEntity.ok(ApiResponse.success("Analytics data", analytics));
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueReport(
            @RequestParam(defaultValue = "monthly") String period) {
        Map<String, Object> report = new HashMap<>();
        report.put("period", period);
        report.put("totalRevenue", 48500000);
        report.put("platformCommission", 4850000);
        return ResponseEntity.ok(ApiResponse.success("Revenue report", report));
    }

    @PostMapping("/notifications/broadcast")
    public ResponseEntity<ApiResponse<String>> broadcastNotification(
            @RequestBody Map<String, String> request) {
        log.info("Broadcasting notification: {}", request.get("title"));
        return ResponseEntity.ok(ApiResponse.success("Notification broadcast sent", null));
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AdminDashboardDto {
        private Integer totalVendors;
        private Integer pendingApprovals;
        private Integer approvedVendors;
        private Integer rejectedVendors;
        private Integer totalUsers;
        private Integer totalBookings;
        private Integer completedBookings;
        private BigDecimal totalRevenue;
        private BigDecimal monthlyRevenue;
        private Integer totalInquiries;
        private Integer activeSubscriptions;
        private Integer premiumVendors;
        private Integer featuredVendors;
    }
}

// ============================================================
// INQUIRY + CATEGORIES CONTROLLERS
// ============================================================
@RestController
@RequestMapping("/api/inquiries")
@CrossOrigin(origins = "*")
class InquiryController {

    @PostMapping("/public/send")
    public ResponseEntity<ApiResponse<Map<String, Long>>> sendInquiry(
            @RequestBody InquiryRequest request) {
        Map<String, Long> response = Map.of("inquiryId", new Random().nextLong(1000, 9999));
        return ResponseEntity.ok(ApiResponse.success("Inquiry sent successfully! The vendor will respond within 24 hours.", response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyInquiries() {
        return ResponseEntity.ok(ApiResponse.success("Inquiries fetched", List.of()));
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class InquiryRequest {
        private Long vendorId;
        private String name;
        private String email;
        private String phone;
        private String eventType;
        private String eventDate;
        private String message;
        private String budget;
    }
}

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
class CategoryController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        String[][] cats = {
            {"1","Photographer","photographer","camera"},
            {"2","Makeup Artist","makeup-artist","brush"},
            {"3","Mehendi Artist","mehendi-artist","palette"},
            {"4","Caterer","caterer","utensils"},
            {"5","Decorator","decorator","star"},
            {"6","DJ","dj","music"},
            {"7","Wedding Planner","wedding-planner","calendar"},
            {"8","Venue","venue","building"},
            {"9","Jewelry","jewelry","gem"},
            {"10","Others","others","grid"}
        };
        for (String[] cat : cats) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", Long.parseLong(cat[0]));
            m.put("name", cat[1]);
            m.put("slug", cat[2]);
            m.put("icon", cat[3]);
            m.put("vendorCount", (int)(Math.random() * 300 + 50));
            categories.add(m);
        }
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categories));
    }
}
