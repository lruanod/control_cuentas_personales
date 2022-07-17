package com.luiskik10.controlcuentas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.luiskik10.controlcuentas.Models.Debito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ActividadFormReporte extends AppCompatActivity {
    EditText descripciond,fechainicio, fechafin,total;
    Spinner idcategoria, idcuenta;
    //manipular spiner
    String categoriaselecionada="";
    String cuentaselecionada="";
    String strcategoria="";
    String stridcategoria="";
    String strnumero="";
    String stridcuenta="";
    String strentidad="";
    double strsaldo=0;
    ListView listV_debitos;
    String iduser="";
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth mAuth;
    // para listar
    private List<Debito> listDebito = new ArrayList<Debito>();
    ArrayAdapter<Debito> arrayAdapterDebito;

    final List<Categoria> categorias=new ArrayList<>();
    final List<Cuenta> cuentas=new ArrayList<>();

    //conexion a bd
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_form_reporte);
        //mostrar datos
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //
        iduser= currentUser.getUid();
        //
        fechainicio=findViewById(R.id.txt_fechainicio);
        fechafin=findViewById(R.id.txt_fechafinal);
        listV_debitos=findViewById(R.id.lv_datosDebito);
        idcategoria=findViewById(R.id.txt_categoriaDebito);
        idcuenta=findViewById(R.id.txt_cuentaDebito);
        total=findViewById(R.id.total);
        inicializarFirebase();
        cargarCategorias();
        cargarCuentas();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reported,menu);
        return super.onCreateOptionsMenu(menu);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String fechainicial=fechainicio.getText().toString();
        String fechafinal= fechafin.getText().toString();
        String idcategoriadr =stridcategoria.toString();
        String idcuentadr =stridcuenta.toString();
        // switch

        switch (item.getItemId()){

            case R.id.icon_return:{
                ActividadFormReporte.this.finish();
                onBackPressed();
                break;
            }
            case R.id.icon_search:{

                if (fechainicial.equals("")||fechafinal.equals("")){
                    Validacion(); }
                    else{
                        total.setText("");
                        databaseReference.child("Debito").orderByChild("fechad").startAt(fechainicial).endAt(fechafinal).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    listDebito.clear();
                                    double suma =0.0;
                                    for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                                        Categoria categoria = objSnaptshot.getValue(Categoria.class);
                                        Cuenta cuenta = objSnaptshot.getValue(Cuenta.class);
                                        Debito c = objSnaptshot.getValue(Debito.class);
                                        // los dos campos validos
                                            if (categoria.getIdcategoria().equals(stridcategoria) && cuenta.getIdcuenta().equals(stridcuenta) && cuenta.getIduser().equals(iduser) && categoria.getIduser().equals(iduser)){
                                                    suma= suma+c.getMontod();
                                                    listDebito.add(c);
                                                    arrayAdapterDebito = new ArrayAdapter<Debito>(ActividadFormReporte.this, android.R.layout.simple_list_item_1, listDebito);
                                                    listV_debitos.setAdapter(arrayAdapterDebito);
                                               }
                                        // los dos campos no validos
                                               else if (stridcategoria.equals("null") && stridcuenta.equals("null") && cuenta.getIduser().equals(iduser) && categoria.getIduser().equals(iduser)){
                                                    suma= suma+c.getMontod();
                                                    listDebito.add(c);
                                                    arrayAdapterDebito = new ArrayAdapter<Debito>(ActividadFormReporte.this, android.R.layout.simple_list_item_1, listDebito);
                                                    listV_debitos.setAdapter(arrayAdapterDebito);
                                                }
                                        //solo un campo valido
                                                else if ( categoria.getIdcategoria().equals(stridcategoria) && stridcuenta.equals("null")&& cuenta.getIduser().equals(iduser) && categoria.getIduser().equals(iduser)){
                                                    suma= suma+c.getMontod();
                                                    listDebito.add(c);
                                                    arrayAdapterDebito = new ArrayAdapter<Debito>(ActividadFormReporte.this, android.R.layout.simple_list_item_1, listDebito);
                                                    listV_debitos.setAdapter(arrayAdapterDebito);
                                                }
                                        //solo un campo valido
                                                else if (cuenta.getIdcuenta().equals(stridcuenta) && stridcategoria.equals("null")&& cuenta.getIduser().equals(iduser) && categoria.getIduser().equals(iduser) ){
                                                    suma= suma +c.getMontod();
                                                    listDebito.add(c);
                                                    arrayAdapterDebito = new ArrayAdapter<Debito>(ActividadFormReporte.this, android.R.layout.simple_list_item_1, listDebito);
                                                    listV_debitos.setAdapter(arrayAdapterDebito);
                                                }
                                        ///
                                    }//for
                                    String dsaldos= String.valueOf(suma);
                                    total.setText(dsaldos);
                                } else {
                                    listDebito.clear();
                                    stridcategoria="";
                                    stridcuenta="";
                                    cargarCategorias();
                                    cargarCuentas();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                    }


                 Limpiarcaja();

                break;
            }

            default:break;
        }
        return true;
    }

    private void Limpiarcaja() {
        //fechafin.setText("");
        // fechainicio.setText("");
        cargarCategorias();
        cargarCuentas();
    }


    public void OnClickFechainicio(View view) {
        Calendar cal = Calendar.getInstance();
        int anio=cal.get(Calendar.YEAR);
        int mes= cal.get(Calendar.MONTH);
        int dia= cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActividadFormReporte.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
               // String fechacalen=dayOfMonth + "/" + month + "/" + year;
                String fechacalen=year + "/" + month + "/" + dayOfMonth;
                fechainicio.setText(fechacalen);
            }
        }, anio,mes,dia);
        dpd.show();
    }

    public void OnClickFechafinal(View view) {

        Calendar cal = Calendar.getInstance();
        int anio=cal.get(Calendar.YEAR);
        int mes= cal.get(Calendar.MONTH);
        int dia= cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActividadFormReporte.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
              //  String fechacalen=dayOfMonth + "/" + month + "/" + year;
                String fechacalen=year + "/" + month + "/" + dayOfMonth;
                fechafin.setText(fechacalen);
            }
        }, anio,mes,dia);
        dpd.show();

    }

    private void Validacion() {
        String fechainicial=fechainicio.getText().toString();
        String fechafinal= fechafin.getText().toString();
        String idcuentadr =stridcuenta.toString();
        String idcategoriadr =stridcategoria.toString();

        if(fechainicial.equals("")){
            fechainicio.setError("Required");
        }
        else if (fechafinal.equals("")){
            fechafin.setError("Required");
        }
        else if (idcuentadr.equals("")){

            // dialogo
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Alevertencia");
            dialogo1.setMessage("Debes selecionar una opcion de cuenta");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialogo1.show();
        }

        else if (idcategoriadr.equals("")){

            // dialogo
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Alevertencia");
            dialogo1.setMessage("Debes selecionar una opcion de categoria");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialogo1.show();
        }
    }

    private void cargarCategorias(){

        databaseReference.child("Categoria").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categorias.clear();
                if(dataSnapshot.exists()){
                    stridcategoria="";
                    categorias.add(new Categoria("","--Selecionca categoria--",""));
                    categorias.add(new Categoria("null","--Todas las categorias--",""));
                    for( DataSnapshot ds: dataSnapshot.getChildren()){
                        Categoria c = dataSnapshot.getValue(Categoria.class);
                            String idcategoria = ds.getKey();
                            //String idcategoria = ds.child("idcategoria").getValue().toString();
                            String categoriac = ds.child("categoria").getValue().toString();
                            String iduser2 = ds.child("iduser").getValue().toString();
                            if(iduser.equals(iduser2)) {
                                categorias.add(new Categoria(idcategoria, categoriac, iduser2));
                            }
                    }
                    ArrayAdapter<Categoria> arrayAdapter =new ArrayAdapter<>(ActividadFormReporte.this, android.R.layout.simple_dropdown_item_1line,categorias);
                    idcategoria.setAdapter(arrayAdapter);
                    idcategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            categoriaselecionada = parent.getItemAtPosition(position).toString();
                            stridcategoria = categorias.get(position).getIdcategoria();
                            strcategoria =categorias.get(position).getCategoria();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void cargarCuentas(){
        databaseReference.child("Cuenta").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cuentas.clear();
                if(dataSnapshot.exists()){
                        stridcuenta="";
                        cuentas.add(new Cuenta("","","--Selecionca cuenta--",0.0,""));
                        cuentas.add(new Cuenta("null","","--Todaas las cuentas--",0.0,""));
                    for( DataSnapshot ds: dataSnapshot.getChildren()){
                        Cuenta cu = dataSnapshot.getValue(Cuenta.class);
                            String idcuenta = ds.getKey();
                            String entidad = ds.child("entidad").getValue().toString();
                            String numero = ds.child("numero").getValue().toString();
                            String saldo = ds.child("saldo").getValue().toString();
                            String iduser2= ds.child("iduser").getValue().toString();
                            Double dsaldo = Double.valueOf(saldo);
                            if(iduser.equals(iduser2)) {
                                cuentas.add(new Cuenta(idcuenta, numero, entidad, dsaldo, iduser2));
                            }
                    }
                    ArrayAdapter<Cuenta> arrayAdapter =new ArrayAdapter<>(ActividadFormReporte.this, android.R.layout.simple_dropdown_item_1line,cuentas);
                    idcuenta.setAdapter(arrayAdapter);
                    idcuenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            cuentaselecionada = parent.getItemAtPosition(position).toString();
                            stridcuenta=cuentas.get(position).getIdcuenta();
                            strentidad=cuentas.get(position).getEntidad();
                            strsaldo=cuentas.get(position).getSaldo();
                            strnumero=cuentas.get(position).getNumero();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    }
