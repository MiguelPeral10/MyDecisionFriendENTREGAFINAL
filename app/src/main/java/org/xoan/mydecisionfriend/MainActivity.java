package org.xoan.mydecisionfriend;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    EditText emailD, password;
    validacionTexto validator = new validacionTexto();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button n_usuario = (Button) findViewById(R.id.btn_sesion_registro);
        Button ini_sesion = (Button) findViewById(R.id.btn_sesion_ini);
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        emailD = findViewById(R.id.edit_email);
        password = findViewById(R.id.edit_pass);
        n_usuario.setOnClickListener(this);
        ini_sesion.setOnClickListener(this);

    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.btn_sesion_registro):
                finish();
                Intent i = new Intent(this, registrarUsuario.class);
                startActivity(i);
                break;
            case (R.id.btn_sesion_ini):
                String email = emailD.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if(!validator.validarEmail(email)) {
                    Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT).show();
                }
                if(!validator.validarContraseña(pass)) {
                    Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT).show();
                }
                if(validator.validarContraseña(pass) && validator.validarEmail(email)){
                    autenticarUsuario(email,pass);
                }
                break;
        }
    }

    public void autenticarUsuario(String email,String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String id = mAuth.getUid();
                db.collection("users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String nombre = documentSnapshot.getData().get("nombre").toString();
                        String puntos = documentSnapshot.getData().get("puntos").toString();
                        Bundle parametros = new Bundle();
                        parametros.putString("nombre", nombre);
                        parametros.putString("id", id);
                        parametros.putString("puntos", puntos);
                        Intent x = new Intent(MainActivity.this,inicioAplicacion.class);
                        x.putExtras(parametros);
                        startActivity(x);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }



    }

