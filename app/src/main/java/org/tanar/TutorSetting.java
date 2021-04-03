package org.tanar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.tanar.data.Repository;
import org.tanar.data.model.Subject;
import org.tanar.data.model.Tutor;
import org.tanar.data.result.PasswordResult;
import org.tanar.data.result.SubjectResult;
import org.tanar.utils.Utils;

import java.util.List;

public class TutorSetting extends AppCompatActivity {

    private final MutableLiveData<PasswordResult> passwordResultMutableLiveData = new MutableLiveData<PasswordResult>();
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_setting);

        repository=Repository.getInstance();

        EditText tutpass=(EditText) findViewById(R.id.tutpassword);
        EditText tutcpass=(EditText) findViewById(R.id.tutcpassword);

        TextView tutName=(TextView) findViewById(R.id.tutName);

        tutName.setText(repository.getUsername());

        AppCompatButton update=(AppCompatButton) findViewById(R.id.tutorUpdate);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isDataValid = false;
                if (!Utils.isPasswordValid(tutpass.getText().toString())) {
                   tutpass.setError(getString(R.string.invalid_password));
                } else if (!tutpass.getText().toString().equals(tutcpass.getText().toString())) {
                    tutcpass.setError(getString(R.string.passwords_not_matching));
                } //else isDataValid = longitude != 0.0 || latitude != 0.0;
                else{
                    isDataValid=true;
                }
               update.setEnabled(isDataValid);
            }
        };

        tutpass.addTextChangedListener(afterTextChangedListener);
        tutcpass.addTextChangedListener(afterTextChangedListener);

        update.setOnClickListener(v -> {
            repository.updateUser(tutpass.getText().toString(),passwordResultMutableLiveData);
        });

        passwordResultMutableLiveData.observe(this, passwordResult -> {
            if (passwordResult == null) {
                return;
            }
            if (passwordResult.getError() != null) {
                Toast.makeText(this, passwordResult.getError(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Password Updated Successfully.", Toast.LENGTH_LONG).show();

            }

        });
    }
}