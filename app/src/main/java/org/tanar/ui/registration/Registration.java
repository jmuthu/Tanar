package org.tanar.ui.registration;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.tanar.R;
import org.tanar.data.CreateUserResult;
import org.tanar.data.Repository;
import org.tanar.utils.PermissionUtils;
import org.tanar.utils.Utils;

public class Registration extends AppCompatActivity {
    private Repository repository;
    private MutableLiveData<CreateUserResult> createUserResult = new MutableLiveData<>();
    Boolean isTutor = true;
    private FusedLocationProviderClient fusedLocationClient;
    private Double latitude = 0.0;
    private Double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        repository = Repository.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final EditText nameEditText = findViewById(R.id.registration_name);
        final EditText usernameEditText = findViewById(R.id.registration_username);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText classNumberEditText = findViewById(R.id.classNumber);
        final EditText passwordEditText = findViewById(R.id.registration_password);
        final EditText confirmPasswordEditText = findViewById(R.id.cpassword);
        final ProgressBar loadingProgressBar = findViewById(R.id.loadingRegistration);
        final AppCompatButton registerButton = findViewById(R.id.register);

        getLocation();
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
                if (!Utils.isUserNameValid(usernameEditText.getText().toString())) {
                    usernameEditText.setError(getString(R.string.invalid_username));
                } else if (!Utils.isPasswordValid(passwordEditText.getText().toString())) {
                    passwordEditText.setError(getString(R.string.invalid_password));
                } else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                    confirmPasswordEditText.setError(getString(R.string.passwords_not_matching));
                } else if (longitude == 0.0 && latitude == 0.0) {
                    isDataValid = false;
                } else {
                    isDataValid = true;
                }

                registerButton.setEnabled(isDataValid);
            }
        };

        createUserResult.observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            repository.createUser(nameEditText.getText().toString(),
                    isTutor,
                    emailEditText.getText().toString(),
                    classNumberEditText.getText().toString(),
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    latitude, longitude,
                    createUserResult);

        });
    }

    public void onUserTypeClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.tutor_radio_button:
                if (checked)
                    isTutor = true;
                    break;
            case R.id.student_radio_button:
                if (checked)
                    isTutor = false;
                    break;
        }
    }

    private void updateUiWithUser(String displayName) {
        String welcome = getString(R.string.create_user_success) ;
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void getLocation () {
       final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                });
    }
}