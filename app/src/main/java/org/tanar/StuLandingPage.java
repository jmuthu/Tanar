package org.tanar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tanar.data.Repository;
import org.tanar.data.model.Subject;
import org.tanar.data.result.CreateUserResult;
import org.tanar.data.result.TutorsNearbyResult;

import java.util.ArrayList;
import java.util.List;

public class StuLandingPage extends AppCompatActivity {

    ImageView list, settings;
    private ListView tutors;
    private final MutableLiveData<TutorsNearbyResult> tutorNearbyResultMLD = new MutableLiveData<>();
    private Repository repository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_landing_page);
        repository = Repository.getInstance();

        repository.getTutorsNearby(tutorNearbyResultMLD);
        list=(ImageView) findViewById(R.id.right_icon);
        settings=(ImageView) findViewById(R.id.left_icon);

        tutors = findViewById(R.id.tutorPeople);

        tutorNearbyResultMLD.observe(this, tutorNearbyResult -> {
            if (tutorNearbyResult == null) {
                return;
            }
            if (tutorNearbyResult.getError() != null) {
                Toast.makeText(this, "Failed to fetch from Database.", Toast.LENGTH_LONG).show();
            }
            if (tutorNearbyResult.getTutorList() != null) {
                MyAdapter adapter = new MyAdapter(this, tutorNearbyResult.getTutorList());

                // after passing this array list to our adapter
                // class we are setting our adapter to our list view.
                tutors.setAdapter(adapter);
            }

        });

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