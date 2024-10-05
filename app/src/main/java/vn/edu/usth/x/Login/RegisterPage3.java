package vn.edu.usth.x.Login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import vn.edu.usth.x.Login.Data.User;
import vn.edu.usth.x.Login.Data.UserManager;
import vn.edu.usth.x.R;

public class RegisterPage3 extends AppCompatActivity {
    private ImageView profileImageView;
    private String base64Image = "";
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_image);

        profileImageView = findViewById(R.id.profile_image);
        ImageButton addPhotoButton = findViewById(R.id.btn_add_photo);
        Button nextButton = findViewById(R.id.nextButton);
        TextView skipForNow = findViewById(R.id.skipForNow);

        addPhotoButton.setOnClickListener(v -> openGallery());
        nextButton.setOnClickListener(v -> finishRegistration());
        skipForNow.setOnClickListener(v -> finishRegistration());
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
                base64Image = bitmapToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void finishRegistration() {
        User user = UserManager.getUser();
        user.setProfileImageBase64(base64Image);
        Intent intent = new Intent(RegisterPage3.this, RegisterPage4.class);
        Log.d("RegisterPage1", "Name: " + user.getName());
        Log.d("RegisterPage1", "Email: " + user.getEmail());
        Log.d("RegisterPage1", "DOB: " + user.getDob());
        Log.d("RegisterPage1", "Password: " + user.getPasswordHash());
        Log.d("RegisterPage1", "Display Name: " + user.getDisplayName());

        startActivity(intent);
        finish();
    }
}