#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/asset_manager.h>
#include <CL/cl.h>
#include <opencv2/core.hpp>
#include <opencv2/core/ocl.hpp>
#include <opencv2/calib3d.hpp>
#include <unistd.h>
#include <pthread.h>
#include <AL/al.h>
#include "AL/alc.h"

using namespace cv;

float x = 20.0f;
float y = 20.0f;
float z = 20.0f;
ALuint sourceId;

void* temp(void *arg){
    for(int i = 0; i < 1000; i++){
        y -= 0.03f;
        alSource3f(sourceId, AL_POSITION, 0, y, 0);
        usleep(10000);
    }
}

// syntheize a sine wave at the given frequency and sample rate
void generateTone(int16_t* data, int size, float frequency, int sampleRate) {
    float inc_freq = 0.0f;
    for (int i = 0; i < size; i++) {
        data[i] = (int16_t)(32767.0 * sin(2 * M_PI * frequency * i / sampleRate));
        frequency += inc_freq;

        if(frequency < 100 || frequency > 5000)
            inc_freq = -inc_freq;
    }
}


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
                std::size_t platformNameSize;
                clGetPlatformInfo(platforms[i], CL_PLATFORM_NAME, 0, NULL, &platformNameSize);

                char* platformName = new char[platformNameSize];
                clGetPlatformInfo(platforms[i], CL_PLATFORM_NAME, platformNameSize, platformName, NULL);

                std::size_t vendorSize;
                clGetPlatformInfo(platforms[i], CL_PLATFORM_VENDOR, 0, NULL, &vendorSize);

                char* vendor = new char[vendorSize];
                clGetPlatformInfo(platforms[i], CL_PLATFORM_VENDOR, vendorSize, vendor, NULL);

                std::size_t versionSize;
                clGetPlatformInfo(platforms[i], CL_PLATFORM_VERSION, 0, NULL, &versionSize);

                char* version = new char[versionSize];
                clGetPlatformInfo(platforms[i], CL_PLATFORM_VERSION, versionSize, version, NULL);


                // Print information about each platform using Android logging
                __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "Platform %u:", i);
                __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "  Name: %s", platformName);
                __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "  Vendor: %s", vendor);
                __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "  Version: %s", version);

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

extern "C" JNIEXPORT jstring
Java_com_susano_WalkEasy_MainActivity_init( JNIEnv *env, jobject /* this */) {
    // Initialize OpenAL context
    ALCdevice *device = alcOpenDevice(NULL);
    if (!device) {
        __android_log_print(ANDROID_LOG_INFO, "OpenALPlatformInfo", "Failed to open default device");
    }

    ALCcontext *alContext = alcCreateContext(device, NULL);
    alcMakeContextCurrent(alContext);
    // Check for errors
    ALenum error = alGetError();
    if (error != AL_NO_ERROR) {
        __android_log_print(ANDROID_LOG_INFO, "OpenALPlatformInfo", "OpenAL error: %d", error);
    }
    else {
        __android_log_print(ANDROID_LOG_INFO, "OpenALPlatformInfo", "OpenAL initialized successfully");
    }
    ALuint buffer;
    alGenBuffers(1, &buffer);
    int seconds = 1;
    int sampleRate = 44100;
    size_t size = seconds * sampleRate * sizeof(int16_t);
    int16_t* data = new int16_t[size];
    generateTone(data, size, 4000, sampleRate);
    alBufferData(buffer, AL_FORMAT_MONO16, data, size, sampleRate);
    alListener3f(AL_POSITION, 0, 0, 0);
    alListener3f(AL_VELOCITY, 0, 0, 0);
    alGenSources(1, &sourceId);
    alSourcef(sourceId, AL_GAIN, 1);
    alSourcef(sourceId, AL_PITCH, 1);
    // set the direction to be totally on the left
    alSource3f(sourceId, AL_POSITION, 0, y, 0);
    alSource3f(sourceId, AL_VELOCITY, 0, 0, 0);
    alSourcei(sourceId, AL_BUFFER, buffer);
    alSourcei(sourceId, AL_LOOPING, AL_TRUE);
    alSourcePlay(sourceId);
    // execute this in separate thread

    pthread_t thread;
    pthread_create(&thread, NULL, temp, NULL);

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


extern "C"
JNIEXPORT void JNICALL
Java_com_susano_WalkEasy_ESP_1Cam_RenderFrame_updateImage(JNIEnv *env, jobject thiz, jlong mat_addr,
                                                          jstring path) {
    // TODO: implement updateImage()
    Mat img = *(Mat*)mat_addr;
    const char *nativeString = env->GetStringUTFChars(path, 0);
    __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "rows: %d", img.rows);
    __android_log_print(ANDROID_LOG_INFO, "OpenCLPlatformInfo", "path: %s", nativeString);

}