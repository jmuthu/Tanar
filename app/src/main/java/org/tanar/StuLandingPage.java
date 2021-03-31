package org.tanar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class StuLandingPage extends AppCompatActivity {

    ImageView list, settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_landing_page);

        list=(ImageView) findViewById(R.id.right_icon);
        settings=(ImageView) findViewById(R.id.left_icon);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //STUDENT APPOINTMENT LIST
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //STUDENT DETAILS SETTINGS
            }
        });

    }
}