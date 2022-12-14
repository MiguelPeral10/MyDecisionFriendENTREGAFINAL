package org.xoan.mydecisionfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class inicioAplicacion extends Activity implements View.OnClickListener {
    Bundle datosUsuario;
    Map<String,Object> mapa;
    Map<String,Object> mapa2;
    Boolean amigosConectados;
    FirebaseFirestore mFirestore;
    ArrayList<String> nivelesDisp,nombrenivelesDisp,nombres;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicioaplicacion);
        datosUsuario = getIntent().getExtras();
        mFirestore = FirebaseFirestore.getInstance();
        comprobar_peticiones();
        cargar_puntos_niveles();
        obtener_usuarios_conectados();
        TextView mostrar_puntos = findViewById(R.id.textView9);
        mostrar_puntos.setText(datosUsuario.get("puntos").toString());
        //Inicializacion de botones
        Button Nivel1 = (Button) findViewById(R.id.Nivel1);
        Button Nivel2 = (Button) findViewById(R.id.Nivel2);
        Button Nivel3 = (Button) findViewById(R.id.Nivel3);
        Button btnAmigos = (Button) findViewById(R.id.btn_controlAmigos);
        Nivel1.setOnClickListener(this);
        Nivel2.setOnClickListener(this);
        Nivel3.setOnClickListener(this);
        btnAmigos.setOnClickListener(this);
    }

    private void obtener_usuarios_conectados() {
        mFirestore.collection("users").document(datosUsuario.get("id").toString()).collection("amigos").limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() <= 0){
                    amigosConectados = false;
                    Toast.makeText(inicioAplicacion.this, "No tienes amigos",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    amigosConectados = true;
                    nombres = new ArrayList<>();
                    int x = 0;
                    for(x = 0;x<=queryDocumentSnapshots.getDocuments().size()-1;x++){
                        mapa = queryDocumentSnapshots.getDocuments().get(x).getData();
                        nombres.add(mapa.get("nombre").toString());
                    }
                };
            }
        });
    }

    private void cargar_puntos_niveles() {
        mFirestore.collection("niveles").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() <= 0){
                    Toast.makeText(inicioAplicacion.this, "Error al cargar los niveles",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    mapa2 = new HashMap<>();
                    nombrenivelesDisp = new ArrayList<>();
                    nivelesDisp = new ArrayList<>();
                    int x = 0;
                    for(x = 0;x<=queryDocumentSnapshots.getDocuments().size()-1;x++){
                        mapa2 = queryDocumentSnapshots.getDocuments().get(x).getData();
                        nivelesDisp.add(mapa2.get("puntos").toString());
                        nombrenivelesDisp.add(queryDocumentSnapshots.getDocuments().get(x).getId().toString());
                    }
                    }
                };
            });
    }


    private void comprobar_peticiones() {
        mFirestore.collection("users").document(datosUsuario.get("id").toString()).collection("peticiones").limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() <= 0){
                    Toast.makeText(inicioAplicacion.this, "No tienes peticiones pendientes",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    mapa = queryDocumentSnapshots.getDocuments().get(0).getData();
                    mapa.put("id",queryDocumentSnapshots.getDocuments().get(0).getId());
                    Toast.makeText(inicioAplicacion.this, "Tienes peticiones pendientes de " + mapa.get("nombre").toString(),
                            Toast.LENGTH_SHORT).show();
                    crearPeticionAmistad(mapa.get("nombre").toString(),mapa.get("id").toString());
                };
            }
        });
    }

private void crearPeticionAmistad(String nombrePeti,String idPeti) {
        AlertDialog.Builder e = new AlertDialog.Builder(this);
        e.setMessage("Tienes una nueva peticion de amistad de " + nombrePeti)
                .setTitle("Hola").setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFirestore.collection("users").document(datosUsuario.getString("id").toString()).collection("peticiones").document(idPeti).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(inicioAplicacion.this, "La petición se ha eliminado con éxito",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(inicioAplicacion.this, "Ha surgido un error al eliminaer la petición",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFirestore.collection("users").document(datosUsuario.getString("id").toString()).collection("peticiones").document(idPeti).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Map<String, Object> docData = new HashMap<>();
                                docData.put("nombre", datosUsuario.get("nombre").toString());
                                Map<String, Object> docData2 = new HashMap<>();
                                docData2.put("nombre", nombrePeti);
                                mFirestore.collection("users").document(idPeti).collection("amigos").document(datosUsuario.get("id").toString()).set(docData);
                                mFirestore.collection("users").document(datosUsuario.get("id").toString()).collection("amigos").document(idPeti).set(docData2);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(inicioAplicacion.this, "Ha surgido un error al añadir la petición",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
        e.show();
    }

    @Override
    public void onClick(View v) {
        int puntos = Integer.parseInt(datosUsuario.get("puntos").toString());
        switch (v.getId()){
            case R.id.Nivel1:
                datosUsuario.putString("nombreNivel",nombrenivelesDisp.get(0));
                seleccion_amigos_partida();
                break;
            case R.id.Nivel2:
                if(puntos>= Integer.parseInt(nivelesDisp.get(1))) {
                    datosUsuario.putString("nombreNivel",nombrenivelesDisp.get(1));
                    seleccion_amigos_partida();
                }
                else{
                    Toast.makeText(inicioAplicacion.this, "No tienes puntos suficientes para este nivel",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Nivel3:
                if(puntos>= Integer.parseInt(nivelesDisp.get(2))) {
                    datosUsuario.putString("nombreNivel",nombrenivelesDisp.get(2));
                    seleccion_amigos_partida();
                }
                else{
                    Toast.makeText(inicioAplicacion.this, "No tienes puntos suficientes para este nivel",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_controlAmigos:
                finish();
                Intent y = new Intent(this,controlAmigos.class);
                y.putExtras(datosUsuario);
                startActivity(y);
        }
    }
    private void seleccion_amigos_partida() {
        AlertDialog.Builder b = new AlertDialog.Builder(inicioAplicacion.this);
        b.setTitle("Amigos conectados en este momento");
        b.setIcon(R.drawable.ic_android_black_24dp);
        final boolean[] checkAmigos = new boolean[]{};
        String[] amigosEnLinea = new String[]{
        };
        int x = 0;
        b.setMultiChoiceItems(amigosEnLinea, checkAmigos, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                checkAmigos[i] = b;
            }
        });
        b.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ejecutarRuleta();
            }
        });
        b.show();
    }

    public void ejecutarRuleta(){
        finish();
        Intent i = new Intent(this,ruletaDecision.class);
        i.putExtras(datosUsuario);
        startActivity(i);
    }

}