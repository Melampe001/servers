# Predictive Analytics Android App

A modular Android application for data visualization and predictive analysis with built-in security features.

## 📋 Features

### ✅ Implemented Features

- **Modular Architecture**: Clean separation into `/ui`, `/logic`, `/data`, `/utils`, and `/visuals` packages
- **Data Visualization**: Animated histogram with dynamic gradients and auto-scaling Y-axis
- **Statistical Analysis**: Mean, median, standard deviation, and trend detection
- **Security**: AES-256-GCM encryption using Android Keystore
- **Local Storage**: Room database for persistent data storage
- **WebSocket Support**: Optional client with automatic reconnection
- **Unit Tests**: Tests for statistical analysis and encryption logic

## 🏗️ Project Structure

```
android-app/
├── app/
│   ├── build.gradle.kts          # App-level dependencies
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/predictiveapp/
│       │   │   ├── ui/           # User interface components
│       │   │   │   ├── MainActivity.kt
│       │   │   │   └── MainViewModel.kt
│       │   │   ├── logic/        # Business logic
│       │   │   │   └── StatisticalAnalyzer.kt
│       │   │   ├── data/         # Data layer
│       │   │   │   ├── AppDatabase.kt
│       │   │   │   └── DataRepository.kt
│       │   │   ├── utils/        # Utilities
│       │   │   │   ├── EncryptionManager.kt
│       │   │   │   └── WebSocketClient.kt
│       │   │   └── visuals/      # Visualization components
│       │   │       └── HistogramView.kt
│       │   └── res/              # Resources
│       └── test/                 # Unit tests
├── build.gradle.kts              # Project-level config
└── settings.gradle.kts           # Project settings
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or later
- Android SDK 34
- Minimum SDK: API 24 (Android 7.0)

### Installation

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd servers/android-app
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the `android-app` directory

3. **Sync Gradle**:
   - Android Studio will automatically sync Gradle files
   - Wait for dependencies to download

4. **Run the app**:
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10

### VS Code Setup with GitHub Codespaces

1. **Open in Codespaces**:
   - Click "Code" → "Open with Codespaces" on GitHub
   - Select or create a new Codespace

2. **Install Android SDK** (if not available):
   ```bash
   sudo apt-get update
   sudo apt-get install -y android-sdk
   ```

3. **Build the project**:
   ```bash
   cd android-app
   ./gradlew build
   ```

## 📊 Module Documentation

### 1. UI Module (`/ui`)

**MainActivity.kt**: Main application screen using Jetpack Compose
```kotlin
// Display histogram and analysis results
@Composable
fun MainScreen(viewModel: MainViewModel)
```

**MainViewModel.kt**: Manages UI state and business logic coordination
```kotlin
// Generate sample data
viewModel.generateSampleData()

// Perform analysis
viewModel.analyzeData()
```

### 2. Logic Module (`/logic`)

**StatisticalAnalyzer.kt**: Core analytical algorithms

```kotlin
val analyzer = StatisticalAnalyzer()

// Perform comprehensive analysis
val result = analyzer.analyze(data)
// Returns: AnalysisResult(mean, median, stdDev, trend)

