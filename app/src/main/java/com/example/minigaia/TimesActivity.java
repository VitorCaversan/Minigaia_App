package com.example.minigaia;

import com.example.minigaia.databinding.ActivityTimesBinding;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class TimesActivity extends AppCompatActivity {
    static final int DAYTIME   = 0;
    static final int NIGHTTIME = 1;

    private ActivityTimesBinding binding;
    private String earlyMeasureTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityTimesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (null != intent)
        {
            this.earlyMeasureTime = (String)intent.getStringExtra("earlyMeasureTime");
        }

        binding.dayButton.setText(this.earlyMeasureTime);
        // Adds 12h to the later button
        binding.nightButton.setText(setNextTime(this.earlyMeasureTime, NIGHTTIME));

        binding.dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTextInput(DAYTIME);
            }
        });


        binding.nightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                createTextInput(NIGHTTIME);
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
                Intent mainActivityIntent = new Intent();
                mainActivityIntent.putExtra("earlyMeasureTime", earlyMeasureTime);
                setResult(Activity.RESULT_OK, mainActivityIntent);
                finish();
            }
        });
    }

    private void createTextInput(int currentTimeType)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(TimesActivity.this);
        builder.setTitle("Horário no template HH:MM:");

        // Sets up the input
        final EditText input = new EditText(TimesActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Sets up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredText = input.getText().toString();

                treatEnteredText(enteredText, currentTimeType);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void treatEnteredText(String enteredText, int currentTimeType)
    {
        if (enteredText.length() < 5)
        {
            Toast.makeText(TimesActivity.this, "Formato errado",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            int hour = Integer.parseInt(enteredText.substring(0,2));
            int min  = Integer.parseInt(enteredText.substring(3,enteredText.length()));

            if ((hour < 0) || (hour > 24) || (min < 0) || (min > 60))
            {
                Toast.makeText(TimesActivity.this, "Valores excedem o limite",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        catch (NumberFormatException nE)
        {
            Toast.makeText(TimesActivity.this, "Formato errado",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (DAYTIME == currentTimeType)
        {
            this.earlyMeasureTime = enteredText;
            this.binding.dayButton.setText(this.earlyMeasureTime);
            this.binding.nightButton.setText(setNextTime(enteredText, NIGHTTIME));
        }
        else
        {
            this.binding.nightButton.setText(enteredText);
            this.earlyMeasureTime = setNextTime(enteredText, DAYTIME);
            this.binding.dayButton.setText(this.earlyMeasureTime);
        }
    }

    private String setNextTime(String currentTime, int nextTimeType)
    {
        String nextTime = currentTime;
        long hour = Long.parseLong(nextTime.substring(0,2));

        if (NIGHTTIME == nextTimeType)
        {
            hour = (hour + 12) % 24;
        }
        else
        {
            hour = (hour - 12);
            hour = hour & 0xFFFFFFFFL; // Makes it an unsigned value
            hour = hour % 24;
        }

        int stringSize = currentTime.length();
        String auxString = nextTime.substring(2,stringSize);

        if (hour < 10)
        {
            nextTime = "0" + Long.toString(hour) + auxString;
        }
        else
        {
            nextTime = Long.toString(hour) + auxString;
        }

        return nextTime;
    }

}