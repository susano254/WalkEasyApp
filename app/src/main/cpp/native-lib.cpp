#include <jni.h>
#include <string>
#include <android/log.h>
#include "CL/cl.h"
#include <opencv2/core.hpp>
#include <opencv2/core/ocl.hpp>
#include <opencv2/calib3d.hpp>

using namespace cv;

void test(){
    cl_int CL_err = CL_SUCCESS;
    cl_uint numPlatforms = 0;

    CL_err = clGetPlatformIDs( 0, NULL, &numPlatforms );

    if (CL_err == CL_SUCCESS) {
        __android_log_print(ANDROID_LOG_INFO, "YourTag", "%u platform(s) found", numPlatforms);
        if (numPlatforms > 0) {
            cl_platform_id* platforms = new cl_platform_id[numPlatforms];
            clGetPlatformIDs(numPlatforms, platforms, NULL);

            for (cl_uint i = 0; i < numPlatforms; ++i) {
                size_t platformNameSize;
                clGetPlatformInfo(platforms[i], CL_PLATFORM_NAME, 0, NULL, &platformNameSize);

                char* platformName = new char[platformNameSize];
                clGetPlatformInfo(platforms[i], CL_PLATFORM_NAME, platformNameSize, platformName, NULL);

                size_t vendorSize;
                clGetPlatformInfo(platforms[i], CL_PLATFORM_VENDOR, 0, NULL, &vendorSize);

                char* vendor = new char[vendorSize];
                clGetPlatformInfo(platforms[i], CL_PLATFORM_VENDOR, vendorSize, vendor, NULL);

                // Print information about each platform using Android logging
                __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "Platform %u:", i);
                __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "  Name: %s", platformName);
                __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "  Vendor: %s", vendor);

                cl_uint numDevices = 0;
                clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, 0, NULL, &numDevices);

                if (numDevices > 0) {
                    cl_device_id* devices = new cl_device_id[numDevices];
                    clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, numDevices, devices, NULL);

                    for (cl_uint j = 0; j < numDevices; ++j) {
                        cl_device_type deviceType;
                        int computeUnits;
                        cl_uint memSize, clock;
                        clGetDeviceInfo(devices[j], CL_DEVICE_TYPE, sizeof(deviceType), &deviceType, NULL);
                        clGetDeviceInfo(devices[j], CL_DEVICE_MAX_COMPUTE_UNITS, sizeof(computeUnits), &computeUnits, NULL);
                        clGetDeviceInfo(devices[j], CL_DEVICE_GLOBAL_MEM_SIZE, sizeof(memSize), &memSize, NULL);
                        clGetDeviceInfo(devices[j], CL_DEVICE_MAX_CLOCK_FREQUENCY, sizeof(clock), &clock, NULL);

                        const char* deviceTypeName = (deviceType == CL_DEVICE_TYPE_CPU) ? "CPU" :
                                                     (deviceType == CL_DEVICE_TYPE_GPU) ? "GPU" :
                                                     "Other";

                        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "    Device %u:", j);
                        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "    Type: %s", deviceTypeName);
                        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "    clock: %u", clock);
                        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "    computeUnits: %d", computeUnits);
                        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "    memSize: %u", memSize);
                    }

                    delete[] devices;
                }

                delete[] platformName;
                delete[] vendor;
            }

            delete[] platforms;
        }
    }
    else
        __android_log_print(ANDROID_LOG_ERROR, "YourTag", "clGetPlatformIDs(%i)", CL_err);
}

//void firstKernel(){
//    clGetDeviceIDs()
//
//}

extern "C" JNIEXPORT jstring
JNICALL
Java_com_susano_WalkEasy_MainActivity_stringFromJNI( JNIEnv *env, jobject /* this */) {
    test();
    if(ocl::haveOpenCL() && ocl::useOpenCL() ){
        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "OpenCV Loaded with OpenCL support Successfully");
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "OpenCV-OpenCL support failed");
    }
    cv::ocl::Context context;
    if (!context.create(cv::ocl::Device::TYPE_GPU)) {
        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "Failed creating the context...");
    }
    __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", " GPU devices detected: %zu", context.ndevices());

    for (int i = 0; i < context.ndevices(); i++) {
        ocl::Device device = context.device(i);
        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "name: %s", device.name().c_str());
        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "available: %d", device.available());
        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "imageSupport: %d", device.imageSupport());
        __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "OpenCL_C_Version:  %s",  device.OpenCL_C_Version().c_str());
    }
    Ptr<StereoBM> bm = StereoBM::create(16, 9);
    __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "block size: %d",  bm->getBlockSize());
    bm->setBlockSize(21);
    __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "block size: %d",  bm->getBlockSize());

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

