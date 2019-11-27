package com.example.tcc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.priyankvasa.android.cameraviewex.CameraView;
import com.priyankvasa.android.cameraviewex.Image;

import org.opencv.android.Utils;
import org.opencv.core.MatOfKeyPoint;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs; // imread, imwrite
import org.opencv.features2d.*;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;



import java.util.Locale;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private CameraView mCameraView;
    private String[] permissions = {Manifest.permission.CAMERA};
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mCameraView = findViewById(R.id.camera);
        tts = new TextToSpeech(this, this);

//        mCameraView.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("MissingPermission")
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Tocou", Toast.LENGTH_SHORT).show();
//                mCameraView.capture();
//            }
//        });

        mCameraView.addPictureTakenListener(new Function1<Image, Unit>() {
            @Override
            public Unit invoke(Image image) {
                Toast.makeText(MainActivity.this, "Bateu a foto", Toast.LENGTH_SHORT).show();
                SURF(image);
                return null;
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
                }else{
                    mCameraView.capture();
                    //speakText("Tirou a foto.");
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }else{
            mCameraView.start();
        }
    }

    @Override
    public void onPause(){
        mCameraView.stop();
        tts.stop();
        super.onPause();
    }

    @Override
    public void onDestroy(){
        mCameraView.destroy();
        tts.shutdown();
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(new Locale("pt", "BR"));
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(MainActivity.this, "Idioma não suportado!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "OK!!", Toast.LENGTH_SHORT).show();
                //speakText("");
            }
        }
    }

    private void speakText(String texto){
        tts.speak(texto,TextToSpeech.QUEUE_FLUSH,null, "fala");
    }

