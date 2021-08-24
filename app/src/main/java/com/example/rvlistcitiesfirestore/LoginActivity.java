package com.example.rvlistcitiesfirestore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    private EditText etEmail;
    private EditText etKey;
    private Button btnLogin;
    private Button btnRegister;

    private TextView tvCustom;
    private ImageView imgIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usersRef = db.collection("users");

        etEmail = findViewById(R.id.etEmail);
        etKey = findViewById(R.id.etKey);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        Button mauthLoginButton = findViewById(R.id.btnLogin);
        mauthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeLogin(etEmail.getText().toString(), etKey.getText().toString());
            }
        });

        Button mauthRegisterButton = findViewById(R.id.btnRegister);
        mauthRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAccount(etEmail.getText().toString(), etKey.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity();
        }
    }

    private void setCustomAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.customalertdialog, null);
        tvCustom = findViewById(R.id.tvCustom);
        imgIcon = findViewById(R.id.imgIcon);



        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Required.");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            etKey.setError("Required.");
            valid = false;
        } else {
            etKey.setError(null);
        }

        return valid;
    }


    private void makeLogin(String email, String password) {
        // si el email o la contraseña no son válidos, paramos el método
        if (!validateForm(email, password)) {
            return;
        }

        //este método realiza el login con el usuario y contraseña enviados
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // si task isSuccessful es que la operacion ha ido bien
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user.isEmailVerified()) {
                        startActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email no verificado. No puedes acceder", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Login incorrecto. Prueba de nuevo", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setAccount(String email, String password) {
        // si el email o la contraseña no son válidos, paramos el método
        if (!validateForm(email, password)) {
            return;
        }

// este método crea un usuario a partir de un email y un password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //si la operación ha sido con éxito
                if (task.isSuccessful()) {
                    // UserProfileChangeRequest permite asignar datos adicionales al usuario creado
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName("Daniel").build();

                    // si FirebaseUser no es nulo, actualizamos el usuario con los datos adicionales
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null)
                        user.updateProfile(profileUpdates);

                    // manda un email a la cuenta registrada para verificar el correo
                    sendEmail();
                    setCustomAlertDialog();

                    City city = new City();

                    usersRef.document(user.getEmail()).collection("cities").add(city);
                } else {
                    Toast.makeText(LoginActivity.this, "Fallo al registrar al usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmail() {
        FirebaseUser mAuser = mAuth.getCurrentUser();
        if (mAuser != null) {
            // sendEmailVerification manda el email de verificación al usuario que llama al método
            mAuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    // si task isSuccessful, se ha enviado el correo correctamente
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this,
                                "Email de verificación enviado a " + mAuser.getEmail(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Fallo al enviar el email de verificación", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }




}

