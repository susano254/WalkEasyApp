package com.susano.WalkEasy.SpatialSound;

import android.content.Context;

import com.google.vr.sdk.audio.GvrAudioEngine;

public class SpatialSound implements SpatialInterface{
//    private static final String SOUND_FILE = "audio/machine_gun.mp3";
    private static final String SOUND_FILE = "audio/HelloVR_Activation.ogg";
    public GvrAudioEngine gvrAudioEngine;
    public int sourceId;

    public SpatialSound(Context context){
        this.gvrAudioEngine = new GvrAudioEngine(context, GvrAudioEngine.RenderingMode.BINAURAL_HIGH_QUALITY);
        gvrAudioEngine.setHeadPosition(0, 0, 0);
        gvrAudioEngine.setHeadRotation(0, 0, 0, 1);

        new Thread(() -> {
            // Preload the sound file
            gvrAudioEngine.preloadSoundFile(SOUND_FILE);
            // Create the 'sourceId' sound object.
            // You can create multiple sound objects using the same sound file.
            sourceId = gvrAudioEngine.createSoundObject(SOUND_FILE);
            // Set the sound object position relative to the room model.
            gvrAudioEngine.setSoundObjectPosition(
                    sourceId, 0, 0, 0);

            // Start audio playback of the sound object sound file at the
            // cube model position. You can update the sound object position
            // whenever the cube moves during run time.
            gvrAudioEngine.playSound(sourceId, true /* looped playback */);
        }).start();
    }
    public SpatialSound(Context context, int renderingMode){
        this.gvrAudioEngine = new GvrAudioEngine(context, renderingMode);
    }

    @Override
    public void sound(float x, float y, float z) {
        gvrAudioEngine.setSoundObjectPosition(
                sourceId, x, y, z);

    }
}
