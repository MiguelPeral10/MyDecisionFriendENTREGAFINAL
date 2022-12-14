package org.xoan.mydecisionfriend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class validacionTexto {
    private boolean validado;
    public validacionTexto(){
        this.validado = false;
    }
    public boolean validarEmail(String email) {
        if(email.isEmpty()){
            return false;
        }
        Pattern patron = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = patron.matcher(email);
        if(matcher.find()){
            return true;
        }
        return this.validado;
    }
    public boolean validarContraseña(String pass) {
        if(pass.isEmpty()){
            return false;
        }
        Pattern patron = Pattern.compile("^(?=.*[0-9])" + "(?=.*[a-z])(?=.*[A-Z])" + "(?=\\S+$).{8,20}$");
        Matcher matcher = patron.matcher(pass);
        if(matcher.find()){
            return true;
        }
        return this.validado;
    }
    public boolean validarNombre(String nombre) {
        if(nombre.isEmpty()){
            return false;
        }
        Pattern patron = Pattern.compile("^" + "(?=.*[a-z])(?=.*[A-Z])" + ".{1,30}$");
        Matcher matcher = patron.matcher(nombre);
        if(matcher.find()){
            return true;
        }

        return this.validado;
    }
}
