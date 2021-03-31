package org.tanar.ui.login;

import android.app.Activity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.tanar.R;
import org.tanar.data.LoginResult;
import org.tanar.data.Repository;

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
        AppCompatButton signup=findViewById(R.id.signup);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GOES TO REGISTRATION CLASS
            }
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
                if (!isUserNameValid(usernameEditText.getText().toString())) {
                    usernameEditText.setError(getString(R.string.invalid_username));
                } else if (!isPasswordValid(passwordEditText.getText().toString())) {
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

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}