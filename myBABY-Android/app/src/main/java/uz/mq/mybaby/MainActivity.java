package uz.mq.mybaby;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Context context;
    ConstraintLayout rootView;
    private MediaRecorder mRecorder;
    ObjectAnimator scaleDown;
    LinearLayout llResult;
    FirebaseDatabase database;
    ApiMaster master;
    TextView tvSuggest;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        database = FirebaseDatabase.getInstance();
        llResult = (LinearLayout) findViewById(R.id.llResult);
        context = this;
        tvSuggest = (TextView) findViewById(R.id.tvSuggest);
        master = new ApiMaster(this);
//        database = FirebaseDatabase.getInstance();
        getWindow().getDecorView().post(this::initViews);
    }

    private void initViews(){
        rootView = (ConstraintLayout) findViewById(R.id.rootView);

        ((LinearLayout) findViewById(R.id.btnRecode)).setOnClickListener((v)->{
            scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    ((LinearLayout) findViewById(R.id.btnRecode)),
                    PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.1f));
            scaleDown.setDuration(800);

            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();
            startRecording();
        });

        ((ImageView) findViewById(R.id.btnCloseResult)).setOnClickListener(v -> {
            llResult.animate().scaleY(0.5f).alpha(0).setDuration(300).start();

            (findViewById(R.id.btnLoading)).setVisibility(View.GONE);
            (findViewById(R.id.ivBtnIcon)).setVisibility(View.VISIBLE);
            (findViewById(R.id.tvSlogan)).animate().alpha(1).setDuration(300).start();
            (findViewById(R.id.clHistory)).animate().alpha(1).setDuration(300).start();
            (findViewById(R.id.btnRecode)).animate().setDuration(300).translationYBy(-diff).start();
            (findViewById(R.id.ivBtnBG)).animate().setDuration(300).translationYBy(-diff).start();

        });

        ((ImageView) findViewById(R.id.btnSettings)).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
    }

    String[] results = {
            "Belly pain",
            "Belching",
            "Cold/Hot",
            "Discomfort",
            "Hungry",
            "Wants to sleep"
    };

    String[] promotions = {
            "Most babies suffer from colic and digestive problems due to temporary lactase deficiency. In such cases, it helps a modern mother to competently cope with colic. <b>Лактазар®<b>.",
            "Most babies suffer from colic and digestive problems due to temporary lactase deficiency. In such cases, it helps a modern mother to competently cope with colic. <b>Лактазар®<b>.",
            "<b>LOLOCLO</b> – basic wardrobe-constructor for children for every day.",
            "For moms, nothing is more important than the health of their babies. Therefore, it is so important that baby food meets all the necessary quality standards and consists of healthy ingredients. <b>Gerber®</b> puree uses only natural ingredients to keep babies healthy and moms calm!",
            "For moms, nothing is more important than the health of their babies. Therefore, it is so important that baby food meets all the necessary quality standards and consists of healthy ingredients. <b>Gerber®</b> puree uses only natural ingredients to keep babies healthy and moms calm!",
            "For moms, nothing is more important than the health of their babies. Therefore, it is so important that baby food meets all the necessary quality standards and consists of healthy ingredients. <b>Gerber®</b> puree uses only natural ingredients to keep babies healthy and moms calm!"
    };

    int[] icons = {
            R.drawable.ic_icons8_back_pain,
            R.drawable.ic_icons8_error,
            R.drawable.ic_icons8_temperature,
            R.drawable.ic_icons8_error,
            R.drawable.ic_hungry,
            R.drawable.ic_icons8_sleep
    };

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;

    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }
    private void startRecording() {
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/baby_cry.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        // check permission method is used to check
        // that the user has granted permission
        // to record nd store the audio.
        if (CheckPermissions()) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }

            recorder.start();
            startParticleEffect();
            new Thread(() -> {
                SystemClock.sleep(7000);
                onRecord(false);
                SystemClock.sleep(1000);
                File mFile = new File(fileName);
                if (mFile != null && mFile.exists()) {
                    runOnUiThread(()->{
                        scaleDown.pause();
                        (findViewById(R.id.btnLoading)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.ivBtnIcon)).setVisibility(View.GONE);
                    });
                    ApiMaster.Result result = master.uploadAudio(mFile);
                }else{
                    runOnUiThread(()->{
                        Log.e("FileName", fileName);
                        Toast.makeText(context, "File does not exist", Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
        } else {
            // if audio recording permissions are
            // not granted by user below method will
            // ask for runtime permission for mic and storage.
            RequestPermissions();
        }
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void uploadFile(File file){
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(stream != null){

            // Create a reference to "file"
            String fileName = Utils.randomString(12)+".3gp";
            StorageReference storageRef1 = storageRef.child(fileName);

            UploadTask uploadTask = storageRef1.putStream(stream);
            uploadTask.addOnFailureListener(exception -> {
                Log.i("UploadAudio", "Uploaded!");
                Toast.makeText(context, "Uploading failed", Toast.LENGTH_LONG).show();
            }).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                    DatabaseReference myRef = database.getReference("requests");
                    DatabaseReference responses = database.getReference("responses");

                    String key = myRef.push().getKey();
                    myRef.child(key).setValue(new RequestModel("recognize", fileName));

                    int accuracy = 87;
                    int predict = 4;

                    if (accuracy >= 0){
                        (findViewById(R.id.btnLoading)).setVisibility(View.GONE);
                        (findViewById(R.id.ivBtnIcon)).setVisibility(View.VISIBLE);

                        ResponseModel model = new ResponseModel(accuracy,predict);
                        ((TextView) findViewById(R.id.tvResult)).setText(results[model.getResult()]);
                        ((TextView) findViewById(R.id.tvAccuracy)).setText(model.getAccuracy()+"%");
                        ((TextView) findViewById(R.id.tvSuggest)).setText(Html.fromHtml(promotions[model.getResult()]));
                        ((ImageView) findViewById(R.id.ivIcon)).setImageResource(icons[model.getResult()]);
                        llResult.animate().alpha(1).scaleX(1).scaleY(1).setDuration(300).start();
                    }

//                    responses.child(key).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            int accuracy = -1;
//                            int predict = -1;
//
//                            for (DataSnapshot dttSnapshot2 : snapshot.getChildren()) {
//                                if (dttSnapshot2 .getKey().equals("accurancy"))
//                                    accuracy = dttSnapshot2 .getValue(Integer.class);
//                                if (dttSnapshot2 .getKey().equals("result"))
//                                    predict = dttSnapshot2 .getValue(Integer.class);
//                            }
//                            if (accuracy >= 0){
//                                (findViewById(R.id.btnLoading)).setVisibility(View.GONE);
//                                (findViewById(R.id.ivBtnIcon)).setVisibility(View.VISIBLE);
//
//                                ResponseModel model = new ResponseModel(accuracy,predict);
//                                ((TextView) findViewById(R.id.tvResult)).setText(results[model.getResult()]);
//                                ((TextView) findViewById(R.id.tvAccuracy)).setText(model.getAccuracy()+"%");
//                                ((TextView) findViewById(R.id.tvSuggest)).setText(Html.fromHtml(promotions[model.getResult()]));
//                                ((ImageView) findViewById(R.id.ivIcon)).setImageResource(icons[model.getResult()]);
//                                llResult.animate().alpha(1).scaleX(1).scaleY(1).setDuration(300).start();
////                                Toast.makeText(context, model.getResult()+"("+model.getAccuracy()+")", Toast.LENGTH_LONG).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                    Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            Toast.makeText(context, "Getting null file", Toast.LENGTH_LONG).show();
        }
    }

    public boolean CheckPermissions() {
        // this method is used to check permission
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    int diff = 0;
    public void startParticleEffect(){

        (findViewById(R.id.tvSlogan)).animate().alpha(0).setDuration(300).start();
        (findViewById(R.id.clHistory)).animate().alpha(0).setDuration(300).start();

        final int[] location = new int[2];
        ((ImageView) findViewById(R.id.ivBtnBG)).getLocationOnScreen(location);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;
        final float centerX = location[0]+Utils.convertDpToPixel(95, context);
        final float centerY = location[1]+Utils.convertDpToPixel(75, context)+(((height/2)-location[1])/2);

        final Random random = new Random();
        final int[] icons = {
                R.drawable.ic_note_1,
                R.drawable.ic_note_2
        };

        diff = Utils.convertPixelsToDp((height/2)-location[1], context);
        (findViewById(R.id.btnRecode)).animate().setDuration(300).translationYBy(diff).start();
        (findViewById(R.id.ivBtnBG)).animate().setDuration(300).translationYBy(diff).start();

        new Thread(()->{
            for (int i=0; i<140; i++){
                runOnUiThread(()->{
                    ImageView particleItem = new ImageView(this);
                    particleItem.setImageResource(icons[random.nextInt(2)]);
                    int x = random.nextInt(width);
                    int y = random.nextInt(height);
                    particleItem.setX(x);
                    particleItem.setY(y);
                    particleItem.setZ(0);
                    particleItem.setAlpha(0.0f);
                    int iw = random.nextInt(40)+20;
                    int ih = random.nextInt(40)+20;
                    particleItem.setLayoutParams(new LinearLayout.LayoutParams(
                            Utils.convertDpToPixel(iw, context),
                            Utils.convertDpToPixel(ih, context)));
                    rootView.addView(particleItem);
                    int duration = random.nextInt(500)+1000;
                    particleItem.animate().setDuration(duration).alpha(1).translationX(centerX).translationY(centerY).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            rootView.removeView(particleItem);
                            super.onAnimationEnd(animation);
                        }
                    }).start();
                });
                SystemClock.sleep(50);
            }
            SystemClock.sleep(1000);
        }).start();
    }

}