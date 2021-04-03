package org.tanar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tanar.data.model.Tutor;

import java.util.List;

public class TutorsAdapter extends ArrayAdapter<Tutor> {


    // constructor for our list view adapter.
    public TutorsAdapter(@NonNull Context context, List<Tutor> tutorList) {
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
        TextView name = listitemView.findViewById(R.id.idName);
        TextView tutClass = listitemView.findViewById(R.id.idClass);
        TextView expYr = listitemView.findViewById(R.id.idExpYr);
        TextView distance = listitemView.findViewById(R.id.idDistance);
        ImageView bookingStatus = listitemView.findViewById(R.id.idStatus);
        Drawable myDrawable = listitemView.getResources().getDrawable(R.drawable.add);
        if (tutor.getStatus().equals("Approved")) {
            myDrawable = listitemView.getResources().getDrawable(R.drawable.approved);
        } else if (tutor.getStatus().equals("Pending")) {
            myDrawable = listitemView.getResources().getDrawable(R.drawable.pending);
        } else if (tutor.getStatus().equals("Denied")) {
            myDrawable = listitemView.getResources().getDrawable(R.drawable.denied);
        }
        bookingStatus.setImageDrawable(myDrawable);


        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        name.setText(tutor.getName());
        subject.setText(tutor.getSubjectList());
        tutClass.setText(tutor.getTutClass());
        expYr.setText(tutor.getExpyear());
        String dist = String.format("%.2f", tutor.getDistance());
        distance.setText(dist);

        return listitemView;
    }
}