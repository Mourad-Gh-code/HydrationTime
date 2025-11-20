# 💧 HydrationTime - Advanced Hydration Tracking App

<div align="center">

![Status](https://img.shields.io/badge/Status-Complete-success)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Language](https://img.shields.io/badge/Language-Kotlin-purple)
![Min SDK](https://img.shields.io/badge/Min%20SDK-29-blue)
![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue)

**A beautiful, feature-rich hydration tracking app with stunning animations and modern UI**

[Features](#-features) • [Screenshots](#-screenshots) • [Installation](#-installation) • [Usage](#-usage) • [Documentation](#-documentation)

</div>

---

## 🌟 Features

### 🎨 **Beautiful UI & Animations**
- ✨ **Human Body Silhouette** with animated water level visualization
- 🌊 **Horizon Stabilization** - Water stays level when phone tilts (accelerometer-based)
- 💧 **Bubble Animations** - Rising bubbles in water and progress bars
- 🌈 **Wave Effects** - Smooth water surface animations
- 🎭 **Light/Dark Themes** - Fully customizable appearance
- 🌍 **Multi-Language** - English, French, and Arabic support

### 📊 **Advanced Statistics**
- 📅 **Timeline Views** - Daily, Weekly, Monthly, and Yearly breakdowns
- 📈 **Circular Charts** - Beautiful drink distribution visualization with glow effects
- 📊 **Summary Cards** - Total, Average, Most Logged, Goal Achievement
- 🔍 **View Logged** - Historical data filtering (shows only logged periods)
- 🎯 **Goal Tracking** - Daily hydration goal monitoring

### 🚀 **Smart Features**
- 🔐 **Firebase Authentication** - Secure login and registration
- 💾 **Local Database** - Room database for offline functionality
- 🔔 **Notifications** - Customizable hydration reminders
- ⚙️ **Customizable Settings** - Theme, language, daily goals
- 🎮 **Debug Mode** - Triple-click logo to auto-fill forms (development feature)

### 🎯 **User Experience**
- 📱 **Bottom Navigation** - Easy access to all features
- 🎨 **Material Design 3** - Modern Android design guidelines
- ⚡ **Smooth Transitions** - 60fps animations throughout
- 🎪 **Expandable FAB** - Quick-add drink options with stagger animations
- 📊 **Real-time Updates** - Instant UI refresh on data changes

---

## 📱 Screenshots

### Dashboard
- Human body silhouette with water level
- Animated bubbles and waves
- Horizon stabilization effect
- Expandable FAB menu
- Progress bar with bubble animation

### Statistics
- D/W/M/Y timeline tabs
- Bar charts for consumption history
- Circular drink distribution chart
- Summary cards with key metrics
- View logged historical data

### Settings
- Theme selection (Light/Dark)
- Language selection (EN/FR/AR)
- Daily goal customization
- Notification preferences
- Account management

---

## 🛠️ Installation

### Prerequisites
- Android Studio Narwhal Feature Drop | 2025.1.2 Patch 2 or later
- Android SDK 29 or higher
- Firebase account (for authentication)

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/HydrationTime.git
   cd HydrationTime
   ```

2. **Configure Firebase**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json`
   - Place it in `app/` directory
   - Enable Email/Password authentication in Firebase Console

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**
   - Connect Android device or start emulator
   - Click Run in Android Studio

---

## 🎮 Usage

### First Time Setup
1. **Splash Screen** - Animated logo with fade effects
2. **Onboarding** - Quick introduction (first time only)
3. **Sign Up** - Create account with Firebase
4. **Dashboard** - Start tracking hydration

### Daily Usage

#### Adding Water
1. Tap the **+** button (FAB)
2. Select drink amount:
   - 🥤 Glass (200ml)
   - ☕ Cup (250ml)
   - 🍶 Bottle (500ml)
3. Watch the water level rise with animation!

#### Viewing Statistics
1. Navigate to **Statistics** tab
2. Select timeline: **D** / **W** / **M** / **Y**
3. View consumption charts and summaries
4. Tap **View Logged** for historical data

#### Customizing Settings
1. Navigate to **Settings** tab
2. Change theme (Light/Dark)
3. Select language (EN/FR/AR)
4. Set daily goal (1500-3500ml)
5. Toggle notifications

### Debug Features

#### Auto-Fill Forms (Development)
- **Location**: Login or Sign Up screen
- **Action**: Triple-click the logo (3 clicks within 500ms)
- **Result**: Forms auto-fill and submit
- **Credentials**: `testuser@hydration.com` / `password123`

---

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Authentication**: Firebase Auth
- **UI**: Material Design 3, ViewBinding
- **Async**: Kotlin Coroutines
- **Reactive**: LiveData
- **Navigation**: ViewPager2 + BottomNavigation

### Project Structure
```
app/
├── data/
│   ├── local/
│   │   ├── dao/          # Database access objects
│   │   ├── entities/     # Data models
│   │   └── database/     # Room database
│   └── repository/       # Data repositories
├── ui/
│   ├── auth/            # Login & Sign Up
│   ├── splash/          # Splash screen
│   ├── onboarding/      # First-time guide
│   ├── main/            # Main activity
│   ├── fragments/       # App fragments
│   │   ├── dashboard/   # Main tracking screen
│   │   ├── statistics/  # Data visualization
│   │   ├── tips/        # Hydration tips
│   │   └── settings/    # App settings
│   └── components/      # Custom views
│       ├── WaterBodyView.kt
│       ├── BubbleProgressBar.kt
│       └── DrinkTypeCircularChart.kt
├── utils/               # Utility classes
└── workers/             # Background tasks
```

---

## 🎨 Custom Views

### 1. WaterBodyView
**Human silhouette with animated water level**

Features:
- Water level fill animation
- Wave motion effect
- Rising bubble particles
- Horizon stabilization (tilt-responsive)
- Gradient water colors

Usage:
```kotlin
waterBodyView.setWaterLevel(0.73f) // 73% full
waterBodyView.setTilt(-15f) // Tilt left 15°
```

### 2. BubbleProgressBar
**Animated progress bar with moving bubbles**

Features:
- Gradient fill
- Animated bubbles
- Smooth transitions
- Wave motion

Usage:
```kotlin
bubbleProgressBar.setProgress(73f) // 73%
```

### 3. DrinkTypeCircularChart
**Circular ring chart with glow effects**

Features:
- Multi-segment ring
- Blur glow effects
- Animated filling
- Percentage display

Usage:
```kotlin
val data = mapOf(
    "Water" to 10f,
    "Tea" to 2f,
    "Coffee" to 4f
)
drinkTypeChart.setData(data)
```

---

## 📚 Documentation

- **[FEATURES.md](FEATURES.md)** - Complete feature list and advantages
- **[IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md)** - Technical implementation details
- **[API Documentation](docs/api.md)** - Code documentation (if available)

---

## 🎯 Key Highlights

### Unique Features
1. **Horizon Stabilization** - Water surface stays horizontal when phone tilts
2. **Bubble Animations** - Throughout the app for visual appeal
3. **Multi-Drink Tracking** - Track water, tea, coffee, juice, smoothies, milk
4. **Glow Effects** - Beautiful blur effects on charts
5. **Debug Auto-Fill** - Quick testing during development

### Performance
- ⚡ 60fps animations
- 🔋 Battery-friendly sensor usage
- 💾 Efficient database queries
- 🎨 Hardware-accelerated rendering

### Accessibility
- 🌍 3 languages (EN/FR/AR)
- 🎨 Light & Dark themes
- 📱 Responsive layouts
- ♿ Clear visual feedback

---

## 🔮 Future Enhancements

### Planned Features
- [ ] Week streak badges (Duolingo-style)
- [ ] Cloud sync with Firebase Firestore
- [ ] Wear OS companion app
- [ ] Home screen widgets
- [ ] Social sharing
- [ ] Advanced analytics
- [ ] Custom drink types
- [ ] Export data (CSV/PDF)

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- Material Design 3 guidelines
- Firebase for authentication
- MPAndroidChart library
- Android Jetpack components
- Kotlin Coroutines

---

## 📞 Support

For support, email your.email@example.com or open an issue on GitHub.

---

<div align="center">

**Made with ❤️ and lots of 💧**

⭐ Star this repo if you find it helpful!

</div>
