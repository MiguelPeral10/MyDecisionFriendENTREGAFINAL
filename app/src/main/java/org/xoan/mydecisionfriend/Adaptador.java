package org.xoan.mydecisionfriend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class Adaptador extends BaseAdapter implements ListAdapter {
    private Context context;
    private int layout;
    private ArrayList<String> nombres;
    ArrayList<String> copia_nombres = new ArrayList<>();
    public Adaptador(Context contexto, int layout, ArrayList<String> nombresR) {
        this.context = contexto;
        this.layout = layout;
        this.nombres = nombresR;
        this.copia_nombres.addAll(nombres);
    }

    public void filtrar(String  texto) {
        nombres.clear();

        // Si no hay texto: agrega de nuevo los datos del ArrayList copiado
        // al ArrayList que se carga en los elementos del adaptador
        if (texto.length() == 0) {
            nombres.addAll(copia_nombres);
        } else {
            // Recorre todos los elementos que contiene el ArrayList copiado
            // y dependiendo de si estos contienen el texto ingresado por el
            // usuario los agrega de nuevo al ArrayList que se carga en los
            // elementos del adaptador.
            for(int x = 0;x<= copia_nombres.size()-1;x++){
                if (copia_nombres.get(x).contains(texto)) {
                    nombres.add(copia_nombres.get(x));
                }
            }
        }

        // Actualiza el adaptador para aplicar los cambios
        notifyDataSetChanged();
    }

    @Override

    public int getCount() {
        return this.nombres.size();
    }

    @Override
    public Object getItem(int position) {
        return this.nombres.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
  // Copiamos la vist
        View v = convertView;

 //Inflamos la vista con nuestro propio layout
LayoutInflater layoutInflater = LayoutInflater.from(this.context);
v= layoutInflater.inflate(R.layout.amigo_lista, null);
 // Valor actual según la posición
        String currentName = nombres.get(position);
// Referenciamos el elemento a modificar y lo rellenamos
TextView textView = (TextView) v.findViewById(R.id.mostrar_nombre_amigo);
 textView.setText(currentName);

 //Devolvemos la vista inflada
 return v;
    }
}