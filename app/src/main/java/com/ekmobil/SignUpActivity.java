package com.ekmobil;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ekmobil.utility.Constants;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;
import com.redmadrobot.inputmask.MaskedTextChangedListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {
    private EditText edt_name_surname, edt_username, edt_password, edt_password_again, edt_mail, edt_phone;
    private Button btn_signup;
    private View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        defineComponents();
    }

    private void defineComponents() {
        edt_phone = findViewById(R.id.signup_edt_phone);
        edt_name_surname = findViewById(R.id.signup_edt_name_surname);
        edt_username = findViewById(R.id.signup_edt_username);
        edt_password = findViewById(R.id.signup_edt_password);
        edt_password_again = findViewById(R.id.signup_edt_password_again);
        edt_mail = findViewById(R.id.signup_edt_mail);
        btn_signup = findViewById(R.id.signup_btn);

        progress = findViewById(R.id.progress);

        btn_signup.setOnClickListener(this);

        final MaskedTextChangedListener listener = new MaskedTextChangedListener(
                "+0 ([000]) [000] [00] [00]",
                true,
                edt_phone,
                null,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue) {
                        Log.d(MainActivity.class.getSimpleName(), extractedValue);
                        Log.d(MainActivity.class.getSimpleName(), String.valueOf(maskFilled));
                    }
                }
        );


        edt_phone.addTextChangedListener(listener);
        edt_phone.setOnFocusChangeListener(listener);
        edt_phone.setHint(listener.placeholder());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_btn:
                String name_surname = edt_name_surname.getText().toString();
                String username = edt_username.getText().toString();
                String password = edt_password.getText().toString();
                String password_again = edt_password_again.getText().toString();
                String mail = edt_mail.getText().toString();
                String phone = edt_phone.getText().toString();
                if (checkIfEmptyField(name_surname, username, password, password_again, mail, phone)) {
                    signup(name_surname, username, password, mail, phone);
                }
                break;
        }
    }

    private void signup(String name_surname, String username, String password, String mail, String phone) {
        if (HttpUtility.isOnline(SignUpActivity.this)) {
            progress.setVisibility(View.VISIBLE);
            HttpUrl url = HttpUrl.parse(Constants.URL_SIGNUP).newBuilder()
                    .addQueryParameter("kadi", username)
                    .addQueryParameter("sifre", password)
                    .addQueryParameter("email", mail)
                    .addQueryParameter("telefon", phone)
                    .addQueryParameter("adsoyad", name_surname)
                    .addQueryParameter("giriskodu", Constants.LoginCode).build();
            HttpUtility.get(url.toString(), new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            ProjectUtility.showDialog(SignUpActivity.this, getString(R.string.connection_error_title), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!res.equals("\"true\"")) {
                                String errorMessage = res.replaceAll("\"", "");
                                ProjectUtility.showDialog(SignUpActivity.this, getString(R.string.messages_error), errorMessage, getString(R.string.messages_ok), null, null, null);
                            } else {
                                ProjectUtility.showDialog(SignUpActivity.this, getString(R.string.messages_info), getString(R.string.messages_signup_success), getString(R.string.messages_ok), null, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }, null);
                            }
                            progress.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } else {
            ProjectUtility.showDialog(SignUpActivity.this, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }


    }

    private boolean checkIfEmptyField(String name_surname, String username, String password, String password_again, String mail, String phone) {
        if (name_surname == null || name_surname.length() <= 0 ||
                username == null || username.length() <= 0 ||
                password == null || password.length() <= 0 ||
                password_again == null || password_again.length() <= 0 ||
                mail == null || mail.length() <= 0 ||
                phone == null || phone.length() <= 0) {
            ProjectUtility.showDialog(SignUpActivity.this, getString(R.string.connection_error_title), getString(R.string.messages_blank_field), getString(R.string.messages_ok), null, null, null);

            return false;
        } else if (!password.equals(password_again)) {
            ProjectUtility.showDialog(SignUpActivity.this, getString(R.string.connection_error_title), getString(R.string.messages_password_do_not_match), getString(R.string.messages_ok), null, null, null);
            return false;
        }
        return true;
    }
}
