# 📱 Product Review Mobile App

A cross-platform mobile application built with **React Native** and **Expo** for browsing products, submitting reviews, and viewing ratings.

## 🎯 Features

- **Product List** – Browse all products with sorting and category filtering
- **Product Details** – View product info, average rating, and all reviews
- **Add Review** – Submit star ratings (1–5) with optional comments
- **Cross-Platform** – Runs on iOS, Android, and web

## 🛠️ Tech Stack

- **React Native** with Expo
- **TypeScript**
- **Expo Router** (file-based routing)
- **React Navigation** (screen focus handling)

## 📁 Project Structure

```
mobile/
├── app/                    # Screens (file-based routing)
│   ├── (tabs)/             # Tab navigation
│   │   └── index.tsx       # Product list screen
│   └── product/
│       └── [id].tsx        # Product details screen
├── components/             # Reusable UI components
│   ├── add-review-modal.tsx
│   ├── product-filter-modal.tsx
│   └── ...
├── src/
│   └── api/                # API client and endpoints
│       ├── client.ts       # Fetch wrapper
│       ├── products.ts     # Product list API
│       └── productDetails.ts # Product details & reviews API
└── constants/              # Theme and config
```

## 🚀 Getting Started

### Prerequisites

- Node.js (v18+)
- npm or yarn
- Expo Go app on your phone (for testing)

### Installation

```bash
# Navigate to mobile directory
cd mobile

# Install dependencies
npm install

# Start the development server
npx expo start
```

### Running the App

After starting, you can open the app in:
- **Expo Go** – Scan the QR code with your phone
- **Android Emulator** – Press `a` in the terminal
- **iOS Simulator** – Press `i` in the terminal (macOS only)
- **Web Browser** – Press `w` in the terminal

## 🔗 API Configuration

The backend API URL is configured in `src/api/client.ts`:

```typescript
export const API_BASE_URL = "http://34.118.98.3";
```

Update this if running the backend locally or on a different server.

## 📌 Notes

- **Authentication** is not included (out of scope)
- Category filtering is done client-side
- Sorting is handled by the backend API
