package org.xoan.mydecisionfriend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class registrarUsuario extends Activity implements View.OnClickListener {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    EditText nombreR,contrasena,emailR;
    validacionTexto validator = new validacionTexto();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_usuario);
        this.db = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        nombreR = findViewById(R.id.edit_nombre);
        emailR = findViewById(R.id.edit_emailR);
        contrasena = findViewById(R.id.edit_passR);
        Button bt_registrarse = (Button) findViewById(R.id.btn_registrarse);
        Button vt_volver = (Button) findViewById(R.id.volver);
        bt_registrarse.setOnClickListener(this);
        vt_volver.setOnClickListener(this);
    }

    private void registrarUsuario(String email, String pass,String nombre) {
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(registrarUsuario.this,"Auntenticado con exito",Toast.LENGTH_SHORT).show();
                String id = mAuth.getCurrentUser().getUid();
                Map<String,Object> mapa = new HashMap<>();
                mapa.put("email",email);
                mapa.put("pass",pass);
                mapa.put("nombre",nombre);
                mapa.put("puntos",0);
                db.collection("users").document(id).set(mapa).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(registrarUsuario.this,"Registrado con éxito",Toast.LENGTH_SHORT).show();
                        //db.collection("users").document(id).collection("peticiones").add(amigos);
                        finish();
                        Intent i = new Intent(registrarUsuario.this,MainActivity.class);
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(registrarUsuario.this,"Mierda",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(registrarUsuario.this,"Algo salió mal",Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.btn_registrarse):
                String email = emailR.getText().toString().trim();
                String pass = contrasena.getText().toString().trim();
                String nombre = nombreR.getText().toString();
                if(!validator.validarEmail(email)) {
                    TextView error_email = (TextView) findViewById(R.id.txt_e_email);
                    error_email.setVisibility(View.VISIBLE);
                }
                if(!validator.validarContraseña(pass)) {
                    TextView error_pass = (TextView) findViewById(R.id.txt_e_pass);
                    error_pass.setVisibility(View.VISIBLE);
                }
                if(!validator.validarNombre(nombre)) {
                    TextView error_nombre = (TextView) findViewById(R.id.txt_e_nombre);
                    error_nombre.setVisibility(View.VISIBLE);
                }
                if(validator.validarNombre(nombre) && validator.validarContraseña(pass) && validator.validarEmail(email)){
                    registrarUsuario(email,pass,nombre);
                }

                break;
            case(R.id.volver):
                finish();
                Intent i = new Intent(registrarUsuario.this,MainActivity.class);
                startActivity(i);
                break;

        }
    }
}
