package com.example.especials.buscaminas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class AccesoBDActivity extends Activity implements QueryFrag.PartidasListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso_bd);
        QueryFrag queryFrag = (QueryFrag) getFragmentManager().findFragmentById(R.id.fragmentQuery);
        queryFrag.setPartidasListener(this);
    }

    public void goMainActivityBD(View v){
        Intent in = new Intent(AccesoBDActivity.this,MainActivity.class);
        startActivity(in);
        finish();
    }

    @Override
    public void onPartidaSeleccionada(Partida c) {
        RegistroFrag registroFrag = (RegistroFrag) getFragmentManager().findFragmentById(R.id.fragmentRegistro);
        if (registroFrag != null && registroFrag.isInLayout()){
            registroFrag.mostrarRegistro(c.getLog());
        }else{
            Intent i = new Intent(this, DetalleActivity.class);
            i.putExtra(DetalleActivity.EXTRA_TEXTO,c.getLog());
            startActivity(i);
        }
    }
}
