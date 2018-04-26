package com.ekmobil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ekmobil.utility.Constants;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText edt_username, edt_password;
    private Button btn_login;
    private TextView txt_signup;
    private View progress;
    private Switch remember_me;
    private static final String EMAIL = "email";
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        defineComponents();

        checkLoginInfo();
    }

    private void checkLoginInfo() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        String userName = pref.getString(Constants.DEFAULT_PREF_USERNAME, "");
        String password = pref.getString(Constants.DEFAULT_PREF_PASSWORD, "");
        boolean isLogin = pref.getBoolean(Constants.DEFAULT_IS_LOGIN, false);

        if (isLogin) {
            edt_username.setText(userName.trim());
            edt_password.setText(password.trim());
        }
    }

    private void defineComponents() {
        progress = findViewById(R.id.progress);
        edt_username = findViewById(R.id.login_edt_username);
        edt_password = findViewById(R.id.login_edt_password);

        remember_me = findViewById(R.id.login_swtch);

        btn_login = findViewById(R.id.login_btn);
        txt_signup = findViewById(R.id.login_txt_signup);

        btn_login.setOnClickListener(this);
        txt_signup.setOnClickListener(this);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));


        loginButton.setHeight(btn_login.getHeight());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject user,
                                    GraphResponse response) {

                                String name = ProjectUtility.getJsonValue(user, "name").trim();
                                String id = ProjectUtility.getJsonValue(user, "id").trim();
                                String mail = ProjectUtility.getJsonValue(user, "id").trim();

                                loginWithFacebook(name, id, mail);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    private void loginWithFacebook(String name, String id, String mail) {
        if (HttpUtility.isOnline(LoginActivity.this)) {
            progress.setVisibility(View.VISIBLE);
            String[] nameSurname = name.split(" ");

            HttpUrl url = HttpUrl.parse(Constants.URL_FACEBOOK_LOGIN).newBuilder()
                    .addQueryParameter("facebookid", nameSurname[0])
                    .addQueryParameter("ad", nameSurname[0])
                    .addQueryParameter("soyad", (nameSurname.length > 1 ? nameSurname[1] : nameSurname[0]))
                    .addQueryParameter("email", Constants.LoginCode).build();

            HttpUtility.get(url.toString(), new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            ProjectUtility.showDialog(LoginActivity.this, getString(R.string.connection_error_title), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);

                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parseFacebookData(res);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });
        } else {
            ProjectUtility.showDialog(LoginActivity.this, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if (edt_password.getText().toString().length() > 0 && edt_username.getText().toString().length() > 0) {
                    login();
                } else {
                    Toast.makeText(this, getString(R.string.login_blank_field), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.login_txt_signup:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login() {
        if (HttpUtility.isOnline(this)) {
            progress.setVisibility(View.VISIBLE);

            final String username = edt_username.getText().toString().trim();
            String password = edt_password.getText().toString().trim();

            HttpUrl url = HttpUrl.parse(Constants.URL_LOGIN).newBuilder()
                    .addQueryParameter("ad", username)
                    .addQueryParameter("sifre", password)
                    .addQueryParameter("giriskodu", Constants.LoginCode).build();

            HttpUtility.get(url.toString(), new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                            ProjectUtility.showDialog(LoginActivity.this, getString(R.string.connection_error_title), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res.equals("\"false\"")) {
                                //Kullanıcı adı şifre yanlış
                                Toast.makeText(LoginActivity.this, getString(R.string.login_wrong_username_or_password), Toast.LENGTH_LONG).show();
                            } else if (res.equals("\"Giriş Kodu Yanlış\"")) {
                                //Giris Kodu yanlis
                                Toast.makeText(LoginActivity.this, getString(R.string.login_unexpected_error), Toast.LENGTH_LONG).show();

                            } else if (res.toLowerCase().contains(username.toLowerCase())) {
                                saveUserInformation(res);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            progress.setVisibility(View.GONE);

                        }
                    });
                }
            });
        } else {
            ProjectUtility.showDialog(LoginActivity.this, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parseFacebookData(String res) {
        try {
            JSONArray array = new JSONArray(res);
            if (array != null && array.length() > 0) {
                JSONObject object = array.getJSONObject(0);

                String id = ProjectUtility.getJsonValue(object, "ID").trim();
                String name_surname = ProjectUtility.getJsonValue(object, "adSoyad").trim();
                String mail = ProjectUtility.getJsonValue(object, "email").trim();
                String image = ProjectUtility.getJsonValue(object, "resim").trim();

                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCE_USER_INFO, MODE_PRIVATE).edit();
                editor.putString(Constants.PREF_ID, id).apply();
                editor.putString(Constants.PREF_NAME_SURNAME, name_surname).apply();
                editor.putString(Constants.PREF_MAIL, mail).apply();
                editor.putString(Constants.PREF_IMAGE, image).apply();
                editor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveUserInformation(String res) {
        try {
            JSONArray array = new JSONArray(res);
            if (array != null && array.length() > 0) {
                JSONObject object = array.getJSONObject(0);

                String id = ProjectUtility.getJsonValue(object, "ID").trim();
                String username = ProjectUtility.getJsonValue(object, "ad").trim();
                String name_surname = ProjectUtility.getJsonValue(object, "adSoyad").trim();
                String mail = ProjectUtility.getJsonValue(object, "email").trim();
                String phone = ProjectUtility.getJsonValue(object, "telefon").trim();
                String image = ProjectUtility.getJsonValue(object, "resim").trim();

                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCE_USER_INFO, MODE_PRIVATE).edit();
                editor.putString(Constants.PREF_ID, id).apply();
                editor.putString(Constants.PREF_USERNAME, username).apply();
                editor.putString(Constants.PREF_NAME_SURNAME, name_surname).apply();
                editor.putString(Constants.PREF_MAIL, mail).apply();
                editor.putString(Constants.PREF_PHONE, phone).apply();
                editor.putString(Constants.PREF_IMAGE, image).apply();
                editor.commit();

                SharedPreferences.Editor defaultEditor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                if (remember_me.isChecked()) {
                    defaultEditor.putString(Constants.DEFAULT_PREF_USERNAME, edt_username.getText().toString().trim()).apply();
                    defaultEditor.putString(Constants.DEFAULT_PREF_PASSWORD, edt_password.getText().toString().trim()).apply();
                    defaultEditor.putBoolean(Constants.DEFAULT_IS_LOGIN, true).apply();
                    defaultEditor.commit();
                } else {
                    defaultEditor.clear().apply();
                    defaultEditor.commit();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
