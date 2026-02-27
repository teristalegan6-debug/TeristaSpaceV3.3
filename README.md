# TeristaSpace - Android Virtualization Framework

## ⚠️ IMPORTANT LEGAL NOTICE

This is a **LEGITIMATE** Android virtualization framework that uses **ONLY official Android APIs**. 
It does NOT include:
- System service hooking
- Binder transaction interception  
- IMEI/Android ID spoofing
- Root exploits
- Security bypass mechanisms

Instead, it uses:
- ✅ Android Work Profile (enterprise containerization)
- ✅ VirtualDeviceManager (Android 14+ official API)
- ✅ isolatedProcess (official Android security feature)
- ✅ PackageManager APIs (legitimate app installation)

## Project Structure

```
TeristaSpace/
├── app/                          # Main application module
│   ├── src/main/
│   │   ├── java/com/terista/space/
│   │   │   ├── TeristaApplication.kt
│   │   │   ├── admin/            # Device Admin components
│   │   │   ├── data/             # Data models and repositories
│   │   │   ├── di/               # Dependency injection
│   │   │   ├── provider/         # Content providers
│   │   │   ├── service/          # Background services
│   │   │   ├── ui/               # Compose UI
│   │   │   │   ├── components/   # Reusable UI components
│   │   │   │   ├── navigation/   # Navigation setup
│   │   │   │   ├── screens/      # UI screens
│   │   │   │   └── theme/        # Material3 theme
│   │   │   └── viewmodel/        # ViewModels
│   │   ├── res/                  # Android resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── bcore/                        # Core virtualization engine
│   ├── src/main/
│   │   ├── cpp/                  # Native code (CMake)
│   │   └── java/com/terista/space/core/
│   │       ├── VirtualEngine.kt
│   │       ├── VirtualAppManager.kt
│   │       ├── engine/           # Engine state
│   │       ├── manager/          # User/Process managers
│   │       └── utils/            # Native bridge
│   └── build.gradle.kts
├── compiler/                     # Annotation processor
│   └── src/main/
│       ├── java/com/terista/space/compiler/processor/
│       │   └── ReflectionProcessor.kt
│       └── resources/META-INF/services/
├── reflection/                   # Reflection annotations
│   └── src/main/java/com/terista/space/reflection/annotation/
├── sandbox/                      # Isolated process runtime
├── virtual-device/               # VirtualDeviceManager API
├── virtual-fs/                   # Virtual file system
├── virtual-process/              # Process management
├── virtual-services/             # Service interfaces
└── proxy/                        # Service proxies
```

## Requirements

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 21
- **Android SDK**: 35
- **NDK**: 27.1.12297006
- **Gradle**: 8.11.1
- **Target Device**: Android 8.0+ (API 26), Android 14+ (API 34) for VirtualDeviceManager features

## Building

### 1. Setup

Download and extract the ZIP file, then:

```bash
cd TeristaSpace
```

### 2. Download Gradle Wrapper (if needed)

If `gradle/wrapper/gradle-wrapper.jar` is missing:

```bash
# Option 1: Use Android Studio's built-in wrapper
# Option 2: Download manually:
curl -L -o gradle/wrapper/gradle-wrapper.jar   https://raw.githubusercontent.com/gradle/gradle/v8.11.1/gradle/wrapper/gradle-wrapper.jar
```

### 3. Build

```bash
# Make gradlew executable (Unix/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Or build release
./gradlew assembleRelease
```

### 4. Install

```bash
# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Features

### 1. Virtual Apps
- Install apps into isolated environments
- Manage app lifecycle
- Separate storage spaces

### 2. Work Profile
- Enterprise-grade containerization
- Device Admin managed
- Separate work/personal data

### 3. Virtual Devices (Android 14+)
- Create companion virtual devices
- Test apps on virtual hardware
- Official Android VirtualDeviceManager API

### 4. Sandbox
- isolatedProcess API for secure execution
- No permissions in sandbox
- Separate UID and process

### 5. File System
- Isolated storage per virtual environment
- ContentProvider for file sharing
- Scoped storage compliant

## Architecture

### Core Components

| Component | Implementation | API Level |
|-----------|---------------|-----------|
| VirtualAppManager | PackageManager APIs | 26+ |
| Work Profile | DevicePolicyManager | 26+ |
| VirtualDeviceManager | VirtualDeviceManager | 34+ |
| Sandbox | isolatedProcess | 26+ |
| File System | FileProvider + Scoped Storage | 26+ |
| Reflection | JavaPoet + Annotation Processor | 26+ |

### Security Model

```
┌─────────────────────────────────────┐
│           Main App Process          │
│  (Normal permissions, user UID)     │
└─────────────┬───────────────────────┘
              │
    ┌─────────┴─────────┐
    │                   │
┌───▼────┐        ┌────▼────┐
│ Work   │        │Sandbox  │
│Profile │        │Service  │
│(Managed│        │(isolated│
│  User) │        │Process) │
└────────┘        └─────────┘
```

## Permissions

Required permissions (all official Android APIs):

- `MANAGE_USERS` - Multi-user support
- `BIND_DEVICE_ADMIN` - Work Profile management
- `COMPANION_DEVICE_MANAGER` - Virtual devices
- `WRITE_EXTERNAL_STORAGE` - File operations

## Troubleshooting

### Build Issues

1. **Gradle wrapper not found**
   - Download from: https://services.gradle.org/distributions/
   - Or use Android Studio's "Sync Project with Gradle Files"

2. **NDK not found**
   - Install NDK 27.1.12297006 via SDK Manager
   - Or update `gradle.properties` with your NDK version

3. **Java version mismatch**
   - Ensure JDK 21 is installed
   - Set `JAVA_HOME` environment variable

### Runtime Issues

1. **Work Profile creation fails**
   - Ensure device supports managed users
   - Check if already has work profile
   - Requires Device Admin enrollment

2. **VirtualDeviceManager not available**
   - Requires Android 14+ (API 34)
   - Check `PackageManager.hasSystemFeature()`

## License

Copyright 2024 TeristaSpace Contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Disclaimer

This software is provided for educational and legitimate testing purposes only.
Users are responsible for complying with all applicable laws and regulations.
The authors assume no liability for misuse of this software.
