package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.Rect;
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

import java.util.ArrayList;
//TODO Make this to work like here(https://developer.android.com/codelabs/camerax-getting-started#5). Currently i can't bind this to the camera/
public class ImageAnalyzer implements ImageAnalysis.Analyzer {
    private final String TAG = ImageAnalyzer.class.getSimpleName();
    private final MyTranslator translator = new MyTranslator();
    private TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    public static ArrayList<Rect> blocks = new ArrayList<>();

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            // Get a ready to use image with calculated rotation degrees to work with
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
                                    if(resultText!=null) {
                                        translator.
                                                translate(resultText);
                                    }
                                    // Extracting blocks of the text.
                                    // To LavraSource: there we can start visualising the borders of each line &
                                    // insert the translation
                                    blocks=new ArrayList<>();
                                    for (Text.TextBlock block : visionText.getTextBlocks()) {
                                        String blockText = block.getText();
                                        Point[] blockCornerPoints = block.getCornerPoints();
                                        Rect blockFrame = block.getBoundingBox();
                                        blocks.add(blockFrame);

                                        for (Text.Line line : block.getLines()) {
                                            String lineText = line.getText();
                                            Point[] lineCornerPoints = line.getCornerPoints();
                                            Rect lineFrame = line.getBoundingBox();
                                            for (Text.Element element : line.getElements()) {
                                                String elementText = element.getText();
                                                Point[] elementCornerPoints = element.getCornerPoints();
                                                Rect elementFrame = element.getBoundingBox();
                                            }
                                        }
                                    }
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
