package com.app.hammocklife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.hammocklife.custom.BSImagePicker;
import com.app.hammocklife.custom.Utility;
import com.app.hammocklife.model.ObjectUser;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class InforUser extends BaseActivity implements View.OnClickListener {
    private AppCompatTextView tv_email, tv_date_join, tv_employment, tv_hobbies, tv_save;
    private AppCompatEditText edt_name, edt_location;
    private List<String> arrhobbies = new ArrayList<>();
    private ObjectUser dataUser;
    private AppCompatImageButton img_back;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private CircleImageView img_avatar;
    private final int REQUEST_CAMERA = 0, CAPTURE_PICTURE = 1, SELECT_FILE = 2;
    private Uri uri = null;
    private RelativeLayout rl_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_infouser);
        initUI();
    }

    private void initUI() {
        if (getIntent() != null) {
            dataUser = (ObjectUser) getIntent().getSerializableExtra("dataUser");
        }
        img_avatar = findViewById(R.id.img_avatar);
        img_back = findViewById(R.id.img_back);
        tv_email = findViewById(R.id.tv_email);
        edt_name = findViewById(R.id.edt_name);
        tv_date_join = findViewById(R.id.tv_date_join);
        edt_location = findViewById(R.id.edt_location);
        tv_employment = findViewById(R.id.tv_employment);
        tv_hobbies = findViewById(R.id.tv_hobbies);
        tv_save = findViewById(R.id.tv_save);
        rl_loading = findViewById(R.id.rl_loading);

        tv_employment.setOnClickListener(this);
        tv_hobbies.setOnClickListener(this);
        img_back.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        img_avatar.setOnClickListener(this);

        try {
            tv_email.setText(dataUser.getEmail());
            edt_name.setText(dataUser.getName());
            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            String dateString = formatter.format(new Date(dataUser.getCreateAt() * 1000L));
            tv_date_join.setText(dateString);
            if (dataUser.getProfileUrl() != null) {
                Glide.with(InforUser.this)
                        .load(dataUser.getProfileUrl())
                        .into(img_avatar);
            }
            if (dataUser.getLivingLocation() != null) {
                edt_location.setText(dataUser.getLivingLocation());
            }
            if (dataUser.getEmployment() != null) {
                tv_employment.setText(dataUser.getEmployment());
            }
            if (dataUser.getHobbies() != null && dataUser.getHobbies().size() > 0) {
                Log.e("size hon", dataUser.getHobbies().size() + "--");
                for (int q = 0; q <= dataUser.getHobbies().size() - 1; q++) {
                    if (dataUser.getHobbies().size() == 1) {
                        tv_hobbies.setText(dataUser.getHobbies().get(q));
                    } else {
                        if (q == 0) {
                            tv_hobbies.setText(dataUser.getHobbies().get(q));
                        } else {
                            tv_hobbies.setText(tv_hobbies.getText().toString() + ", " + dataUser.getHobbies().get(q));
                        }
                    }
                }
                arrhobbies.addAll(dataUser.getHobbies());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Capture photo", "Choose photo from Gallery"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(InforUser.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Capture photo")) {
                    cameraIntent();


                } else if (items[item].equals("Choose photo from Gallery")) {
                    galleryIntent();
                }
            }
        });
        builder.show();
    }

    private void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private File getTempFile(Context context) {
        String fileName = "avatar.jpg";
        File path = new File(Environment.getExternalStorageDirectory(),
                context.getPackageName() + "avatar");
        if (!path.exists()) {
            path.mkdir();
        }
        return new File(path, fileName);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    public void cameraIntent() {
        Log.d("MainActivity", "Showing camera");
        try {
            Log.d("MainActivity", "Showing camera2");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (Exception e) {
            Toast.makeText(InforUser.this, "Please grant camera permission for the app in Settings", Toast.LENGTH_LONG).show();
            Log.e("MainActivity", "" + e.toString());
            ActivityCompat.requestPermissions(InforUser.this,
                    new String[]{Manifest.permission.CAMERA},
                    2);
        }
    }

    private Intent createIntentForCamera(Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        return intent;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(InforUser.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
            return;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                uri = data.getData();
                Glide.with(InforUser.this)
                        .load(uri.toString())
                        .into(img_avatar);
//                setPhoto(3, uri);
                Log.e("MainActivity", "SELECT_FILE:" + data.getData().toString());
            } else if (requestCode == REQUEST_CAMERA) {
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                Glide.with(InforUser.this)
                        .load(bm)
                        .into(img_avatar);
//                setPhoto(1, uri);
            } else if (requestCode == CAPTURE_PICTURE) {
                deleteCache(InforUser.this);
                uri = Uri.fromFile(getTempFile(InforUser.this));
                Glide.with(InforUser.this)
                        .load(uri.toString())
                        .into(img_avatar);
//                setPhoto(2, uri);
                Log.e("MainActivity", "CAPTURE_PICTURE:" + uri.toString());
            }

        }
    }

    private void updateUser() {
        mDatabase.child("users").child(mAuth.getUid()).child("employment").setValue(tv_employment.getText().toString());
        if (arrhobbies.size() > 0) {
            mDatabase.child("users").child(mAuth.getUid()).child("hobbies").removeValue();
        }
        for (int q = 0; q <= arrhobbies.size() - 1; q++) {
            mDatabase.child("users").child(mAuth.getUid()).child("hobbies").child(q + "").setValue(arrhobbies.get(q));
        }
        mDatabase.child("users").child(mAuth.getUid()).child("livingLocation").setValue(edt_location.getText().toString());
        mDatabase.child("users").child(mAuth.getUid()).child("name").setValue(edt_name.getText().toString());
        if (uri != null) {
            try {
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://hammocklife-a7eff.appspot.com");
                StorageReference storageRef = storage.getReference();
                final StorageReference riversRef = storageRef.child("user_avatars/" + mAuth.getUid() + "/" + uri.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(uri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return riversRef.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            mDatabase.child("users").child(mAuth.getUid()).child("profileUrl").setValue(downloadUri.toString());
                            rl_loading.setVisibility(View.GONE);
                            Toast.makeText(InforUser.this, "Your profile have been updated.", Toast.LENGTH_LONG).show();
                        } else {
                            rl_loading.setVisibility(View.GONE);
                            // Handle failures
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            rl_loading.setVisibility(View.GONE);
            Toast.makeText(InforUser.this, "Your profile have been updated.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                if (edt_name.getText().toString().length() > 0) {
                    if (edt_location.getText().toString().length() > 0) {
                        if (tv_employment.getText().toString().length() > 0) {
                            if (tv_hobbies.getText().toString().length() > 0) {
                                rl_loading.setVisibility(View.VISIBLE);
                                updateUser();
                            } else {
                                Toast.makeText(InforUser.this, "invalid Hobbies", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(InforUser.this, "invalid Employment", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(InforUser.this, "invalid Location", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(InforUser.this, "invalid Name", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.img_avatar:
                if (Build.VERSION.SDK_INT >= 33) {
                    if ((ContextCompat.checkSelfPermission(InforUser.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(InforUser.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(
                                new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.CAMERA},
                                1);
                    } else {
                        selectImage();
                    }
                } else {
                    if ((ContextCompat.checkSelfPermission(InforUser.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(InforUser.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(InforUser.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        requestPermissions(
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    } else {
                        selectImage();
                    }
                }
                break;
            case R.id.img_back:
                try {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_employment:
                try {
                    final String[] items = {"Teacher", "Software Engineer", "Blogger", "Student", "Account Manager", "Data Analyst", "Dentist", "Lawyer", "Other"};

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(InforUser.this);

                    builder.setTitle("Employment")
                            .setItems(items, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    tv_employment.setText(items[item]);
                                }
                            });
                    builder.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_hobbies:
                try {
                    arrhobbies.clear();
                    final String[] items = {"Socializing", "Housework", "Camping", "Animal Care", "Painting", "Billiards", "Cooking", "Computer Programming", "Cosplaying", "Soccer", "Other"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(InforUser.this);
                    builder.setTitle("Hobbies")
                            .setMultiChoiceItems(items, null,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        public void onClick(DialogInterface dialog, int item, boolean isChecked) {
                                            if (isChecked) {
                                                arrhobbies.add(items[item]);
                                            } else {
                                                for (int q = 0; q <= arrhobbies.size() - 1; q++) {
                                                    if (arrhobbies.get(q).toLowerCase().trim().equals(items[item].toLowerCase().trim())) {
                                                        arrhobbies.remove(q);
                                                    }
                                                }
                                            }
                                            Log.i("Dialogos", "Opción elegida: " + items[item]);
                                        }
                                    });
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int q = 0; q <= arrhobbies.size() - 1; q++) {
                                        if (arrhobbies.size() == 1) {
                                            tv_hobbies.setText(arrhobbies.get(q));
                                        } else {
                                            if (q == 0) {
                                                tv_hobbies.setText(arrhobbies.get(q));
                                            } else {
                                                tv_hobbies.setText(tv_hobbies.getText().toString() + ", " + arrhobbies.get(q));
                                            }
                                        }
                                        Log.e("arrhobbies", arrhobbies.get(q));
                                    }
                                }
                            });

                    builder.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (dataUser.getHobbies() != null && dataUser.getHobbies().size() > 0) {
                                        arrhobbies.addAll(dataUser.getHobbies());
                                    }
                                }
                            });

                    builder.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
