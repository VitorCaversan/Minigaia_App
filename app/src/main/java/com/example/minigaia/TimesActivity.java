package com.example.minigaia;

import com.example.minigaia.databinding.ActivityTimesBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class TimesActivity extends AppCompatActivity {

    private ActivityTimesBinding binding;
    private String measureTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityTimesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (null != intent)
        {
            this.measureTime = (String)intent.getStringExtra("measureTime");
        }

        binding.dayButton.setText(this.measureTime);
        // Adds 12h to the later button
        String nightString = (String)this.measureTime;
        int hour = Integer.parseInt(nightString.substring(0,2));
        hour = (hour + 12) % 24;
        String auxString = nightString.substring(2,5);
        nightString = Integer.toString(hour) + auxString;
        binding.nightButton.setText(nightString);

        binding.dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.nightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.syncButtonTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

            }
        });

        binding.homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                // setResult(); // Use this if wanting to return a value for startActivityForResult()
                finish();
            }
        });
    }


}