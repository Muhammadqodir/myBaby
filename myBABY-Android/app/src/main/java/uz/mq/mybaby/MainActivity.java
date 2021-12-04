package uz.mq.mybaby;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.ParticleSystem;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class MainActivity extends AppCompatActivity {

    Context context;
    ConstraintLayout rootView;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        context = this;
        getWindow().getDecorView().post(() -> {
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    ((LinearLayout) findViewById(R.id.btnRecode)),
                    PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.1f));
            scaleDown.setDuration(800);

            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();

            rootView = (ConstraintLayout) findViewById(R.id.rootView);

            ((LinearLayout) findViewById(R.id.btnRecode)).setOnClickListener((v)->{
                startParticleEffect();
            });

        });
    }

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

        final int diff = Utils.convertPixelsToDp((height/2)-location[1], context);
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
            runOnUiThread(()->{
                (findViewById(R.id.tvSlogan)).animate().alpha(1).setDuration(300).start();
                (findViewById(R.id.clHistory)).animate().alpha(1).setDuration(300).start();
                (findViewById(R.id.btnRecode)).animate().setDuration(300).translationYBy(-diff).start();
                (findViewById(R.id.ivBtnBG)).animate().setDuration(300).translationYBy(-diff).start();
            });
        }).start();
    }

}