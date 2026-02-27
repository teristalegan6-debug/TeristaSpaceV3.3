cmake_minimum_required(VERSION 3.22.1)
project("terista_native")

# Set C++ standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Add native library
add_library(
    terista_native
    SHARED
    native_bridge.cpp
    system_info.cpp
    process_monitor.cpp
)

# Find Android log library
find_library(
    log-lib
    log
)

# Link libraries
target_link_libraries(
    terista_native
    ${log-lib}
    android
)

# Compiler flags
target_compile_options(terista_native PRIVATE
    -O2
    -Wall
    -Wextra
    -fPIC
)
