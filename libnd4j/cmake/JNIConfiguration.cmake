# JNIConfiguration.cmake - Locates the JDK/JRE JNI headers and the JVM library
#
# This file is included unconditionally from libnd4j/CMakeLists.txt so that
# JVM_LIBRARY is available for linking (see MainBuildFlow.cmake and
# BuildCPU.cmake). It was part of the upstream CMake modernisation but the
# file itself was never committed, which broke any from-source build. It is
# restored here as part of the RISC-V build support patch.
#
# Provides:
#   JNI_INCLUDE_DIRS  - list of include directories containing jni.h/jni_md.h
#   JVM_LIBRARY       - path to the JVM shared library (libjvm.so), if found
#   EXTERNAL_INCLUDE_DIRS is extended with JNI_INCLUDE_DIRS

# Resolve JAVA_HOME from the environment first, then fall back to well-known
# system locations so that plain cmake runs (outside of mvn/bns.sh) also work.
if(NOT JAVA_HOME)
    if(DEFINED ENV{JAVA_HOME} AND NOT "$ENV{JAVA_HOME}" STREQUAL "")
        set(JAVA_HOME "$ENV{JAVA_HOME}")
    elseif(EXISTS "/usr/lib/jvm/default-java")
        set(JAVA_HOME "/usr/lib/jvm/default-java")
    elseif(EXISTS "/usr/lib/jvm/java-11-openjdk")
        set(JAVA_HOME "/usr/lib/jvm/java-11-openjdk")
    elseif(EXISTS "/usr/lib/jvm/java-17-openjdk")
        set(JAVA_HOME "/usr/lib/jvm/java-17-openjdk")
    elseif(EXISTS "/usr/lib/jvm/java-21-openjdk")
        set(JAVA_HOME "/usr/lib/jvm/java-21-openjdk")
    elseif(EXISTS "/usr/lib/jvm/java-1.8.0-openjdk")
        set(JAVA_HOME "/usr/lib/jvm/java-1.8.0-openjdk")
    endif()
endif()

if(JAVA_HOME)
    # jni.h lives directly under ${JAVA_HOME}/include; the platform-specific
    # header (jni_md.h) lives in a sub-directory named after the platform.
    list(APPEND JNI_INCLUDE_DIRS "${JAVA_HOME}/include")
    if(EXISTS "${JAVA_HOME}/include/linux")
        list(APPEND JNI_INCLUDE_DIRS "${JAVA_HOME}/include/linux")
    elseif(EXISTS "${JAVA_HOME}/include/darwin")
        list(APPEND JNI_INCLUDE_DIRS "${JAVA_HOME}/include/darwin")
    elseif(EXISTS "${JAVA_HOME}/include/win32")
        list(APPEND JNI_INCLUDE_DIRS "${JAVA_HOME}/include/win32")
    endif()

    # The JVM shared library is used only when linking directly against it;
    # when it is not found (e.g. JRE-only install) the variable stays empty
    # and target_link_libraries simply ignores it.
    find_library(JVM_LIBRARY
        NAMES jvm
        PATHS
            "${JAVA_HOME}/lib/server"
            "${JAVA_HOME}/lib/client"
            "${JAVA_HOME}/jre/lib/server"
            "${JAVA_HOME}/jre/lib/client"
            "${JAVA_HOME}/jre/lib/amd64/server"
            "${JAVA_HOME}/jre/lib/riscv64/server"
        NO_DEFAULT_PATH)
endif()

if(JNI_INCLUDE_DIRS)
    list(APPEND EXTERNAL_INCLUDE_DIRS ${JNI_INCLUDE_DIRS})
endif()

message(STATUS "JAVA_HOME:        ${JAVA_HOME}")
message(STATUS "JNI_INCLUDE_DIRS: ${JNI_INCLUDE_DIRS}")
message(STATUS "JVM_LIBRARY:      ${JVM_LIBRARY}")