//    private void SURF(Image image){
//        File lib = null;
//        String os = System.getProperty("os.name");
//        String bitness = System.getProperty("sun.arch.data.model");
//
//        if (os.toUpperCase().contains("WINDOWS")) {
//            if (bitness.endsWith("64")) {
//                lib = new File("libs//x64//" + System.mapLibraryName("opencv_java2411"));
//            } else {
//                lib = new File("libs//x86//" + System.mapLibraryName("opencv_java2411"));
//            }
//        }
//
//        System.out.println(lib.getAbsolutePath());
//        System.load(lib.getAbsolutePath());
//
//        String bookObject = "images//bookobject.jpg";
//        String bookScene = "images//bookscene.jpg";
//
//        System.out.println("Started....");
//        System.out.println("Loading images...");
//        Mat objectImage = Imgcodecs.imread(bookObject, Imgcodecs.CV_LOAD_IMAGE_COLOR);
//        Mat sceneImage = Imgcodecs.imread(bookScene, Imgcodecs.CV_LOAD_IMAGE_COLOR);
//
//        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
//        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
//        System.out.println("Detecting key points...");
//        featureDetector.detect(objectImage, objectKeyPoints);
//        KeyPoint[] keypoints = objectKeyPoints.toArray();
//        System.out.println(keypoints);
//
//        MatOfKeyPoint objectDescriptors = new MatOfKeyPoint();
//        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
//        System.out.println("Computing descriptors...");
//        descriptorExtractor.compute(objectImage, objectKeyPoints, objectDescriptors);
//
//        // Create the matrix for output image.
//        Mat outputImage = new Mat(objectImage.rows(), objectImage.cols(), Imgcodecs.CV_LOAD_IMAGE_COLOR);
//        Scalar newKeypointColor = new Scalar(255, 0, 0);
//
//        System.out.println("Drawing key points on object image...");
//        Features2d.drawKeypoints(objectImage, objectKeyPoints, outputImage, newKeypointColor, 0);
//
//        // Match object image with the scene image
//        MatOfKeyPoint sceneKeyPoints = new MatOfKeyPoint();
//        MatOfKeyPoint sceneDescriptors = new MatOfKeyPoint();
//        System.out.println("Detecting key points in background image...");
//        featureDetector.detect(sceneImage, sceneKeyPoints);
//        System.out.println("Computing descriptors in background image...");
//        descriptorExtractor.compute(sceneImage, sceneKeyPoints, sceneDescriptors);
//
//        Mat matchoutput = new Mat(sceneImage.rows() * 2, sceneImage.cols() * 2, Imgcodecs.CV_LOAD_IMAGE_COLOR);
//        Scalar matchestColor = new Scalar(0, 255, 0);
//
//        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
//        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
//        System.out.println("Matching object and scene images...");
//        descriptorMatcher.knnMatch(objectDescriptors, sceneDescriptors, matches, 2);
//
//        System.out.println("Calculating good match list...");
//        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
//
//        float nndrRatio = 0.7f;
//
//        for (int i = 0; i < matches.size(); i++) {
//            MatOfDMatch matofDMatch = matches.get(i);
//            DMatch[] dmatcharray = matofDMatch.toArray();
//            DMatch m1 = dmatcharray[0];
//            DMatch m2 = dmatcharray[1];
//
//            if (m1.distance <= m2.distance * nndrRatio) {
//                goodMatchesList.addLast(m1);
//
//            }
//        }
//
//        if (goodMatchesList.size() >= 7) {
//            System.out.println("Object Found!!!");
//
//            List<KeyPoint> objKeypointlist = objectKeyPoints.toList();
//            List<KeyPoint> scnKeypointlist = sceneKeyPoints.toList();
//
//            LinkedList<Point> objectPoints = new LinkedList<>();
//            LinkedList<Point> scenePoints = new LinkedList<>();
//
//            for (int i = 0; i < goodMatchesList.size(); i++) {
//                objectPoints.addLast(objKeypointlist.get(goodMatchesList.get(i).queryIdx).pt);
//                scenePoints.addLast(scnKeypointlist.get(goodMatchesList.get(i).trainIdx).pt);
//            }
//
//            MatOfPoint2f objMatOfPoint2f = new MatOfPoint2f();
//            objMatOfPoint2f.fromList(objectPoints);
//            MatOfPoint2f scnMatOfPoint2f = new MatOfPoint2f();
//            scnMatOfPoint2f.fromList(scenePoints);
//
//            Mat homography = Calib3d.findHomography(objMatOfPoint2f, scnMatOfPoint2f, Calib3d.RANSAC, 3);
//
//            Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
//            Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);
//
//            obj_corners.put(0, 0, new double[]{0, 0});
//            obj_corners.put(1, 0, new double[]{objectImage.cols(), 0});
//            obj_corners.put(2, 0, new double[]{objectImage.cols(), objectImage.rows()});
//            obj_corners.put(3, 0, new double[]{0, objectImage.rows()});
//
//            System.out.println("Transforming object corners to scene corners...");
//            Core.perspectiveTransform(obj_corners, scene_corners, homography);
//
//            Mat img = Imgcodecs.imread(bookScene, Imgcodecs.CV_LOAD_IMAGE_COLOR);
//
//            Imgproc.line(img, new Point(scene_corners.get(0, 0)), new Point(scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
//            Imgproc.line(img, new Point(scene_corners.get(1, 0)), new Point(scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
//            Imgproc.line(img, new Point(scene_corners.get(2, 0)), new Point(scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
//            Imgproc.line(img, new Point(scene_corners.get(3, 0)), new Point(scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);
//
//            System.out.println("Drawing matches image...");
//            MatOfDMatch goodMatches = new MatOfDMatch();
//            goodMatches.fromList(goodMatchesList);
//
//            Features2d.drawMatches(objectImage, objectKeyPoints, sceneImage, sceneKeyPoints, goodMatches, matchoutput, matchestColor, newKeypointColor, new MatOfByte(), 2);
//
//            Imgcodecs.imwrite("output//outputImage.jpg", outputImage);
//            Imgcodecs.imwrite("output//matchoutput.jpg", matchoutput);
//            Imgcodecs.imwrite("output//img.jpg", img);
//        } else {
//            System.out.println("Object Not Found");
//        }
//
//        System.out.println("Ended....");
//    }

    public byte[] GrayScale(byte[] pixels, int width, int height) {
        int dataWidth = width;
        int dataHeight = height;
        int left = 0;
        int top = 0;

        // Total number of pixels suffices, can ignore shape
        int size = width * height;
        byte[] luminances = new byte[size];
        for (int offset = 0; offset < size; offset++) {
            int r = pixels[offset * 3] & 0xff; // red
            int g2 = (pixels[offset * 3 + 1] & 0xff); // green
            int b = pixels[offset * 3 + 2] & 0xff; // blue

            // Calculate green-favouring average cheaply
            luminances[offset] = (byte) ((r + g2 + b) / 4);
        }
        return luminances;
    }

    private void SURF(Image image){

//        InputStream stream = null;
//        try {
//            stream = new FileInputStream("model.sav");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        Toast.makeText(MainActivity.this, "Key Point OK", Toast.LENGTH_SHORT).show();

//        Bitmap bitmap = BitmapFactory.decodeByteArray(image.getData(), 0, image.getData().length);
//        Mat objectImage = new Mat();
//        Utils.bitmapToMat(bitmap, objectImage);

        Mat objectImage = Imgcodecs.imdecode(new MatOfByte(GrayScale(image.getData(), image.getWidth(), image.getHeight())), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);

        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
        System.out.println("Detecting key points...");
        featureDetector.detect(objectImage, objectKeyPoints);
        KeyPoint[] keypoints = objectKeyPoints.toArray();
        System.out.println(keypoints);


//        InputStream stream = getAssets().open("model.sav");
//        Unpickler unpickler = new Unpickler();
//        Object data = unpickler.load(stream);
//        // And cast *data* to the appropriate type.
    }

}

