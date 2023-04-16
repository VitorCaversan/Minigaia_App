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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ListView          listView;
    private ArrayList<String> listViewStrings = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        this.appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);

        this.listView = findViewById(R.id.listView);

        this.listViewStrings.add("Régua 1");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                                          android.R.layout.simple_list_item_1,
                                                          this.listViewStrings);

        this.listView.setAdapter(adapter);

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

        switch (btnPos)
        {
            case 0:
//                NavHostFragment.findNavController(this)
//                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            break;

            default:
                // Nothing to be done
            break;
        }
    }

    /**
     * Function called to create a new ruler whenever the add button is pressed
     */
    public void createNewRuler()
    {
        int newId = this.listViewStrings.size() + 1;
        this.listViewStrings.add("Régua " + newId);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                                          android.R.layout.simple_list_item_1,
                                                          this.listViewStrings);
        this.listView.setAdapter(adapter);
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