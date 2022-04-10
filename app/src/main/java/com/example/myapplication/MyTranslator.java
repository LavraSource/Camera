package com.example.myapplication;

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
    public void translate(Text text) {
        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(Objects.requireNonNull(TranslateLanguage.fromLanguageTag(
                                languageIdentification.identifyLanguage(text)))
                        )
                        .setTargetLanguage(TranslateLanguage.RUSSIAN)
                        .build();
        final Translator toTargetLanguageTranslator = Translation.getClient(options);
        // TODO: Further translation
    }
}
