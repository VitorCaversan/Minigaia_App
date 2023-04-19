package com.example.minigaia;

import android.os.Bundle;

import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.NavigationUI;

//import java.util.ArrayList;

public class RulerActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private TableLayout         tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);

        ////////////// APPBAR SETTINGS //////////////////
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        this.appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);

        ///////////// TABLE LAYOUT AND TEXT /////////////////////
        this.tableLayout.findViewById(R.id.rulerTableLayout);
        this.createTableContent();
    }

    private void createTableContent()
    {
        TableRow row0  = new TableRow(this);
        this.tableLayout.addView(row0);
        TableRow row1  = new TableRow(this);
        this.tableLayout.addView(row1);
        TableRow row2  = new TableRow(this);
        this.tableLayout.addView(row2);

        EditText text0 = new EditText(this);
        text0.setText("Nível Atual do Ph: ");
        row0.addView(text0);

        EditText text1 = new EditText(this);
        text1.setText("Nível Desejado de Ph: ");
        row1.addView(text1);

        EditText text2 = new EditText(this);
        text2.setText("Nível da água: ");
        row2.addView(text2);
    }
}
