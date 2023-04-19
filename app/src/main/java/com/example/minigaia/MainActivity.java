package com.example.minigaia;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.minigaia.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ListView          listView;
    private ArrayList<String> listViewStrings = new ArrayList<>();
    private ArrayList<Intent> activitiesList  = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        this.appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);

        /////////////// LIST VIEW ////////////////////////////

        // Links the listView to the actual list on the screen
        this.listView = findViewById(R.id.listView);

        // Adds the first string to the listView
        this.listViewStrings.add("Régua 1");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                                          android.R.layout.simple_list_item_1,
                                                          this.listViewStrings);
        this.listView.setAdapter(adapter);

        // Creates the first activity to link with the listView
        Intent firstIntent = new Intent(this, RulerActivity.class);
        this.activitiesList.add(firstIntent);

        ///////////////// BUTTONS FUNCTIONS ///////////////////

        // Sets the function to be called when pressing buttons
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                openActivity(view);
            }
        });

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                createNewRuler();
            }
        });
    }

    public void openActivity(View view)
    {
        int btnPos = this.listView.getPositionForView(view);

        startActivity(this.activitiesList.get(btnPos));

        return;
    }

    /**
     * Function called to create a new ruler whenever the add button is pressed
     */
    public void createNewRuler()
    {
        // Creates a new string to be written in the listView
        int newId = this.listViewStrings.size() + 1;
        this.listViewStrings.add("Régua " + newId);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                                          android.R.layout.simple_list_item_1,
                                                          this.listViewStrings);
        this.listView.setAdapter(adapter);

        // Creates the actual new screen
        Intent intent = new Intent(this, RulerActivity.class);
        this.activitiesList.add(intent);

        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}