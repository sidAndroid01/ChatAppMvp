# ChatApp - Offline-First Messaging Application

## Images 
<img width="1080" height="2408" alt="Screenshot_20251231_211710" src="https://github.com/user-attachments/assets/97703163-e3d6-4dac-b0b5-7a8e895e8efe" />
<img width="1080" height="2408" alt="Screenshot_20251231_211748" src="https://github.com/user-attachments/assets/15bd4f3b-86ec-4e43-abaf-85fcf18a08be" />

## Overview

This is a fully functional offline-first chat application built with modern Android development practices. The app demonstrates a robust implementation of local data persistence, reactive UI patterns, and clean architecture principles. It creates a messaging experience that works seamlessly without any network connection, making it perfect for understanding how to build resilient mobile applications that don't depend on server connectivity.

The application allows users to have conversations with an AI agent simulator. All data is stored locally using Room Database, ensuring that chats persist across app restarts and work flawlessly even in airplane mode. The UI is built entirely with Jetpack Compose, providing a modern and declarative approach to building Android interfaces.

## Architecture and Design Decisions

### MVVM Architecture Pattern

The application follows the Model-View-ViewModel (MVVM) architecture pattern, which provides clear separation of concerns and makes the codebase highly maintainable and testable. Here's how each layer works and why this pattern was chosen:

The **Model layer** consists of the data models and the Room Database. This layer represents the actual data structure of our application. We have two main entities: Chat and Message. These entities are defined as data classes with Room annotations, which allows Room to automatically generate the necessary SQL code for creating tables and performing database operations. This approach eliminates boilerplate code and reduces the chance of SQL errors.

The **Repository layer** acts as a single source of truth for all data operations. Rather than having ViewModels directly access the database, they go through the repository. This abstraction provides several benefits. First, it makes testing easier because we can mock the repository in our tests. Second, it centralizes data logic, so if we later wanted to add a remote API alongside our local database, we'd only need to modify the repository without touching the ViewModels or UI. Third, it provides a clean API for data operations that makes sense from a business logic perspective.

The **ViewModel layer** holds the UI state and handles user interactions. ViewModels survive configuration changes like screen rotation, which means users don't lose their place in a conversation when rotating their device. ViewModels use Kotlin Flows to expose data reactively, meaning the UI automatically updates whenever the underlying data changes. This eliminates the need for manual refresh logic and creates a smooth user experience.

The **View layer** is built entirely with Jetpack Compose. Each screen is a composable function that observes state from its ViewModel and recomposes automatically when that state changes. This declarative approach makes UI code much more readable and maintainable compared to traditional View-based Android development.

### Room Database for Local Persistence

Room was chosen as the persistence solution because it provides type-safe database access with compile-time verification of SQL queries. Unlike using SQLite directly, Room catches errors at compile time rather than runtime, which makes development faster and reduces bugs in production.

The database schema consists of two tables with a foreign key relationship. The Chat table stores conversation metadata like title and last message timestamp. The Message table stores individual messages with a foreign key reference to their parent chat. This relationship is configured with CASCADE delete, which means when a chat is deleted, all its messages are automatically removed as well. This ensures data consistency without requiring manual cleanup code.

Room's integration with Kotlin Coroutines and Flow makes asynchronous database operations straightforward. All database operations that modify data are suspend functions, ensuring they don't block the main thread. Query results are exposed as Flows, which emit new values whenever the underlying data changes, creating a reactive data pipeline from database to UI.

### Dependency Management and Injection

The application uses Dagger hilt dependency injection approach. My choice of Dagger hilt over Dagger2 was due to Dagger2 being too verbose while Hilt taking care of monotonous component classes code makes it eacy to manage the dependencies.

The ChatApplication class creates singleton instances of the database, repository, and AI agent simulator. These instances are created lazily, meaning they're only initialized when first accessed. The MainActivity retrieves these dependencies from the application instance and creates ViewModelFactories that inject the dependencies into ViewModels.

### Reactive State Management with Kotlin Flow

The application extensively uses Kotlin Flow for reactive state management. Flow is a cold stream that emits values over time, similar to RxJava or LiveData but with better integration into Kotlin's coroutines system.

