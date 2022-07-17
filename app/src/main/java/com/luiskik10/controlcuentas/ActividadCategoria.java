package com.luiskik10.controlcuentas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luiskik10.controlcuentas.Models.Categoria;
import com.luiskik10.controlcuentas.Models.Cuenta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActividadCategoria extends AppCompatActivity {
    // campos del formulario
    EditText categoria;
    ListView listV_categorias;
    String iduser="";
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth mAuth;
    // para listar
    private List<Categoria> listCategoria = new ArrayList<Categoria>();
    ArrayAdapter<Categoria> arrayAdapterCategoria;



    //seleccionar categoria
    Categoria categoriaSelected;

    //conexion a bd
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ///
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_categoria);
        //mostrar datos
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //
        iduser= currentUser.getUid();
        ///enlazar formulario
        categoria=findViewById(R.id.txt_categoria);
        listV_categorias=findViewById(R.id.lv_datosCategorias);
        //inicializar firebase
        inicializarFirebase();
        //listar datos en lista
        listarDatos();
        // seleccionar persona
        listV_categorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoriaSelected= (Categoria) parent.getItemAtPosition(position);
                categoria.setText(categoriaSelected.getCategoria());

            }
        });

    }

    private void cancelar() {
        LimpiarCajas();
    }

    private void aceptar() {
        Categoria c = new Categoria();
        c.setIdcategoria(categoriaSelected.getIdcategoria());
        databaseReference.child("Categoria").child(c.getIdcategoria()).removeValue();
        Toast.makeText(this, "Eliminar", Toast.LENGTH_SHORT).show();
        LimpiarCajas();
        listarDatos();
    }

    private void modificar() {
        Categoria c = new Categoria();
        c.setIdcategoria(categoriaSelected.getIdcategoria());
        c.setCategoria(categoria.getText().toString().trim());
        c.setIduser(iduser.trim());
        databaseReference.child("Categoria").child(c.getIdcategoria()).setValue(c);
        Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
        LimpiarCajas();
    }

    private void listarDatos() {
        databaseReference.child("Categoria").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listCategoria.clear();
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    if (objSnaptshot.exists()) {
                        Categoria c = objSnaptshot.getValue(Categoria.class);
                        if (c.getIduser().equals(iduser)) {
                            listCategoria.add(c);
                        }


                        arrayAdapterCategoria = new ArrayAdapter<Categoria>(ActividadCategoria.this, android.R.layout.simple_list_item_1, listCategoria);
                        listV_categorias.setAdapter(arrayAdapterCategoria);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase =FirebaseDatabase.getInstance();
        // persistencia
        // firebaseDatabase.setPersistenceEnabled(true);
        //
        databaseReference=firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String categoriac =categoria.getText().toString();
        // switch
        switch (item.getItemId()){

            case R.id.icon_return:{
                ActividadCategoria.this.finish();
                onBackPressed();
                break;
            }


            case R.id.icon_add:{
                //validar
                if (categoriac.equals("")){
                    Validacion();
                } else{
                    Categoria c =new Categoria();
                    c.setIdcategoria(UUID.randomUUID().toString());
                    c.setCategoria(categoriac);
                    c.setIduser(iduser);
                    databaseReference.child("Categoria").child(c.getIdcategoria()).setValue(c);
                    Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
                    LimpiarCajas();

                }
                break;
                //

            }
            case R.id.icon_save:{
                if (categoriac.equals("")){
                    Validacion();
                } else {
                    // dialogo
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Alevertencia");
                    dialogo1.setMessage("Relamente deseas actualizar?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            modificar();
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelar();
                        }
                    });
                    dialogo1.show();
                }
                break;
            }
            case R.id.icon_delete:{
                // dialogo
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Alevertencia");
                dialogo1.setMessage("Relamente deseas eliminar?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aceptar();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelar();
                    }
                });
                dialogo1.show();
                break;
            }
            default:break;
        }
        return true;
    }

    private void LimpiarCajas() {
        categoria.setText("");

    }

    private void Validacion() {
        String categoriac =categoria.getText().toString();
        if(categoriac.equals("")){
            categoria.setError("Required");
        }

    }
}