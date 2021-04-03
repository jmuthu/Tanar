package org.tanar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.tanar.data.Repository;
import org.tanar.data.model.Tutor;
import org.tanar.data.result.BookingResult;
import org.tanar.data.result.TutorsNearbyResult;

public class StuLandingPage extends AppCompatActivity {


    private ListView tutors;
    private final MutableLiveData<TutorsNearbyResult> tutorNearbyResultMLD = new MutableLiveData<>();
    private final MutableLiveData<BookingResult> bookingResultMutableLiveData = new MutableLiveData<>();
    private Repository repository;
    private TutorsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_landing_page);

        repository = Repository.getInstance();
        repository.getTutorsNearby(tutorNearbyResultMLD);

        ImageView settings=(ImageView) findViewById(R.id.left_icon);
        tutors = findViewById(R.id.tutorPeople);

        tutorNearbyResultMLD.observe(this, tutorNearbyResult -> {
            if (tutorNearbyResult == null) {
                return;
            }
            if (tutorNearbyResult.getError() != null) {
                Toast.makeText(this, "Failed to fetch from Database.", Toast.LENGTH_LONG).show();
            }
            if (tutorNearbyResult.getTutorList() != null) {
                adapter = new TutorsAdapter(this, tutorNearbyResult.getTutorList());

                // after passing this array list to our adapter
                // class we are setting our adapter to our list view.
                tutors.setAdapter(adapter);
            }

        });

        bookingResultMutableLiveData.observe(this, bookingResult -> {
            if (bookingResult == null) {
                return;
            }
            if (bookingResult.getError() != null) {
                Toast.makeText(this, bookingResult.getError(), Toast.LENGTH_LONG).show();
            } else {
                Tutor t = adapter.getItem(bookingResult.getPosition());
                t.setStatus("Pending");
                adapter.notifyDataSetChanged();
            }

        });

        tutors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tutor tutor = (Tutor)parent.getItemAtPosition(position);

                // on the item click on our list view.
                //tutor.getEmail()
                //student email : loggedIn
                if (!tutor.getStatus().equals("Add")) {
                    return;
                }

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(view.getContext());
                View mView = layoutInflaterAndroid.inflate(R.layout.appointmentbooking, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(view.getContext());
                alertDialogBuilderUserInput.setView(mView);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                final EditText userInputDialogEditText = mView.findViewById(R.id.userInputDialog);
                                String s = userInputDialogEditText.getText().toString();
                                repository.createBooking(tutor.getEmail(), position,"Pending", s, bookingResultMutableLiveData);
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(StuLandingPage.this, StuSetting.class);
                startActivity(i);
            }
        });

    }

}