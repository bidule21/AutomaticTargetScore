package com.ninadtech.imagecapture;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.File;

public class MainActivity extends Activity {
    private static String TAG = new String("MainActivity");
    private static int TAKE_PICTURE = 1;

    File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"picture.jpg");
    private Uri imageURI = Uri.fromFile(photo);


    static{
        System.loadLibrary("MyOpenCvLibs");
    }
    //Function to load the OpenCV lib
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case BaseLoaderCallback.SUCCESS :
                    Log.d(TAG,"OpenCv Loaded and this is called.");

                    break;
                default:
                    super.onManagerConnected(status);
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = (Button) findViewById(R.id.capture_button);
        cameraButton.setOnClickListener(cameraClickListner);
    }
    @Override
    protected void onResume(){
        super.onResume();
        //load OpenCV.
        if(OpenCVLoader.initDebug()){
            Log.d(TAG,"OpenCV Loaded Successfully");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            Log.d(TAG,"OpenCV Failed to load ");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0,this,mLoaderCallback);
        }
    }



    private View.OnClickListener cameraClickListner = new View.OnClickListener(){
        public void onClick(View v){
            takePhoto(v);
        }
    };

    //Starts Camera to take photo.
    private void takePhoto(View v){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        //Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", createImageFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageURI);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,TAKE_PICTURE);
    }

    //After the photo is clicked...
    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);

        //If photo is fine...
        if(resultCode == Activity.RESULT_OK){
            //this.showDetectBlobs();
            this.showHogueTransform();
        }
    }

    //Takes imageURL as set during camera intent and renders Gray image to imageView.
    private void showGrayImage(){
        getContentResolver().notifyChange(imageURI,null);
        //Init Stuff.
        ImageView imageview = (ImageView) findViewById(R.id.imageView);
        ContentResolver cr = getContentResolver();
        Bitmap originalBitmap;
        Mat mRgba,mGray;
        try {
            //Read Bitmap from the file.
            originalBitmap = MediaStore.Images.Media.getBitmap(cr,imageURI);

            //Init Matrices
            mRgba = new Mat ( originalBitmap.getHeight(), originalBitmap.getWidth(), CvType.CV_8U, new Scalar(4));
            mGray = new Mat ( originalBitmap.getHeight(), originalBitmap.getWidth(), CvType.CV_8U, new Scalar(4));
            // Create new matrix with ARGB_8888 config as bitmaptomat requires this.
            Bitmap myBitmap32 = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

            //Convert the bitmap to mat
            Utils.bitmapToMat(myBitmap32,mRgba);

            //Send the mat to OpenCV
            OpenCVFunctions.convertToGray(mRgba.getNativeObjAddr(),mGray.getNativeObjAddr());

            //mGray should be populated now.
            //Convert mGray to bitmap
            Bitmap resultBitmap = Bitmap.createBitmap(mGray.cols(),  mGray.rows(),Bitmap.Config.ARGB_8888);;
            //Convert the mGray mat to bitmap to show on the Imageview.
            Utils.matToBitmap(mGray, resultBitmap);

            //set imageView as this bitmap
            imageview.setImageBitmap(resultBitmap);

            //Send toast so that user is notified.
            Toast.makeText(MainActivity.this,imageURI.toString(),Toast.LENGTH_LONG).show();
        }
        catch(Exception e){
            Log.d(TAG,e.toString());
        }
    }


    //Takes imageURL as set during camera intent and renders Gray image to imageView.
    private void showHogueTransform(){
        getContentResolver().notifyChange(imageURI,null);
        //Init Stuff.
        ImageView imageview = (ImageView) findViewById(R.id.imageView);
        ContentResolver cr = getContentResolver();
        Bitmap originalBitmap;
        Mat mRgba;
        try {
            //Read Bitmap from the file.
            originalBitmap = MediaStore.Images.Media.getBitmap(cr,imageURI);

            //Init Mat
            mRgba = new Mat ( originalBitmap.getHeight(), originalBitmap.getWidth(), CvType.CV_8U, new Scalar(4));

            // Create new matrix with ARGB_8888 config as bitmaptomat requires this.
            Bitmap myBitmap32 = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

            //Convert the bitmap to mat
            Utils.bitmapToMat(myBitmap32,mRgba);
            Log.d(TAG,"Sending to OpenCV class");
            //Send the mat to OpenCV
            OpenCVFunctions.hogueTransform(mRgba.getNativeObjAddr());
            Log.d(TAG,"Got from OpenCV class");
            //mGray should be populated now.
            //Convert mGray to bitmap
            Bitmap resultBitmap = Bitmap.createBitmap(mRgba.cols(),  mRgba.rows(),Bitmap.Config.ARGB_8888);;
            //Convert the mGray mat to bitmap to show on the Imageview.
            Log.d(TAG,"Resultant bitmap calculated");
            Utils.matToBitmap(mRgba, resultBitmap);

            //set imageView as this bitmap
            imageview.setImageBitmap(resultBitmap);

            //Send toast so that user is notified.
            Toast.makeText(MainActivity.this,imageURI.toString(),Toast.LENGTH_LONG).show();
        }
        catch(Exception e){
            Log.d(TAG,e.toString());
        }
    }
    //Takes imageURL as set during camera intent and renders Gray image to imageView.
    private void showDetectBlobs(){
        getContentResolver().notifyChange(imageURI,null);
        //Init Stuff.
        ImageView imageview = (ImageView) findViewById(R.id.imageView);
        ContentResolver cr = getContentResolver();
        Bitmap originalBitmap;
        Mat mRgba;
        try {
            //Read Bitmap from the file.
            originalBitmap = MediaStore.Images.Media.getBitmap(cr,imageURI);

            //Init Mat
            mRgba = new Mat ( originalBitmap.getHeight(), originalBitmap.getWidth(), CvType.CV_8U, new Scalar(4));

            // Create new matrix with ARGB_8888 config as bitmaptomat requires this.
            Bitmap myBitmap32 = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

            //Convert the bitmap to mat
            Utils.bitmapToMat(myBitmap32,mRgba);
            Log.d(TAG,"Sending to OpenCV class");
            //Send the mat to OpenCV
            OpenCVFunctions.detectBlobs(mRgba.getNativeObjAddr());
            Log.d(TAG,"Got from OpenCV class");
            //mGray should be populated now.
            //Convert mGray to bitmap
            Bitmap resultBitmap = Bitmap.createBitmap(mRgba.cols(),  mRgba.rows(),Bitmap.Config.ARGB_8888);;
            //Convert the mGray mat to bitmap to show on the Imageview.
            Log.d(TAG,"Resultant bitmap calculated");
            Utils.matToBitmap(mRgba, resultBitmap);

            //set imageView as this bitmap
            imageview.setImageBitmap(resultBitmap);

            //Send toast so that user is notified.
            Toast.makeText(MainActivity.this,imageURI.toString(),Toast.LENGTH_LONG).show();
        }
        catch(Exception e){
            Log.d(TAG,e.toString());
        }
    }
}
