# TagAI 📝🤖

TagAI is a modern, AI-powered smart note-taking and organization application built for Android. It leverages generative AI capabilities to dynamically process, categorize, and tag user notes in real-time, transforming unstructured text into an organized, easily searchable knowledge base.

Designed with **Senior-level engineering practices**, the project showcases a highly robust, testable, and offline-first architecture utilizing the modern **MVI (Model-View-Intent)** pattern and **Jetpack Compose**.

---

## 🚀 Features

- **AI-Powered Smart Categorization:** Automatically analyzes note content using cutting-edge Generative AI APIs (Gemini/OpenAI) to detect context and assign precise categories.
- **Dynamic Automated Tagging:** Extracts relevant keywords and context-driven tags instantly, eliminating manual tagging overhead.
- **Offline-First Design:** Full local persistence support ensures seamless note creation, browsing, and editing capabilities even without network connectivity.
- **Advanced Real-Time Search:** Instantly filters notes by dynamic tags, categories, or text queries via highly responsive UI states.
- **Modern Declarative UI:** Built entirely using Jetpack Compose with sleek animations, adaptive light/dark themes, and intuitive Unidirectional Data Flow (UDF).

---

## 🏗️ Architecture & Blueprints

TagAI is built on **Clean Architecture** principles and strictly enforces separation of concerns. It transitions from traditional MVVM to an advanced **MVI (Model-View-Intent)** architecture to maintain immutable, deterministic UI states for Jetpack Compose.

### Key Architectural Pillars:
- **Presentation Layer (MVI):** Collects user intents, processes them inside the ViewModel using state reducers, and exposes a single, immutable `UIState` stream via Kotlin Flows.
- **Domain Layer:** Contains pure business logic and use cases, ensuring the core platform remains decoupled from third-party frameworks or UI changes.
- **Data Layer (Repository Pattern):** Manages data routing between the local SQL database (Room) and remote network endpoints (Retrofit), serving as the single source of truth.

---

## 🛠️ Tech Stack & Libraries

- **Language:** [Kotlin](https://kotlinlang.org/) (100% native)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) — Modern declarative UI components.
- **Asynchronous Flow:** [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [StateFlow / SharedFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) for reactive stream management.
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) — Built on top of Dagger for clean, boilerplate-free DI.
- **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room) — SQLite abstraction layer for robust offline caching.
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) — Type-safe HTTP client with custom interceptors for secure AI API authentication.
- **AI Integration:** Generative AI Services (OpenAI / Google Gemini REST APIs).
- **Code Quality & Guardrails:** Configured with static analysis tools including **Detekt** and **Android Lint** to enforce strict clean code boundaries and formatting rules.

---

## 🔧 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Android SDK 34+
- A valid API Key from OpenAI or Google AI Studio (Gemini).

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/SenaCelikk/TagAI.git](https://github.com/SenaCelikk/TagAI.git)
   cd TagAI
