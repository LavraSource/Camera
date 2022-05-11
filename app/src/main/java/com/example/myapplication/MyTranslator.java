package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.text.Text;

import java.util.Objects;

public class MyTranslator {
    private final String TAG = MyTranslator.class.getSimpleName();
    private final MyLanguageIdentification languageIdentification = new MyLanguageIdentification();

    // TODO: Convert returning type to string after creating the architecture of the project
    // Note: Don't forget about target language to translate as a parameter
    public void translate(String text) {
        if(text!=null) {
            TranslatorOptions options =
                    new TranslatorOptions.Builder()
                            .setSourceLanguage(Objects.requireNonNull(TranslateLanguage.fromLanguageTag(
                                    languageIdentification.identifyLanguage(text)))
                            )
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
            toTargetLanguageTranslator.translate(String.valueOf(text))
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            // TODO: Extract the translated text
                            Log.i(TAG, "Translated successfully! Extracting the translated text.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "Translation of the text went wrong.");
                        }
                    });
        }
    }
}
