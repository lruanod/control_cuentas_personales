package com.luiskik10.controlcuentas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.UUID;

public class ActividadCredito extends AppCompatActivity {

    // campos del formulario
    EditText montoc, descripcionc, fechac;
    Spinner  idcuenta;
    Button btnData, btnHora;
    String iduser="";
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth mAuth;
    Calendar c;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    String cuentaselecionada = "";
    String strnumero, strentidad, stridcuenta= "";
    String strnumero2,strentidad2,stridcuenta2="";
    double strsaldo,strsaldo2=0;
    final List<Cuenta> cuentas = new ArrayList<>();

    ListView listV_creditos;
    // para listar
    private List<Credito> listCredito = new ArrayList<Credito>();
    ArrayAdapter<Credito> arrayAdapterCredito;

    //seleccionar persona
    Credito creditoSelected;

    //conexion a bd
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ///
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito);
        //mostrar datos
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //
        iduser= currentUser.getUid();
        //

        ///enlazar formulario
        montoc = findViewById(R.id.txt_montocCredito);
        descripcionc = findViewById(R.id.txt_descripcionCredito);
        fechac = findViewById(R.id.txt_fechacCredito);
        // horac=findViewById(R.id.txt_horacCredito);
        listV_creditos = findViewById(R.id.lv_datosCreditos);
        idcuenta = findViewById(R.id.txt_cuentaCredito);
        //inicializar firebase
        inicializarFirebase();
        //listar datos en lista
        listarDatos();
        cargarCuentas();

        //seleccionar credito
        listV_creditos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cuentas.clear();
                cargarCuentas();
                creditoSelected = (Credito) parent.getItemAtPosition(position);
                descripcionc.setText(creditoSelected.getDescripcionc());
                Double dsaldo = Double.valueOf(creditoSelected.getMontoc());
                montoc.setText(dsaldo.toString());
                fechac.setText(creditoSelected.getFechac());
                stridcuenta2=creditoSelected.getIdcuenta();
                strnumero2=creditoSelected.getNumero();
                strentidad2=creditoSelected.getEntidad();
                Double strsaldo3= Double.valueOf(creditoSelected.getSaldo());
                strsaldo2=strsaldo3;
            }
        });


    }



    private void cargarCuentas() {
        databaseReference.child("Cuenta").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cuentas.clear();
                if (dataSnapshot.exists()) {

                    if(stridcuenta2.equals("")){
                         cuentas.add(new Cuenta("","","--Selecionca cuenta--",0.0,""));
                    } else {
                          cuentas.add(new Cuenta(stridcuenta2,strnumero2,strentidad2,strsaldo2,iduser));
                       }
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Cuenta cu = dataSnapshot.getValue(Cuenta.class);

                        String idcuenta = ds.getKey();
                        String entidad = ds.child("entidad").getValue().toString();
                        String numero = ds.child("numero").getValue().toString();
                        String saldo = ds.child("saldo").getValue().toString();
                        Double dsaldo = Double.valueOf(saldo);
                        String iduser2= ds.child("iduser").getValue().toString();
                        if (iduser.equals(iduser2)) {
                             cuentas.add(new Cuenta(idcuenta, numero, entidad, dsaldo, iduser2));
                        }
                    }
                    ArrayAdapter<Cuenta> arrayAdapter = new ArrayAdapter<>(ActividadCredito.this, android.R.layout.simple_dropdown_item_1line, cuentas);
                    idcuenta.setAdapter(arrayAdapter);
                    idcuenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            cuentaselecionada = parent.getItemAtPosition(position).toString();
                            stridcuenta = cuentas.get(position).getIdcuenta();
                            strentidad = cuentas.get(position).getEntidad();
                            strsaldo = cuentas.get(position).getSaldo();
                            strnumero = cuentas.get(position).getNumero();

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

    private void cancelar() {
        LimpiarCajas();
    }

    private void aceptar() {
        Credito c = new Credito();
        c.setIdcredito(creditoSelected.getIdcredito());
        // modificar  el saldo   y restar el monto eliminado en cuenta
        Cuenta cu = new Cuenta();
        cu.setIdcuenta(creditoSelected.getIdcuenta());
        cu.setNumero(creditoSelected.getNumero());
        cu.setEntidad(creditoSelected.getEntidad());
        cu.setSaldo(strsaldo - creditoSelected.getMontoc());
        cu.setIduser(iduser);
        databaseReference.child("Cuenta").child(cu.getIdcuenta()).setValue(cu);
        //
        databaseReference.child("Credito").child(c.getIdcredito()).removeValue();
        Toast.makeText(this, "Eliminar", Toast.LENGTH_SHORT).show();
        LimpiarCajas();
        listarDatos();
    }

    private void modificar() {
        Credito c = new Credito();

        Double dmontoc = Double.valueOf(montoc.getText().toString());
        // restar monto denito en saldo cuenta
        Double saldocu = creditoSelected.getSaldo()- creditoSelected.getMontoc() ;
        // 100= 100 -200
        //
        c.setIdcredito(creditoSelected.getIdcredito());
        c.setMontoc(dmontoc);
        c.setDescripcionc(descripcionc.getText().toString().trim());
        c.setIdcuenta(stridcuenta);
        c.setFechac(fechac.getText().toString().trim());
        c.setMontoc(dmontoc);
        c.setEntidad(strentidad);
        c.setNumero(strnumero);
        /// sumar monto debito a saldo en cuenta
        c.setSaldo(saldocu + dmontoc);
        //
        c.setIduser(iduser);
        databaseReference.child("Credito").child(c.getIdcredito()).setValue(c);

        Cuenta cu = new Cuenta();
        cu.setIdcuenta(creditoSelected.getIdcuenta());
        cu.setNumero(creditoSelected.getNumero());
        cu.setEntidad(creditoSelected.getEntidad());
        cu.setSaldo(saldocu + dmontoc);
        cu.setIduser(iduser);
        databaseReference.child("Cuenta").child(cu.getIdcuenta()).setValue(cu);
        Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
        LimpiarCajas();
    }

    private void listarDatos() {
        databaseReference.child("Credito").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listCredito.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Credito c = objSnaptshot.getValue(Credito.class);
                    if(c.getIduser().equals(iduser)){
                    listCredito.add(c);}
                    arrayAdapterCredito = new ArrayAdapter<Credito>(ActividadCredito.this, android.R.layout.simple_list_item_1, listCredito);
                    listV_creditos.setAdapter(arrayAdapterCredito);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // persistencia
        // firebaseDatabase.setPersistenceEnabled(true);
        //
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String descripcioncr = descripcionc.getText().toString();
        String fechacr = fechac.getText().toString();
        // String horadr =fechac.getText().toString();
        String idcuentacr = stridcuenta;
        Double montocr = Double.valueOf(montoc.getText().toString());
        // switch
        switch (item.getItemId()) {

            case R.id.icon_return: {

                ActividadCredito.this.finish();
                onBackPressed();
                break;

            }
            case R.id.icon_add: {
                //validar
                if (descripcioncr.equals("") || fechacr.equals("")  || idcuentacr.equals("") || montocr.equals("")) {
                    Validacion();

                } else {
                    Credito c = new Credito();
                    c.setIdcredito(UUID.randomUUID().toString());
                    c.setDescripcionc(descripcioncr);
                    c.setIdcuenta(stridcuenta);
                    c.setFechac(fechacr);
                    c.setMontoc(montocr);
                    c.setEntidad(strentidad);
                    c.setNumero(strnumero);
                    ///sumar al saldo de la cuenta de la tabla debito
                    c.setSaldo(strsaldo + montocr);
                    c.setIduser(iduser);
                    databaseReference.child("Credito").child(c.getIdcredito()).setValue(c);
                    Cuenta cu = new Cuenta();
                    cu.setIdcuenta(stridcuenta);
                    cu.setNumero(strnumero);
                    cu.setEntidad(strentidad);
                    // sumar al saldo de la tabla debito a la tabla cueta
                    cu.setSaldo(strsaldo + montocr);
                    cu.setIduser(iduser);
                    databaseReference.child("Cuenta").child(cu.getIdcuenta()).setValue(cu);
                    // databaseReference.child("Credito/"+c.getIdcategoria()+"").child("Categoria").child(ca.getIdcategoria()).setValue(ca);
                    //  databaseReference.child("Credito/"+c.getIdcuenta()+"").child("Cuenta").child(cu.getIdcuenta()).setValue(cu);
                    Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
                    LimpiarCajas();
                    listarDatos();
                    cargarCuentas();

                }
                break;


            }
            case R.id.icon_save: {
                if (descripcioncr.equals("") || fechacr.equals("")  || idcuentacr.equals("") || montocr.equals("")) {
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
            case R.id.icon_delete: {
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
            default:
                break;
        }
        return true;
    }

    private void LimpiarCajas() {
        montoc.setText("");
        descripcionc.setText("");
        fechac.setText("");
        // horac.setText("");
        strentidad2="";
        strnumero2="";
        stridcuenta2="";
        strsaldo2=0;
        cuentas.clear();
        cargarCuentas();
    }

    private void Validacion() {
        String descripcioncr = descripcionc.getText().toString();
        String fechacr = fechac.getText().toString();
        // String horacr =fechac.getText().toString();
        String idcuentadr = stridcuenta;
        String montodr = montoc.getText().toString();
        if (descripcioncr.equals("")) {
            descripcionc.setError("Required");
        } else if (fechacr.equals("")) {
            fechac.setError("Required");
        } else if (montodr.equals("")) {
            montoc.setError("Required");
        }  else if (idcuentadr.equals("")) {

            // dialogo
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Alevertencia");
            dialogo1.setMessage("Debes selecionar una cuenta");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialogo1.show();
        }

    }

    public void OnClickFecha(View view) {
        Calendar cal = Calendar.getInstance();
        int anio=cal.get(Calendar.YEAR);
        int mes= cal.get(Calendar.MONTH);
        int dia= cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActividadCredito.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
              //  String fechacalen=dayOfMonth + "/" + month+ "/" + year;
                String fechacalen=year + "/" + month + "/" + dayOfMonth;
                fechac.setText(fechacalen);
            }
        }, anio,mes,dia);
        dpd.show();
    }
}