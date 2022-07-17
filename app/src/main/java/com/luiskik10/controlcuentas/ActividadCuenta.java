package com.luiskik10.controlcuentas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
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
import com.luiskik10.controlcuentas.Models.Cuenta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActividadCuenta extends AppCompatActivity {

    // campos del formulario
    EditText numero,entidad,saldo;
    ListView listV_cuentas;
    String iduser="";
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth mAuth;
    // para listar
    private List<Cuenta> listCuenta = new ArrayList<Cuenta>();
    ArrayAdapter<Cuenta> arrayAdapterCuenta;

    //seleccionar persona
    Cuenta cuentaSelected;

    //conexion a bd
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        //mostrar datos
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //
        iduser= currentUser.getUid();
        ///enlazar formulario
        numero=findViewById(R.id.txt_numeroCuenta);
        entidad=findViewById(R.id.txt_entidadCuenta);
        saldo=findViewById(R.id.txt_saldoCuenta);
        listV_cuentas=findViewById(R.id.lv_datosCuentas);

        //inicializar firebase
        inicializarFirebase();
        //listar datos en lista
        listarDatos();
        // seleccionar persona
        listV_cuentas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cuentaSelected= (Cuenta) parent.getItemAtPosition(position);
                numero.setText(cuentaSelected.getNumero());
                entidad.setText(cuentaSelected.getEntidad());
                saldo.setText(cuentaSelected.getSaldo().toString());
            }
        });

    }

    private void cancelar() {
        LimpiarCajas();
    }

    private void aceptar() {
        Cuenta c = new Cuenta();
        c.setIdcuenta(cuentaSelected.getIdcuenta());
        databaseReference.child("Cuenta").child(c.getIdcuenta()).removeValue();
        Toast.makeText(this, "Eliminar", Toast.LENGTH_SHORT).show();
        LimpiarCajas();
        listarDatos();
    }

    private void modificar() {
        Cuenta c = new Cuenta();
        Double dsaldo = Double.valueOf(saldo.getText().toString());
        c.setIdcuenta(cuentaSelected.getIdcuenta());
        c.setNumero(numero.getText().toString().trim());
        c.setEntidad(entidad.getText().toString().trim());
        c.setSaldo(dsaldo);
        c.setIduser(iduser);
        databaseReference.child("Cuenta").child(c.getIdcuenta()).setValue(c);
        Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
        LimpiarCajas();
    }

    private void listarDatos() {
        databaseReference.child("Cuenta").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listCuenta.clear();
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    if (objSnaptshot.exists()) {
                        Cuenta c = objSnaptshot.getValue(Cuenta.class);
                        if (c.getIduser().equals(iduser)) {
                            listCuenta.add(c);
                        }

                        arrayAdapterCuenta = new ArrayAdapter<Cuenta>(ActividadCuenta.this, android.R.layout.simple_list_item_1, listCuenta);
                        listV_cuentas.setAdapter(arrayAdapterCuenta);
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

        String numeroc =numero.getText().toString();
        String entidadc =entidad.getText().toString();
        Double saldoc = Double.valueOf(saldo.getText().toString());
        // switch
        switch (item.getItemId()){

            case R.id.icon_return:{

                ActividadCuenta.this.finish();
                onBackPressed();
                break;
            }
            case R.id.icon_add:{
                //validar
                if (numeroc.equals("")||entidadc.equals("")||saldoc.equals("")){
                    Validacion();

                } else{
                    Cuenta c =new Cuenta();
                    c.setIdcuenta(UUID.randomUUID().toString());
                    c.setNumero(numeroc);
                    c.setEntidad(entidadc);
                    c.setSaldo(saldoc);
                    c.setIduser(iduser);
                    databaseReference.child("Cuenta").child(c.getIdcuenta()).setValue(c);
                    Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
                    LimpiarCajas();
                    listarDatos();

                }
                break;
                //

            }
            case R.id.icon_save:{
                if (numeroc.equals("")||entidadc.equals("")||saldoc.equals("")){
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
        numero.setText("");
        entidad.setText("");
        saldo.setText("");

    }

    private void Validacion() {
        String numeroc =numero.getText().toString();
        String entidadc =entidad.getText().toString();
        String saldoc =saldo.getText().toString();
        if(numeroc.equals("")){
            numero.setError("Required");
        }
        else if (entidadc.equals("")){
            entidad.setError("Required");
        }
        else if (saldoc.equals("")){
            saldo.setError("Required");
        }

    }
}