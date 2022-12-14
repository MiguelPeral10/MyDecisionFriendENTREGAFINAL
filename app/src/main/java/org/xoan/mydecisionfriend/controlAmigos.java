package org.xoan.mydecisionfriend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class controlAmigos extends Activity implements View.OnClickListener {
    ImageButton nuevo_amigo;
    String id_amigo,nombre,id_amigo2;
    Bundle datosUsuario;
    EditText filtro;
    Adaptador miAdaptador;
    Map<String,Object> mapa,mapa2,mapa3;
    TextView mostrar_txt_nombre_usu,mostrar_txt_puntos_usu;
    private ListView listaAmigos;
    private ArrayList<String> nombres;
    FirebaseFirestore mFirestore;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_amigos);
        datosUsuario = getIntent().getExtras();
        mFirestore = FirebaseFirestore.getInstance();
        filtro = (EditText) findViewById(R.id.campoFiltro);
        mostrar_txt_nombre_usu = findViewById(R.id.mostrar_nombre);
        mostrar_txt_puntos_usu = findViewById(R.id.mostrar_puntos);
        mostrar_txt_nombre_usu.setText(datosUsuario.get("nombre").toString());
        mostrar_txt_puntos_usu.setText(datosUsuario.get("puntos").toString());
        nuevo_amigo = (ImageButton) findViewById(R.id.n_peticion_amistad);
        nuevo_amigo.setOnClickListener(this);
        listaAmigos = (ListView) findViewById(R.id.amigos_lista);
        comprobar_amigos();
        filtro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    miAdaptador.filtrar(filtro.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void comprobar_amigos() {
        mFirestore.collection("users").document(datosUsuario.get("id").toString()).collection("amigos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() <= 0){
                    Toast.makeText(controlAmigos.this, "No tienes amigos",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    nombres = new ArrayList<>();
                    int x = 0;
                    for(x = 0;x<=queryDocumentSnapshots.getDocuments().size()-1;x++){
                        mapa = queryDocumentSnapshots.getDocuments().get(x).getData();
                        nombres.add(mapa.get("nombre").toString());
                    }
                };
                miAdaptador = new Adaptador(controlAmigos.this,R.layout.amigo_lista,nombres);
                listaAmigos.setAdapter(miAdaptador);
                listaAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                       dialogo_eliminar_amigo(i);
                    }
                });
            }
        });
    }

    private void dialogo_eliminar_amigo(Integer amigo) {
        AlertDialog.Builder e = new AlertDialog.Builder(this);
        e.setMessage("¿Deseas eliminar a " + nombres.get(amigo) +" ?")
                .setTitle("¿Estas seguro?").setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mFirestore = FirebaseFirestore.getInstance();
                        mFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                boolean eliminarAmigo = false;
                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                    Map<String,Object> mapa3 = new HashMap<>();
                                    mapa3 = doc.getData();
                                    if(mapa3.get("nombre").toString().equalsIgnoreCase(nombres.get(amigo))){
                                        eliminarAmigo = true;
                                        id_amigo2 = doc.getId();
                                    }
                                }
                                if(eliminarAmigo){
                                    mFirestore.collection("users").document(id_amigo2).collection("amigos").document(datosUsuario.get("id").toString()).delete();
                                    mFirestore.collection("users").document(datosUsuario.get("id").toString()).collection("amigos").document(id_amigo2).delete();
                                }
                                if(!eliminarAmigo){
                                    Toast.makeText(getApplication(),"No se ha podido eliminar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
        e.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case(R.id.n_peticion_amistad):
                dialog_nuevo_amigo();

        }
    }

    private void dialog_nuevo_amigo() {
        AlertDialog.Builder e = new AlertDialog.Builder(this);
        LayoutInflater in  = getLayoutInflater();
        View vi = in.inflate(R.layout.nuevo_amigo,null);
        e.setView(vi);
        EditText nombre_amigo = vi.findViewById(R.id.pruebas_nombre);
        Button btn_nuevo_amigo = vi.findViewById(R.id.pruebas_amigo);
        btn_nuevo_amigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = nombre_amigo.getText().toString();
                mFirestore = FirebaseFirestore.getInstance();
                mFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        boolean encontrarAmigo = false;
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Map<String,Object> mapa2 = new HashMap<>();
                            mapa2 = doc.getData();
                            if(mapa2.get("nombre").toString().equalsIgnoreCase(nombre)){
                                encontrarAmigo = true;
                                id_amigo = doc.getId();
                            }
                        }
                        if(encontrarAmigo){

                            Map mapa = new HashMap<>();
                            mapa.put("nombre",datosUsuario.get("nombre").toString());
                            mFirestore.collection("users").document(id_amigo).collection("peticiones").document(datosUsuario.get("id").toString()).set(mapa).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplication(),"Se ha enviado la peticion con exito", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        if(!encontrarAmigo){
                            Toast.makeText(getApplication(),"Tu amigo no existe,lo siento", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            });
        AlertDialog a3 = e.create();
        a3.show();
    }
}
