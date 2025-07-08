package com.app.hammocklife.fragment;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.hammocklife.About;
import com.app.hammocklife.AddNew;
import com.app.hammocklife.InforUser;
import com.app.hammocklife.Main;
import com.app.hammocklife.MarketPlace;
import com.app.hammocklife.R;
import com.app.hammocklife.Splash;
import com.app.hammocklife.custom.BSImagePicker;
import com.app.hammocklife.custom.Utility;
import com.app.hammocklife.model.ObjectHammocks;
import com.app.hammocklife.model.ObjectLocation;
import com.app.hammocklife.model.ObjectUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

public class Frm_Home extends BaseFragment implements SensorEventListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener,OnMapReadyCallback, View.OnClickListener {
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private FusedLocationProviderClient fusedLocationClient;
    private AppCompatButton tv_satellite, tv_location_current;
    private AppCompatImageButton bt_menu;
    private LinearLayout ln_log_out, ln_login, ln_infouser;
    private View view_bg_menu;
    private RelativeLayout rl_animation_menu, rl_bg_menu;
    private AppCompatTextView tv_guest;
    private AppCompatTextView tv_name_user;
    private AppCompatTextView tv_email_user;
    private AppCompatTextView tv_admin, tv_text_search;
    private DatabaseReference mDataAllHamocks = FirebaseDatabase.getInstance().getReference("all_hammocks");
    private ArrayList<Marker> arrDataAllHamock = new ArrayList<>();
    private CircleImageView img_user;
    private Marker currentLocationMarker;
    private FirebaseUser currentUser;
    private View view;
    private AppCompatButton bt_search;
    private AppCompatEditText edt_longitude, edt_latitude, edt_address;
    private String messageAddNew = "";
    private LinearLayout bt_change_search, ln_lat_lng;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        view = inflater.inflate(R.layout.frm_home, container, false);
        initUI();
        return view;
    }

    public void initUI() {
        try {
            ln_lat_lng = view.findViewById(R.id.ln_lat_lng);
            edt_address = view.findViewById(R.id.edt_address);
            bt_change_search = view.findViewById(R.id.bt_change_search);
            tv_text_search = view.findViewById(R.id.tv_text_search);
            edt_latitude = view.findViewById(R.id.edt_latitude);
            edt_longitude = view.findViewById(R.id.edt_longitude);
            bt_search = view.findViewById(R.id.bt_search);
            tv_location_current = view.findViewById(R.id.tv_location_current);
            img_user = view.findViewById(R.id.img_user);
            bt_menu = view.findViewById(R.id.bt_menu);
            rl_bg_menu = view.findViewById(R.id.rl_bg_menu);
            view_bg_menu = view.findViewById(R.id.view_bg_menu);
            rl_animation_menu = view.findViewById(R.id.rl_animation_menu);
            tv_satellite = view.findViewById(R.id.tv_satellite);
            ln_log_out = view.findViewById(R.id.ln_log_out);
            tv_guest = view.findViewById(R.id.tv_guest);
            AppCompatTextView tv_rate = view.findViewById(R.id.tv_rate);
            AppCompatTextView tv_tutorial = view.findViewById(R.id.tv_tutorial);
            tv_email_user = view.findViewById(R.id.tv_email_user);
            tv_name_user = view.findViewById(R.id.tv_name_user);
            ln_login = view.findViewById(R.id.ln_login);
            ln_infouser = view.findViewById(R.id.ln_infouser);
            AppCompatTextView tv_my_hammock = view.findViewById(R.id.tv_my_hammock);
            tv_admin = view.findViewById(R.id.tv_admin);
            AppCompatTextView tv_marketplace = view.findViewById(R.id.tv_marketplace);
            AppCompatTextView tv_about = view.findViewById(R.id.tv_about);
            AppCompatTextView tv_social = view.findViewById(R.id.tv_social);
            AppCompatTextView tv_contact = view.findViewById(R.id.tv_contact);
            AppCompatTextView tv_Privacy = view.findViewById(R.id.tv_Privacy);

            tv_tutorial.setOnClickListener(this);
            tv_satellite.setOnClickListener(this);
            ln_log_out.setOnClickListener(this);
            view_bg_menu.setOnClickListener(this);
            bt_menu.setOnClickListener(this);
            ln_infouser.setOnClickListener(this);
            tv_rate.setOnClickListener(this);
            img_user.setOnClickListener(this);
            tv_location_current.setOnClickListener(this);
            tv_my_hammock.setOnClickListener(this);
            tv_admin.setOnClickListener(this);
            tv_marketplace.setOnClickListener(this);
            tv_about.setOnClickListener(this);
            tv_social.setOnClickListener(this);
            tv_contact.setOnClickListener(this);
            tv_Privacy.setOnClickListener(this);
            bt_search.setOnClickListener(this);
            bt_change_search.setOnClickListener(this);

            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                Log.d("TAG", "initUI: "+mapFragment);
                mapFragment.getMapAsync(this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            currentUser = mAuth.getCurrentUser();
            checkLoginOrSkip = currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().equals("android@gmail.com");
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            if (checkLoginOrSkip){
                ln_login.setVisibility(View.VISIBLE);
                tv_guest.setVisibility(View.GONE);
            }else {
                ln_login.setVisibility(View.GONE);
                tv_guest.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void setTextUser(ObjectUser data){
        try{
            tv_name_user.setText(data.getName());
            tv_email_user.setText(data.getEmail());
            if (data.getRole().equals("Admin")){
                tv_admin.setVisibility(View.VISIBLE);
            }else {
                tv_admin.setVisibility(View.GONE);
            }

            if (data.getProfileUrl()!=null && data.getProfileUrl().length()>5) {
                Glide.with(getActivity())
                        .load(data.getProfileUrl())
                        .transform(new CenterCrop(), new RoundedCorners(8))
                        .into(img_user);
            }
            try {
                if(data.getEmail()!=null && !data.getEmail().equals("android@gmail.com")){
                    checkLoginOrSkip = true;
                }else {
                    checkLoginOrSkip = false;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                if (checkLoginOrSkip){
                    ln_login.setVisibility(View.VISIBLE);
                    tv_guest.setVisibility(View.GONE);
                }else {
                    ln_login.setVisibility(View.GONE);
                    tv_guest.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("checkLoginOrSkip",checkLoginOrSkip+" - "+currentUser.getUid()+" - "+currentUser.getEmail());
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            try {
                float distance = 0;
                if (location != null && location_current != null) {
                    distance = location_current.distanceTo(location);
                }
                if (distance >= 1) {
                    location_current = location;
                    float zoom = mMap.getCameraPosition().zoom;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            googleMap.setOnMarkerClickListener(this);
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        100);
            } else {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(false);
                mMap = googleMap;
                mMap.setOnMarkerClickListener(this);
                mMap.setOnMapLongClickListener(this);
                mLocationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
                assert mLocationManager != null;
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                        0, mLocationListener);
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        try {
                            location_current = location;
                            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            Log.e("error location1", location_current.getLongitude()+" -- "+location_current.getLatitude()+" -- ");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                mDataAllHamocks.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mMap != null) {
                            try {
                                mMap.clear();
                                LatLng sydney = new LatLng(location_current.getLatitude(), location_current.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        arrDataAllHamock.clear();
                        arrHammock.clear();
                        for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                            ObjectLocation objectLocation = null;
                            try{
                                LatLng sydney2 = new LatLng(ds.child("location").child("_lat").getValue(Double.class), ds.child("location").child("_lng").getValue(Double.class));
                                Marker marker = mMap.addMarker(new MarkerOptions().position(sydney2).title(ds.child("address").getValue(String.class)).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_pin", 70, 70))));
                                arrDataAllHamock.add(marker);
                                objectLocation = new ObjectLocation(ds.child("location").child("_lat").getValue(Double.class), ds.child("location").child("_lng").getValue(Double.class));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            ArrayList<String> arrUrlLink = new ArrayList<>();
                            try {
                                for (int q = 0; q <= ds.child("photoURLs").getChildrenCount()-1; q++){
                                    arrUrlLink.add(ds.child("photoURLs").child(q+"").getValue(String.class));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            ObjectHammocks objectHammocks = new ObjectHammocks(
                                    ds.getKey(),
                                    ds.child("address").getValue(String.class),
                                    ds.child("createdBy").getValue(String.class),
                                    ds.child("description").getValue(String.class),
                                    ds.child("name").getValue(String.class),
                                    ds.child("status").getValue(String.class),
                                    ds.child("userUDID").getValue(String.class),
                                    ds.child("createdAt").getValue(Long.class),
                                    ds.child("serverTime").getValue(Long.class),
                                    objectLocation,
                                    arrUrlLink,
                                    ""
                            );
                            arrHammock.add(objectHammocks);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.getTitle().equals("Add New")){
            if (currentUser.getEmail().equals("android@gmail.com")){
                dialogLogin();
            }else {
                startActivity(new Intent(getActivity(), AddNew.class).putExtra("dataUser",((Main)getActivity()).dataUser).putExtra("lat", marker.getPosition().latitude).putExtra("long", marker.getPosition().longitude));
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                if (null != currentLocationMarker) {
                    currentLocationMarker.remove();
                }
            }
        }else {
            showLoading();
            for (int q = 0; q <= arrDataAllHamock.size() - 1; q++) {
                if (arrDataAllHamock.get(q).getTitle().equals(marker.getTitle())) {
                    try {
                        ((Main)getActivity()).tagDetail = "details";
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Frm_Deails frm_deails = new Frm_Deails(arrHammock.get(q));
                    addFragmentBack(frm_deails);
                    hideLoading();
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_change_search:
                final CharSequence[] items = {"Search for address", "Search for lat,long"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Search for address")) {
                            tv_text_search.setText("Search for address");
                            edt_address.setVisibility(View.VISIBLE);
                            ln_lat_lng.setVisibility(View.GONE);
                            edt_latitude.setText("");
                            edt_longitude.setText("");
                        } else if (items[item].equals("Search for lat,long")) {
                            tv_text_search.setText("Search for lat,long");
                            edt_address.setVisibility(View.GONE);
                            ln_lat_lng.setVisibility(View.VISIBLE);
                            edt_address.setText("");
                        }
                    }
                });
                builder.show();
                break;
            case R.id.bt_search:
                if (tv_text_search.getText().toString().equals("Search for address")){
                    try {
                        if (edt_address.getText()!=null && edt_address.getText().toString().length() == 0){
                            dialogAddNewError("Please enter address");
                            return;
                        }
                        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
                        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> addresses = geoCoder.getFromLocationName(edt_address.getText().toString(), 5);
                        if (addresses.size() > 0)
                        {
                            Double lat = (double) (addresses.get(0).getLatitude());
                            Double lon = (double) (addresses.get(0).getLongitude());
                            Log.d("lat-long", "" + lat + "......." + lon);
                            if (null != currentLocationMarker) {
                                currentLocationMarker.remove();
                            }
                            LatLng latLng = new LatLng(lat, lon);
                            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                                    .title("Add New")
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_pin_new", 90, 89))));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
                        }else {
                            dialogAddNewError("We cannot find address (" + edt_address.getText().toString()+")");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    if (edt_latitude.getText().toString().length() > 0 && edt_longitude.getText().toString().length() > 0) {
                        try {
                            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(INPUT_METHOD_SERVICE);
                            Objects.requireNonNull(imm).hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
                            double lat = Double.parseDouble(edt_latitude.getText().toString());
                            double log = Double.parseDouble(edt_longitude.getText().toString());
                            if (null != currentLocationMarker) {
                                currentLocationMarker.remove();
                            }
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            addresses = geocoder.getFromLocation(lat, log, 1);
                            messageAddNew = addresses.get(0).getAddressLine(0);
                            LatLng latLng = new LatLng(lat, log);
                            currentLocationMarker = mMap.addMarker(new MarkerOptions()
                                    .title("Add New")
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_pin_new", 90, 89))));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13.0f));
                            dialogAddNew(messageAddNew, lat, log);
                        } catch (Exception e) {
                            if (e.getMessage().contains("==")) {
                                dialogAddNewError("We cannot find location (" + edt_latitude.getText().toString() + ", " + edt_longitude.getText().toString() + ")");
                            } else {
                                dialogAddNewError("Please input correct latitude and longitude");
                            }
                            e.printStackTrace();
                        }
                    } else {
                        dialogAddNewError("Please input correct latitude and longitude");
                    }
                }
                break;
            case R.id.tv_location_current:
                try {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().equals("android@gmail.com")) {
                        startActivity(new Intent(getActivity(), AddNew.class).putExtra("dataUser",((Main)getActivity()).dataUser).putExtra("lat", location_current.getLatitude()).putExtra("long", location_current.getLongitude()));
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        if (null != currentLocationMarker) {
                            currentLocationMarker.remove();
                        }
                    } else {
                        dialogLogin();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_satellite:
                try {
                    if (tv_satellite.getText().toString().equals("Satellite")) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        tv_satellite.setText("Map");
                    }else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        tv_satellite.setText("Satellite");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.ln_log_out:
                try {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Do you want to logout?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                    mDatabase.child("device_tokens").child(currentUser.getUid()).removeValue();
                                    startActivity(new Intent(getActivity(), Splash.class));
                                    Objects.requireNonNull(getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    getActivity().finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.bt_menu:
                try {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            rl_bg_menu.setVisibility(View.VISIBLE);
                            rl_animation_menu.setVisibility(View.VISIBLE);
                            rl_animation_menu.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_right));
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.view_bg_menu:
                try {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            rl_animation_menu.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_left));
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        rl_animation_menu.setVisibility(View.GONE);
                                        rl_bg_menu.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 200);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.ln_infouser:
                try {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().equals("android@gmail.com")) {
                        startActivityForResult(new Intent(getActivity(), InforUser.class).putExtra("dataUser",((Main)getActivity()).dataUser),121);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        dialogProfile();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.img_user:
                try {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().equals("android@gmail.com")) {
                        startActivityForResult(new Intent(getActivity(), InforUser.class).putExtra("dataUser",((Main)getActivity()).dataUser),121);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        dialogProfile();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_my_hammock:
                try {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().equals("android@gmail.com")) {
                        Frm_MyHammock frm_deails = new Frm_MyHammock();
                        addFragmentBack(frm_deails);
                    } else {
                        dialogHammock();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_admin:
                try {
                    Frm_Admin frm_deails = new Frm_Admin();
                    addFragmentBack(frm_deails);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_marketplace:
                try {
                    startActivity(new Intent(getActivity(), MarketPlace.class));
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_about:
                try {
                    startActivity(new Intent(getActivity(), About.class));
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_social:
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/471790053522677/"));
                    startActivity(browserIntent);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_contact:
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.setData(Uri.parse("mailto:watsontravis@hotmail.com")); // or just "mailto:" for blank
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                    startActivity(intent);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_rate:
                try {
                    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_tutorial:
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=3FbSakrdewc&t=39s"));
                    startActivity(browserIntent);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.tv_Privacy:
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/privacy-policy/8a2f62dea26c4e0d51ac9845cfc269bc"));
                    startActivity(browserIntent);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private Bitmap resizeMapIcons(String iconName,int width, int height){
        try {
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
            return resizedBitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void dialogAddNew(String message, final double lat, final double lng){
        new AlertDialog.Builder(getActivity())
                .setTitle("We found your location")
                .setMessage(message+"\n("+edt_latitude.getText().toString()+", "+edt_longitude.getText().toString()+")")
                .setPositiveButton(android.R.string.cancel, null)
                .setNegativeButton("String it Up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (currentUser.getEmail().equals("android@gmail.com")){
                            dialogLogin();
                        }else {
                            startActivity(new Intent(getActivity(), AddNew.class).putExtra("dataUser",((Main)getActivity()).dataUser).putExtra("lat", lat).putExtra("long", lng));
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            if (null != currentLocationMarker) {
                                currentLocationMarker.remove();
                            }
                            edt_latitude.setText("");
                            edt_longitude.setText("");
                        }
                    }
                })
                .show();
    }

    private void dialogAddNewError(String message){
        new AlertDialog.Builder(getActivity())
                .setTitle("Not found")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void dialogLogin(){
        new AlertDialog.Builder(getActivity())
                .setMessage("To submit a new Hammock place, you need to login first. ")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), Splash.class));
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void dialogProfile(){
        new AlertDialog.Builder(getActivity())
                .setMessage("To edit profile, you need to login first. ")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), Splash.class));
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void dialogHammock(){
        new AlertDialog.Builder(getActivity())
                .setMessage("To view my hammocks, you need to login first.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getActivity(), Splash.class));
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (null != currentLocationMarker) {
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions()
                .title("Add New")
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_pin_new", 90, 89))));
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }
}