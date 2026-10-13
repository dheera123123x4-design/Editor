# MotionAI Studio

> **Turn your imagination into motion.**
> A modern, cinematic AI-powered video and photo editing application for Android built with Jetpack Compose, Material 3, Room, and Google Gemini AI.

---

## 🌟 Key Features

### 1. Multi-Track Video Editor
- **Dynamic Canvas Preview**: Real-time rendering with 9:16 (Reels/Shorts/TikTok), 16:9 (YouTube/Cinema), 1:1 (Square), and 4:5 (Social Feed) aspect ratio support.
- **Interactive Timeline**:
  - Time ruler with precision seconds markers.
  - Video clips track with split, trim, duplicate, delete, and reorder controls.
  - Synchronized audio track with soundtrack visualizer.
  - Text overlay & subtitle track.
  - Scrubbable playhead with real-time playback simulation.
- **Clip Transformations**: Rotate (90°, 180°, 270°), horizontal & vertical flip, and variable speed control (0.5x, 0.75x, 1.0x, 1.25x, 1.5x, 2.0x).
- **Real-Time Color Matrix Grading**: Live sliders for Brightness, Contrast, Saturation, and Color Temperature.
- **Stylized Shaders & Filters**: Cyberpunk Neon, Teal & Orange Cinematic, Anime Vibrant, Golden Hour Sunset, B&W Film Noir, Vintage 90s, and VHS Glitch.
- **Cinematic Transitions**: Fade, Dissolve, Slide, Zoom, Flash.
- **Text & Kinetic Titles**: Editable text overlays, custom positioning, background cards, and typography styles.
- **Soundtrack Library**: Curated royalty-free tracks (Synthwave Odyssey, Cyberpunk 2077, Cinematic Piano, Phonk Drift, Lofi Cafe).
- **Undo / Redo History**: Full snapshot stack allowing multi-level undo and redo.

### 2. Generative AI Creative Suite
- **AI Motion Transfer**: Extract motion trajectories from reference clips and synthesize dynamic pose and dance motion onto portrait photos with ethical likeness verification.
- **Anime & Art Style Stylizer**: Neural style transfer for Studio Ghibli, Cyberpunk Manga, Makoto Shinkai, and 3D Pixar aesthetics.
- **Photo-to-Video Generator**: Camera motion synthesis (Cinematic Zoom In, Orbit Arc 360, Smooth Pan, Crane Tilt) with Gemini prompt engineering.
- **Smart Background Removal**: Alpha matte extraction and instant replacement with transparent checkerboards, neon cyber gradients, or custom backdrops.
- **AI Video Enhancer**: Bidirectional optical flow simulation for 60 FPS motion smoothing, 1080p super-resolution, and HDR tone mapping.
- **Smart Auto Captions**: Generates timestamped subtitle cues from audio context using Google Gemini models.

### 3. Local Persistence & Project Management
- **Room Database Storage**: Offline-first local project storage.
- **Project Operations**: Instant project cloning, duplicate, rename, search filter, and safe deletion.
- **Ready-Made Templates**: Pre-configured cuts for Phonk beats, Cinematic slow-mo, Travel vlogs, Anime openers, Photo montages, and Social drops.

### 4. Export & Sharing Engine
- **Configurable Presets**: 720p HD and 1080p Full HD resolutions; 24 FPS (Cinematic), 30 FPS (Standard), and 60 FPS (Smooth).
- **Live Encoding Pipeline**: Real progress tracking with cancellation support.
- **Direct Output**: Saves video container directly to local app storage, with Android Share intent integration.

---

## 🛠️ Architecture & Tech Stack

- **UI Framework**: Android Jetpack Compose (Declarative M3)
- **Design System**: Dark Charcoal Studio Theme (`#0B0D12` background, elevated `#151923` panels, `#8B5CF6` violet, `#06B6D4` cyan accents)
- **Local Persistence**: Android Room Database (KSP Symbol Processing)
- **Serialization**: Moshi Kotlin (`com.squareup.moshi`)
- **Networking**: OkHttpClient & Retrofit
- **AI Integration**: Google Gemini Direct REST API (`gemini-3.5-flash`, `veo-3.1`)
- **Image Pipeline**: Coil Compose

---

## 🔑 Environment Configuration & API Keys

API keys are managed via the **Secrets panel in AI Studio** and injected into `BuildConfig` using `.env`:

```bash
# In .env (or configure via AI Studio Secrets Panel)
GEMINI_API_KEY=YOUR_GEMINI_API_KEY_HERE
```

When no API key is configured, MotionAI Studio runs in an **honest demo mode** with simulated previews, transparent provider instructions, and zero fake promises.

### Supported External AI Providers & Estimated Costs
| Provider | Task | Model | Credentials Required | Estimated Cost |
| :--- | :--- | :--- | :--- | :--- |
| **Google AI Studio** | Prompts & Auto Captions | `gemini-3.5-flash` | `GEMINI_API_KEY` | Free Tier / Standard API rate |
| **Google Cloud** | Generative Video | `veo-3.1-generate-preview` | Google Cloud Vertex AI | Per second of video generated |
| **RunwayML** | Motion Diffusion | Gen-3 Alpha | `RUNWAY_API_KEY` | ~$0.05 / credit |
| **Luma Dream Machine** | Camera Movement | Ray-2 | `LUMA_API_KEY` | ~$0.08 / generation |

---

## 🚀 Building & Packaging for Android

### Run Tests
```bash
gradle :app:testDebugUnitTest
```

### Build Debug APK
```bash
gradle assembleDebug
```
The generated APK is located at:
`app/build/outputs/apk/debug/app-debug.apk`

### Export as ZIP / GitHub
In AI Studio, export the project using the top-right settings menu:
- **Export to ZIP** to open in Android Studio.
- **Push to GitHub** for continuous integration.
