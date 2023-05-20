package com.example.minigaia;

import com.example.minigaia.databinding.ActivityTimesBinding;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class TimesActivity extends AppCompatActivity {

    private ActivityTimesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityTimesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
    }


}