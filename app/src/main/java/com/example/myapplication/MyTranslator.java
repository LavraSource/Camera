package com.example.myapplication;

import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;
import java.util.Objects;

public class MyTranslator {
    private final String TAG = MyTranslator.class.getSimpleName();
    private final MyLanguageIdentification languageIdentification = new MyLanguageIdentification();
    Task<String> stringTask;

    public static String finalLang = "und";
    public static ArrayList<Rect> blocks = new ArrayList<>();
    public static ArrayList<String> texts = new ArrayList<>();
    public void translate(String text, Rect rect) {
        if(!finalLang.equals("und")) {
            Log.i(TAG,"Translation started");
            TranslatorOptions options =
                    new TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.ENGLISH)
                            .setTargetLanguage(TranslateLanguage.RUSSIAN)
                            .build();
            final Translator toTargetLanguageTranslator = Translation.getClient(options);
            // TODO: Further translation
            // Downloading the model
            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();
            toTargetLanguageTranslator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.i(TAG, "Model has been downloaded successfully!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Downloading of model went wrong.");
                        }
                    });
            stringTask= toTargetLanguageTranslator.translate(String.valueOf(text))
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.i(TAG, s);
                            blocks.add(rect);
                            texts.add(s);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, e.getMessage());
                        }
                    });
        }
    }
}
