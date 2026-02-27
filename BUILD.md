# Build Instructions

## Quick Start

### Prerequisites

1. Install Android Studio Hedgehog or newer
2. Install JDK 21
3. Install Android SDK 35
4. Install NDK 27.1.12297006

### Step-by-Step Build

#### 1. Extract Project

```bash
unzip TeristaSpace.zip
cd TeristaSpace
```

#### 2. Setup Gradle Wrapper (if needed)

The project uses Gradle 8.11.1. If the wrapper JAR is missing:

```bash
# Download wrapper JAR
curl -L -o gradle/wrapper/gradle-wrapper.jar   https://raw.githubusercontent.com/gradle/gradle/v8.11.1/gradle/wrapper/gradle-wrapper.jar
```

#### 3. Build with Gradle

```bash
# Make executable (Unix/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Clean build
./gradlew clean
```

#### 4. Build with Android Studio

1. Open Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to the `TeristaSpace` folder
4. Click "OK"
5. Wait for Gradle sync to complete
6. Build > Make Project (Ctrl+F9)

### Build Variants

The project has two build types:

- **debug** (default): Includes debug symbols, logging, test features
- **release**: Optimized, obfuscated with ProGuard

### Output Location

After successful build:

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release APK: `app/build/outputs/apk/release/app-release.apk`

### Troubleshooting

#### Issue: "Could not find gradle-wrapper.jar"

**Solution:**
```bash
# Download the wrapper manually
curl -L -o gradle/wrapper/gradle-wrapper.jar   https://services.gradle.org/distributions/gradle-8.11.1-bin.zip

# Or use system Gradle
gradle wrapper --gradle-version 8.11.1
```

#### Issue: "NDK not configured"

**Solution:**
1. Open SDK Manager in Android Studio
2. Go to "SDK Tools" tab
3. Check "NDK (Side by side)"
4. Install version 27.1.12297006
5. Or update `gradle.properties`:
   ```
   android.ndkVersion=27.1.12297006
   ```

#### Issue: "Java version mismatch"

**Solution:**
```bash
# Check Java version
java -version  # Should show 21

# Set JAVA_HOME
export JAVA_HOME=/path/to/jdk-21

# Or update gradle.properties
org.gradle.java.home=/path/to/jdk-21
```

#### Issue: "Out of memory during build"

**Solution:**
Increase heap size in `gradle.properties`:
```
org.gradle.jvmargs=-Xmx8192m -XX:MaxMetaspaceSize=512m
```

### Module Dependencies

```
app
├── bcore
│   ├── reflection
│   ├── virtual-fs
│   └── virtual-process
├── virtual-device
├── virtual-fs
├── sandbox
└── virtual-services
```

### Verification

After build, verify the APK contains all modules:

```bash
# List APK contents
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep classes

# Should show:
# classes.dex (main)
# classes2.dex (bcore)
# classes3.dex (other modules)
```

### Next Steps

1. Install APK: `adb install app/build/outputs/apk/debug/app-debug.apk`
2. Open app on device
3. Grant Device Admin permission for Work Profile
4. Start using virtualization features
