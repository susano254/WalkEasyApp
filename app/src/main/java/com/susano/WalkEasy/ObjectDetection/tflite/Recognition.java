package com.susano.WalkEasy.ObjectDetection.tflite;

import android.graphics.RectF;

public class Recognition {
    private final String id;
    private final String title;
    private final Float confidence;
    private RectF location;
    private long timeExecution;

    public Recognition(final String id, final String title, final Float confidence, final RectF location, final long timeEx) {
        this.id = id;
        this.title = title;
        this.confidence = confidence;
        this.location = location;
        this.timeExecution = timeEx;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Float getConfidence() {
        return confidence;
    }

    public RectF getLocation() {
        return new RectF(location);
    }

    public void setLocation(RectF location) {
        this.location = location;
    }

    public long getTimeExecution(){
        return this.timeExecution;
    }

    @Override
    public String toString() {
        String resultString = "";
        if (id != null) {
            resultString += "[" + id + "] ";
        }

        if (title != null) {
            resultString += title + " ";
        }

        if (confidence != null) {
            resultString += String.format("(%.1f%%) ", confidence * 100.0f);
        }

        if (location != null) {
            resultString += location + " ";
        }

        resultString += "in " + timeExecution + " ms";

        return resultString.trim();
    }
}
