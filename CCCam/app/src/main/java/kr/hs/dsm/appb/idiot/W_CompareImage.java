package kr.hs.dsm.appb.idiot;

/**
 * Created by dsm_024 on 2016-05-31.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.graphics.*;
import android.widget.Toast;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import compare.EyeCompare;
import compare.MansHairCompare;
import compare.FaceCompare;
import compare.MouthCompare;
import compare.NoseCompare;


/**
 * Created by 10415 on 2016-05-30.
 */
public class W_CompareImage extends Activity {

    private Mat temp;
    private String sd = Environment.getExternalStorageDirectory().getAbsolutePath();

    boolean ThFaceCheck;
    boolean ThEyeCheck;
    boolean ThLipsCheck;

    public Bitmap copyBtFace1;

    private Handler confirmHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //완료 후 실행할 처리 삽입
            Toast.makeText(getApplicationContext(),"완료!",Toast.LENGTH_SHORT).show();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare);

        Intent intent11 = new Intent(this,
                MainActivity.class);

        final Intent intent = getIntent();

        try {
            final ImageView img = (ImageView) findViewById(R.id.imageView);
            //ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);

            ThFaceCheck = false;
            ThEyeCheck = false;
            ThLipsCheck = false;

            /**    객체 생성 부분   **/
            W_Face_Type_Path faceTypePath = new W_Face_Type_Path();
            W_Imgread_Type_Path imgreadTypePath = new W_Imgread_Type_Path();
            W_Compare_Type_Rect compareTypeRect = new W_Compare_Type_Rect();
            W_Compare_Type_Var compareTypeVar = new W_Compare_Type_Var();
            W_Final_Type_INT finalTypeInt = new W_Final_Type_INT();
            FaceCompare faceCompare = new FaceCompare();
            EyeCompare eyeCompare = new EyeCompare();
            MouthCompare mouthCompare = new MouthCompare();
            NoseCompare noseCompare = new NoseCompare();

            long LongFace = intent.getLongExtra("face", 0);
            long LongEye1 = intent.getLongExtra("eye1", 0);
            long LongEye2 = intent.getLongExtra("eye2", 0);
            long LongNose = intent.getLongExtra("nose", 0);
            long LongMouth = intent.getLongExtra("mouth", 0);

            int faceX = intent.getIntExtra("faceX", 0);
            int faceY = intent.getIntExtra("faceY", 0);
            int faceWidth = intent.getIntExtra("faceSizeWidth", 0);
            int faceHeight = intent.getIntExtra("faceSizeHeight", 0);
            System.out.println("face Size = " + faceWidth + ", " + faceHeight);
            int eyePointX = intent.getIntExtra("eye1SizeX", 0);
            int eyePointY = intent.getIntExtra("eye1SizeY", 0);
            System.out.println("eyepoint = " + eyePointX + ", " + eyePointY);
            int eye2PointX = intent.getIntExtra("eye2SizeX", 0);
            int eye2PointY = intent.getIntExtra("eye2SizeY", 0);
            System.out.println("eye2point = " + eye2PointX + ", " + eye2PointY);
            int nosePointX = intent.getIntExtra("noseSizeX", 0);
            int nosePointY = intent.getIntExtra("noseSizeY", 0);
            System.out.println("nosepoint = " + nosePointX + ", " + nosePointY);
            int mouthPointX = intent.getIntExtra("mouthSizeX", 0);
            int mouthPointY = intent.getIntExtra("mouthSizeY", 0);
            System.out.println("mouthpoint = " + mouthPointX + ", " + mouthPointY);

            //convert long to mat
            Mat face = new Mat(LongFace);
            Mat eye1 = new Mat(LongEye1);
            Mat nose = new Mat(LongNose);
            Mat mouth = new Mat(LongMouth);


        /*final ProgressDialog progDialog = new ProgressDialog( this );
        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setProgress(0);
        progDialog.setMax(100);
        progDialog.setMessage("please wait....");
        progDialog.setIndeterminate(true);
        //progDialog.show();

        new Thr
                confirmHandler.sendEmptyMessage(0);
                if (progDialog.isShowing()){
                    progDialog.setProgress(2);
                    System.out.println("프로그래스바 동작");
                }*/



               /* imgreadTypePath.facePath = intent.getStringExtra("face");
                imgreadTypePath.eye1Path = intent.getStringExtra("eye1");
                imgreadTypePath.eye2Path = intent.getStringExtra("eye2");
                imgreadTypePath.nosePath =  intent.getStringExtra("nose");
                imgreadTypePath.mouthPath = intent.getStringExtra("mouth");

                final Mat face = Imgcodecs.imread(imgreadTypePath.facePath);
                final Mat eye1 = Imgcodecs.imread(imgreadTypePath.eye1Path);
                final Mat nose = Imgcodecs.imread(imgreadTypePath.nosePath);
                final Mat mouth = Imgcodecs.imread(imgreadTypePath.mouthPath);*/

            Mat compareFace = new Mat();
            Mat compareManHair = new Mat();
            Mat compareGirlHair = new Mat();
            Mat compareEye1 = new Mat();
            Mat compareEye2 = new Mat();
            Mat compareNose = new Mat();
            Mat compareMouth = new Mat();

            Mat finalEye1 = null;
            Mat finalEye2 = null;
            Mat finalNose = null;
            Mat finalMouth = null;

            temp = face;


            for (int q = 0; q < 100; q++) {
                compareFace = Imgcodecs.imread(faceTypePath.face_Path + q + ".png");

                if (compareFace.dataAddr() != 0x0) {
                    try {
                        compareTypeRect.compareFaceRet = faceCompare.compareFeature(face, compareFace);
                    } catch (Exception e) {
                        System.out.println("Face Exception");
                    }
                }

                //System.out.println(q + "번째 얼굴 avg = " + compareTypeRect.compareFaceRet);
                if (compareTypeRect.compareFaceRet > 0 && compareTypeRect.compareFaceRet < compareTypeVar.compareFaceVal) {

                    compareTypeVar.compareFaceVal = compareTypeRect.compareFaceRet;
                    finalTypeInt.finalFaceInt = q;
                    //          System.out.println("i와 fanalFaceInt" + q + " and " + finalTypeInt.finalFaceInt);
                }
            }


            for (int w = 0; w < 100; w++) {
                compareEye1 = Imgcodecs.imread(faceTypePath.eye_Path + w + ".png");

                if (compareEye1.dataAddr() != 0x0) {
                    try {
                        compareTypeRect.compareEye1Ret = eyeCompare.compareFeature(eye1, compareEye1);
                    } catch (Exception e) {
                        System.out.println("Eye Exception");
                    }
                }

                System.out.println(w + "번째 눈 avg = " + compareTypeRect.compareEye1Ret);
                if (compareTypeRect.compareEye1Ret > 0 && compareTypeRect.compareEye1Ret < compareTypeVar.compareEye1Val) {
                    finalEye1 = compareEye1;
                    compareTypeVar.compareEye1Val = compareTypeRect.compareEye1Ret;
                    finalTypeInt.finalEyeInt = w;
                    //            System.out.println("i와 fanalEyeInt" + w + " and " + finalTypeInt.finalEyeInt);
                }

                if (compareTypeRect.compareEye2Ret != 0 && compareTypeRect.compareEye2Ret < compareTypeVar.compareEye2Val) {
                    finalEye2 = compareEye2;
                    compareTypeVar.compareEye2Val = compareTypeRect.compareEye2Ret;
                }
            }

            for (int j = 0; j < 100; j++) {
                compareNose = Imgcodecs.imread(faceTypePath.nose_Path + j + ".png");

                if (compareNose.dataAddr() != 0x0) {
                    try {
                        compareTypeRect.compareNoseRet = noseCompare.compareFeature(nose, compareMouth);
                    } catch (Exception e) {
                        System.out.println("Nose Exception");
                    }
                }

                //    System.out.println(j + "번째 코 avg = " + compareTypeRect.compareNoseRet);
                if (compareTypeRect.compareNoseRet != 0 && compareTypeRect.compareNoseRet < compareTypeVar.compareNoseVal) {
                    compareTypeVar.compareNoseVal = compareTypeRect.compareNoseRet;
                    //      System.out.println("i와 fanalNose" + j + " and " + finalTypeInt.finalNoseInt);
                    finalTypeInt.finalNoseInt = j;
                }
            }


            for (int i = 0; i < 100; i++) {


                compareMouth = Imgcodecs.imread(faceTypePath.mouth_Path + i + ".png");

                if (compareMouth.dataAddr() != 0x0) {
                    try {
                        compareTypeRect.compareMouthRet = mouthCompare.compareFeature(mouth, compareMouth);
                    } catch (Exception e) {
                        System.out.println("Mouth Exception");
                    }

                }


                //   System.out.println(i + "번째 입술 avg = " + compareTypeRect.compareMouthRet);
                if (compareTypeRect.compareMouthRet != 0 && compareTypeRect.compareMouthRet < compareTypeVar.compareMouthVal) {
                    finalMouth = compareMouth;
                    compareTypeVar.compareMouthVal = compareTypeRect.compareMouthRet;
                    System.out.println("i와 fanalLips" + i + " and " + finalTypeInt.finalLipsInt);
                    finalTypeInt.finalLipsInt = i;
                }


                if (compareTypeRect.compareNoseRet != 0 && compareTypeRect.compareNoseRet < compareTypeVar.compareNoseVal) {
                    finalNose = compareNose;
                    compareTypeVar.compareNoseVal = compareTypeRect.compareNoseRet;
                }
                if (compareTypeRect.compareMouthRet != 0 && compareTypeRect.compareMouthRet < compareTypeVar.compareMouthVal) {
                    finalMouth = compareMouth;
                    compareTypeVar.compareMouthVal = compareTypeRect.compareMouthRet;
                }
            }


            int random = (int) (Math.random() * 11) + 1;


            System.out.println("가장 유사한 FACE 번호: " + finalTypeInt.finalFaceInt);
            System.out.println("가장 유사한 EYE 번호 : " + finalTypeInt.finalEyeInt);
            System.out.println("가장 유사한 Lips 번호 : " + finalTypeInt.finalLipsInt);
            System.out.println("가장 유사한 Noes 번호 : " + finalTypeInt.finalNoseInt);
            System.out.println("랜덤 hair 번호 : " + random);


            if (finalTypeInt.finalEyeInt == 0) {
                finalTypeInt.finalEyeInt = (int) (Math.random() * 30 + 1);
                Toast.makeText(getApplicationContext(),
                        "눈 랜덤", Toast.LENGTH_SHORT).show();
                System.out.println("눈 랜덤");
            }
            if (finalTypeInt.finalFaceInt == 0) {
                finalTypeInt.finalFaceInt = (int) (Math.random() * 10 + 1);
                Toast.makeText(getApplicationContext(),
                        "얼굴랜덤", Toast.LENGTH_SHORT).show();
                System.out.println("알굴 랜덤");
            }

            if (finalTypeInt.finalNoseInt == 0) {
                finalTypeInt.finalNoseInt = (int) (Math.random() * 7 + 1);
                System.out.println("코 랜덤");
            }

            if (finalTypeInt.finalLipsInt == 0) {
                finalTypeInt.finalLipsInt = (int) (Math.random() * 10 + 1);
                Toast.makeText(getApplicationContext(),
                        "입 랜덤", Toast.LENGTH_SHORT).show();
                System.out.println("입 랜덤");
            }


            Bitmap BtFace, BtNose, copyBtNose, BtEyeBrow, copyBtEyeBrow, BtHair, copyBtHair = null, BtEye, copyBtEye, BtLips, copyBtLips;

            BtFace = BitmapFactory.decodeFile(faceTypePath.face_Path + finalTypeInt.finalFaceInt + ".png");
            Bitmap copyBtFace = BtFace.copy(Bitmap.Config.ARGB_8888, true);
            copyBtFace = getResizedBitmap(copyBtFace, copyBtFace.getWidth() / 10 * 9, copyBtFace.getHeight() / 10 * 9);
            System.out.println("BtFace Size = " + (copyBtFace.getWidth() / 3) * 2 + ", " + copyBtFace.getHeight());

            double BtFacePerRealFaceWidth = (double) faceWidth / (double) BtFace.getWidth();
            double BtFacePerRealFaceHeight = (double) faceHeight / (double) BtFace.getHeight();

            BtEye = BitmapFactory.decodeFile(faceTypePath.eye_Path + finalTypeInt.finalEyeInt + ".png");
            System.out.println("dance Bteye " + BtEye.getWidth() + ", " + BtEye.getHeight());
            //  Bitmap copyBtEye = Bitmap.createScaledBitmap(BtEye,53 , 26  , true);
            copyBtEye = BtEye.copy(Bitmap.Config.ARGB_8888, true);
            copyBtEye = getResizedBitmap(copyBtEye, copyBtEye.getWidth() / 3, copyBtEye.getHeight() / 3);

            BtLips = BitmapFactory.decodeFile(faceTypePath.mouth_Path + finalTypeInt.finalLipsInt + ".png");
            //     Bitmap sizingBmp = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
            copyBtLips = BtLips.copy(Bitmap.Config.ARGB_8888, true);
            copyBtLips = getResizedBitmap(copyBtLips, copyBtLips.getWidth() / 13, copyBtLips.getHeight() / 13);
            BtNose = BitmapFactory.decodeFile(faceTypePath.nose_Path + finalTypeInt.finalNoseInt + ".png");
            copyBtNose = BtNose.copy(Bitmap.Config.ARGB_8888, true);
            copyBtNose = getResizedBitmap(copyBtNose, copyBtNose.getWidth() / 10, copyBtNose.getHeight() / 10);

//        BtEyeBrow = BitmapFactory.decodeFile(faceTypePath.eyes_brow_Path + 0 + ".png");
            //      copyBtEyeBrow = BtEyeBrow.copy(Bitmap.Config.ARGB_8888, true);

//        BtHair = BitmapFactory.decodeFile(faceTypePath.hair_Path + random + ".png");
//        copyBtHair = BtHair.copy(Bitmap.Config.ARGB_8888, true);
//        copyBtHair = getResizedBitmap(copyBtHair, copyBtHair.getWidth()/8, (copyBtHair.getHeight()/8) );
//        System.out.println("Btperface = "+BtFacePerRealFaceWidth+", "+BtFacePerRealFaceHeight);
            int eyeLX2 = ((copyBtFace.getWidth() / 2) - (copyBtEye.getWidth()) - 14);
            int eyeLY2 = ((copyBtFace.getHeight() / 2) - 40);
            System.out.println("eye position1 = " + eyeLX2 + ", " + eyeLY2);

            double eyeLX = eyePointX / BtFacePerRealFaceWidth - (copyBtEye.getWidth() / 2);
            double eyeLY = eyePointY / BtFacePerRealFaceHeight - (copyBtEye.getHeight() / 2);
            System.out.println("eye position1 = " + eyeLX + ", " + eyeLY);

            int eyeLX3 = eyePointX - (copyBtEye.getWidth() / 2);
            int eyeLY3 = eyePointY - (copyBtEye.getHeight() / 2);
            System.out.println("eye position2 = " + eyeLX3 + ", " + eyeLY3);

            double eyeRX = eye2PointX / BtFacePerRealFaceWidth - (copyBtEye.getWidth() / 2);
            double eyeRY = eye2PointY / BtFacePerRealFaceHeight - (copyBtEye.getHeight() / 2);

            double noseX = nosePointX / BtFacePerRealFaceWidth - (copyBtNose.getWidth() / 2);
            double noseY = nosePointY / BtFacePerRealFaceHeight - (copyBtNose.getHeight() / 2);

            double lipX = mouthPointX / BtFacePerRealFaceWidth - (copyBtLips.getWidth() / 2);
            double lipY = mouthPointY / BtFacePerRealFaceHeight - (copyBtLips.getHeight() / 2);


            // int eyebroRX = eyeRX;
            //int eyebroRY = eyeRY - 20;

            int eyebroLX = (int) eyeLX;
            int eyebroLY = (int) eyeLY - 20;

            //int earRX = eyeRX + 60;
            //int earRY = eyeRY - 14;

            int earLX = (int) eyeLX - 40;
            int earLY = (int) eyeLY - 14;
            Log.d("", "test" + copyBtHair);
            // pasteImage(copyBtFace, copyBtHair, -(faceWidth/14*1), 0);
            pasteImage(copyBtFace, copyBtLips, (int) lipX - faceWidth / 30, (int) lipY - faceHeight / 15);
            pasteImage(copyBtFace, copyBtNose, (int) noseX - faceWidth / 30, (int) noseY - faceHeight / 15);
            pasteImage(copyBtFace, copyBtEye, (int) eyeRX - faceWidth / 30, (int) eyeRY - faceHeight / 20);
            pasteImage(copyBtFace, chage(copyBtEye, 2), (int) eyeLX - faceWidth / 30, (int) eyeLY - faceHeight / 20);
            //      pasteImage(copyBtFace, copyBtEyeBrow, eyebroRX, eyebroRY);
            //      pasteImage(copyBtFace, chage(copyBtEyeBrow, 2), eyebroLX, eyebroLY);
            // pasteImage(copyBtFace, copyBtHair, 0, 0);

            faceTypePath = null;
            imgreadTypePath = null;
            compareTypeRect = null;
            compareTypeVar = null;
            finalTypeInt = null;
            faceCompare = null;
            eyeCompare = null;
            mouthCompare = null;
            noseCompare = null;

            System.gc();

            img.setImageBitmap(copyBtFace);

            /*Button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String name = "caricuture";
                    saveBitmap(copyBtFace1, "boom", name);
                }
            });*/


       }catch (Exception e){
            finish();
            startActivity(intent11);
            Toast.makeText(getApplicationContext(),"오류가 발생했습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    public void  EyeCompare(){
    }

    public static void pasteImage(Bitmap target, Bitmap src, int x, int y) {
        Canvas canvas = new Canvas(target);
        canvas.drawBitmap(src, x, y, null);
        //return bmOverlay;
    }

    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;

    public static Bitmap chage(Bitmap src, int type) {
        Matrix matrix = new Matrix();

        if (type == FLIP_VERTICAL) {
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if (type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();

        return resizedBitmap;
    }


    private void saveBitmap(Bitmap compliteFace, String folder, String name){
        String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath();
        // Get Absolute Path in External Sdcard
        String foler_name = "/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;

        Toast.makeText(getApplicationContext(),
                "저장 중입니다. 조금 시간이 오래 걸릴 수 있어요!", Toast.LENGTH_SHORT).show();

        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            compliteFace.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }
    public void go_home_onclide(View v){
        Intent intent = new Intent(W_CompareImage.this,
                MainActivity.class);

        finish();


        startActivity(intent);
    }
}


class W_Face_Type_Path{

    private String sd = Environment.getExternalStorageDirectory().getAbsolutePath();

    protected String face_Path= sd + "/" + "CCCam_Picture" +"/"+"girls_face"+"/"+"__";
    protected String mansHair_Path = sd + "/" + "CCCam_Picture" +"/"+"mans_hair"+"/"+"_hair";
    protected String girlsHair_Path = sd + "/" + "CCCam_Picture" +"/"+"girls_hair"+"/"+"_hair";
    protected String eye_Path = sd + "/" + "CCCam_Picture" +"/"+"eyes"+"/"+"eye_brow";
    protected String nose_Path = sd + "/" + "CCCam_Picture" +"/"+"nose"+"/"+"_";
    protected String mouth_Path = sd + "/" + "CCCam_Picture" +"/"+"mouth"+"/"+"_";
    protected String eyes_brow_Path = sd + "/" + "CCCam_Picture" +"/"+"eyes_brow"+"/"+"eyesbrow";
    protected String hair_Path = sd + "/" + "CCCam_Picture" +"/"+"mans_hair"+"/"+"_hair";

}

class W_Imgread_Type_Path{

    W_CompareImage w_compareImage = new W_CompareImage();
    Intent intent = w_compareImage.getIntent();

    protected String facePath;
    protected String eye1Path;
    protected String eye2Path;
    protected String nosePath ;
    protected String mouthPath;

}

class W_Compare_Type_Rect{

    protected double compareFaceRet = 0;
    protected double compareManHairRet = 0;
    protected double compareGirlHairRet = 0;
    protected double compareEye1Ret = 0;
    protected double compareEye2Ret = 0;
    protected double compareNoseRet = 0;
    protected double compareMouthRet = 0;

}

class W_Compare_Type_Var {

    protected double compareFaceVal = 10000;
    protected double compareManHairVal = 10000;
    protected double compareEye1Val = 10000;
    protected double compareEye2Val = 10000;
    protected double compareNoseVal = 10000;
    protected double compareMouthVal = 10000;

}

class W_Final_Type_INT {
    public int finalFaceInt = 0;
    public int finalEyeInt = 0;
    public int finalLipsInt = 0;
    public int finalNoseInt = 0;
}