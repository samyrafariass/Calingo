package com.example.calingo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calingo.databinding.ActivityTelaInicialBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TelaInicial extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    String usuario_id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_inicial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_inicio);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.desafio_nav) {
                startActivity(new Intent(this, TelaDesafio.class));
                return true;
            } else if (id == R.id.expressoes_nav) {
                startActivity(new Intent(this, TelaSugestao.class));
                return true;
            } else if (id == R.id.perfil_nav) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user != null){
                    usuario_id = user.getUid();
                    startActivity(new Intent(this, TelaPerfil.class));
                    return true;
                } else{
                    Toast.makeText(TelaInicial.this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TelaInicial.this, TelaCadastro.class));
                    finish();
                }

            }
            return false;
        });
    }
}