package org.tanar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.tanar.data.Repository;
import org.tanar.data.result.PasswordResult;
import org.tanar.utils.Utils;

public class StuSetting extends AppCompatActivity {

    private final MutableLiveData<PasswordResult> passwordResultMutableLiveData = new MutableLiveData<PasswordResult>();
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_setting);

        repository = Repository.getInstance();

        EditText stupass = (EditText) findViewById(R.id.stuPass);
        EditText stucpass = (EditText) findViewById(R.id.studentcpass);

        TextView studName = (TextView) findViewById(R.id.stude);

        studName.setText(repository.getUsername());

        AppCompatButton update = (AppCompatButton) findViewById(R.id.stuUpdate);

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
                if (!Utils.isPasswordValid(stupass.getText().toString())) {
                    stupass.setError(getString(R.string.invalid_password));
                } else if (!stupass.getText().toString().equals(stucpass.getText().toString())) {
                    stucpass.setError(getString(R.string.passwords_not_matching));
                } //else isDataValid = longitude != 0.0 || latitude != 0.0;
                else {
                    isDataValid = true;
                }
                update.setEnabled(isDataValid);
            }
        };

        stupass.addTextChangedListener(afterTextChangedListener);
        stucpass.addTextChangedListener(afterTextChangedListener);

        update.setOnClickListener(v -> {
            repository.updateUser(stupass.getText().toString(), passwordResultMutableLiveData);
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