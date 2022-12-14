package org.xoan.mydecisionfriend;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class haciendoPlan extends Activity implements View.OnClickListener {
    ImageView imgView;
    TextView nombreNivel, nombreActividad;
    Bundle parametros;
    Button btn_planRealizado,btn_sacarFoto;
    FirebaseFirestore mFirestore;
    Map<String, Object> mapa;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.haciendo_plan);
        parametros = getIntent().getExtras();
        mFirestore = FirebaseFirestore.getInstance();
        nombreNivel = findViewById(R.id.mostrar_n_nivel);
        nombreNivel.setText(parametros.get("nombreNivel").toString());
        nombreActividad = findViewById(R.id.txt_nombre_plan);
        nombreActividad.setText(parametros.get("nombreActividad").toString());
        btn_planRealizado = findViewById(R.id.plan_realizado);
        imgView = findViewById(R.id.fotoActividad);
        btn_sacarFoto = findViewById(R.id.sacar_foto);
        btn_sacarFoto.setOnClickListener(this);
        btn_planRealizado.setOnClickListener(this);
    }

    private void abrirCamara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            imgView.setImageBitmap(imgBitmap);
            imgView.setVisibility(View.VISIBLE);
        }
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sacar_foto:
                abrirCamara();
                break;
            case R.id.plan_realizado:

                int puntos = Integer.parseInt(parametros.get("puntos").toString());
                int puntosActividad = Integer.parseInt(parametros.get("puntosVictoria").toString());
                String puntosTotales = String.valueOf(puntosActividad + puntos);
                mFirestore.collection("users").document(parametros.get("nombre").toString()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                    }
                });


                finish();
                Intent main = new Intent(this, inicioAplicacion.class);
                main.putExtras(parametros);
                startActivity(main);
                break;

        }

    }
}


