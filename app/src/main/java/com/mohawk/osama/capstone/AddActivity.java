package com.mohawk.osama.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The type Add activity.
 */
public class AddActivity extends AppCompatActivity {

    private String title;
    private String year;
    private String publisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Search");

        final Button button = (Button) findViewById(R.id.add_search_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText searchTitle = (EditText) findViewById(R.id.add_title);
                EditText searchYear = (EditText) findViewById(R.id.add_year);

                title = searchTitle.getText().toString();
                year = searchYear.getText().toString();

                Intent intent = new Intent(v.getContext(), SearchResultsActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("year", year);
                startActivity(intent);
            }
        });

    }

}
