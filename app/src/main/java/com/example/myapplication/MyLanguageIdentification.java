package com.example.myapplication;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.vision.text.Text;

public class MyLanguageIdentification{
    private final String TAG = MyLanguageIdentification.class.getSimpleName();
    private LanguageIdentifier languageIdentifier =
            LanguageIdentification.getClient();

    // Note: if architecture of project will change don't forget to change the params of this function
    public String identifyLanguage(Text text) {
        final String[] identifiedLanguageCode = new String[1];
        languageIdentifier.identifyLanguage(text.toString())
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                identifiedLanguageCode[0] = languageCode;
                                if (languageCode.equals("und")) {
                                    Log.i(TAG, "Can't identify language.");
                                } else {
                                    Log.i(TAG, "Language: " + languageCode);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                Log.i(TAG, "Something went wrong on identification of language");
                            }
                        });
        return identifiedLanguageCode[0];
    }
}
