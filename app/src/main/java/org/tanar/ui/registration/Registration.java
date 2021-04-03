package org.tanar.ui.registration;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.tanar.R;
import org.tanar.data.model.Subject;
import org.tanar.data.result.CreateUserResult;
import org.tanar.data.Repository;
import org.tanar.data.result.SubjectResult;
import org.tanar.utils.PermissionUtils;
import org.tanar.utils.Utils;

import java.util.List;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Boolean isTutor = false;
    private Repository repository;

    // For Asynchronous retrieval of data we need to watch the result from the db.
    private final MutableLiveData<CreateUserResult> createUserResultMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<SubjectResult> subjectMutableLiveData = new MutableLiveData<SubjectResult>();

    // Location related variables
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
        final EditText phonenumber = findViewById(R.id.phoneno);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText experienceYear=findViewById(R.id.exp_year);
        final Spinner  subjects=findViewById(R.id.spinner);
        final EditText classNumberEditText = findViewById(R.id.classNumber);
        final EditText passwordEditText = findViewById(R.id.registration_password);
        final EditText confirmPasswordEditText = findViewById(R.id.cpassword);
        final ProgressBar loadingProgressBar = findViewById(R.id.loadingRegistration);
        final AppCompatButton registerButton = findViewById(R.id.register);
        subjects.setVisibility(View.INVISIBLE);
        experienceYear.setVisibility(View.INVISIBLE);
        getLocation();

        repository.getSubjects(subjectMutableLiveData);


        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    // Check which radio button has been clicked
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {
                        isTutor = checkedId == R.id.tutor_radio_button;
                        if(isTutor) {
                            isTutor=true;
                            subjects.setVisibility(View.VISIBLE);
                            experienceYear.setVisibility(View.VISIBLE);
                        } else {
                            subjects.setVisibility(View.INVISIBLE);
                            experienceYear.setVisibility(View.INVISIBLE);
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
                if (!Utils.isPasswordValid(passwordEditText.getText().toString())) {
                    passwordEditText.setError(getString(R.string.invalid_password));
                } else if (!passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                    confirmPasswordEditText.setError(getString(R.string.passwords_not_matching));
                }
                else if (phonenumber.getText().toString().trim().length() < 10){
                    phonenumber.setError("Enter a valid phone number");}
                    //else isDataValid = longitude != 0.0 || latitude != 0.0;
                else{
                    isDataValid=true;
                }
                registerButton.setEnabled(isDataValid);
            }
        };

        createUserResultMutableLiveData.observe(this, createUserResult -> {
            if (createUserResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (createUserResult.getError() != null) {
                showLoginFailed(createUserResult.getError());
            }
            if (createUserResult.getSuccess() != null) {
                updateUiWithUser(createUserResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            finish();
        });


       subjectMutableLiveData.observe(this, subjectResult -> {
            if (subjectResult == null) {
                return;
            }
            if (subjectResult.getError() != null) {
                showLoginFailed(subjectResult.getError());
            }
            if (subjectResult.getSubjectList() != null) {
                subjects.setPrompt("Select your Subject");
                List<Subject> st=subjectResult.getSubjectList();
                ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,st);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                subjects.setAdapter(adapter);
            }

        });



        phonenumber.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        confirmPasswordEditText.addTextChangedListener(afterTextChangedListener);

        registerButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            repository.createUser(nameEditText.getText().toString(),
                    isTutor,
                    emailEditText.getText().toString(),
                    classNumberEditText.getText().toString(),
                    phonenumber.getText().toString(),
                    subjects.getSelectedItem().toString(),
                    experienceYear.getText().toString(),
                    passwordEditText.getText().toString(),
                    latitude, longitude, altitude,
                    createUserResultMutableLiveData);

        });
    }

    private void updateUiWithUser(String displayName) {
        Toast.makeText(getApplicationContext(), getString(R.string.create_user_success), Toast.LENGTH_LONG).show();
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
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        altitude = location.getAltitude();
                    }
                });
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}