ViewModels expose StateFlow instances to the UI. StateFlow is a special kind of Flow that always has a current value and caches the most recent value for new collectors. When a composable collects from a StateFlow using collectAsState(), it automatically recomposes whenever the StateFlow emits a new value. This creates a reactive UI where data changes propagate automatically from the database through the repository and ViewModel to the UI.

The Repository layer uses Flow extensively to provide real-time updates. For example, when you call `getMessagesForChat()`, it returns a Flow that emits a new list of messages whenever any message in that chat is added, updated, or deleted. This means the UI stays in sync with the database without any manual refresh logic.

### AI Agent Simulation Logic

The AI agent simulator provides realistic conversational behavior through a simple but effective algorithm. Rather than replying to every user message (which would feel robotic), the agent uses a threshold-based approach. It counts user messages and replies every four to five messages, with randomization to create natural variation.

When the agent decides to reply, it adds a realistic delay of one to two seconds to simulate "thinking time." This small detail makes the interaction feel more natural and less instantaneous. The agent randomly chooses between sending a text response (70% probability) or an image (30% probability), which adds variety to the conversation.

This simulation logic runs asynchronously using Kotlin coroutines, ensuring it doesn't block the UI thread. The delay and response generation happen in the background, and when complete, the response is inserted into the database, which automatically triggers a UI update through the reactive Flow pipeline.

### Image Handling and Storage

The application supports image attachments with careful attention to persistence and performance. When a user selects an image from the gallery, the app copies it to internal storage rather than just storing a reference to the original URI. This approach ensures the image remains accessible even if the original file is deleted or moved.

For optimal performance, the data model includes support for thumbnails. While the current implementation uses the same image for both the full-size and thumbnail versions (for simplicity), the architecture is designed to support proper thumbnail generation. In a production app, you would generate a smaller thumbnail image to improve loading performance in the message list.

Images are loaded using the Coil library, which handles image loading, caching, and display efficiently. Coil integrates seamlessly with Compose through its `rememberAsyncImagePainter` function, providing smooth image loading with automatic error handling.

### Seed Data for Demonstration

The application includes seed data that demonstrates its features immediately without requiring user setup. This seed data consists of three conversations with realistic message histories, including both text messages and image attachments.

The seed data is inserted automatically when the app first launches by the ChatApplication class. It checks if the database is empty and, if so, inserts the predefined chats and messages. This approach provides a better first-run experience and makes it easier to demonstrate the app's capabilities.

## Key Features Implemented

### Chat List Screen

The chat list screen displays all conversations sorted by the most recent message timestamp. Each chat shows its title, a preview of the last message (truncated to avoid very long previews), and a smart timestamp that adapts based on how long ago the message was sent. Recent messages show relative times like "5m ago" or "2h ago," while older messages show dates like "Yesterday" or "Dec 20."

The screen includes swipe-to-delete functionality implemented with Material 3's SwipeToDismissBox. When a user swipes a chat, a confirmation dialog appears to prevent accidental deletions. This two-step deletion process protects users from losing important conversations.

A floating action button allows users to create new chats. When tapped, it creates a new empty chat and immediately navigates to the chat detail screen where the user can start messaging.

The screen gracefully handles empty states, showing an encouraging message when no chats exist. This provides clear guidance to new users about how to get started with the app.

### Chat Detail Screen

The chat detail screen is the heart of the application, providing the main messaging interface. Messages are displayed in a scrollable list with automatic scrolling to the latest message when new messages arrive. This scroll behavior uses LaunchedEffect to respond to changes in the message count, ensuring users always see new messages without manual scrolling.

User and agent messages are styled differently to clearly distinguish who sent each message. User messages appear on the right with a blue background (using the primary container color), while agent messages appear on the left with a gray background (using the surface variant color). This visual distinction is a standard pattern in messaging apps that users immediately understand.

The message input bar stays at the bottom of the screen and adjusts automatically when the keyboard appears. This is achieved using the `imePadding` modifier, which ensures the input field is never hidden behind the keyboard. The send button is disabled when the text field is empty, providing clear visual feedback about when sending is possible.

