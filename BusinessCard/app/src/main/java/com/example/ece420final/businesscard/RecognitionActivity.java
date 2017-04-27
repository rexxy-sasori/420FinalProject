package com.example.ece420final.businesscard;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
/**
 * Created by hanfei on 4/15/17.
 * Recogniton Module after detection
 */

public class RecognitionActivity extends AppCompatActivity {
    private static final String TAG = "RecognitionActivity";
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/";
    public static final String lang = "eng";
    private ImageView myImg;
    private TextView text;
    private TessBaseAPI tessBaseApi;
    private ArrayList<Bitmap> receiveSubImg;
    private String recognized;


    protected void onCreate(Bundle savedBundleState){
        super.onCreate(savedBundleState);
        setContentView(R.layout.activity_recognition);

        receiveSubImg = DetectionActivity.mySubImg;
        loadData();

        myImg = (ImageView)findViewById(R.id.imageView3);
        text = (TextView)findViewById(R.id.textView);

        try{initTessBase();}
        catch(Exception e){Log.d(TAG,"tesseBaseApi init failed "+e.getMessage());}
        //myImg.setImageBitmap(NecessaryOperation.rotateBitmap(receiveSubImg.get(2),receiveSubImg.get(2)));
        for(int i = 0;i < receiveSubImg.size();i++){
            recognized = extractText(receiveSubImg.get(i));
            /*if(i == 0 || recognized.indexOf("@") != -1){
                Log.d(TAG,"extract Text:\t"+recognized);
            }*/
            //Log.d(TAG,"extract Text:\t"+recognized);
            //recognized = "This is 1 test 123-456-7890";
            /*Pattern pattern = Pattern.compile("\\d{3}-\\d{4}");
            Matcher matcher = pattern.matcher(recognized);
            if (matcher.find()) {
                Log.d(TAG,"WE FOUND THE NUMBER"+matcher.group(0));
            }
            else{
                Log.d(TAG,"Sorry no match");
            }*/
            String phoneNumber = keepNumbers(recognized);
            if(phoneNumber != ""){
                Log.d(TAG,"PHONE NUMBER " + phoneNumber);
            }

        }
        tessBaseApi.end();

    }

    private void initTessBase() throws Exception{
        tessBaseApi = new TessBaseAPI();
        tessBaseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_CHAR);
        tessBaseApi.init(DATA_PATH, "eng");
    }

    private void loadData(){
        String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
                    return;
                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        // lang.traineddata file with the app (in assets folder)
        // You can get them at:
        // http://code.google.com/p/tesseract-ocr/downloads/list
        // This area needs work and optimization
        if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH
                        + "tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }
    }

    private String extractText(Bitmap bitmap)
    {
        //bitmap resize;
        //Bitmap resized = Bitmap.createScaledBitmap(bitmap,5*bitmap.getWidth(),5*bitmap.getHeight(),false);
        //tessBaseApi.setImage(resized);
        tessBaseApi.setImage(bitmap);
        String extractedText = tessBaseApi.getUTF8Text();

        //resized.recycle();
        return extractedText;
    }

    private String keepNumbers(String input){
        char currentChar;
        String numbers = "";
        for(int i = 0;i < input.length();i++){
            currentChar = input.charAt(i);
            if(Character.isDigit(currentChar)){
                numbers += currentChar;
            }
        }

        if(numbers.length() >= 10)
            return numbers.substring(0,10);
        else
            return "";
    }

}
