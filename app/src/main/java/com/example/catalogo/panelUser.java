package com.example.catalogo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class panelUser extends AppCompatActivity {

    Button btnSubir;
    ImageView visor;

    private StorageReference mStorage;
    private static final int GALLERY_INTENT = 1;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;
    public String link;
    int llave = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_user);

        btnSubir = findViewById(R.id.btnSubir);
        visor = findViewById(R.id.iv_visor);
        mProgressDialog = new ProgressDialog(this);


        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(llave == 1){

        mDatabase.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //En este metodo se programa para obtener los datos
                //Esta validación sirve para validar que exista el objeto que se le está haciendo referencia
                if(dataSnapshot.exists()){

                    String foto = dataSnapshot.child("url").getValue().toString();

                    Glide.with(panelUser.this)
                            .load(foto)
                            .fitCenter()
                            .centerCrop()
                            .into(visor);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }


        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);

                //camara();
            }
        });
    }
/*
    //Uso de la camára
    private void camara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imgBitmap = (Bitmap) extras.get("data");
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, outputStream);

            //visor. setImageBitmap(imgBitmap);

            StorageReference filePath = mStorage.child("fotos").child(getImageBitmap(imgBitmap));
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(panelUser.this, "Se subio exitosamente", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }*/



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            mProgressDialog.setTitle("Subiendo...");
            mProgressDialog.setMessage("Subiendo foto");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();


            Uri uri = data.getData();

            StorageReference filePath = mStorage.child("fotos").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgressDialog.dismiss();

                    //Uri descargarFoto = taskSnapshot.getStorage().getDownloadUrl();
                    //Task<Uri> descargarFoto = taskSnapshot.getStorage().getDownloadUrl();

                    Task<Uri> descargarFoto = taskSnapshot.getStorage().getDownloadUrl();
                    descargarFoto.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            llave = 2;
                            link = String.valueOf(uri);
                            Glide.with(panelUser.this)
                                    .load(uri)
                                    .fitCenter()
                                    .centerCrop()
                                    .into(visor);




                                mDatabase.child("Persona").child("url").setValue(link);


                        }
                    });








                    Toast.makeText(panelUser.this, "Se subio exitosamente", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}