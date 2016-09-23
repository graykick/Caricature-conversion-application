package kr.hs.dsm.appb.idiot;

/**
 * Created by dsm_024 on 2016-05-31.
 */

import android.app.Activity;
import android.app.ProgressDialog;
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

    public Bitmap copyBtFace;

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

        final ImageView img = (ImageView) findViewById(R.id.imageView);
        ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);

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

        Intent intent = getIntent();


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
                if (progDialog.isShowing()){
                    progDialog.setProgress(2);
                    System.out.println("프로그래스바 동작");
                }
            }
        }).start();


        imgreadTypePath.facePath = intent.getStringExtra("face");
        imgreadTypePath.eye1Path = intent.getStringExtra("eye1");
        imgreadTypePath.eye2Path = intent.getStringExtra("eye2");
        imgreadTypePath.nosePath =  intent.getStringExtra("nose");
        imgreadTypePath.mouthPath = intent.getStringExtra("mouth");

        final Mat face = Imgcodecs.imread(imgreadTypePath.facePath);
        final Mat eye1 = Imgcodecs.imread(imgreadTypePath.eye1Path);
        final Mat nose = Imgcodecs.imread(imgreadTypePath.nosePath);
        final Mat mouth = Imgcodecs.imread(imgreadTypePath.mouthPath);

        Mat compareFace = new Mat();
        Mat compareEye1 = new Mat();
        Mat compareEye2 = new Mat();
        Mat compareNose = new Mat();
        Mat compareMouth = new Mat();

        Mat finalEye1 = null;
        Mat finalEye2 = null;
        Mat finalNose = null;
        Mat finalMouth = null;

        temp = face;

        progDialog.setProgress(5);

        for (int i = 0; i < 100; i++) {

            compareFace = Imgcodecs.imread(faceTypePath.face_Path+i+ ".png");
            compareEye1 = Imgcodecs.imread(faceTypePath.eye_Path+ i + ".png");
            compareNose = Imgcodecs.imread(faceTypePath.nose_Path+ i + ".png");
            compareMouth = Imgcodecs.imread(faceTypePath.mouth_Path + i + ".png");

            if (compareFace.dataAddr() != 0x0) {
                try {
                    compareTypeRect.compareFaceRet = faceCompare.compareFeature(face, compareFace);
                } catch (Exception e) {
                    System.out.println("Face Exception");
                }
            }

            if (compareEye1.dataAddr() != 0x0) {
                try {
                    compareTypeRect.compareEye1Ret = eyeCompare.compareFeature(eye1, compareEye1);
                } catch (Exception e) {
                    System.out.println("Eye Exception");
                }
            }

            if (compareMouth.dataAddr() != 0x0) {
                try {
                    compareTypeRect.compareMouthRet = mouthCompare.compareFeature(mouth, compareMouth);
                } catch (Exception e) {
                    System.out.println("Mouth Exception");
                }

            }

            if (compareNose.dataAddr() != 0x0) {
                try {
                    compareTypeRect.compareNoseRet = noseCompare.compareFeature(nose, compareMouth);
                } catch (Exception e) {
                    System.out.println("Nose Exception");
                }
            }

            progDialog.setProgress(20);

            System.out.println(i + "번째 얼굴 avg = " + compareTypeRect.compareFaceRet);
            if (compareTypeRect.compareFaceRet > 0 && compareTypeRect.compareFaceRet < compareTypeVar.compareFaceVal) {

                compareTypeVar.compareFaceVal = compareTypeRect.compareFaceRet;
                finalTypeInt.finalFaceInt = i;
                System.out.println("i와 fanalFaceInt" + i + " and " + finalTypeInt.finalFaceInt);
            }
            System.out.println(i + "번째 눈 avg = " + compareTypeRect.compareEye1Ret);
            if (compareTypeRect.compareEye1Ret > 0 && compareTypeRect.compareEye1Ret < compareTypeVar.compareEye1Val) {
                finalEye1 = compareEye1;
                compareTypeVar.compareEye1Val = compareTypeRect.compareEye1Ret;
                finalTypeInt.finalEyeInt = i;
                System.out.println("i와 fanalEyeInt" + i + " and " + finalTypeInt.finalEyeInt);
            }
            System.out.println(i + "번째 입술 avg = " + compareTypeRect.compareMouthRet);
            if (compareTypeRect.compareMouthRet != 0 && compareTypeRect.compareMouthRet < compareTypeVar.compareMouthVal) {
                finalMouth = compareMouth;
                compareTypeVar.compareMouthVal = compareTypeRect.compareMouthRet;
                System.out.println("i와 fanalLips" + i + " and " + finalTypeInt.finalLipsInt);
                finalTypeInt.finalLipsInt = i;
            }

            System.out.println(i + "번째 코 avg = " + compareTypeRect.compareNoseRet);
            if (compareTypeRect.compareNoseRet != 0 && compareTypeRect.compareNoseRet < compareTypeVar.compareNoseVal) {
                compareTypeVar.compareNoseVal = compareTypeRect.compareNoseRet;
                System.out.println("i와 fanalNose" + i + " and " + finalTypeInt.finalNoseInt);
                finalTypeInt.finalNoseInt = i;
            }

            progDialog.setProgress(40);

            if (compareTypeRect.compareEye2Ret != 0 && compareTypeRect.compareEye2Ret < compareTypeVar.compareEye2Val ) {
                finalEye2 = compareEye2;
                compareTypeVar.compareEye2Val = compareTypeRect.compareEye2Ret;
            }

            if (compareTypeRect.compareNoseRet != 0 && compareTypeRect.compareNoseRet < compareTypeVar.compareNoseVal ) {
                finalNose = compareNose;
                compareTypeVar.compareNoseVal = compareTypeRect.compareNoseRet;
            }
            if (compareTypeRect.compareMouthRet != 0 && compareTypeRect.compareMouthRet < compareTypeVar.compareMouthVal ) {
                finalMouth = compareMouth;
                compareTypeVar.compareMouthVal = compareTypeRect.compareMouthRet;
            }
        }
        int random = (int) (Math.random() * 31) + 1;


        System.out.println("가장 유사한 FACE 번호: " + finalTypeInt.finalFaceInt);
        System.out.println("가장 유사한 EYE 번호 : " + finalTypeInt.finalEyeInt);
        System.out.println("가장 유사한 Lips 번호 : " + finalTypeInt.finalLipsInt);
        System.out.println("가장 유사한 Noes 번호 : " + finalTypeInt.finalNoseInt);
        System.out.println("랜덤 hair 번호 : " + random);


        if(finalTypeInt.finalEyeInt ==0){
            finalTypeInt.finalEyeInt = (int)(Math.random()*30+1);
            Toast.makeText(getApplicationContext(),
                    "눈 랜덤", Toast.LENGTH_SHORT).show();
            System.out.println("눈 랜덤");
        }
        if(finalTypeInt.finalFaceInt ==0){
            finalTypeInt.finalFaceInt = (int)(Math.random()*10+1);
            Toast.makeText(getApplicationContext(),
                    "얼굴랜덤", Toast.LENGTH_SHORT).show();
            System.out.println("알굴 랜덤");
        }

        if(finalTypeInt.finalNoseInt ==0){
            finalTypeInt.finalNoseInt = (int)(Math.random()*7+1);
            Toast.makeText(getApplicationContext(),
                    "코 랜덤", Toast.LENGTH_SHORT).show();
            System.out.println("코 랜덤");
        }

        if(finalTypeInt.finalLipsInt ==0){
            finalTypeInt.finalLipsInt = (int)(Math.random()*10+1);
            Toast.makeText(getApplicationContext(),
                    "입 랜덤", Toast.LENGTH_SHORT).show();
            System.out.println("입 랜덤");
        }

        progDialog.setProgress(65);

        Bitmap BtFace,BtNose, copyBtNose, BtEyeBrow, copyBtEyeBrow, BtHair, copyBtHair, BtEye, copyBtEye, BtLips, copyBtLips;

        BtFace = BitmapFactory.decodeFile(faceTypePath.face_Path + finalTypeInt.finalFaceInt + ".png");
        final Bitmap copyBtFace = BtFace.copy(Bitmap.Config.ARGB_8888, true);

        BtEye = BitmapFactory.decodeFile(faceTypePath.eye_Path+finalTypeInt.finalEyeInt+ ".png");
        System.out.println("dance Bteye "+BtEye.getWidth()+", "+BtEye.getHeight());
        //  Bitmap copyBtEye = Bitmap.createScaledBitmap(BtEye,53 , 26  , true);
        copyBtEye = BtEye.copy(Bitmap.Config.ARGB_8888, true);
        System.out.println("dance copyBteye "+copyBtEye.getWidth()+", "+copyBtEye.getHeight());

        BtLips = BitmapFactory.decodeFile(faceTypePath.mouth_Path+ finalTypeInt.finalLipsInt + ".png");
        //     Bitmap sizingBmp = Bitmap.createScaledBitmap(bitmap, (int) width, (int) height, true);
        copyBtLips = BtLips.copy(Bitmap.Config.ARGB_8888, true);

        BtNose = BitmapFactory.decodeFile(faceTypePath.nose_Path + finalTypeInt.finalNoseInt + ".png");
        copyBtNose = BtNose.copy(Bitmap.Config.ARGB_8888, true);

        BtEyeBrow = BitmapFactory.decodeFile(faceTypePath.eyes_brow_Path+ 0 + ".png");
        copyBtEyeBrow = BtEyeBrow.copy(Bitmap.Config.ARGB_8888, true);

        BtHair = BitmapFactory.decodeFile(faceTypePath.hair_Path+ random + ".png");
        copyBtHair = BtHair.copy(Bitmap.Config.ARGB_8888, true);

        progDialog.setProgress(80);

        int eyeLX = ((copyBtFace.getWidth() / 2)-(copyBtEye.getWidth())-14);
        int eyeLY = ((copyBtFace.getHeight() / 2) - 40);

        int eyeRX = ((copyBtFace.getWidth() / 2)+14);
        int eyeRY = ((copyBtFace.getHeight() / 2) - 40);

        int noseX = (copyBtFace.getWidth()/2)-(copyBtNose.getWidth()/2);
        int noseY = (copyBtFace.getHeight()/2-40);

        int lipX = ((copyBtFace.getWidth() / 2)-(copyBtLips.getWidth()/2));
        int lipY = (noseY+copyBtNose.getHeight()+10);

        int eyebroRX = eyeRX;
        int eyebroRY = eyeRY-20;

        int eyebroLX = eyeLX;
        int eyebroLY = eyeLY-20;

        int earRX = eyeRX+60;
        int earRY = eyeRY-14;

        int earLX = eyeLX-40;
        int earLY = eyeLY-14;

        pasteImage(copyBtFace, copyBtLips, lipX, lipY);
        pasteImage(copyBtFace, copyBtNose, noseX, noseY);
        pasteImage(copyBtFace, copyBtEye, eyeRX, eyeRY);
        pasteImage(copyBtFace, chage(copyBtEye, 2), eyeLX, eyeLY);
        pasteImage(copyBtFace, copyBtEyeBrow, eyebroRX, eyebroRY);
        pasteImage(copyBtFace, chage(copyBtEyeBrow, 2), eyebroLX, eyebroLY);
        pasteImage(copyBtFace,copyBtHair,0,0);


        progDialog.setProgress(100);

        img.setImageBitmap(copyBtFace);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = "caricuture";
                saveBitmap(copyBtFace,"boom", name);
            }
        });
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
        startActivity(intent);
        finish();
    }
}

