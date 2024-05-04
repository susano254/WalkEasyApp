package com.susano.WalkEasy.ObjectDetection.tflite;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TFLiteYoloV5 {

    private Interpreter interpreter;
    private List<String> labels;
    private int INPUT_SIZE;
    private int NUM_THREADS = 4;
    private int IMAGE_MEAN = 0;
    private float IMAGE_STD = 255.0f;
    private boolean isModelQuantized = false;

    private GpuDelegate gpuDelegate;
    private int height = 0;
    private int width = 0;

    public TFLiteYoloV5(AssetManager assetManager, String modelPath, String labelPath, int inputSize, boolean isModelQuantized) throws Exception {
        this.isModelQuantized = isModelQuantized;
        INPUT_SIZE = inputSize;
        Interpreter.Options options = new Interpreter.Options();
        gpuDelegate = new GpuDelegate();
        options.addDelegate(gpuDelegate);
//        options.setUseNNAPI(true);
        options.setNumThreads(NUM_THREADS);

        interpreter = new Interpreter(loadModelFile(assetManager, modelPath), options);
        labels = loadLabelList(assetManager, labelPath);
    }

    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
       List<String> labelList = new ArrayList<>();
        InputStream labelsInput = assetManager.open(labelPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
        String line;
        while ((line = br.readLine()) != null) {
            labelList.add(line);
        }
        br.close();

        return labelList;
    }

    private ByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);

    }

    public void recognizeImage(Bitmap bitmap) {
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

        ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaledBitmap);
        Object[] inputArray = {byteBuffer};

        HashMap<Integer, Object> outputMap = new HashMap<>();




    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap scaledBitmap) {
        ByteBuffer byteBuffer;
        int size = INPUT_SIZE;
        if(isModelQuantized) {
            byteBuffer = ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * 3 * 4);
        } else {
            byteBuffer = ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * 3 * 4);
        }

        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight());
        int pixel = 0;
        for(int i = 0; i < INPUT_SIZE; ++i) {
            for(int j = 0; j < INPUT_SIZE; ++j) {
                int val = intValues[pixel++];
                if(isModelQuantized){
                    byteBuffer.putFloat(((val >> 16) & 0xFF) / IMAGE_STD);
                    byteBuffer.putFloat(((val >> 8) & 0xFF) / IMAGE_STD);
                    byteBuffer.putFloat((val & 0xFF) / IMAGE_STD);
                }
                else {
                    byteBuffer.put((byte) ((val >> 16) & 0xFF));
                    byteBuffer.put((byte) ((val >> 8) & 0xFF));
                    byteBuffer.put((byte) (val & 0xFF));
                }
            }
        }

        return byteBuffer;
    }
}
