package com.example.renthubpablo.controller;

import static java.util.Base64.getDecoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.example.renthubpablo.resources.Constants;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Usuario extends AppCompatActivity {
    String token;
    String nombre;
    String apellido;
    String password;
    String email;
    String phone;
    String descripcion;
    String date;
    String genero;
    String pronombres;
    boolean estado;
    String pfp;

    public Usuario(String nombre, String apellido, String password, String email, String phone, String descripcion, String date, String genero, String pronombres, boolean estado, String pfp) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.descripcion = descripcion;
        this.date = date;
        this.genero = genero;
        this.pronombres = pronombres;
        this.estado = estado;
        this.pfp = pfp;
    }

    public Usuario() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getPronombres() {
        return pronombres;
    }

    public void setPronombres(String pronombres) {
        this.pronombres = pronombres;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getPfp() {
        return pfp;
    }

    public void setPfp(String pfp) {
        this.pfp = pfp;
    }

    public String hashPassword(String password){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
    public static String generarNombreImagenPerfil(){
        String result = "";
        try {
            Random rand = new Random();
            int seed = rand.nextInt();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(String.valueOf(seed).getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result.substring(0, 20);
    }

    public String encrypt(String data) throws Exception {
        String key = Constants.KEY_DEV;

        SecretKeySpec secretKeySpec = crearClave(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,secretKeySpec);
        byte[] datosEncriptadosBytes=cipher.doFinal(data.getBytes());
        String datosEncriptadosString=Base64.encodeToString(datosEncriptadosBytes,Base64.DEFAULT);
        return datosEncriptadosString;

    }

    public String desencriptar(String data) throws Exception {
        if(data!=null){
            String key = Constants.KEY_DEV;
            SecretKeySpec secretKeySpec = crearClave(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE,secretKeySpec);
            byte[] datosDescodificados = Base64.decode(data,Base64.DEFAULT);
            byte[] datosDescodificadosByte = cipher.doFinal(datosDescodificados);
            String datosDescodificadosString = new String(datosDescodificadosByte);
            return datosDescodificadosString;
        }else{
            return "not found";
        }
    }
    private SecretKeySpec crearClave (String key)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyed = key.getBytes("UTF-8");
        keyed=sha.digest(keyed);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyed, "AES");
        return secretKeySpec;

    }
    public String getCurrentUserEmail(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String correo=prefs.getString(Constants.SHARED_PREFERENCES_CORREO, "");
        return correo;
    }


}
