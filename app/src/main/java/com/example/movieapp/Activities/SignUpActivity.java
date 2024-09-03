package com.example.movieapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText userNameTxt, emailTxt, passTxt, cfPassTxt;
    private Button signUpBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView loginTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        initView();
    }

    private boolean checkPassword(){
        String password = passTxt.getText().toString();
        if(password.length()<6){
            return false;
        }
        return true;
    }

    private void initView() {
        userNameTxt = findViewById(R.id.userName);
        emailTxt = findViewById(R.id.email);
        passTxt = findViewById(R.id.passTxt);
        cfPassTxt = findViewById(R.id.cfPassTxt);
        signUpBtn = findViewById(R.id.signUpBtn);
        loginTxt = findViewById(R.id.textView8);

        loginTxt.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        signUpBtn.setOnClickListener(v -> {
            if(checkPassword()==false){
                Toast.makeText(SignUpActivity.this, "Mật khẩu phải có từ 6 kí tự trở lên! Vui lòng nhập lại mật khấu!", Toast.LENGTH_SHORT).show();
                passTxt.setText("");
                cfPassTxt.setText("");
                return;
            }
            String userName = userNameTxt.getText().toString().trim();
            String email = emailTxt.getText().toString().trim();
            String password = passTxt.getText().toString().trim();
            String confirmPassword = cfPassTxt.getText().toString().trim();

            if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Mật khẩu nhập lại không trùng khớp!", Toast.LENGTH_SHORT).show();
                cfPassTxt.setText("");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            DatabaseReference currentUserDb = mDatabase.child(userId);
                            currentUserDb.child("userName").setValue(userName);
                            currentUserDb.child("email").setValue(email);
                            currentUserDb.child("password").setValue(password);

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công! Chúc bạn xem phim vui vẻ <3", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Đăng ký không thành công!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
