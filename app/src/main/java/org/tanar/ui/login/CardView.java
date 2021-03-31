package org.tanar.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.tanar.R;

public class CardView extends AppCompatActivity {

    AppCompatButton stu,tut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        stu=(AppCompatButton) findViewById(R.id.student);
        tut=(AppCompatButton) findViewById(R.id.tutor);

        stu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(CardView.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }
}