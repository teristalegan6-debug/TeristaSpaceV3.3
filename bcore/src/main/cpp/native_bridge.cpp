#include <jni.h>
#include <android/log.h>
#include <string>
#include <unistd.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define LOG_TAG "TeristaNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_terista_space_core_utils_NativeBridge_nativeInit(JNIEnv *env, jobject thiz) {
    LOGI("Native bridge initialized");
    return JNI_TRUE;
}

JNIEXPORT jstring JNICALL
Java_com_terista_space_core_utils_NativeBridge_nativeGetSystemInfo(JNIEnv *env, jobject thiz) {
    std::string info = "Architecture: " + std::string(getenv("HOSTTYPE") ?: "unknown");
    info += "; PID: " + std::to_string(getpid());
    info += "; UID: " + std::to_string(getuid());
    return env->NewStringUTF(info.c_str());
}

JNIEXPORT jint JNICALL
Java_com_terista_space_core_utils_NativeBridge_nativeGetProcessStatus(JNIEnv *env, jobject thiz, jint pid) {
    char path[256];
    snprintf(path, sizeof(path), "/proc/%d/stat", pid);

    int fd = open(path, O_RDONLY);
    if (fd < 0) {
        return -1; // Process not found
    }
    close(fd);
    return 0; // Process exists
}

JNIEXPORT jlong JNICALL
Java_com_terista_space_core_utils_NativeBridge_nativeGetMemoryUsage(JNIEnv *env, jobject thiz) {
    struct rusage usage;
    if (getrusage(RUSAGE_SELF, &usage) == 0) {
        return usage.ru_maxrss; // Maximum resident set size in KB
    }
    return -1;
}

JNIEXPORT jdouble JNICALL
Java_com_terista_space_core_utils_NativeBridge_nativeGetCpuUsage(JNIEnv *env, jobject thiz) {
    // Simplified CPU usage calculation
    struct rusage usage;
    if (getrusage(RUSAGE_SELF, &usage) == 0) {
        long userTime = usage.ru_utime.tv_sec * 1000000 + usage.ru_utime.tv_usec;
        long sysTime = usage.ru_stime.tv_sec * 1000000 + usage.ru_stime.tv_usec;
        return (userTime + sysTime) / 1000000.0; // Return as seconds
    }
    return -1.0;
}

} // extern "C"
