package kr.hs.dsm.appb.idiot;
/**
 * Created by dsm_024 on 2016-05-31.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.*;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class CameraResult extends Activity {

    private static final String TAG = "ResultActivity";
    public  CascadeClassifier facesDetector;
    public  CascadeClassifier eyesDetector;
    public  CascadeClassifier nosesDetector;
    public  CascadeClassifier mouthesDetector;

    private Handler mHandler;
    private ProgressDialog mProgressDialog;

    private Mat matFace;
    private Mat matEye1;
    private Mat matEye2;
    private Mat matNose;
    private Mat matMouth;
    private String facePath;
    private String eye1Path;
    private String eye2Path;
    private String mouthPath;
    private String nosePath;
    public ImageView img;
    private String sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    private String path = sd + "/";
    private ImageButton changeButton;
    public Mat ImageMat;
    public Mat ImageMatClone;
    public int deviceWidth;
    public int deviceHeight;


    private Handler confirmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //완료 후 실행할 처리 삽입
            Toast.makeText(getApplicationContext(),"완료!",Toast.LENGTH_SHORT).show();
        }
    };


    int i = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean checkMatClone =false;

        setContentView(R.layout.activity_camera_result);

        final ProgressDialog progDialog = new ProgressDialog( this );
        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setProgress(0);
        progDialog.setMax(100);
        progDialog.setMessage("please wait....");
        progDialog.setIndeterminate(true);
        progDialog.show();


        new Thread(new Runnable() {
            @Override
            public void run() {
                confirmHandler.sendEmptyMessage(0);
                progDialog.setProgress(2);
                System.out.println("프로그래스바 동작");
            }
        }).start();

        Intent intent = getIntent();
        String photoPath = intent.getStringExtra("strParamName");

        Display display = getWindowManager().getDefaultDisplay();
        deviceWidth = display.getWidth();
        deviceHeight = display.getHeight();

        System.loadLibrary("opencv_java3");
        ImageMat = Imgcodecs.imread(photoPath);
        ImageMatClone = ImageMat.clone();

        img = (ImageView) findViewById(R.id.imageView);
        //progDialog.setCancelable(false);

        progDialog.setProgress(5);

        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File faces_cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            FileOutputStream os = new FileOutputStream(faces_cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            //-------------------------- faces cascade

            InputStream is2 = getResources().openRawResource(R.raw.haarcascade_eye);
            File cascadeDir2 = getDir("cascade", Context.MODE_PRIVATE);
            File eyes_cascadeFile = new File(cascadeDir2, "haarcascade_eye.xml");
            FileOutputStream os2 = new FileOutputStream(eyes_cascadeFile);

            byte[] buffer2 = new byte[4096];
            int bytesRead2;
            while ((bytesRead2 = is2.read(buffer2)) != -1) {
                os2.write(buffer2, 0, bytesRead2);
            }
            is2.close();
            os2.close();
            //----------------------------- noses cascade
            InputStream is3 = getResources().openRawResource(R.raw.haarcascade_nose);
            File cascadeDir3 = getDir("cascade", Context.MODE_PRIVATE);
            File noses_cascadeFile = new File(cascadeDir3, "haarcascade_nose.xml");
            FileOutputStream os3 = new FileOutputStream(noses_cascadeFile);

            byte[] buffer3 = new byte[4096];
            int bytesRead3;
            while ((bytesRead3 = is3.read(buffer3)) != -1) {
                os3.write(buffer3, 0, bytesRead3);
            }
            is3.close();
            os3.close();
            //----------------------------- noses cascade
            InputStream is4 = getResources().openRawResource(R.raw.haarcascade_mouth);
            File cascadeDir4 = getDir("cascade", Context.MODE_PRIVATE);
            File mouthes_cascadeFile = new File(cascadeDir4, "haarcascade_mouth.xml");
            FileOutputStream os4 = new FileOutputStream(mouthes_cascadeFile);

            byte[] buffer4 = new byte[4096];
            int bytesRead4;
            while ((bytesRead4 = is4.read(buffer4)) != -1) {
                os4.write(buffer4, 0, bytesRead4);
            }
            is4.close();
            os4.close();
            //----------------------- mouth cascade

            facesDetector = new CascadeClassifier(faces_cascadeFile.getAbsolutePath());
            eyesDetector = new CascadeClassifier(eyes_cascadeFile.getAbsolutePath());
            nosesDetector = new CascadeClassifier(noses_cascadeFile.getAbsolutePath());
            mouthesDetector = new CascadeClassifier(mouthes_cascadeFile.getAbsolutePath());

            if (facesDetector.empty() || eyesDetector.empty() || nosesDetector.empty() || mouthesDetector.empty()) {
                Log.e(TAG, "Failed to load cascade");
                facesDetector = null;
                eyesDetector = null;
                nosesDetector = null;
                mouthesDetector = null;
            } else {
                Log.i(TAG, "Loaded cascade");
                faces_cascadeFile.delete();
                eyes_cascadeFile.delete();
                noses_cascadeFile.delete();
                mouthes_cascadeFile.delete();
                cascadeDir.delete();
                cascadeDir2.delete();
                cascadeDir3.delete();
                cascadeDir4.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade, Exception thrown:" + e);
        }


        progDialog.setProgress(20);

        MatOfRect faces = new MatOfRect();
        facesDetector.detectMultiScale(ImageMat, faces);
        if (faces.toArray().length >= 1) {
            for (Rect faceRect : faces.toArray()) {

                Rect face = faceRect.clone();
                matFace = ImageMat.submat(face);
                facePath = path + "face.jpg";
                Imgcodecs.imwrite(facePath, matFace);

                Imgproc.rectangle(ImageMatClone, new Point(faceRect.x - 10, faceRect.y + 10),
                        new Point(faceRect.x - 10 + faceRect.width + 10, faceRect.y + 10 + faceRect.height + 10),
                        new Scalar(0, 255, 0)); // face

                final MatOfRect eyes = new MatOfRect();
                final MatOfRect noses = new MatOfRect();
                final MatOfRect mouthes = new MatOfRect();

                Thread ThredEye = new Thread() {
                    public void run() {
                        Long start = System.currentTimeMillis();
                        eyesDetector.detectMultiScale(ImageMat, eyes);
                        Long end = System.currentTimeMillis();
                        System.out.println("Eye Find time = " + (end - start) / 1000);
                    }
                };
                ThredEye.start();

                Thread ThredNose = new Thread() {
                    public void run() {
                        Long start = System.currentTimeMillis();

                        nosesDetector.detectMultiScale(ImageMat, noses);
                        Long end = System.currentTimeMillis();
                        System.out.println("Eye Find time = " + (end - start) / 1000);
                    }
                };
                ThredNose.start();

                Thread ThredMouth = new Thread() {
                    public void run() {
                        Long start = System.currentTimeMillis();
                        mouthesDetector.detectMultiScale(ImageMat, mouthes);
                        Long end = System.currentTimeMillis();
                        System.out.println("Eye Find time = " + (end - start) / 1000);
                    }
                };
                ThredMouth.start();

                System.out.println("눈 Mat of Rect = " + eyes);
                System.out.println("코 Mat of Rect = " + noses);
                System.out.println("입 Mat of Rect = " + mouthes);


                progDialog.setProgress(30);

                while (true) {
                    if ((eyes.isContinuous()) || (noses.isContinuous()) || (mouthes.isContinuous())) {
                        break;
                    }
                }

                int rectLengthX = (deviceWidth - faceRect.x) - (deviceWidth - (faceRect.x + faceRect.width));
                int rectLengthY = (deviceHeight - faceRect.y) - (deviceHeight - (faceRect.y + faceRect.width));
                int centerX = rectLengthX / 2;


                //----------- eyes
                int eyesDistance = 1000;
                int tempX = 0;
                int tempY = 0;
                int tempXY = 0;
                Rect realEye1 = null;
                Rect realEye2 = null;

                if (eyes.toArray().length >= 2) {
                    for (Rect eyeRect1 : eyes.toArray()) {
                        if (faceRect.contains(new Point(eyeRect1.x, eyeRect1.y))) {
                            for (Rect eyeRect2 : eyes.toArray()) {
                                if (faceRect.contains(new Point(eyeRect2.x, eyeRect2.y))) {

                                    tempY = eyeRect1.y - eyeRect2.y;
                                    if (tempY < 0) {
                                        tempY = Math.abs(tempY);
                                    }

                                    tempX = centerX - ((eyeRect2.x + eyeRect2.width) - eyeRect1.x);
                                    tempXY = tempX + tempY;

                                    if (eyesDistance > tempXY) {
                                        eyesDistance = tempXY;
                                        realEye1 = eyeRect1;
                                        realEye2 = eyeRect2;
                                    }
                                }
                            }
                        }
                    }
                    try {
                        realEye1.x = realEye1.x - (rectLengthX / 10);
                    }catch (Exception e){
                        System.out.println(e.toString());

                        Intent intent1 = new Intent(CameraResult.this,
                                MainActivity.class);
                        startActivity(intent1);

                        Toast.makeText(getApplicationContext(),
                                "ㅅㄱ 오류 realeye1", Toast.LENGTH_LONG).show();


                    }


                    progDialog.setProgress(50);


                    try {
                        realEye1.width = realEye1.width + (rectLengthX / 10) * 2;
                        realEye2.x = realEye2.x - (rectLengthX / 10);
                        realEye2.width = realEye2.width + (rectLengthX / 10) * 2;
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"오류가 발생했습니다.",Toast.LENGTH_SHORT);
                        startActivity(intent);

                    }
                    Rect eye1 = realEye1.clone();
                    Rect eye2 = realEye2.clone();

                    matEye1 = ImageMat.submat(eye1);
                    matEye2 = ImageMat.submat(eye2);
                    eye1Path = path + "eye1.png";
                    eye2Path = path + "eye2.png";
                    Imgcodecs.imwrite(eye1Path, matEye1);
                    Imgcodecs.imwrite(eye2Path, matEye2);

                    Imgproc.rectangle(ImageMatClone, new Point(realEye1.x, realEye1.y),
                            new Point(realEye1.x + realEye1.width, realEye1.y + realEye1.height),
                            new Scalar(255, 0, 0));

                    Imgproc.rectangle(ImageMatClone, new Point(realEye2.x, realEye2.y),
                            new Point(realEye2.x + realEye2.width, realEye2.y + realEye2.height),
                            new Scalar(255, 0, 0));
                } else {
                    Toast.makeText(getApplicationContext(),
                            "눈을 찾지 못했습니다!", Toast.LENGTH_LONG).show();
                    finish();

                }


                progDialog.setProgress(60);

                if (realEye1 != null && realEye2 != null) {
                    if (noses.toArray().length == 1) {
                        for (Rect noseRect : noses.toArray()) {
                            if (noseRect.y > realEye1.y) {
                                if (faceRect.contains(new Point(noseRect.x, noseRect.y))) {
                                    if (!(noseRect.contains(new Point(realEye1.x, realEye1.y)) || noseRect.contains(new Point(realEye2.x, realEye2.y)))) {
                                        noseRect.y = noseRect.y - (rectLengthY / 10 * 1);
                                        noseRect.height = noseRect.height + (rectLengthY / 10 * 1);

                                        Rect nose = noseRect.clone();
                                        matNose = ImageMat.submat(nose);
                                        nosePath = path + "nose.png";
                                        Imgcodecs.imwrite(nosePath, matNose);

                                        Imgproc.rectangle(ImageMatClone, new Point(noseRect.x, noseRect.y),
                                                new Point(noseRect.x + noseRect.width, noseRect.y + noseRect.height),
                                                new Scalar(0, 0, 255));
                                        Rect mouthRect = new Rect();
                                        mouthRect.x = faceRect.x + rectLengthX / 10 * 2;
                                        mouthRect.y = noseRect.y + noseRect.height - rectLengthY / 10 * 1;
                                        mouthRect.width = (rectLengthX) / 10 * 6;
                                        mouthRect.height = (rectLengthY) / 10 * 2;

                                        Rect mouth = mouthRect.clone();
                                        matMouth = ImageMat.submat(mouth);
                                        mouthPath = path + "mouth.png";
                                        Imgcodecs.imwrite(mouthPath, matMouth);

                                        Imgproc.rectangle(ImageMatClone, new Point(mouthRect.x, mouthRect.y),
                                                new Point(mouthRect.x + mouthRect.width, mouthRect.y + mouthRect.height),
                                                new Scalar(255, 255, 0));
                                    }
                                }
                            }
                        }
                    } else {
                        int noseX = (realEye1.x + (realEye2.x + realEye2.width)) / 2;
                        noseX = noseX - (rectLengthX / 10 * 2);
                        Rect nose = new Rect(noseX, realEye1.y, rectLengthX / 10 * 4, rectLengthY / 2);

                        matNose = ImageMat.submat(nose);
                        nosePath = path + "nose.png";
                        Imgcodecs.imwrite(nosePath, matNose);

                        Imgproc.rectangle(ImageMatClone, new Point(nose.x, nose.y),
                                new Point(nose.x + nose.width, nose.y + nose.height),
                                new Scalar(0, 0, 255));
                        Rect mouthRect = new Rect();
                        mouthRect.x = faceRect.x + rectLengthX / 10 * 2;
                        mouthRect.y = nose.y + nose.height - rectLengthY / 10 * 1;
                        mouthRect.width = (rectLengthX) / 10 * 6;
                        mouthRect.height = (rectLengthY) / 10 * 3;

                        matMouth = ImageMat.submat(mouthRect);
                        mouthPath = path + "mouth.png";
                        Imgcodecs.imwrite(mouthPath, matMouth);

                        Imgproc.rectangle(ImageMatClone, new Point(mouthRect.x, mouthRect.y),
                                new Point(mouthRect.x + mouthRect.width, mouthRect.y + mouthRect.height),
                                new Scalar(255, 255, 0));
                    }
                }
            }
            checkMatClone = true;
            progDialog.setProgress(85);
        } else {
            Toast.makeText(getApplicationContext(),
                    "얼굴을 찾지 못했습니다!", Toast.LENGTH_LONG).show();
            finish();
        }

        if(checkMatClone) {
            Bitmap photo = Bitmap.createBitmap(ImageMatClone.cols(), ImageMatClone.rows(), Bitmap.Config.ARGB_8888);

            Imgproc.cvtColor(ImageMatClone, ImageMatClone, Imgproc.COLOR_BGR2RGB);
            Utils.matToBitmap(ImageMatClone, photo);

            progDialog.setProgress(100);

            img.setImageBitmap(photo);

            progDialog.dismiss();

            System.out.println("프로그레스바 종료");

        }

        changeButton = (ImageButton) findViewById(R.id.change_cariculture);

        changeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    final Intent intent = new Intent(CameraResult.this,
                            CompareImage.class);

                    intent.putExtra("face", facePath);
                    intent.putExtra("eye1", eye1Path);
                    intent.putExtra("eye2", eye2Path);
                    intent.putExtra("nose", nosePath);
                    intent.putExtra("mouth", mouthPath);

                    AlertDialog.Builder builder = new AlertDialog.Builder(CameraResult.this);

                    builder.setTitle("성별 여부")
                            .setMessage("성별을 선택해주세요!")
                            .setPositiveButton("남성", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("여성", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(intent);
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void garbage_onClick(View v){
        Intent intent = new Intent(CameraResult.this,
                MainActivity.class);
        img.setImageBitmap(null);
        finish();
        startActivity(intent);
    }
}
