package com.example.covidmc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.TextureView;
import android.view.Surface;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.CountDownTimer;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity  {
    private Button button3;
    private Button button2;
    private Button button;
    SQLiteDatabase db;
    private RespirationMonitor accelerometer;
    private TextView countdownText;
    private CountDownTimer countDownTimer;
    private int timeNeeded = 45000;
    private long timeLeftInMilliseconds = timeNeeded;
    private boolean timerRunning;
    private int storeResult;
    private int c = 0;
    private Uri fileUri;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    public static MainActivity ActivityContext = null;
    public static TextView output;
    public String pathh;
    private final CameraView mainCameraView = new CameraView(this);
    private RedValue mainRedValue;
    public static String mainHeartRate = "0";
    private CountDownTimer mainTimer;
    private int mainDetectedValleys = 0;
    private int mainTicksPassed = 0;
    private CopyOnWriteArrayList<Long> mainTroughs;
    public static Integer mainRespRate=0;
    public long millisUntilFinishedH;






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String []{Manifest.permission.CAMERA}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String []{Manifest.permission.READ_EXTERNAL_STORAGE}, 1
            );
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


        }


        countdownText = findViewById(R.id.countdown_text);
        accelerometer = new RespirationMonitor(this);
        pathh= getExternalFilesDir(null).getAbsolutePath() + "/FingerTip.mp4";
        Log.e("", ""+pathh);
        accelerometer.setListener(new RespirationMonitor.Listener() {

            @Override
            public void onTranslation(float tz) {
                Log.i("", "Current Value: " + c + " Time Left: " + timeLeftInMilliseconds);
                if (timerRunning == true) {
                    if (tz > 0.08f) {
                        c++;
                        storeResult = c;
                    }
                }
            }

        });
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button2.getText().toString() == "Done") {
                    button2.setText("Monitor Respiratory Rate");
                    timeLeftInMilliseconds = timeNeeded;
                    storeResult = 0;
                    c = 0;
                } else {
                    accelerometer.register();
                    startTimer();
                }
            }


        });
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCameraView();
//                startTimer();
                mainTimer.start();

            }
        });

        button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Calling","You");
                Intent intent = new Intent(MainActivity.this, SymptomTracker.class);
                startActivity(intent);

                try {

                    db = SQLiteDatabase.openOrCreateDatabase(getExternalFilesDir(null).getAbsolutePath() + "/records.db", null);
                    db.beginTransaction();
                    try {
                        //perform your database operations here ...
                        db.execSQL("create table if not exists monitor ("
                                + " ID integer PRIMARY KEY autoincrement, "
                                + " HeartRate string, "
                                + " RespiratoryRate integer, "
                                + " Fever float, "
                                + " ShortnessOfBreath float, "
                                + " Headache float, "
                                + " Vomiting float, "
                                + " Cough float, "
                                + " Diarhhea float, "
                                + " SoreThroat float, "
                                + " FeelingTired float, "
                                + " MuscleAche float, "
                                + " LossOfSmell float ); ");

                        db.setTransactionSuccessful(); //commit your changes
                    } catch (SQLiteException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        db.endTransaction();
                    }
//                    Intent intent = new Intent(MainActivity.this, SymptomTracker.class);
//                    float results[] = new float[1];
//                    results[0] = storeResult*60/45 ;
//                    intent.putExtra("results", results);
                } catch (SQLException e) {

                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initCameraView() {

        ((Button) findViewById(R.id.button)).setText("Calculating");
        //((TextView) findViewById(R.id.heartrate_textview)).setText(mainTicksPassed);
        mainTroughs = new CopyOnWriteArrayList<>();
        TextureView cameraTextureView = findViewById(R.id.video);

        SurfaceTexture previewSurfaceTexture = cameraTextureView.getSurfaceTexture();
        if (previewSurfaceTexture != null) {

            Surface previewSurface = new Surface(previewSurfaceTexture);

            mainCameraView.start(previewSurface);
            measureHeartbeat(cameraTextureView);
        }
    }

    private boolean isTrough() {
        final int windowSize = 13;
        CopyOnWriteArrayList<Redcomponent<Integer>> subList = mainRedValue.getWindow(windowSize);
        if (subList.size() < windowSize) {
            return false;
        } else {
            Integer referenceValue = subList.get((int) Math.ceil(windowSize / 2)).redValue;

            for (Redcomponent<Integer> redComponent : subList) {
                if (redComponent.redValue < referenceValue) return false;
            }
            return (!subList.get((int) Math.ceil(windowSize / 2)).redValue.equals(
                    subList.get((int) Math.ceil(windowSize / 2) - 1).redValue));
        }
    }

    void measureHeartbeat(final TextureView textureView) {

        mainRedValue = new RedValue();
        mainDetectedValleys = 0;
        mainTimer = new CountDownTimer(50000, 45) {
            @Override
            public void onTick(final long millisUntilFinished) {
                millisUntilFinishedH = millisUntilFinished;
                if ((++mainTicksPassed * 45) < 3500) return;

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int pixelCount = textureView.getWidth() * textureView.getHeight();
                        int[] pixels = new int[pixelCount];
                        Bitmap currentBitmap = textureView.getBitmap();
                        currentBitmap.getPixels(pixels, 0, textureView.getWidth(), 0, 0, textureView.getWidth(), textureView.getHeight());

                        int redComponent = 0;
                        for (int pixelIndex = 0; pixelIndex < pixelCount; pixelIndex++) {
                            redComponent += (pixels[pixelIndex] >> 16) & 0xff;
                        }

                        mainRedValue.add(redComponent);

                        if (isTrough()) {
                            mainDetectedValleys = mainDetectedValleys + 1;
                            mainTroughs.add(mainRedValue.getLastTimestamp().getTime());
                        }
                    }
                });
                thread.start();
                updateTimerH();
            }

            @Override
            public void onFinish() {
                mainHeartRate = String.valueOf(60f * (mainDetectedValleys - 1) / (Math.max(1, (mainTroughs.get(mainTroughs.size() - 1) - mainTroughs.get(0)) / 1000f)));
                String currentValue = String.format(Locale.getDefault(),"Heart Rate: " + mainHeartRate);
                ((TextView) findViewById(R.id.heartrate_textview)).setText(currentValue);
                button.setText("Done");
                mainCameraView.stop();
            }
        };
    }

    void stop() {
        if (mainTimer != null) {
            mainTimer.cancel();
        }
    }
    public void startStop(){
        if(timerRunning){
            stopTimer();
        }
        else{
            startTimer();
        }
    }
    public void startTimer(){
        countDownTimer= new CountDownTimer(timeLeftInMilliseconds,1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilliseconds= l;
                updateTimer();

            }

            @Override
            public void onFinish() {

            }
        }.start();
        button2.setText("Calculating");
        timerRunning=true;
    }
    public void stopTimer(){
        Log.i("","Result: "+storeResult);
        countDownTimer.cancel();
        button2.setText("Done");
        timerRunning=false;
        mainRespRate=(int)(storeResult*60/45);
        accelerometer.unregister();
        countdownText.setText("Rate: "+mainRespRate);


    }
    public void updateTimer(){
        int seconds=(int)timeLeftInMilliseconds/1000;
        String timeLeftText;
        timeLeftText=""+seconds;
        countdownText.setText(timeLeftText);
        if (seconds<=0){
            stopTimer();
        }
    }
    public void updateTimerH(){
        int seconds=(int)millisUntilFinishedH/1000;
        String timeLeftText = String.valueOf(seconds);
        ((TextView) findViewById(R.id.heartrate_textview)).setText(timeLeftText);
    }


    public void openSymptomTracker() {
        Intent intent = new Intent(this, SymptomTracker.class);
        startActivity(intent);
    }


}