package org.tanar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tanar.data.model.Tutor;
import org.w3c.dom.Text;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Tutor> {


    // constructor for our list view adapter.
    public MyAdapter(@NonNull Context context, List<Tutor> tutorList) {
        super(context, 0, tutorList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.image_lv_item, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        Tutor tutor = getItem(position);

        // initializing our UI components of list view item.
        TextView subject = listitemView.findViewById(R.id.idSubject);
        TextView name=listitemView.findViewById(R.id.idName);
        TextView tutClass=listitemView.findViewById(R.id.idClass);
        TextView expYr=listitemView.findViewById(R.id.idExpYr);
        TextView distance=listitemView.findViewById(R.id.idDistance);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        name.setText(tutor.getName());
        subject.setText(tutor.getSubjectList());
        tutClass.setText(tutor.getTutClass());
        expYr.setText(tutor.getExpyear());
        String dist = String.format("%.2f",tutor.getDistance());
        distance.setText(dist);


        // below line is use to add item click listener
        // for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.

                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(v.getContext());
                View mView = layoutInflaterAndroid.inflate(R.layout.appointmentbooking, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(v.getContext());
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();            }
        });
        return listitemView;
    }
}