### Image Support and Fullscreen Viewer

Users can attach images to messages using the attachment button in the input bar. This opens a dialog offering two options: choosing from the gallery or taking a photo with the camera. The gallery option is fully functional, using ActivityResultContracts to request images from the system picker.

When viewing messages with images, users can tap any image to open a fullscreen viewer. This viewer implements pinch-to-zoom functionality using gesture detection, allowing users to zoom in for details. The zoom implementation maintains the aspect ratio and allows panning when zoomed in. A close button and zoom percentage indicator provide clear interaction cues.

### Smart Timestamp Formatting

The application includes sophisticated timestamp formatting that adapts based on context and how much time has passed. In the chat list, timestamps are concise to save space, showing relative times for recent messages and dates for older ones. In the message view, timestamps are more detailed, including the time of day to provide better context within conversations.

This formatting is centralized in the TimeFormatter utility class, making it easy to maintain consistency across the app and update the formatting logic if needed.

### Title Editing

Chat titles can be edited by tapping the title in the app bar. This switches the title to an editable text field with save and cancel buttons. The title automatically generates from the first user message when a chat is created, but users can customize it to something more memorable.

## Project Structure

The project is organized into logical packages that reflect the MVVM architecture. The `data` package contains everything related to data management, including model classes, Room DAOs, the database class, and the repository. This keeps all data-related code together and separate from UI concerns.

The `ui` package is organized by screen, with each screen having its own package containing the composable UI and ViewModel. This structure makes it easy to find all the code related to a specific screen. The shared ViewModelFactory classes are at the root of the ui package since they're used by multiple screens.

The `utils` package contains utility classes that provide services used throughout the app, such as timestamp formatting, file size formatting, the AI agent simulator, and seed data provider. These utilities are designed to be reusable and have no dependencies on Android framework classes when possible.

The `navigation` package contains the navigation graph that defines how screens connect to each other. Keeping navigation separate from individual screens makes it easier to understand and modify the app's navigation structure.

## Setup Instructions

To run this application, you will need Android Studio Hedgehog (2023.1.1) or newer with Kotlin plugin version 1.9.0 or higher. The minimum Android SDK version is 24 (Android 7.0), and the target SDK is 34 (Android 14).

First, clone or download the project to your local machine. Open Android Studio and select "Open an Existing Project," then navigate to the ChatApp directory and open it. Android Studio will automatically detect that this is a Gradle project and begin syncing the dependencies.

The first sync may take several minutes as Gradle downloads all the required dependencies, including Jetpack Compose libraries, Room Database components, and Coil for image loading. Once sync completes successfully, you can build and run the app.

To run on an emulator, create an Android Virtual Device (AVD) through Android Studio's Device Manager. Choose any device with API level 24 or higher. For the best experience, use a device with API level 31 or higher to see Material You dynamic colors in action.

To run on a physical device, enable Developer Options on your Android phone and turn on USB Debugging. Connect your device to your computer via USB, and it should appear in Android Studio's device selector.

## Testing Recommendations

For testing this application, you should focus on three main areas. First, test the Repository and DAOs using instrumented tests that run on a device or emulator. Room provides in-memory database support that makes these tests fast and reliable. Second, test ViewModels using unit tests with mock repositories. This allows you to test business logic without needing a real database. Third, test composables using Compose Testing to verify UI behavior and user interactions.

## Future Enhancements

While this application provides a solid foundation, there are several directions for enhancement. You could add search functionality to find specific messages or chats. Message editing and deletion would give users more control over their conversations. Push notifications could alert users to new messages when the app is in the background. User profiles with avatars would make the agent feel more personal. Chat grouping or folders could help organize many conversations. Export and import functionality would allow users to backup their data or move it between devices.

## Conclusion

This application demonstrates modern Android development best practices including MVVM architecture, Room Database for persistence, Jetpack Compose for UI, Kotlin Coroutines and Flow for asynchronous operations, and Material 3 design guidelines. The offline-first approach ensures reliability and performance, while the reactive architecture creates a smooth user experience with automatic UI updates. The clear separation of concerns makes the codebase maintainable and testable, setting a solid foundation for future enhancements.
