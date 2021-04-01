package org.tanar.ui.login;

import android.app.Activity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.tanar.R;
import org.tanar.data.LoginResult;
import org.tanar.data.Repository;
import org.tanar.ui.registration.Registration;
import org.tanar.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private Repository repository;
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        repository = Repository.getInstance();

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final AppCompatButton loginButton = findViewById(R.id.login);
        final AppCompatButton signup=findViewById(R.id.signup);
        signup.setEnabled(true);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        Intent intent = new Intent(this, Registration.class);
        signup.setOnClickListener(v -> {
             startActivity(intent);
        });

        loginResult.observe(this, loginResult -> {
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
            //finish();
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
                } else {
                    isDataValid = true;
                }
                loginButton.setEnabled(isDataValid);
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                repository.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), loginResult);
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            repository.login(usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(), loginResult);
        });
    }

    private void updateUiWithUser(String displayName) {
        String welcome = getString(R.string.welcome) + displayName;
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        //  startActivity(intent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}