class W_Face_Type_Path{

    private String sd = Environment.getExternalStorageDirectory().getAbsolutePath();

    protected String face_Path= sd + "/" + "CCCam_Picture" +"/"+"faces"+"/"+"face";
    protected String eye_Path = sd + "/" + "CCCam_Picture" +"/"+"eyes"+"/"+"eyes";
    protected String nose_Path = sd + "/" + "CCCam_Picture" +"/"+"nose"+"/"+"nose";
    protected String mouth_Path = sd + "/" + "CCCam_Picture" +"/"+"mouth"+"/"+"mouth";
    protected String eyes_brow_Path = sd + "/" + "CCCam_Picture" +"/"+"eyes_brow"+"/"+"eyesbrow";
    protected String hair_Path = sd + "/" + "CCCam_Picture" +"/"+"hair"+"/"+"woman/";

}

class W_Imgread_Type_Path{

    CompareImage compareImage = new CompareImage();
    Intent intent = compareImage.getIntent();

    protected String facePath;
    protected String eye1Path;
    protected String eye2Path;
    protected String nosePath ;
    protected String mouthPath;

}

class W_Compare_Type_Rect{

    protected double compareFaceRet = 0;
    protected double compareEye1Ret = 0;
    protected double compareEye2Ret = 0;
    protected double compareNoseRet = 0;
    protected double compareMouthRet = 0;

}

class W_Compare_Type_Var {

    protected double compareFaceVal = 10000;
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