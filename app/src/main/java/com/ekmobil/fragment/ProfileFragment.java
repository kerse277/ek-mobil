package com.ekmobil.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ekmobil.R;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.GlideApp;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.EasyImageConfig;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private Activity nActivity;
    private EditText edt_name_surname, edt_username, edt_password, edt_password_again, edt_mail, edt_phone;
    private Button btn_update;
    private View progress;
    private ImageView img_profile;
    private final int CAMERA_REQUEST_CODE = 1001;
    private String base64string = "";

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        defineComponents(view);
        return view;
    }


    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        img_profile = view.findViewById(R.id.profile_imageView);
        edt_phone = view.findViewById(R.id.profile_edt_phone);
        edt_name_surname = view.findViewById(R.id.profile_edt_name_surname);
        edt_username = view.findViewById(R.id.profile_edt_username);
        edt_password = view.findViewById(R.id.profile_edt_password);
        edt_password_again = view.findViewById(R.id.profile_edt_password_again);
        edt_mail = view.findViewById(R.id.profile_edt_mail);
        btn_update = view.findViewById(R.id.profile_btn);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modifyUIElements();
    }

    private void modifyUIElements() {
        img_profile.setOnClickListener(this);
        btn_update.setOnClickListener(this);

        SharedPreferences preferences = nActivity.getSharedPreferences(Constants.PREFERENCE_USER_INFO, MODE_PRIVATE);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(nActivity);

        String password = pref.getString(Constants.DEFAULT_PREF_PASSWORD, "");

        edt_name_surname.setText(preferences.getString(Constants.PREF_NAME_SURNAME, ""));
        edt_username.setText(preferences.getString(Constants.PREF_USERNAME, ""));
        edt_password.setText(password);
        edt_password_again.setText(password);
        edt_mail.setText(preferences.getString(Constants.PREF_MAIL, ""));
        edt_phone.setText(preferences.getString(Constants.PREF_PHONE, ""));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, nActivity, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                GlideApp.with(nActivity).load(imageFile)
                        .placeholder(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .fitCenter().into(img_profile);
                try {
                    byte[] bytes = loadFile(imageFile);
                    byte[] encoded = Base64.encode(bytes, Base64.DEFAULT);

                    base64string = new String(encoded);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.nActivity = (Activity) context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.nActivity = activity;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionForCamera() {
        int hasWriteDataPermission = nActivity.checkSelfPermission(Manifest.permission.CAMERA);
        if (hasWriteDataPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.messages_qr_need_permission), getString(R.string.messages_ok), null, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                    }
                }, null);
                return;
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }
        EasyImage.openCamera(this, EasyImageConfig.REQ_TAKE_PICTURE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (permission.equals(Manifest.permission.CAMERA)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        EasyImage.openCamera(this, EasyImageConfig.REQ_TAKE_PICTURE);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_imageView:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionForCamera();
                } else {
                    EasyImage.openCamera(this, EasyImageConfig.REQ_TAKE_PICTURE);
                }
                break;
            case R.id.profile_btn:
                String name_surname = edt_name_surname.getText().toString();
                String username = edt_username.getText().toString();
                String password = edt_password.getText().toString();
                String password_again = edt_password_again.getText().toString();
                String mail = edt_mail.getText().toString();
                String phone = edt_phone.getText().toString();
                if (checkIfEmptyField(name_surname, username, password, password_again, mail, phone)) {
                    updateInfo(name_surname, username, password, mail, phone);
                }
                break;
        }
    }

    private void updateInfo(final String name_surname, final String username, final String password, final String mail, final String phone) {
        if (HttpUtility.isOnline(nActivity)) {
            progress.setVisibility(View.VISIBLE);
            SharedPreferences preferences = nActivity.getSharedPreferences(Constants.PREFERENCE_USER_INFO, MODE_PRIVATE);
            String costumerId = preferences.getString(Constants.PREF_ID, "");
            HttpUrl url = HttpUrl.parse(Constants.URL_UPDATE_COSTUMER).newBuilder()
                    .addQueryParameter("musteriid", costumerId)
                    .addQueryParameter("adsoyad", name_surname)
                    .addQueryParameter("sifre", password)
                    .addQueryParameter("kullaniciadi", username)
                    .addQueryParameter("telefon", phone)
                    .addQueryParameter("email", mail)
                    .addQueryParameter("base64string", base64string)
                    .addQueryParameter("resimAdi", UUID.randomUUID().toString())
                    .build();

            HttpUtility.get(url.toString(), new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    nActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
                                progress.setVisibility(View.GONE);
                                ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();
                    nActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
                                progress.setVisibility(View.GONE);
                                if (res.contains("webservis.hepies.com")) {
                                    ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.update_success), getString(R.string.messages_ok), null, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String imageUrl = res.replaceAll("\"", "");
                                            SharedPreferences.Editor defaultEditor = PreferenceManager.getDefaultSharedPreferences(nActivity).edit();
                                            defaultEditor.putString(Constants.DEFAULT_PREF_PASSWORD, password).apply();
                                            defaultEditor.commit();

                                            SharedPreferences.Editor editor = nActivity.getSharedPreferences(Constants.PREFERENCE_USER_INFO, MODE_PRIVATE).edit();
                                            editor.putString(Constants.PREF_USERNAME, username).apply();
                                            editor.putString(Constants.PREF_NAME_SURNAME, name_surname).apply();
                                            editor.putString(Constants.PREF_MAIL, mail).apply();
                                            editor.putString(Constants.PREF_PHONE, phone).apply();
                                            editor.putString(Constants.PREF_IMAGE, imageUrl).apply();
                                            editor.commit();
                                        }
                                    }, null);
                                }
                                System.out.println(res);
                            }
                        }
                    });
                }
            });

        } else {
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private boolean checkIfEmptyField(String name_surname, String username, String password, String password_again, String mail, String phone) {
        if (name_surname == null || name_surname.length() <= 0 ||
                username == null || username.length() <= 0 ||
                password == null || password.length() <= 0 ||
                password_again == null || password_again.length() <= 0 ||
                mail == null || mail.length() <= 0 ||
                phone == null || phone.length() <= 0) {
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.messages_blank_field), getString(R.string.messages_ok), null, null, null);

            return false;
        } else if (!password.equals(password_again)) {
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.messages_password_do_not_match), getString(R.string.messages_ok), null, null, null);
            return false;
        }
        return true;
    }
}
