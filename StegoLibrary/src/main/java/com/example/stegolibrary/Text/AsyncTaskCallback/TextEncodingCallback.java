package com.example.stegolibrary.Text.AsyncTaskCallback;

import com.example.stegolibrary.Text.ImageSteganography;

/**
 * This the callback interface for TextEncoding AsyncTask.
 */

public interface TextEncodingCallback {

    void onStartTextEncoding();

    void onCompleteTextEncoding(ImageSteganography result);

}
