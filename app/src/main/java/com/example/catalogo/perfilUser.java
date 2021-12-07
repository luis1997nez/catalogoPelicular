package com.example.catalogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class perfilUser extends AppCompatActivity {

    TextView nameUser;
    TextView userName;
    TextView emailUser;
    TextView paisUser;
    ImageView fotoView;
    Button btnEnviar;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_user);

        nameUser = (TextView) findViewById(R.id.nameUser);
        userName = (TextView) findViewById(R.id.userName);
        emailUser = (TextView) findViewById(R.id.emailUser);
        paisUser = (TextView) findViewById(R.id.paisUser);
        fotoView = findViewById(R.id.fotoView);
        btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent siguiente = new Intent(perfilUser.this, panelUser.class);
                startActivity(siguiente);
            }
        });

        //Este hace referencia al uso del nodo principal
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //Para hacer uso del subnodo Persona
        mDatabase.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //En este metodo se programa para obtener los datos
                //Esta validación sirve para validar que exista el objeto que se le está haciendo referencia
                if(dataSnapshot.exists()){
                    String nombre = dataSnapshot.child("nombre").getValue().toString();
                    nameUser.setText(nombre);
                    String usuario = dataSnapshot.child("usuario").getValue().toString();
                    userName.setText(usuario);
                    String correo = dataSnapshot.child("email").getValue().toString();
                    emailUser.setText(correo);
                    String pais = dataSnapshot.child("pais").getValue().toString();
                    paisUser.setText(pais);

                    String foto = dataSnapshot.child("url").getValue().toString();

                    Glide.with(perfilUser.this)
                            .load(foto)
                            .fitCenter()
                            .centerCrop()
                            .into(fotoView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}