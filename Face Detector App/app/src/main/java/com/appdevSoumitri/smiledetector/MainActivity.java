package com.appdevSoumitri.smiledetector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Frame;
import com.otaliastudios.cameraview.FrameProcessor;
import com.theartofdev.edmodo.cropper.CropImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FrameProcessor {

    private Facing cameraFacing=Facing.FRONT;
    private ImageView imageView;
    private CameraView faceDetectionCameraView;
    private RecyclerView bottomSheetRecyclerView;
    private BottomSheetBehavior bottomSheetBehavior;
    private ArrayList<FaceDetectorModel> faceDetectorModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        faceDetectorModels=new ArrayList<>();
        bottomSheetBehavior=BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        imageView=findViewById(R.id.face_detection_image_view);
        faceDetectionCameraView=findViewById(R.id.face_detection_camera_view);
        Button toggle=findViewById(R.id.face_detection_camera_toggle_button);
        FrameLayout bottomSheetButton=findViewById(R.id.bottom_sheet_button);
        bottomSheetRecyclerView=findViewById(R.id.bottom_sheet_recycler_view);

        // setting up the camera view from our library
        faceDetectionCameraView.setFacing(cameraFacing);
        faceDetectionCameraView.setLifecycleOwner(MainActivity.this);
        faceDetectionCameraView.addFrameProcessor(MainActivity.this);

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraFacing=(cameraFacing==Facing.FRONT) ? Facing.BACK : Facing.FRONT;
                faceDetectionCameraView.setFacing(cameraFacing);
                Log.d("Status ","Toggled");
            }
        });

        Log.d("Status ","Camera view set up!");

        // setting up the bottom sheet container
        bottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(MainActivity.this);
            }
        });
        bottomSheetRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        bottomSheetRecyclerView.setAdapter(new FaceDetectionAdapter(faceDetectorModels,MainActivity.this));
        Log.d("Status ","Bottom sheet adapter set up!");



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                assert result != null;
                Uri imageUri=result.getUri();
                try {
                    // function call that will analyse image features by ML-Kit
                    analyseImage(MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Status ","Exception thrown!");
                }
            }
        }
    }

    private void analyseImage(final Bitmap bitmap)
    {
        if(bitmap==null)
        {
            Toast.makeText(this, "There was an error!", Toast.LENGTH_SHORT).show();
            return;
        }
        imageView.setImageBitmap(null);
        faceDetectorModels.clear(); // starting from scratch here

        Objects.requireNonNull(bottomSheetRecyclerView.getAdapter())
                .notifyDataSetChanged();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        showProgress();

        // invoking Firebase Vision stuffs for analysing

        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        FirebaseVisionFaceDetector faceDetector =
                FirebaseVision.getInstance()
                .getVisionFaceDetector(options);

        faceDetector.detectInImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                            Log.d("Status:","Success! we have received a list of faces in the bitmap");

                            // create a bitmap that can be mutated/changed to suit our purposes
                            Bitmap mutableImage=bitmap.copy(Bitmap.Config.ARGB_8888,true);

                            detectFaces(firebaseVisionFaces,mutableImage);

                            imageView.setImageBitmap(mutableImage);

                            hideProgress();
                            // show the details of the image
                            Objects.requireNonNull(bottomSheetRecyclerView.getAdapter()).notifyDataSetChanged();
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            Log.d("Status: ","Big Success !");
                            Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Status:","Failure");
                        hideProgress();
                    }
                    });

    }

    private void detectFaces(List<FirebaseVisionFace> firebaseVisionFaces,
                             Bitmap bitmap)
    {
        if(firebaseVisionFaces==null || bitmap==null)
        {
            Toast.makeText(this, "There was an error", Toast.LENGTH_SHORT).show();
            return;
        }

        Canvas canvas=new Canvas(bitmap);

        Paint facePaint=new Paint();
        facePaint.setColor(Color.GREEN);
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(4.5f);

        Paint faceTextPaint=new Paint();
        faceTextPaint.setColor(Color.BLUE);
        faceTextPaint.setTypeface(Typeface.SANS_SERIF);
        faceTextPaint.setTextSize(25f);

        // marking the main features of the face
        Paint landmarkPaint=new Paint();
        landmarkPaint.setColor(Color.RED);
        landmarkPaint.setStyle(Paint.Style.FILL);
        landmarkPaint.setStrokeWidth(7f);

        for(int i=0;i<firebaseVisionFaces.size();i++)
        {
            // to draw distinctive borders around the faces detected

            FirebaseVisionFace face=firebaseVisionFaces.get(i);

            canvas.drawRect(face.getBoundingBox(),facePaint);

            canvas.drawText("Face "+ (i+1),
                    (face.getBoundingBox().centerX()
                    - (face.getBoundingBox().width()>>1) + 8f),
                    (face.getBoundingBox().centerY()
                    - (face.getBoundingBox().height()>>1) - 8f),
                    facePaint);
            Log.d("Status: ","Boxes drawn around faces");

            if(face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)!=null) {
                FirebaseVisionFaceLandmark faceFeature=face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);

                // draw a little circle on left eye
                assert faceFeature != null;
                canvas.drawCircle(
                        faceFeature.getPosition().getX(),
                        faceFeature.getPosition().getY(),
                        7.5f,landmarkPaint);
            }
            if(face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)!=null) {
                FirebaseVisionFaceLandmark faceFeature=face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);

                // draw a little circle on right eye
                assert faceFeature != null;
                canvas.drawCircle(
                        faceFeature.getPosition().getX(),
                        faceFeature.getPosition().getY(),
                        7.5f,landmarkPaint);
            }
            if(face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)!=null) {
                FirebaseVisionFaceLandmark faceFeature=face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE);

                // draw a little circle on nose base
                assert faceFeature != null;
                canvas.drawCircle(
                        faceFeature.getPosition().getX(),
                        faceFeature.getPosition().getY(),
                        7.5f,landmarkPaint);
            }
            if(face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)!=null) {
                FirebaseVisionFaceLandmark faceFeature=face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);

                // draw a little circle on left ear
                assert faceFeature != null;
                canvas.drawCircle(
                        faceFeature.getPosition().getX(),
                        faceFeature.getPosition().getY(),
                        7.5f,landmarkPaint);
            }
            if(face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)!=null) {
                FirebaseVisionFaceLandmark faceFeature=face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);

                // draw a little circle on right ear
                assert faceFeature != null;
                canvas.drawCircle(
                        faceFeature.getPosition().getX(),
                        faceFeature.getPosition().getY(),
                        7.5f,landmarkPaint);
            }
            /* For mouth it is tricky as you need to check for all features identified for mouth */
            if(face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)!=null
            && face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)!=null
            && face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)!=null)
            {
                FirebaseVisionFaceLandmark leftMouth=face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT);
                FirebaseVisionFaceLandmark rightMouth=face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT);
                FirebaseVisionFaceLandmark bottomMouth=face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM);

                assert bottomMouth != null;
                assert leftMouth != null;
                assert rightMouth != null;

                canvas.drawLine(
                        leftMouth.getPosition().getX(),
                        leftMouth.getPosition().getY(),
                        bottomMouth.getPosition().getX(),
                        bottomMouth.getPosition().getY(),
                        landmarkPaint);

                canvas.drawLine(
                        bottomMouth.getPosition().getX(),
                        bottomMouth.getPosition().getY(),
                        rightMouth.getPosition().getX(),
                        rightMouth.getPosition().getY(),
                        landmarkPaint);
            }
            // putting in the probabilities
            faceDetectorModels.add(new FaceDetectorModel(i+1,"Smiling probability: "
                                    + face.getSmilingProbability()));
            faceDetectorModels.add(new FaceDetectorModel(i+1,"Left eye open probability: "
                                    + face.getLeftEyeOpenProbability()));
            faceDetectorModels.add(new FaceDetectorModel(i+1,"Right eye open probability: "
                                    + face.getRightEyeOpenProbability()));

        } // end of for loop

    }

    private void showProgress()
    {
        findViewById(R.id.bottom_sheet_button_image).setVisibility(View.GONE);
        findViewById(R.id.bottom_sheet_button_progressbar).setVisibility(View.VISIBLE);
    }

    private void hideProgress()
    {
        findViewById(R.id.bottom_sheet_button_image).setVisibility(View.VISIBLE);
        findViewById(R.id.bottom_sheet_button_progressbar).setVisibility(View.GONE);
    }

    @Override
    public void process(@NonNull Frame frame) {
        // get our frame where image will be
        final int width=frame.getSize().getWidth();
        final int height=frame.getSize().getHeight();

        FirebaseVisionImageMetadata metadata=new FirebaseVisionImageMetadata
                .Builder()
                .setHeight(height)
                .setWidth(width)
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation(
                        (cameraFacing==Facing.FRONT) ?
                                 FirebaseVisionImageMetadata.ROTATION_270 :
                                 FirebaseVisionImageMetadata.ROTATION_90
                ).build();

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage
                .fromByteArray(frame.getData(), metadata);
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build();

        /*FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        faceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        imageView.setImageBitmap(null);

                        Bitmap bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        Paint dotPaint = new Paint();
                        dotPaint.setColor(Color.RED);
                        dotPaint.setStyle(Paint.Style.FILL);
                        dotPaint.setStrokeWidth(3f);

                        Paint linePaint = new Paint();
                        linePaint.setColor(Color.GREEN);
                        linePaint.setStyle(Paint.Style.STROKE);
                        linePaint.setStrokeWidth(2f);

                        for (FirebaseVisionFace face : firebaseVisionFaces) {
                            List<FirebaseVisionPoint> faceContours = face.getContour(
                                    FirebaseVisionFaceContour.FACE
                            ).getPoints();
                            for (int i = 0; i < faceContours.size(); i++) {
                                FirebaseVisionPoint faceContour = null;
                                if (i != (faceContours.size() - 1)) {
                                    faceContour = faceContours.get(i);
                                    canvas.drawLine(faceContour.getX(),
                                            faceContour.getY(),
                                            faceContours.get(i + 1).getX(),
                                            faceContours.get(i + 1).getY(),
                                            linePaint

                                    );

                                } else {
                                    assert faceContour != null;
                                    canvas.drawLine(faceContour.getX(),
                                            faceContour.getY(),
                                            faceContours.get(0).getX(),
                                            faceContours.get(0).getY(),
                                            linePaint);
                                }
                                canvas.drawCircle(faceContour.getX(),
                                        faceContour.getY(),
                                        4f,
                                        dotPaint );
                            }

                            List<FirebaseVisionPoint> leftEyebrowTopCountours = face.getContour(
                                    FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).getPoints();
                            for (int i = 0; i < leftEyebrowTopCountours.size(); i++) {
                                FirebaseVisionPoint contour = leftEyebrowTopCountours.get(i);
                                if (i != (leftEyebrowTopCountours.size() - 1))
                                    canvas.drawLine(contour.getX(), contour.getY(), leftEyebrowTopCountours.get(i + 1).getX(),leftEyebrowTopCountours.get(i + 1).getY(), linePaint);
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }

                            List<FirebaseVisionPoint> rightEyebrowTopCountours = face.getContour(
                                    FirebaseVisionFaceContour. RIGHT_EYEBROW_TOP).getPoints();
                            for (int i = 0; i < rightEyebrowTopCountours.size(); i++) {
                                FirebaseVisionPoint contour = rightEyebrowTopCountours.get(i);
                                if (i != (rightEyebrowTopCountours.size() - 1))
                                    canvas.drawLine(contour.getX(), contour.getY(), rightEyebrowTopCountours.get(i + 1).getX(),rightEyebrowTopCountours.get(i + 1).getY(), linePaint);
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }

                            List<FirebaseVisionPoint> rightEyebrowBottomCountours = face.getContour(
                                    FirebaseVisionFaceContour. RIGHT_EYEBROW_BOTTOM).getPoints();
                            for (int i = 0; i < rightEyebrowBottomCountours.size(); i++) {
                                FirebaseVisionPoint contour = rightEyebrowBottomCountours.get(i);
                                if (i != (rightEyebrowBottomCountours.size() - 1))
                                    canvas.drawLine(contour.getX(), contour.getY(), rightEyebrowBottomCountours.get(i + 1).getX(),rightEyebrowBottomCountours.get(i + 1).getY(), linePaint);
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }

                            List<FirebaseVisionPoint> leftEyeContours = face.getContour(
                                    FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                            for (int i = 0; i < leftEyeContours.size(); i++) {
                                FirebaseVisionPoint contour = leftEyeContours.get(i);
                                if (i != (leftEyeContours.size() - 1)){
                                    canvas.drawLine(contour.getX(), contour.getY(), leftEyeContours.get(i + 1).getX(),leftEyeContours.get(i + 1).getY(), linePaint);

                                }else {
                                    canvas.drawLine(contour.getX(), contour.getY(), leftEyeContours.get(0).getX(),
                                            leftEyeContours.get(0).getY(), linePaint);
                                }
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);


                            }

                            List<FirebaseVisionPoint> rightEyeContours = face.getContour(
                                    FirebaseVisionFaceContour.RIGHT_EYE).getPoints();
                            for (int i = 0; i < rightEyeContours.size(); i++) {
                                FirebaseVisionPoint contour = rightEyeContours.get(i);
                                if (i != (rightEyeContours.size() - 1)){
                                    canvas.drawLine(contour.getX(), contour.getY(), rightEyeContours.get(i + 1).getX(),rightEyeContours.get(i + 1).getY(), linePaint);

                                }else {
                                    canvas.drawLine(contour.getX(), contour.getY(), rightEyeContours.get(0).getX(),
                                            rightEyeContours.get(0).getY(), linePaint);
                                }
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);


                            }

                            List<FirebaseVisionPoint> upperLipTopContour = face.getContour(
                                    FirebaseVisionFaceContour.UPPER_LIP_TOP).getPoints();
                            for (int i = 0; i < upperLipTopContour.size(); i++) {
                                FirebaseVisionPoint contour = upperLipTopContour.get(i);
                                if (i != (upperLipTopContour.size() - 1)){
                                    canvas.drawLine(contour.getX(), contour.getY(),
                                            upperLipTopContour.get(i + 1).getX(),
                                            upperLipTopContour.get(i + 1).getY(), linePaint);
                                }
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }

                            List<FirebaseVisionPoint> upperLipBottomContour = face.getContour(
                                    FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();
                            for (int i = 0; i < upperLipBottomContour.size(); i++) {
                                FirebaseVisionPoint contour = upperLipBottomContour.get(i);
                                if (i != (upperLipBottomContour.size() - 1)){
                                    canvas.drawLine(contour.getX(), contour.getY(), upperLipBottomContour.get(i + 1).getX(),upperLipBottomContour.get(i + 1).getY(), linePaint);
                                }
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }
                            List<FirebaseVisionPoint> lowerLipTopContour = face.getContour(
                                    FirebaseVisionFaceContour.LOWER_LIP_TOP).getPoints();
                            for (int i = 0; i < lowerLipTopContour.size(); i++) {
                                FirebaseVisionPoint contour = lowerLipTopContour.get(i);
                                if (i != (lowerLipTopContour.size() - 1)){
                                    canvas.drawLine(contour.getX(), contour.getY(), lowerLipTopContour.get(i + 1).getX(),lowerLipTopContour.get(i + 1).getY(), linePaint);
                                }
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }
                            List<FirebaseVisionPoint> lowerLipBottomContour = face.getContour(
                                    FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).getPoints();
                            for (int i = 0; i < lowerLipBottomContour.size(); i++) {
                                FirebaseVisionPoint contour = lowerLipBottomContour.get(i);
                                if (i != (lowerLipBottomContour.size() - 1)){
                                    canvas.drawLine(contour.getX(), contour.getY(), lowerLipBottomContour.get(i + 1).getX(),lowerLipBottomContour.get(i + 1).getY(), linePaint);
                                }
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }

                            List<FirebaseVisionPoint> noseBridgeContours = face.getContour(
                                    FirebaseVisionFaceContour.NOSE_BRIDGE).getPoints();
                            for (int i = 0; i < noseBridgeContours.size(); i++) {
                                FirebaseVisionPoint contour = noseBridgeContours.get(i);
                                if (i != (noseBridgeContours.size() - 1)) {
                                    canvas.drawLine(contour.getX(), contour.getY(), noseBridgeContours.get(i + 1).getX(),noseBridgeContours.get(i + 1).getY(), linePaint);
                                }
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }

                            List<FirebaseVisionPoint> noseBottomContours = face.getContour(
                                    FirebaseVisionFaceContour.NOSE_BOTTOM).getPoints();
                            for (int i = 0; i < noseBottomContours.size(); i++) {
                                FirebaseVisionPoint contour = noseBottomContours.get(i);
                                if (i != (noseBottomContours.size() - 1)) {
                                    canvas.drawLine(contour.getX(), contour.getY(), noseBottomContours.get(i + 1).getX(),noseBottomContours.get(i + 1).getY(), linePaint);
                                }
                                canvas.drawCircle(contour.getX(), contour.getY(), 4f, dotPaint);

                            }
                            if (cameraFacing == Facing.FRONT) {
                                //Flip image!
                                Matrix matrix = new Matrix();
                                matrix.preScale(-1f, 1f);
                                Bitmap flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                        bitmap.getWidth(), bitmap.getHeight(),
                                        matrix, true);
                                imageView.setImageBitmap(flippedBitmap);
                            }else
                                imageView.setImageBitmap(bitmap);
                        }//end forloop


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageBitmap(null);

            }
        });*/
    }

}
