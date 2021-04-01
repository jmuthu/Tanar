package org.tanar.ui.registration;

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
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.tanar.R;
import org.tanar.data.CreateUserResult;
import org.tanar.data.Repository;
import org.tanar.utils.PermissionUtils;
import org.tanar.utils.Utils;

public class Registration extends AppCompatActivity {
    Boolean isTutor = true;
    private Repository repository;
    private final MutableLiveData<CreateUserResult> createUserResult = new MutableLiveData<>();
    private FusedLocationProviderClient fusedLocationClient;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private Double altitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        repository = Repository.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final RadioGroup radioGroup = findViewById(R.id.user_type);
        final EditText nameEditText = findViewById(R.id.registration_name);
        final EditText usernameEditText = findViewById(R.id.registration_username);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText classNumberEditText = findViewById(R.id.classNumber);
        final EditText passwordEditText = findViewById(R.id.registration_password);
        final EditText confirmPasswordEditText = findViewById(R.id.cpassword);
        final ProgressBar loadingProgressBar = findViewById(R.id.loadingRegistration);
        final AppCompatButton registerButton = findViewById(R.id.register);

        getLocation();

        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    // Check which radio button has been clicked
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {
                        isTutor = checkedId == R.id.tutor_radio_button;
                        if(isTutor) {
                            classNumberEditText.setVisibility(View.INVISIBLE);
                        } else {
                            classNumberEditText.setVisibility(View.VISIBLE);
                        }
                    }
                });


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
                } else isDataValid = longitude != 0.0 || latitude != 0.0;

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
                    latitude, longitude, altitude,
                    createUserResult);

        });
    }

    private void updateUiWithUser(String displayName) {
        String welcome = getString(R.string.create_user_success);
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void getLocation() {
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
                            altitude = location.getAltitude();
                        }
                    }
                });
    }
}