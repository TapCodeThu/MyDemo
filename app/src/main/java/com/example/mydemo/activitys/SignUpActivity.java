package com.example.mydemo.activitys;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mydemo.R;
import com.example.mydemo.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    private ImageView logo;
    private Uri imgUri;
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),result ->{
                if(result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                    imgUri = result.getData().getData();
                    binding.logo.setImageURI(imgUri);
                }
            }

    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        //Khoi tao
        iNit();

        //Push thong tin len firebase
        binding.btnContinue.setOnClickListener(v->{
           if(isValid() && imgUri != null){
               uploadInfoToFirebase();
           }
           else{
               Toast.makeText(SignUpActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
           }
        });
        binding.btnUpload.setOnClickListener(v->{
            openGallery(binding.iconUpload);
        });


    }
    private boolean isValid(){
        String nameGym = binding.edtGymName.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String address = binding.edtGymAddress.getText().toString().trim();
        String city = binding.edtGymCity.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        String pre_pass = binding.edtPrePassword.getText().toString().trim();
        return !nameGym.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !address.isEmpty() && !city.isEmpty() && !password.isEmpty() && !pre_pass.isEmpty();

    }

    private void uploadInfoToFirebase() {
        DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference("infomation");
        StorageReference storageLogo = FirebaseStorage.getInstance().getReference("infomation");
        String nameGym = binding.edtGymName.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String address = binding.edtGymAddress.getText().toString().trim();
        String city = binding.edtGymCity.getText().toString().trim();
        String password = binding.edtPassword.getText().toString().trim();
        String pre_pass = binding.edtPrePassword.getText().toString().trim();

        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(it->{
            String imageName = System.currentTimeMillis() + ".jpg";

            StorageReference imageRef = storageLogo.child(imageName);
            UploadTask uploadTask = imageRef.putFile(imgUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {

                //Lay url tu firestorage
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                    Map<String, Object> gymMap = new HashMap<>();
                    gymMap.put("name", nameGym);
                    gymMap.put("email", email);
                    gymMap.put("phone", phone);
                    gymMap.put("address", address);
                    gymMap.put("city", city);
                    gymMap.put("password", password);
                    gymMap.put("pre_pass", pre_pass);
                    gymMap.put("logoUrl", uri.toString());

                    //Push du lieu len realtime
                    String gymId = infoRef.push().getKey();
                    infoRef.child(gymId).setValue(gymMap)
                            .addOnSuccessListener(aVoid ->{
                                Toast.makeText(this,"Upload infor success",Toast.LENGTH_SHORT).show();

                            })
                            .addOnFailureListener(e->{
                                Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show();
                                Log.e("Logg",e.getMessage());

                            });

                });

            });

        });


    }


    private void iNit() {
        auth = FirebaseAuth.getInstance();
    }
    private void openGallery(ImageView imageView) {
       logo = imageView;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launcher.launch(intent);
    }
}