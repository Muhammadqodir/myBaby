package uz.mq.mybaby;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
    }

    Spinner age, sex;
    private void initViews(){
        BabyProfile profile = Utils.getProfile(this);
        sex = findViewById(R.id.sexSpinner);
        age = findViewById(R.id.ageSpinner);
        sex.setSelection(profile.getSex());
        age.setSelection(profile.getAge());

        ((Button) findViewById(R.id.btnSave)).setOnClickListener((v)->{
            Utils.saveProfile(this, new BabyProfile(sex.getSelectedItemPosition(), sex.getSelectedItemPosition()));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}