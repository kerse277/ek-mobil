package com.ekmobil.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ekmobil.R;
import com.ekmobil.adapter.CameraAdapter;
import com.ekmobil.adapter.PersonelChoicesAdapter;
import com.ekmobil.entity.PersonelEntity;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;
import com.google.android.cameraview.CameraView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements View.OnClickListener {
    private Activity nActivity;
    private View progress;
    private CameraView camera;
    private ImageButton imb_switch_camera;
    private Button fb_take_picture, btn_cancel, btn_send;
    private final int CAMERA_REQUEST_CODE = 1234;
    private ViewPager viewPager;
    private ImageView img_camera;
    private List<PersonelEntity> list = new ArrayList<>();
    private Bitmap postBitmap;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        defineComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modifyUIElements();

        if (camera != null) {
            camera.addCallback(mCallback);
        }

        getPersonelData();
    }

    private void getPersonelData() {
        progress.setVisibility(View.VISIBLE);
        if (HttpUtility.isOnline(nActivity)) {

            HttpUrl url = HttpUrl.parse(Constants.URL_PERSONEL).newBuilder()
                    .addQueryParameter("firmaID", Constants.PARAM_FIRMA_ID).build();
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
                                parsePersonelData(res);
                            }
                        }
                    });
                }
            });
        } else {
            progress.setVisibility(View.GONE);
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parsePersonelData(String res) {
        if (res != null && res.length() > 0) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        PersonelEntity entity = new PersonelEntity();
                        entity.setId(ProjectUtility.getJsonValue(object, "ID").trim());
                        entity.setPersonelImage(ProjectUtility.getJsonValue(object, "resim").trim());
                        entity.setPersonelPhone(ProjectUtility.getJsonValue(object, "telefon").trim());
                        entity.setPersonelMail(ProjectUtility.getJsonValue(object, "email").trim());
                        entity.setPersonelNameSurname(ProjectUtility.getJsonValue(object, "adSoyad").trim());

                        list.add(entity);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        progress.setVisibility(View.GONE);
    }


    private void modifyUIElements() {
        imb_switch_camera.setOnClickListener(this);
        fb_take_picture.setOnClickListener(this);

        btn_cancel.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        viewPager.setOffscreenPageLimit(2);
        CameraAdapter adapter = new CameraAdapter(nActivity);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
    }


    private void defineComponents(View view) {
        viewPager = view.findViewById(R.id.camera_view_pager);
        camera = view.findViewById(R.id.camera);
        imb_switch_camera = view.findViewById(R.id.camera_switch);
        fb_take_picture = view.findViewById(R.id.camera_take_picture);
        progress = view.findViewById(R.id.progress);
        img_camera = view.findViewById(R.id.camera_img);
        btn_cancel = view.findViewById(R.id.camera_btn_cancel);
        btn_send = view.findViewById(R.id.camera_btn_send);

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

    private CameraView.Callback mCallback
            = new CameraView.Callback() {

        @Override
        public void onCameraOpened(CameraView cameraView) {
            Log.d("onCameraOpened", "onCameraOpened");
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            Log.d("onCameraClosed", "onCameraClosed");
        }

        @Override
        public void onPictureTaken(CameraView cameraView, final byte[] data) {
            Log.d("onPictureTaken", "onPictureTaken: ");

            try {
                int rotationDegrees = 0;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    ExifInterface exifInterface = new ExifInterface(new ByteArrayInputStream(data));
                    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotationDegrees = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotationDegrees = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotationDegrees = 270;
                            break;
                    }
                }
                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                viewPager.setCurrentItem(1, true);
                postBitmap = getResizedBitmap(rotateBitmap(bitmap, rotationDegrees), 1000);
                img_camera.setImageBitmap(postBitmap);
                progress.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void postImage(String personelId) {
        progress.setVisibility(View.VISIBLE);
        if (HttpUtility.isOnline(nActivity)) {
            if (postBitmap != null) {
                String imgString = Base64.encodeToString(getBytesFromBitmap(postBitmap), Base64.NO_WRAP);
                SharedPreferences preferences = nActivity.getSharedPreferences(Constants.PREFERENCE_USER_INFO, Context.MODE_PRIVATE);
                String costumerId = preferences.getString(Constants.PREF_ID, "");

                RequestBody body = new FormBody.Builder()
                        .add("firmaID", Constants.PARAM_FIRMA_ID)
                        .add("musteriid", costumerId)
                        .add("personelid", personelId)
                        .add("base64string", imgString)
                        .add("resimAdi", UUID.randomUUID().toString()).build();
                HttpUrl url = HttpUrl.parse(Constants.URL_POST_IMAGE).newBuilder()
                        .addQueryParameter("firmaID", Constants.PARAM_FIRMA_ID)
                        .addQueryParameter("musteriid", costumerId)
                        .addQueryParameter("personelid", personelId)
                        .addQueryParameter("base64string", imgString)
                        .addQueryParameter("resimAdi", UUID.randomUUID().toString())
                        .build();

                HttpUtility.post(Constants.URL_POST_IMAGE, body, new Callback() {
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
                                    parsePostImageResponse(res);
                                }
                            }
                        });
                    }
                });

            } else {
                progress.setVisibility(View.GONE);
                ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.unexpected_error), getString(R.string.messages_ok), null, null, null);
            }


        } else {
            progress.setVisibility(View.GONE);
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parsePostImageResponse(String res) {
        progress.setVisibility(View.GONE);
        if (res.contains("TRUE")) {
            ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.post_image_success), getString(R.string.messages_ok), null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    viewPager.setCurrentItem(0, true);
                }
            }, null);
        } else {
            res = res.replaceAll("\"", "");
            ProjectUtility.showDialog(nActivity, getString(R.string.messages_error), res, getString(R.string.messages_ok), null, null, null);
        }
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        return stream.toByteArray();
    }

    private Bitmap rotateBitmap(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissionForCamera();
        } else {
            camera.start();
        }
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
                        camera.start();
                    }
                }
            }
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
        camera.start();
    }

    @Override
    public void onPause() {
        camera.stop();

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_switch:
                camera.setFacing(camera.getFacing() == CameraView.FACING_BACK ? CameraView.FACING_FRONT : CameraView.FACING_BACK);
                break;
            case R.id.camera_take_picture:
                if (camera != null) {
                    progress.setVisibility(View.VISIBLE);
                    camera.takePicture();
                }
                break;

            case R.id.camera_btn_send:
                showDialog();
                break;
            case R.id.camera_btn_cancel:
                viewPager.setCurrentItem(0, true);
                break;
        }
    }


    private void showDialog() {
        final Dialog dialog = new Dialog(nActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choice_personel);

        RecyclerView rcy_dialog = dialog.findViewById(R.id.rcy_choice_personel);
        if (list != null && list.size() > 0) {
            PersonelChoicesAdapter adapter = new PersonelChoicesAdapter(nActivity, list, dialog, this);
            rcy_dialog.setHasFixedSize(true);
            rcy_dialog.setLayoutManager(new LinearLayoutManager(nActivity));
            rcy_dialog.setAdapter(adapter);
        } else {
            ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.could_not_find_personel), getString(R.string.messages_ok), null, null, null);
        }

        Button btn_dialog_cancel = dialog.findViewById(R.id.btn_choice_personel_cancel);
        btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
