package com.example.football_field_booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.IOException;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;

public class CreateFootballFieldActivity extends AppCompatActivity {

    private Button btnChooseImg;
    private ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_football_field);
        imgPhoto = findViewById(R.id.img_photo);
        btnChooseImg = findViewById(R.id.btnChooseImage);

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });
    }

    private void requestPermissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openImagePicker();
                }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(CreateFootballFieldActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA)
                .check();

    }

    private void openImagePicker(){
        TedBottomPicker.OnImageSelectedListener listener= new TedBottomPicker.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Uri uri) {
                try {
                    Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    imgPhoto.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(CreateFootballFieldActivity.this)
                .setOnImageSelectedListener(listener)
                .create();

        tedBottomPicker.show(getSupportFragmentManager());
    }
}