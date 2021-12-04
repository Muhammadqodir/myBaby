package uz.mq.mybaby;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().getDecorView().post(() -> {
            ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                    ((LinearLayout) findViewById(R.id.btnRecode)),
                    PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.1f));
            scaleDown.setDuration(800);

            scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
            scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

            scaleDown.start();
        });
    }

//    String[] texts = {
//            "Translate your baby's cry",
//            "Tap to recode"
//    };
//    void changeText(int textId){
//        new Thread(()->{
//            SystemClock.sleep(5000);
//            runOnUiThread(()->{
//                hTextView.setAnimateType(HTextViewType.EVAPORATE);
//                hTextView.animateText(texts[textId]);
//                if (textId == 0){
//                    changeText(1);
//                }else{
//                    changeText(0);
//                }
//            });
//        }).start();
//    }
}