// Calculate moving average
val smoothed = analyzer.calculateMovingAverage(data, windowSize = 5)
```

**Analysis Features**:
- Mean (arithmetic average)
- Median (middle value)
- Standard deviation (data spread)
- Trend detection (Upward/Downward/Stable)
- Moving average calculation

### 3. Data Module (`/data`)

**AppDatabase.kt**: Room database configuration
```kotlin
@Database(entities = [AnalysisEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase()
```

**DataRepository.kt**: Data persistence with encryption
```kotlin
val repository = DataRepository()

// Save encrypted results
repository.saveResults(data, results)

// Export to encrypted JSON
val encrypted = repository.exportToJson(data)

// Import from encrypted JSON
val data = repository.importFromJson(encrypted)
```

### 4. Utils Module (`/utils`)

**EncryptionManager.kt**: AES-256-GCM encryption

```kotlin
val encryption = EncryptionManager()

// Encrypt data
val encrypted = encryption.encryptString("sensitive data")

// Decrypt data
val decrypted = encryption.decryptString(encrypted)
```

**Features**:
- AES-256-GCM encryption
- Android Keystore integration
- Automatic key generation
- Secure key storage

**WebSocketClient.kt**: Optional network synchronization

```kotlin
val client = WebSocketClient("wss://example.com/sync")

// Connect to server
client.connect()

// Send message
client.send("{ \"type\": \"sync\", \"data\": [...] }")

// Receive messages
client.messageChannel.receive()

// Disconnect
client.disconnect()
```

**Features**:
- Automatic reconnection with exponential backoff
- Event-based message handling
- Error management

### 5. Visuals Module (`/visuals`)

**HistogramView.kt**: Animated data visualization

```kotlin
@Composable
fun HistogramView(data: List<Double>)
```

**Features**:
- Animated bar transitions
- Dynamic gradient coloring based on value intensity
- Automatic Y-axis scaling
- Staggered animation for visual appeal

## 🧪 Testing

### Run Unit Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests StatisticalAnalyzerTest

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport
```

### Test Structure

- **StatisticalAnalyzerTest.kt**: Tests for analytical algorithms
  - Empty data handling
  - Mean/median calculations
  - Moving average
  - Trend detection

- **EncryptionManagerTest.kt**: Tests for encryption (requires Android framework)

### Performance Testing

Generate large datasets for testing:

```kotlin
// In MainActivity or ViewModel
val largeDataset = List(10000) { Random.nextDouble(0.0, 100.0) }
viewModel.performanceTest(largeDataset)
```

## 📝 Example Usage

### Basic Data Analysis

```kotlin
// 1. Generate or load data
val data = listOf(10.5, 20.3, 15.7, 30.2, 25.8)

// 2. Analyze data
val analyzer = StatisticalAnalyzer()
val result = analyzer.analyze(data)

// 3. Access results
println("Mean: ${result.mean}")
println("Median: ${result.median}")
println("Std Dev: ${result.stdDev}")
println("Trend: ${result.trend}")
```

### Data Encryption

```kotlin
// 1. Create encryption manager
val encryption = EncryptionManager()

// 2. Encrypt sensitive data
val jsonData = """{"values": [1, 2, 3, 4, 5]}"""
val encrypted = encryption.encryptString(jsonData)

// 3. Store encrypted data
// Save to file or database

// 4. Decrypt when needed
val decrypted = encryption.decryptString(encrypted)
```

### JSON Import/Export

```json
{
  "exported_at": 1704067200000,
  "data": [10.5, 20.3, 15.7, 30.2, 25.8]
}
```

```kotlin
// Export
val repository = DataRepository()
val encrypted = repository.exportToJson(data)
// Save encrypted to file

// Import
val data = repository.importFromJson(encryptedFileContent)
```

## 🔐 Security Features

### Encryption Details

- **Algorithm**: AES-256-GCM (Galois/Counter Mode)
- **Key Storage**: Android Keystore System
- **Key Size**: 256 bits
- **GCM Tag Length**: 128 bits
- **IV Size**: 12 bytes (96 bits)

### Security Best Practices

1. **Never hardcode encryption keys** - Uses Android Keystore
2. **Encrypt before storage** - All saved data is encrypted
3. **Secure key generation** - Hardware-backed when available
4. **Proper IV handling** - Unique IV for each encryption operation

## 📈 Performance Considerations

- **Large Datasets**: Tested with up to 10,000 data points
- **Animation**: Uses Compose animation APIs for smooth transitions
- **Database**: Room with coroutines for non-blocking I/O
- **Memory**: Efficient data structures and streaming where possible

## 🐛 Troubleshooting

### Build Issues

**Problem**: Gradle sync fails
```bash
# Solution: Clean and rebuild
./gradlew clean
./gradlew build
```

**Problem**: Dependency resolution errors
```bash
# Solution: Clear Gradle cache
rm -rf ~/.gradle/caches/
./gradlew build --refresh-dependencies
```

### Runtime Issues

**Problem**: Encryption fails on emulator
- **Cause**: Android Keystore not available
- **Solution**: Use a real device or emulator with API 24+

**Problem**: Database not persisting
- **Cause**: Database not properly initialized
- **Solution**: Check Room database instantiation in Application class

## 🔄 Future Enhancements

- [ ] PDF export functionality
- [ ] Advanced regression models (polynomial, exponential)
- [ ] Real-time data streaming
- [ ] Cloud synchronization
- [ ] Multi-device support
- [ ] Custom visualization themes
- [ ] Data sharing capabilities

## 📄 License

This project is part of the MCP servers repository.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📞 Support

For issues and questions:
- Open an issue on GitHub
- Check existing documentation
- Review test cases for examples
