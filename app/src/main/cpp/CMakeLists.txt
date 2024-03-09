## For more information about using CMake with Android Studio, read the
## documentation: https://d.android.com/studio/projects/add-native-code.html.
## For more examples on how to use CMake, see https://github.com/android/ndk-samples.
#

cmake_minimum_required(VERSION 3.22.1)
project("WalkEasy")


## List of libraries to import
#set(LIBRARIES_TO_IMPORT
#        GLES_mali
#        ion_exynos
#        c++
#        )
#
## Import libraries in a loop
#foreach(LIBRARY ${LIBRARIES_TO_IMPORT})
#    add_library(${LIBRARY} SHARED IMPORTED)
##    set_target_properties(${LIBRARY} PROPERTIES IMPORTED_LOCATION /home/mahmoud/OpenCL_Android/64bit/lib${LIBRARY}.so)
#    set_target_properties(${LIBRARY} PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/lib/${ANDROID_ABI}/lib${LIBRARY}.so)
#endforeach()

add_library(${CMAKE_PROJECT_NAME} SHARED
    # List C/C++ source files with relative paths to this CMakeLists.txt.
    native-lib.cpp
)


add_library(OpenCL SHARED IMPORTED)

if (EXISTS ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libGLES_mali.so)
    set_target_properties(OpenCL PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libGLES_mali.so)
elseif (EXISTS ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libOpenCL.so)
    set_target_properties(OpenCL PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libOpenCL.so)
elseif (EXISTS ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libPVROCL.so)
    set_target_properties(OpenCL PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libPVROCL.so)
else()
    message(FATAL_ERROR "None of the required OpenCL libraries found.")
endif()



set(Root_Dir "${PROJECT_SOURCE_DIR}/../../../../")
set(OpenCV_DIR ${Root_Dir}/OpenCV/native/jni)
#set(OpenCV_DIR /home/mahmoud/OpenCV/Build/OpenCV-android-sdk/sdk/native/jni/)

# Add the OpenCV package with Java support
find_package(OpenCV 4.9 REQUIRED java)

include_directories(${CMAKE_SOURCE_DIR}/include/)
link_libraries("-w")


# Specifies libraries CMake should link to your target library. You
# can link libraries from various origins, such as libraries defined in this
# build script, prebuilt third-party libraries, or Android system libraries.
target_link_libraries(${CMAKE_PROJECT_NAME}
    # List libraries link to the target library
    android
    log
    OpenCL
    ${OpenCV_LIBRARIES}
)


#target_link_libraries(${CMAKE_PROJECT_NAME}
#        # List libraries linked to the target library
#        android
#        log
#    )
