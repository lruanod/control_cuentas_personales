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
import com.luiskik10.controlcuentas.Models.Credito;
import com.luiskik10.controlcuentas.Models.Cuenta;
import com.luiskik10.controlcuentas.Models.Debito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class form_reporte2 extends AppCompatActivity {
    EditText montod,descripciond,fechainicio, fechafin,total;
    Spinner  idcuenta;
    //manipular spiner
    String categoriaselecionada="";
    String cuentaselecionada="";
    String strcategoria="";
    String stridcategoria="";
    String strnumero="";
    String stridcuenta="";
    String strentidad="";
    String iduser="";
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth mAuth;
    double strsaldo=0;
    ListView listV_creditos;
    // para listar
    private List<Credito> listCredito = new ArrayList<Credito>();
    ArrayAdapter<Credito> arrayAdapterCredito;

    final List<Cuenta> cuentas=new ArrayList<>();

    //conexion a bd
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_reporte2);
        //mostrar datos
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //
        iduser= currentUser.getUid();
        //
        fechainicio=findViewById(R.id.txt_fechainicio);
        fechafin=findViewById(R.id.txt_fechafinal);
        listV_creditos=findViewById(R.id.lv_datosCreditos);
        idcuenta=findViewById(R.id.txt_cuentaCredito);
        total=findViewById(R.id.total);
        inicializarFirebase();
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
        String idcuentadr =stridcuenta.toString();
        // switch

        switch (item.getItemId()){

            case R.id.icon_return:{
                form_reporte2.this.finish();
                onBackPressed();
                break;
            }
            case R.id.icon_search:{

                if (fechainicial.equals("")||fechafinal.equals("")){
                    Validacion(); }
                else{
                    total.setText("");
                    databaseReference.child("Credito").orderByChild("fechac").startAt(fechainicial).endAt(fechafinal).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                listCredito.clear();
                                double suma =0.0;
                                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                                    Cuenta cuenta = objSnaptshot.getValue(Cuenta.class);
                                    Credito c = objSnaptshot.getValue(Credito.class);
                                    // los dos campos validos
                                    if (cuenta.getIdcuenta().equals(stridcuenta)&& cuenta.getIduser().equals(iduser)){
                                        suma=suma+c.getMontoc();
                                        listCredito.add(c);
                                        arrayAdapterCredito = new ArrayAdapter<Credito>(form_reporte2.this, android.R.layout.simple_list_item_1, listCredito);
                                        listV_creditos.setAdapter(arrayAdapterCredito);
                                    }
                                    // los dos campos no validos
                                    else if (stridcuenta.equals("null")&& cuenta.getIduser().equals(iduser)){
                                        suma=suma+c.getMontoc();
                                        listCredito.add(c);
                                        arrayAdapterCredito = new ArrayAdapter<Credito>(form_reporte2.this, android.R.layout.simple_list_item_1, listCredito);
                                        listV_creditos.setAdapter(arrayAdapterCredito);
                                    }
                                }//for
                                String dsaldos= String.valueOf(suma);
                                total.setText(dsaldos);
                            } else {
                                listCredito.clear();
                                stridcuenta="";
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
        //  fechainicio.setText("");
        //   stridcategoria="";
        //   stridcuenta="";
        cargarCuentas();
    }


    public void OnClickFechainicio(View view) {
        Calendar cal = Calendar.getInstance();
        int anio=cal.get(Calendar.YEAR);
        int mes= cal.get(Calendar.MONTH);
        int dia= cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(form_reporte2.this, new DatePickerDialog.OnDateSetListener() {
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

        DatePickerDialog dpd = new DatePickerDialog(form_reporte2.this, new DatePickerDialog.OnDateSetListener() {
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
                            String iduser2 = ds.child("iduser").getValue().toString();
                            Double dsaldo = Double.valueOf(saldo);
                            if(iduser.equals(iduser2)) {
                                cuentas.add(new Cuenta(idcuenta, numero, entidad, dsaldo, iduser2));
                            }
                    }
                    ArrayAdapter<Cuenta> arrayAdapter =new ArrayAdapter<>(form_reporte2.this, android.R.layout.simple_dropdown_item_1line,cuentas);
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