package com.luiskik10.controlcuentas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private TextView userNombre,userEmail,userID;
    private CircleImageView userImg;
    Button btnCerrarSesion;
    //Variable para gestionar FirebaseAuth
    private FirebaseAuth mAuth;
    //Variables opcionales para desloguear de google tambien
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNombre = findViewById(R.id.userNombre);
        userEmail = findViewById(R.id.userEmail);
        userImg =findViewById(R.id.userImagen);
        btnCerrarSesion=findViewById(R.id.btnLogout);
        //mostrar datos
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //
        // establecer campos
        userNombre.setText(currentUser.getDisplayName());
        userEmail.setText(currentUser.getEmail());
        Glide.with(this).load(currentUser.getPhotoUrl()).into(userImg);
        //Configurar las gso para google signIn con el fin de luego desloguear de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cerrar sesion en google
                mAuth.signOut();
                //Cerrar sesión con google tambien: Google sign out
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Abrir MainActivity con SigIn button
                        if(task.isSuccessful()){
                            Intent loginactivity = new Intent(getApplicationContext(), loginActivity.class);
                            startActivity(loginactivity);
                            MainActivity.this.finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }

    public void OnClick(View view){
     Intent miIntent=new Intent(MainActivity.this,ActividadCuenta.class);
     startActivity(miIntent);
    }

    public void OnClick2(View view){
        Intent miIntent=new Intent(MainActivity.this,ActividadCategoria.class);
        startActivity(miIntent);
    }

    public void OnClick3(View view){
        Intent miIntent=new Intent(MainActivity.this, ActividadDebito.class);
        startActivity(miIntent);
    }

    public void OnClick4(View view){
        Intent miIntent=new Intent(MainActivity.this, ActividadCredito.class);
        startActivity(miIntent);
    }
    public void OnClick5(View view){
        this.finish();
        System.exit(0);
    }
    public void OnClick6(View view) {
        Intent miIntent=new Intent(MainActivity.this, ActividadFormReporte.class);
        startActivity(miIntent);
    }

    public void OnClick7(View view) {
        Intent miIntent=new Intent(MainActivity.this, form_reporte2.class);
        startActivity(miIntent);

    }
}