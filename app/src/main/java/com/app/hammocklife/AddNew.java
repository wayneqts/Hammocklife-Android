package com.app.hammocklife;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hammocklife.adapter.MyRecyclerViewAdapterSelectImage;
import com.app.hammocklife.custom.BSImagePicker;
import com.app.hammocklife.custom.RetrofitClient;
import com.app.hammocklife.custom.ServiceCallbacks;
import com.app.hammocklife.custom.Utility;
import com.app.hammocklife.model.ObjectUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNew extends BaseActivity implements View.OnClickListener, BSImagePicker.OnSingleImageSelectedListener,
        BSImagePicker.OnMultiImageSelectedListener,
        BSImagePicker.ImageLoaderDelegate,
        BSImagePicker.OnSelectImageCancelledListener {
    ArrayList<String> stringImage;
    RelativeLayout rl_loading;
    AppCompatEditText edt_location, edt_desciption, edt_name;
    RecyclerView rcv_photo;
    MyRecyclerViewAdapterSelectImage myRecyclerViewAdapter;
    public ArrayList<Uri> arrData = new ArrayList<>();
    File photoFile;
    private ArrayList<String> fileList = new ArrayList<String>();
    private int nameImage = 0;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    double getLong = 0;
    double getLat = 0;
    ObjectUser dataUser;
    int countImage = 0;
    int CODE_CAM = 1, CODE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_add_new);
        File file = new File(android.os.Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/");
        getFile2(file);
        initUI();
        if (getIntent() != null) {
            dataUser = (ObjectUser) getIntent().getSerializableExtra("dataUser");
        }
    }

    private void initUI() {
        AppCompatImageButton img_back = findViewById(R.id.img_back);
        AppCompatButton bt_submit = findViewById(R.id.bt_submit);
        rl_loading = findViewById(R.id.rl_loading);
        edt_location = findViewById(R.id.edt_location);
        edt_desciption = findViewById(R.id.edt_desciption);
        edt_name = findViewById(R.id.edt_name);
        rcv_photo = findViewById(R.id.rcv_photo);
        img_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        try {
            getLong = getIntent().getDoubleExtra("long", 0);
            getLat = getIntent().getDoubleExtra("lat", 0);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(AddNew.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(getLat, getLong, 1);
            String address = addresses.get(0).getAddressLine(0);
            edt_location.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            arrData.clear();
            Uri myUri = Uri.parse("http://www.google.com");
            arrData.add(myUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        resetPhoto();
    }

    public void resetPhoto() {
        try {
            rcv_photo.setLayoutManager(new GridLayoutManager(AddNew.this, 3));
            myRecyclerViewAdapter = new MyRecyclerViewAdapterSelectImage(AddNew.this, arrData, wwidth, height);
            rcv_photo.setAdapter(myRecyclerViewAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT >= 33){
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.CAMERA},
                        1);
            } else {
                showSelectMutipleImage();
            }
        }else {
            if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                requestPermissions(
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            } else {
                showSelectMutipleImage();
            }
        }
    }

    public void showSelectMutipleImage() {
        final CharSequence[] items = {"Capture photo", "Choose photo from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNew.this);
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

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSelectMutipleImage();
            } else {
                Toast.makeText(AddNew.this, "Permission Write External Storage denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void cameraIntent() {
        try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                this.startActivityForResult(intent, 0);
        } catch (Exception e) {
            Toast.makeText(AddNew.this, "Please grant camera permission for the app in Settings", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(AddNew.this,
                    new String[]{android.Manifest.permission.CAMERA},
                    2);
        }
    }

    private File getTempFile(Context context) {
        nameImage = QTSConstrains.getName(AddNew.this);
        nameImage++;
        QTSConstrains.setName(AddNew.this, nameImage);
        String fileName = "hammocklike" + nameImage + ".jpg";
        File path = new File(Environment.getExternalStorageDirectory(),
                context.getPackageName());
        if (!path.exists()) {
            path.mkdir();
        }
        return new File(path, fileName);
    }

    private Intent createIntentForCamera(Uri imageUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                Uri uri = data.getData();
                arrData.add(uri);
            } else if (requestCode == 0) {
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bm, "Title", null);
                Uri uri = Uri.parse(path);
                arrData.add(uri);
            } else if (requestCode == 1) {
                deleteCache(AddNew.this);
                File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName() + "/");
                getFile(file);
            }
        }
        myRecyclerViewAdapter.notifyDataSetChanged();
        for (int q = 0; q <= arrData.size() - 1; q++) {
            Log.e("data", arrData.get(q).toString());
        }
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

    public ArrayList<String> getFile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    getFile(file);
                } else {
                    if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
                        Uri uri = Uri.fromFile(file.getAbsoluteFile());
                        boolean add = false;
                        for (int q = 0; q <= arrData.size() - 1; q++) {
                            if (uri.toString().equals(arrData.get(q).toString())) {
                                add = true;
                            }
                        }
                        if (!add) {
                            arrData.add(uri);
                        }
                    }
                }
            }

            resetPhoto();
        }
        return fileList;
    }

    public ArrayList<String> getFile2(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    getFile2(file);
                } else {
                    if (file.getName().endsWith(".png")
                            || file.getName().endsWith(".jpg")
                            || file.getName().endsWith(".jpeg")) {
                        file.delete();
                    }
                }
            }
        }
        return fileList;
    }

    private void pushDataMyHammock() {
        rl_loading.setVisibility(View.VISIBLE);
        Map<String, Object> updates = new HashMap<String, Object>();
        long startTime = System.currentTimeMillis() / 1000;
        updates.put("address", edt_location.getText().toString());
        updates.put("createdAt", startTime);
        updates.put("createdBy", dataUser.getName());
        updates.put("description", edt_desciption.getText().toString());
        updates.put("name", edt_name.getText().toString());
        updates.put("serverTime", startTime);
        updates.put("status", "waiting");
        updates.put("userUDID", mAuth.getUid());

        mDatabase.child("my_hammock").child(mAuth.getUid()).push().setValue(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                Log.e("key", databaseReference.getKey());
                mDatabase.child("my_hammock").child(mAuth.getUid()).child(databaseReference.getKey()).child("location").child("_lat").setValue(getLat);
                mDatabase.child("my_hammock").child(mAuth.getUid()).child(databaseReference.getKey()).child("location").child("_lng").setValue(getLong);
                stringImage = new ArrayList<>();
                stringImage.clear();
                try {
                    for (int q = 1; q <= arrData.size() - 1; q++) {
                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://hammocklife-a7eff.appspot.com");
                        StorageReference storageRef = storage.getReference();
                        final StorageReference riversRef = storageRef.child("hammock_photos/" + databaseReference.getKey() + "/" + arrData.get(q).getLastPathSegment());
                        UploadTask uploadTask = riversRef.putFile(arrData.get(q));
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
                                    mDatabase.child("my_hammock").child(mAuth.getUid()).child(databaseReference.getKey()).child("photoURLs").child(countImage + "").setValue(downloadUri.toString());
                                    stringImage.add(downloadUri.toString());
                                    countImage++;
                                    if (countImage == arrData.size() - 1) {
                                        pushDataPending(databaseReference.getKey());
                                    }
                                } else {
                                    rl_loading.setVisibility(View.GONE);
                                    // Handle failures
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void pushDataPending(final String key) {
        rl_loading.setVisibility(View.VISIBLE);
        Map<String, Object> updates = new HashMap<String, Object>();
        long startTime = System.currentTimeMillis() / 1000;
        updates.put("address", edt_location.getText().toString());
        updates.put("createdAt", startTime);
        updates.put("createdBy", dataUser.getName());
        updates.put("description", edt_desciption.getText().toString());
        updates.put("name", edt_name.getText().toString());
        updates.put("serverTime", startTime);
        updates.put("status", "waiting");
        updates.put("userUDID", mAuth.getUid());

        mDatabase.child("pending_approval").child(key).setValue(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull final DatabaseReference databaseReference) {
                mDatabase.child("pending_approval").child(key).child("location").child("_lat").setValue(getLat);
                mDatabase.child("pending_approval").child(key).child("location").child("_lng").setValue(getLong);
                for (int q = 0; q <= stringImage.size() - 1; q++) {
                    mDatabase.child("pending_approval").child(key).child("photoURLs").child(q + "").setValue(stringImage.get(q));
                }
                pushNoti(edt_name.getText().toString(), edt_location.getText().toString());
                try {
                    rl_loading.setVisibility(View.GONE);
                    new AlertDialog.Builder(AddNew.this)
                            .setMessage("Your request is sent. Please wait for approval")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void pushNoti(final String name, final String address) {
        try {
            mDatabase.child("admin_device_tokens").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                            final Map<String, Object> requestBody = new HashMap<>();
                            requestBody.put("to", ds.getKey());
                            DataNoti dataNoti = new DataNoti("A new Hammock is added", "Name: " + name + "\n" + "Address: " + address);
                            requestBody.put("notification", dataNoti);
                            RetrofitClient.getClient("https://fcm.googleapis.com/").create(ServiceCallbacks.class).postToken(
                                    "key=AAAAEJBBUec:APA91bErbnZrDrrVTRbFG-v5Qiik_VdlrNVewbxGbeLLYDeQ2gYT59LkkHLZ4UR6ZeWPC0RjureOr8wr8wkisaJEkId1fJSUG09ogD5qjvavQNG0m3EV7spPpvP5d5WwGwS9sb1hHByw",
                                    requestBody
                            ).enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {

                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DataNoti {
        String title;
        String body;

        public DataNoti(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                try {
                    File file = new File(android.os.Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/");
                    getFile2(file);
                    QTSConstrains.setName(AddNew.this, 0);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_submit:
                if (edt_name.getText().toString().length() > 0) {
                    if (edt_location.getText().toString().length() > 0) {
                        if (edt_desciption.getText().toString().length() > 0) {
                            if (arrData.size() > 1) {
                                pushDataMyHammock();
                            } else {
                                Toast.makeText(AddNew.this, "Photo is required", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(AddNew.this, "Description is required", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AddNew.this, "Location is required", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddNew.this, "Name is required", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void loadImage(Uri imageUri, ImageView ivImage) {
        Glide.with(AddNew.this)
                .load(imageUri)
                .transform(new CenterCrop(), new RoundedCorners(1))
                .into(ivImage);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList, String tag) {
        arrData.addAll(uriList);
        resetPhoto();
    }

    @Override
    public void onCancelled(boolean isMultiSelecting, String tag) {

    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        File file = new File(android.os.Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/");
        getFile2(file);
        QTSConstrains.setName(AddNew.this, 0);
    }

    @Override
    protected void onDestroy() {
        File file = new File(android.os.Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/");
        getFile2(file);
        Log.e("onDestroy", "delete");
        super.onDestroy();
    }
}
