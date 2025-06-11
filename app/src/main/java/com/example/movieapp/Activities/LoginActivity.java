package com.example.movieapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText userEdt, passEdt;
    private Button loginBtn;
    private TextView signUpTxt, forgetPass;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        initView();
        configureGoogleSignIn();
        initFacebookLogin();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initView() {
        userEdt = findViewById(R.id.editTextText);
        passEdt = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.loginBtn);
        signUpTxt = findViewById(R.id.signUpTxt);
        forgetPass = findViewById(R.id.ForgetPass);
        SignInButton googleSignInBtn = findViewById(R.id.googleSignInBtn);

        signUpTxt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        forgetPass.setOnClickListener(v -> forgetPassword());
        loginBtn.setOnClickListener(v -> signInWithEmail());
        googleSignInBtn.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithEmail() {
        String email = userEdt.getText().toString().trim();
        String password = passEdt.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            saveLoginState(currentUser.getUid());
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed. Please check your email and password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void forgetPassword() {
        String email = userEdt.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> googleTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = googleTask.getResult();
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (Exception e) {
                Log.e("GoogleSignIn", "Google Sign-In failed", e);
                Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Người dùng đã đăng nhập, liên kết tài khoản Google
            currentUser.linkWithCredential(credential)
                    .addOnCompleteListener(linkTask -> {
                        if (linkTask.isSuccessful()) {
                            Toast.makeText(this, "Google account linked successfully.", Toast.LENGTH_SHORT).show();
                            updateUserData(currentUser);
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to link Google account: " + linkTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Kiểm tra email đã tồn tại
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null && account.getEmail() != null) {
                mAuth.fetchSignInMethodsForEmail(account.getEmail()).addOnCompleteListener(fetchTask -> {
                    if (fetchTask.isSuccessful()) {
                        List<String> signInMethods = fetchTask.getResult().getSignInMethods();
                        if (signInMethods != null && !signInMethods.isEmpty()) {
                            // Email đã tồn tại, đăng nhập và liên kết
                            Toast.makeText(this, "Email already exists. Linking Google account.", Toast.LENGTH_SHORT).show();
                            mAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(signInTask -> {
                                        if (signInTask.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                updateUserData(user);
                                                startActivity(new Intent(this, MainActivity.class));
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(this, "Failed to sign in with Google: " + signInTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Email chưa tồn tại, đăng nhập bình thường
                            mAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                updateUserData(user);
                                                startActivity(new Intent(this, MainActivity.class));
                                                finish();
                                            }
                                        } else {
                                            Toast.makeText(this, "Authentication failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Error checking email: " + fetchTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void initFacebookLogin() {
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList("email", "public_profile"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Facebook login cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull FacebookException error) {
                Log.e("FacebookLogin", "Facebook authentication failed", error);
                Toast.makeText(LoginActivity.this, "Facebook authentication failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            handleFacebookAccessToken(accessToken);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("FacebookLogin", "handleFacebookAccessToken:" + token);

        // Lấy email từ Facebook Graph API
        GraphRequest request = GraphRequest.newMeRequest(token, (object, response) -> {
            try {
                String email = object != null ? object.getString("email") : null;
                if (email != null) {
                    checkAndSignInWithFacebook(token, email);
                } else {
                    Toast.makeText(LoginActivity.this, "Unable to retrieve email from Facebook.", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                }
            } catch (JSONException e) {
                Log.e("FacebookLogin", "Error parsing Facebook response", e);
                Toast.makeText(LoginActivity.this, "Error retrieving Facebook data.", Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logOut();
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void checkAndSignInWithFacebook(AccessToken token, String email) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Người dùng đã đăng nhập, liên kết tài khoản Facebook
            currentUser.linkWithCredential(credential)
                    .addOnCompleteListener(linkTask -> {
                        if (linkTask.isSuccessful()) {
                            Toast.makeText(this, "Facebook account linked successfully.", Toast.LENGTH_SHORT).show();
                            updateUserData(currentUser);
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to link Facebook account: " + linkTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Kiểm tra email đã tồn tại
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(fetchTask -> {
                if (fetchTask.isSuccessful()) {
                    List<String> signInMethods = fetchTask.getResult().getSignInMethods();
                    if (signInMethods != null && !signInMethods.isEmpty()) {
                        // Email đã tồn tại, đăng nhập và liên kết
                        Toast.makeText(this, "Email already exists. Linking Facebook account.", Toast.LENGTH_SHORT).show();
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(signInTask -> {
                                    if (signInTask.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            updateUserData(user);
                                            startActivity(new Intent(this, MainActivity.class));
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(this, "Failed to sign in with Facebook: " + signInTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        LoginManager.getInstance().logOut();
                                    }
                                });
                    } else {
                        // Email chưa tồn tại, đăng nhập bình thường
                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            updateUserData(user);
                                            startActivity(new Intent(this, MainActivity.class));
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(this, "Authentication failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        LoginManager.getInstance().logOut();
                                    }
                                });
                    }
                } else {
                    Toast.makeText(this, "Error checking email: " + fetchTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                }
            });
        }
    }

    private void updateUserData(FirebaseUser user) {
        String userId = user.getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.child("userName").setValue(user.getDisplayName());
        usersRef.child("email").setValue(user.getEmail());
        saveLoginState(userId);
    }

    private void saveLoginState(String userId) {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userId", userId);
        editor.apply();
    }

    private void signOut() {
        mAuth.signOut();
        googleSignInClient.signOut();
        LoginManager.getInstance().logOut();
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        preferences.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}