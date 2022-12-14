package org.xoan.mydecisionfriend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ruletaDecision extends Activity implements Animation.AnimationListener, View.OnClickListener {
    boolean btnRotacion = true;
    long lngGrados = 0;
    int nDecisiones = 8;
    TextView nombreNiv;
    SharedPreferences sp;
    ImageButton b_girarRuleta;
    ImageView imgRuleta,imgEleccion;
    Bundle datos;
    FirebaseFirestore mFirestore;
    String nombre,descripcion;
    Map<String,Object> mapa;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ruletadecision);
        nombreNiv = findViewById(R.id.Nivel1_txt);
        datos = getIntent().getExtras();
        nombreNiv.setText(datos.get("nombreNivel").toString());
        mFirestore = FirebaseFirestore.getInstance();
        this.sp = PreferenceManager.getDefaultSharedPreferences(this);
        this.nDecisiones = this.sp.getInt("nDecisiones",8);
        b_girarRuleta = (ImageButton) findViewById(R.id.girarRuleta);
        imgRuleta = (ImageView) findViewById(R.id.imgRuleta);
        imgEleccion = (ImageView) findViewById(R.id.imgEleccion);
        b_girarRuleta.setOnClickListener(this);
        mapa = new HashMap<>();
        Random r = new Random();
        int plan = r.nextInt(7);
        mFirestore.collection("Actividades").document(datos.get("nombreNivel").toString()).collection("actividad").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                mapa = queryDocumentSnapshots.getDocuments().get(plan).getData();
                nombre = mapa.get("nombre").toString();
                descripcion = mapa.get("descripcion").toString();
            }
        });
    }


    @Override
    public void onAnimationStart(Animation animation) {
        this.btnRotacion = false;
        b_girarRuleta.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        this.btnRotacion = true;
        nuevo_plan();
    }

    private void nuevo_plan() {
        AlertDialog.Builder e = new AlertDialog.Builder(this);
        e.setMessage(descripcion)
                .setTitle(nombre).setNegativeButton("Paso", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        datos.putString("puntosVictoria",mapa.get("puntos").toString());
                        datos.putString("nombreActividad",mapa.get("nombre").toString());
                        finish();
                        Intent x = new Intent(ruletaDecision.this,haciendoPlan.class);
                        x.putExtras(datos);
                        startActivity(x);
                    }
                });
        e.show();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.girarRuleta:
                if(this.btnRotacion){
                    int aleatorio = new Random().nextInt(360) + 3600;
                    RotateAnimation rt = new RotateAnimation((float)this.lngGrados,(float)(this.lngGrados + ((long)aleatorio)),1,0.5f,1,0.5f);
                    this.lngGrados = (this.lngGrados + ((long)aleatorio) % 360);
                    rt.setDuration((long)aleatorio);
                    rt.setFillAfter(true);
                    rt.setInterpolator(new DecelerateInterpolator());
                    rt.setAnimationListener(this);
                    imgRuleta.setAnimation(rt);
                    imgRuleta.startAnimation(rt);
                }
                break;
        }

    }


}
