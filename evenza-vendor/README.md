# 🎊 Evenza Vendor — Premium Wedding & Event Vendor Platform

**India's most elegant marketplace for wedding and event professionals.**

Built with Spring Boot 3 + MySQL + Vanilla HTML/CSS/JS

---

## 🌟 Features

### For Customers
- 🔍 Search and filter 2,800+ verified vendors by city, category, budget, rating
- 🗂 Browse detailed vendor profiles with portfolio, reviews, pricing
- 📩 Send direct booking inquiries in seconds
- ♥ Save favourite vendors to wishlist
- ⭐ Read and write genuine, verified reviews

### For Vendors
- 📋 Multi-step registration with email OTP verification
- 📊 Powerful dashboard with KPIs, booking management, analytics
- 🖼 Drag-and-drop portfolio management with masonry gallery
- 🪪 **Digital Visiting Card Generator** — QR code, multiple premium templates, shareable link
- 💬 Inquiry and booking management system
- 🔔 Real-time notification centre
- 📈 Analytics: profile views, conversion rate, revenue tracking

### Admin Panel
- 🏛 Complete vendor approval/rejection workflow
- 📊 Platform-wide analytics and revenue dashboard
- 👥 User and vendor management
- 📣 Notification broadcast system

---

## 🗂 Project Structure

```
evenza-vendor/
├── backend/                          # Spring Boot 3 API
│   ├── src/main/java/com/evenza/vendor/
│   │   ├── config/SecurityConfig.java     # JWT auth + CORS
│   │   ├── controller/
│   │   │   ├── AuthController.java        # Register, login, OTP, reset
│   │   │   ├── VendorController.java      # Vendor CRUD + search + dashboard
│   │   │   └── OtherControllers.java      # Portfolio, VisitingCard, Admin, Inquiry, Category
│   │   ├── entity/
│   │   │   ├── Vendor.java                # Full vendor entity
│   │   │   ├── Category.java              # Category entity
│   │   │   └── Entities.java              # All other entities
│   │   └── security/JwtUtil.java          # JWT generate/validate/extract
│   ├── src/main/resources/
│   │   ├── application.yml                # Full app config
│   │   └── schema.sql                     # Complete MySQL schema + seed data
│   ├── build.gradle                       # Gradle dependencies
│   └── Dockerfile                         # Production Docker image
│
├── frontend/                         # Pure HTML/CSS/JS (no framework)
│   ├── css/main.css                  # Full design system (1,200+ lines)
│   ├── js/main.js                    # Shared utilities (API, toast, scroll, etc.)
│   └── pages/
│       ├── index.html                # Landing page with hero, categories, featured vendors
│       ├── register.html             # 4-step vendor registration with OTP
│       ├── login.html                # Split-panel login (vendor + customer tabs)
│       ├── vendors.html              # Searchable vendor listing with filters
│       ├── vendor-profile.html       # Public vendor profile with booking inquiry
│       ├── dashboard.html            # Full vendor dashboard (5 sections)
│       ├── portfolio.html            # Drag & drop portfolio manager
│       ├── visiting-card.html        # Digital visiting card generator with QR
│       └── about.html                # About, team, timeline
│
├── render.yaml                       # Render.com deployment config
└── README.md                         # This file
```

---

## 🗄 Database Schema

| Table | Purpose |
|-------|---------|
| `categories` | 10 vendor categories (Photographer, Caterer, etc.) |
| `users` | Customer + admin accounts |
| `vendors` | Vendor accounts with full profile |
| `vendor_services` | Individual service packages |
| `portfolio` | Photo/video portfolio items |
| `reviews` | Customer reviews with vendor replies |
| `bookings` | Booking requests and status tracking |
| `inquiries` | Customer inquiry messages |
| `notifications` | In-app notifications |
| `visiting_cards` | Digital card configurations |
| `otp_verifications` | OTP codes for email/phone verification |
| `subscriptions` | Vendor subscription plans |
| `wishlists` | Customer saved vendors |
| `refresh_tokens` | JWT refresh tokens |

---

## 🔑 Key API Endpoints

### Auth
```
POST /api/auth/vendor/register     — Vendor registration
POST /api/auth/vendor/login        — Login (returns JWT)
POST /api/auth/otp/send            — Send OTP
POST /api/auth/otp/verify          — Verify OTP
POST /api/auth/forgot-password     — Forgot password
POST /api/auth/reset-password      — Reset password
POST /api/auth/refresh             — Refresh JWT token
POST /api/auth/logout              — Logout
```

### Vendors (Public)
```
GET  /api/vendors/public/search    — Search with filters
GET  /api/vendors/public/{id}      — Vendor profile
GET  /api/vendors/public/featured  — Featured vendors
GET  /api/vendors/public/trending  — Trending by category
```

### Vendors (Authenticated)
```
GET  /api/vendors/me               — My profile
PUT  /api/vendors/me               — Update profile
GET  /api/vendors/me/dashboard     — Dashboard stats
POST /api/vendors/me/profile-picture — Upload photo
POST /api/vendors/me/logo          — Upload logo
```

### Portfolio
```
GET  /api/portfolio/public/vendor/{id}  — Vendor portfolio
POST /api/portfolio/me/upload           — Upload item
DELETE /api/portfolio/me/{id}           — Delete item
```

### Visiting Cards
```
GET  /api/visiting-cards/me             — My cards
POST /api/visiting-cards/me/generate    — Generate card
GET  /api/visiting-cards/public/{token} — Public card view
GET  /api/visiting-cards/me/{id}/download — Download
```

### Categories
```
GET  /api/categories                    — All categories
```

---

## 🚀 Local Setup

### Prerequisites
- Java 17+
- MySQL 8+
- Node.js (optional, for serving frontend)

### Backend

```bash
cd backend

# Configure environment variables (or edit application.yml)
export DATABASE_URL=jdbc:mysql://localhost:3306/evenza_vendor
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-super-secret-key-minimum-32-chars

# Run
./gradlew bootRun
```

API available at `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Frontend

```bash
cd frontend

# Any static server works
npx serve .
# or
python -m http.server 3000
```

Open `http://localhost:3000/pages/index.html`

---

## 🐳 Docker

```bash
cd backend
docker build -t evenza-vendor .
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:mysql://host.docker.internal:3306/evenza_vendor \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e JWT_SECRET=your-secret-key \
  evenza-vendor
```

---

## ☁️ Deploy to Render

1. Push this repo to GitHub
2. Go to [render.com](https://render.com) → New → Blueprint
3. Connect your GitHub repo
4. Render reads `render.yaml` and provisions:
   - MySQL database (starter plan)
   - Spring Boot API (web service)
   - Static frontend (free)
5. Set secrets in Render dashboard:
   - `MAIL_USERNAME` and `MAIL_PASSWORD`
   - `AWS_ACCESS_KEY` and `AWS_SECRET_KEY` (for file uploads)

---

## 🎨 Design System

| Token | Value |
|-------|-------|
| Primary (Burgundy) | `#6B1A2A` |
| Gold | `#C9973A` |
| Cream | `#F5EFE0` |
| Dark | `#2A1A10` |
| Display Font | Cormorant Garamond (Google Fonts) |
| Body Font | Jost (Google Fonts) |
| Border Radius | 12px (cards), 6px (inputs) |

---

## 🔐 Default Credentials (Dev Only)

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@evenza.com | Admin@123 |
| Demo Vendor | demo@evenza.com | Demo@123 |

> ⚠️ Change all credentials before production deployment.

---

## 📄 License

MIT © 2024 Evenza Vendor Pvt. Ltd.

---

*Built with ❤️ — Making every celebration extraordinary.*
