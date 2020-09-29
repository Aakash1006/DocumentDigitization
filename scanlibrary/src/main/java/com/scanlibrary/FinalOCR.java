package com.scanlibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FinalOCR extends AppCompatActivity {

    static int count=1;
    private ImageView scannedImageView;
    private EditText OCRresult;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            Intent intent = new Intent(this,
                    Class.forName("com.teapink.ocr_reader.activities.MainActivity"));
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_o_c_r);

        OCRresult=(EditText)findViewById(R.id.ocroutput);
        scannedImageView = (ImageView)findViewById(R.id.ocrimage);

        scannedImageView.setImageBitmap(ResultFragment.transformed);

        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()){
            Toast.makeText(FinalOCR.this,"Could not get Text",Toast.LENGTH_LONG).show();
        }
        else{
            Frame frame=new Frame.Builder().setBitmap(ResultFragment.transformed).build();

            SparseArray <TextBlock> items=textRecognizer.detect(frame);

            StringBuilder sb=new StringBuilder();

            for(int i=0;i<items.size();++i)
            {
                TextBlock myItem=items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }
            OCRresult.setText(sb.toString());
        }

        findViewById(R.id.savepdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap=ResultFragment.transformed;
                PdfDocument pdfDocument=new PdfDocument();
                PdfDocument.PageInfo pi=new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();

                PdfDocument.Page page=pdfDocument.startPage(pi);
                Canvas canvas=page.getCanvas();
                Paint paint=new Paint();
                paint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawPaint(paint);
                bitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
                paint.setColor(Color.BLUE);
                canvas.drawBitmap(bitmap,0,0,null);
                pdfDocument.finishPage(page);

                File root=new File(Environment.getExternalStorageDirectory(),"PDF Folder");
                if(!root.exists()){
                    root.mkdir();
                }

                File file=new File(root,"ScannedPicture"+Integer.toString(count)+".pdf");
                count+=1;
                try{
                    FileOutputStream fileOutputStream=new FileOutputStream(file);
                    pdfDocument.writeTo(fileOutputStream);
                    Toast.makeText(FinalOCR.this,"File Saved",Toast.LENGTH_LONG).show();
                }catch (IOException e){
                    e.printStackTrace();
                }

                pdfDocument.close();

            }
        });



    }

//    public void getTextFromImage(View v){
//
//
//        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();
//
//        if(!textRecognizer.isOperational()){
//            Toast.makeText(FinalOCR.this,"Could not get Text",Toast.LENGTH_LONG).show();
//        }
//        else{
//            Frame frame=new Frame.Builder().setBitmap(ResultFragment.BWBimage).build();
//
//            SparseArray <TextBlock> items=textRecognizer.detect(frame);
//
//            StringBuilder sb=new StringBuilder();
//
//            for(int i=0;i<items.size();++i)
//            {
//                TextBlock myItem=items.valueAt(i);
//                sb.append(myItem.getValue());
//                sb.append("\n");
//            }
//            OCRresult.setText(sb.toString());
//        }
//    }
//    private String getText(Bitmap bitmap){
//        try{
//            tessBaseAPI = new TessBaseAPI();
//        }catch (Exception e){
//            Log.e(TAG, e.getMessage());
//        }
//        tessBaseAPI.init(DATA_PATH,"eng");
//        tessBaseAPI.setImage(bitmap);
//        String retStr = "No result";
//        try{
//            retStr = tessBaseAPI.getUTF8Text();
//        }catch (Exception e){
//            Log.e(TAG, e.getMessage());
//        }
//        tessBaseAPI.end();
//        return retStr;
//    }
}
