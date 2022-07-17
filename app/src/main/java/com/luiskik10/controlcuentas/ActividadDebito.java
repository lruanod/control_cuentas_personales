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
import com.luiskik10.controlcuentas.Models.Debito;
import com.luiskik10.controlcuentas.Models.Cuenta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ActividadDebito extends AppCompatActivity {

    // campos del formulario
    EditText montod,descripciond,fechad;
    Spinner idcategoria, idcuenta;
    Button btnData, btnHora;
    Calendar c;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    String categoriaselecionada="";
    String cuentaselecionada="";
    String strcategoria,stridcategoria="";
    String strnumero,stridcuenta,strentidad="";
    String strcategoria2,stridcategoria2="";
    String strnumero2,strentidad2,stridcuenta2="";
    String iduser="";
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth mAuth;

    int btnsave,btnadd=0;

    double strsaldo,strsaldo2=0;
    final List<Categoria> categoria=new ArrayList<>();
    final List<Cuenta> cuentas=new ArrayList<>();


    ListView listV_debitos;
    // para listar
    private List<Debito> listDebito = new ArrayList<Debito>();
    ArrayAdapter<Debito> arrayAdapterDebito;

    //seleccionar persona
    Debito debitoSelected;

    //conexion a bd
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //private MenuItem item;
    //private int icon_save;

    ///
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_debito);
        //mostrar datos
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        iduser= currentUser.getUid();

        ///enlazar formulario
        montod=findViewById(R.id.txt_montodDebito);
        descripciond=findViewById(R.id.txt_descripcionDebito);
        fechad=findViewById(R.id.txt_fechadDebito);
        listV_debitos=findViewById(R.id.lv_datosDebito);
        idcategoria=findViewById(R.id.txt_categoriaDebito);
        idcuenta=findViewById(R.id.txt_cuentaDebito);

        //inicializar firebase
        inicializarFirebase();
        btnsave=0;
        btnadd=0;

        //listar datos en lista
         listarDatos();
         cargarCategorias();
         cargarCuentas();

        //seleccionar credito
         listV_debitos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               cuentas.clear();
               categoria.clear();
               cargarCategorias();
               cargarCuentas();
                  debitoSelected = (Debito) parent.getItemAtPosition(position);
                  descripciond.setText(debitoSelected.getDescripciond());
                  Double dsaldo = Double.valueOf(debitoSelected.getMontod());
                  montod.setText(dsaldo.toString());
                  fechad.setText(debitoSelected.getFechad());
                  strcategoria2=debitoSelected.getCategoria();
                  stridcategoria2=debitoSelected.getIdcategoria();
                  stridcuenta2=debitoSelected.getIdcuenta();
                  strnumero2=debitoSelected.getNumero();
                  strentidad2=debitoSelected.getEntidad();
                 // strsaldo2= Double.valueOf(debitoSelected.getSaldo());
                  Double strsaldo3= Double.valueOf(debitoSelected.getSaldo());
                  strsaldo2=strsaldo3;
           }
        });


    }

  private void cargarCategorias(){
          databaseReference.child("Categoria").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  categoria.clear();
                if(dataSnapshot.exists()){
                    if(stridcategoria2.equals("")){
                        categoria.add(new Categoria("","--Selecionca categoria--",""));
                    } else {
                        categoria.add(new Categoria(stridcategoria2,strcategoria2,iduser));}

                    for( DataSnapshot ds: dataSnapshot.getChildren()){
                        Categoria c = dataSnapshot.getValue(Categoria.class);

                            String idcategoria = ds.getKey();
                            //String idcategoria = ds.child("idcategoria").getValue().toString();
                            String categoriac = ds.child("categoria").getValue().toString();
                            String iduser2= ds.child("iduser").getValue().toString();
                        if (iduser.equals(iduser2)) {
                            categoria.add(new Categoria(idcategoria, categoriac, iduser2));
                        }
                    }
                    ArrayAdapter<Categoria> arrayAdapter =new ArrayAdapter<>(ActividadDebito.this, android.R.layout.simple_dropdown_item_1line,categoria);
                    idcategoria.setAdapter(arrayAdapter);
                    idcategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                       @Override
                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                           categoriaselecionada = parent.getItemAtPosition(position).toString();
                           stridcategoria = categoria.get(position).getIdcategoria();
                           strcategoria =categoria.get(position).getCategoria();
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
                    if(stridcuenta2.equals("")){
                        cuentas.add(new Cuenta("","","--Selecionca cuenta--",0.0,""));
                    } else {
                        cuentas.add(new Cuenta(stridcuenta2,strnumero2,strentidad2,strsaldo2,iduser));}

                    for( DataSnapshot ds: dataSnapshot.getChildren()){
                        Cuenta cu = dataSnapshot.getValue(Cuenta.class);

                            String idcuenta = ds.getKey();
                            String entidad = ds.child("entidad").getValue().toString();
                            String numero = ds.child("numero").getValue().toString();
                            String saldo = ds.child("saldo").getValue().toString();
                            Double dsaldo = Double.valueOf(saldo);
                            String iduser2= ds.child("iduser").getValue().toString();
                        if(iduser.equals(iduser2)) {
                            cuentas.add(new Cuenta(idcuenta, numero, entidad, dsaldo,iduser2));
                         }
                    }
                    ArrayAdapter<Cuenta> arrayAdapter =new ArrayAdapter<>(ActividadDebito.this, android.R.layout.simple_dropdown_item_1line,cuentas);
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

    private void cancelar() {
        LimpiarCajas();
    }

    private void aceptar() {
        Debito c = new Debito();
        c.setIddebito(debitoSelected.getIddebito());
        // modificar  el saldo   y sumar el monto eliminado en cuenta
                Cuenta cu = new Cuenta();
                cu.setIdcuenta(debitoSelected.getIdcuenta());
                cu.setNumero(debitoSelected.getNumero());
                cu.setEntidad(debitoSelected.getEntidad());
                cu.setSaldo(strsaldo+ debitoSelected.getMontod());
                cu.setIduser(iduser);
                databaseReference.child("Cuenta").child(cu.getIdcuenta()).setValue(cu);
        //
        databaseReference.child("Debito").child(c.getIddebito()).removeValue();
        Toast.makeText(this, "Eliminar", Toast.LENGTH_SHORT).show();
        LimpiarCajas();
        listarDatos();
    }

    private void modificar() {

        Debito c = new Debito();
        Double dmontoc = Double.valueOf(montod.getText().toString());
        // sumarr monto denito en saldo cuenta
           Double saldocu = debitoSelected.getMontod()+debitoSelected.getSaldo();
        //
        c.setIddebito(debitoSelected.getIddebito());
        c.setMontod(dmontoc);
        c.setDescripciond(descripciond.getText().toString().trim());
        c.setIdcategoria(stridcategoria);
        c.setIdcuenta(stridcuenta);
        c.setFechad(fechad.getText().toString().trim());
        c.setMontod(dmontoc);
        c.setCategoria(strcategoria);
        c.setEntidad(strentidad);
        c.setNumero(strnumero);
        c.setIduser(iduser);
        /// restar monto debito a saldo en cuenta
            c.setSaldo(saldocu-dmontoc);
        //
        databaseReference.child("Debito").child(c.getIddebito()).setValue(c);

        Cuenta cu = new Cuenta();
        cu.setIdcuenta(debitoSelected.getIdcuenta());
        cu.setNumero(debitoSelected.getNumero());
        cu.setEntidad(debitoSelected.getEntidad());
        cu.setSaldo(saldocu-dmontoc);
        cu.setIduser(iduser);
        databaseReference.child("Cuenta").child(cu.getIdcuenta()).setValue(cu);
        Toast.makeText(this, "Actualizado", Toast.LENGTH_SHORT).show();
        LimpiarCajas();
    }

    private void listarDatos() {
        databaseReference.child("Debito").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                listDebito.clear();
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Debito c = objSnaptshot.getValue(Debito.class);
                   if(c.getIduser().equals(iduser)){
                    listDebito.add(c);  }
                    arrayAdapterDebito= new ArrayAdapter<Debito>(ActividadDebito.this, android.R.layout.simple_list_item_1, listDebito);
                    listV_debitos.setAdapter(arrayAdapterDebito);
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

        String descripciondr =descripciond.getText().toString();
        String fechadr =fechad.getText().toString();
       // String horadr =fechac.getText().toString();
        String idcategoriadr =stridcategoria;
        String idcuentadr =stridcuenta;
        Double montodr = Double.valueOf(montod.getText().toString());
        // switch

        switch (item.getItemId()){

            case R.id.icon_return:{
                ActividadDebito.this.finish();
                onBackPressed();
                break;
            }
            case R.id.icon_add:{
                Double montodebito=Double.valueOf(montod.getText().toString());
                Double saldocuenta = Double.valueOf(strsaldo);

                //validar
                if (descripciondr.equals("")||fechadr.equals("")||idcategoriadr.equals("")||idcuentadr.equals("")||montodr.equals("")){
                    Validacion();

                } else{
                    if (montodebito>saldocuenta){

                        // dialogo
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                        dialogo1.setTitle("Alevertencia");
                        dialogo1.setMessage("El saldo de la cuenta es insuficiente para realizar este debito");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelar();
                            }
                        });

                        dialogo1.show();
                        break;
                    }
                    Debito c =new Debito();
                    c.setIddebito(UUID.randomUUID().toString());
                    c.setDescripciond(descripciondr);
                    c.setIdcategoria(stridcategoria);
                    c.setIdcuenta(stridcuenta);
                    c.setFechad(fechadr);
                    c.setMontod(montodr);
                    c.setCategoria(strcategoria);
                    c.setEntidad(strentidad);
                    c.setNumero(strnumero);
                    ///restar al saldo de la cuenta de la tabla debito
                    c.setSaldo(strsaldo-montodr);
                    c.setIduser(iduser);
                    databaseReference.child("Debito").child(c.getIddebito()).setValue(c);
                    Cuenta cu = new Cuenta();
                    cu.setIdcuenta(stridcuenta);
                    cu.setNumero(strnumero);
                    cu.setEntidad(strentidad);
                    // restar al saldo de la tabla debito a la tabla cueta
                    cu.setSaldo(strsaldo-montodr);
                    cu.setIduser(iduser);
                    databaseReference.child("Cuenta").child(cu.getIdcuenta()).setValue(cu);
                   // databaseReference.child("Credito/"+c.getIdcategoria()+"").child("Categoria").child(ca.getIdcategoria()).setValue(ca);
                  //  databaseReference.child("Credito/"+c.getIdcuenta()+"").child("Cuenta").child(cu.getIdcuenta()).setValue(cu);
                    Toast.makeText(this, "Agregar", Toast.LENGTH_SHORT).show();
                    LimpiarCajas();
                    listarDatos();
                    cargarCategorias();
                    cargarCuentas();


                }
                break;


            }
            case R.id.icon_save:{
               // if(btnadd==0){
              //      break;
              //  }

                if (descripciondr.equals("")||fechadr.equals("")||idcategoriadr.equals("")||idcuentadr.equals("")||montodr.equals("")){
                    Validacion();

                } else {
                    // dialogo
                    Double dmontoc = Double.valueOf(montod.getText().toString());
                    // sumarr monto denito en saldo cuenta
                    Double saldocu = debitoSelected.getMontod()+debitoSelected.getSaldo();
                    //
                    if (dmontoc>saldocu){

                        // dialogo
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                        dialogo1.setTitle("Alevertencia");
                        dialogo1.setMessage("El saldo de la cuenta es insuficiente para realizar este debito");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelar();
                            }
                        });

                        dialogo1.show();
                        break;
                    }

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
        montod.setText("");
        descripciond.setText("");
        fechad.setText("");
       // horac.setText("");
        strcategoria2="";
        stridcategoria2="";
        strentidad2="";
        strnumero2="";
        stridcuenta2="";
        strsaldo2=0;
        cuentas.clear();
        cargarCuentas();
        categoria.clear();
        cargarCategorias();




    }

    private void Validacion() {
        String descripciondr =descripciond.getText().toString();
        String fechadr =fechad.getText().toString();
       // String horacr =fechac.getText().toString();
        String idcategoriadr =stridcategoria;
        String idcuentadr =stridcuenta;
        String montodr = montod.getText().toString();

        if(descripciondr.equals("")){
            descripciond.setError("Required");
        }
        else if (fechadr.equals("")){
            fechad.setError("Required");
        }

        else if (montodr.equals("")){
            montod.setError("Required");
        }
        else if (idcategoriadr.equals("")){

            // dialogo
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Alevertencia");
            dialogo1.setMessage("Debes selecionar una categoria");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialogo1.show();
        }

        else if (idcuentadr.equals("")){

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

        DatePickerDialog dpd = new DatePickerDialog(ActividadDebito.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
               // String fechacalen=dayOfMonth + "/" + month + "/" + year;
                String fechacalen=year + "/" + month + "/" + dayOfMonth;
                fechad.setText(fechacalen);
            }
        }, anio,mes,dia);
        dpd.show();
    }
}