package com.example.moneymanager.imageOCR;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;

public class OCRTool {
    Context _context;
    public String content = null;
    public OCRTool(Context context, Bitmap bitmap) {
        this._context = context;
        InputImage image = InputImage.fromBitmap(bitmap, 0);
//        try {
//            //change to uri obtained from gallery selection, maybe crop photo as well
//            file = new File("/storage/emulated/0/Download/image.png");
//            image = InputImage.fromFilePath(_context, uri);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        TextRecognizer recognizer = TextRecognition.getClient();
        final Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // Task completed successfully
                                // ...
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });

        class getMessage implements Runnable {
            @Override
            public void run() {
                while(!result.isComplete()){
                    try{
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String resultText = result.getResult().getText();
                content = resultText;
            }
        }
        new getMessage().run();
    }
}
