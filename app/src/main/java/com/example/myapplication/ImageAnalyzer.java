package com.example.myapplication;

import android.annotation.SuppressLint;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private final String TAG = ImageAnalyzer.class.getSimpleName();
    private TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            // Get ready to use image with calculated rotation degrees to work with
            InputImage image =
                    InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {
                                    // Task completed successfully
                                    Log.i(TAG, "Image analysis went successfully. Extracting the text");
                                    String resultText = visionText.getText();
                                    // TODO: Extract block of the text for further translation
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            Log.i(TAG, "Image analysis went wrong.");
                                        }
                                    });
        }
    }
}
