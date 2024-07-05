//
// Created by mahmoud on 4/12/24.
//

#include <math.h>
#include "SpatialAudio.h"
#include "AL/alc.h"

float x = 20.0f;    // horizontal position right for positive and left for negative
float y = 20.0f;    // vertical position up for positive and down for negative
float z = 20.0f;    // depth position forward for positive and backward for negative

//// temp is just an example of one tone playing in a separate thread
//void* temp(void *arg){
//    ALuint sourceId = *(ALuint *) arg;
//    for(int i = 0; x > -20.0f; i++){
//        x -= 0.03f;
//        alSource3f(sourceId, AL_POSITION, x, 0, 0);
//        usleep(10000);
//    }
//    alSourceStop(sourceId);
//    __android_log_print(ANDROID_LOG_INFO, "OpenALPlatformInfo", "Thread finished");
//    pthread_exit(NULL);
//}

// syntheize a sine wave at the given frequency and sample rate
ALuint SpatialAudio::generateTone(float frequency) {
    int seconds = 1;
    int sampleRate = 44100;
    size_t size = seconds * sampleRate * sizeof(int16_t);
    int16_t* data = new int16_t[size];

    float inc_freq = 0.0f;
    for (int i = 0; i < size; i++) {
        // size => 1/frequendcy
        // size => wavelength
        data[i] = (int16_t)(32767.0 * sin(2 * M_PI * frequency * i / sampleRate));

//        //optional
//        frequency += inc_freq;
//        if(frequency < 100 || frequency > 5000)
//            inc_freq = -inc_freq;
    }

    ALuint sourceId;
    ALuint buffer;

    //register the buffer with OpenAL
    alGenBuffers(1, &buffer);
    // fill the buffer with the audio data
    alBufferData(buffer, AL_FORMAT_MONO16, data, size, sampleRate);

    // set hte listener position
    alListener3f(AL_POSITION, 0, 0, 0);
    alListener3f(AL_VELOCITY, 0, 0, 0);

    //register one source with OpenAL
    alGenSources(1, &sourceId);
    alSourcef(sourceId, AL_GAIN, 1);
    alSourcef(sourceId, AL_PITCH, 1);

    // attach the buffer to the source
    alSourcei(sourceId, AL_BUFFER, buffer);
    alSourcei(sourceId, AL_LOOPING, AL_TRUE);

    return sourceId;
}


void SpatialAudio::playSound(ALuint sourceId) {
    alSourcePlay(sourceId);

//    // execute this in separate thread
//    pthread_t thread;
//    pthread_create(&thread, NULL, temp, &sourceId);
}

SpatialAudio::SpatialAudio() {
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

//    ALuint sourceId = generateTone(4000);
//    playSound(sourceId);
}

