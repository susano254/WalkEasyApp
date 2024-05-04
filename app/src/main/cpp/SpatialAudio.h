//
// Created by mahmoud on 4/12/24.
//

#ifndef WALKEASYAPP_SPATIALAUDIO_H
#define WALKEASYAPP_SPATIALAUDIO_H


#include <android/log.h>
#include <pthread.h>
#include "include/AL/al.h"
#include "unistd.h"

class SpatialAudio {
public:


    SpatialAudio();
    ALuint generateTone(float frequency);
    void playSound(ALuint sourceId);

};


#endif //WALKEASYAPP_SPATIALAUDIO_H
