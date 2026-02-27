# ProGuard rules for TeristaSpace

# Keep Application class
-keep public class com.terista.space.TeristaApplication { *; }

# Keep ViewModels
-keep public class * extends androidx.lifecycle.ViewModel { *; }

# Keep Device Admin Receiver
-keep public class com.terista.space.admin.TeristaDeviceAdminReceiver { *; }

# Keep data models
-keep class com.terista.space.data.model.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class * extends androidx.compose.ui.node.UiApplier

# Keep Hilt
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keepclassmembers class * extends dagger.hilt.internal.GeneratedComponent {
    <init>(...);
}

# Keep Room
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
