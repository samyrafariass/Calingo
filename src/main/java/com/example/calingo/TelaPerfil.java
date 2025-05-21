package com.example.calingo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calingo.databinding.ActivityTelaInicialBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class TelaPerfil extends AppCompatActivity {

    private EditText apelido, email, senha, cidade, estado;
    private Button btn_expressao;
    private BottomNavigationView bottomNavigationView;
    FirebaseFirestore db_calingo = FirebaseFirestore.getInstance();
    String usuario_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_perfil);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.desafio_nav) {
                startActivity(new Intent(this, TelaDesafio.class));
                return true;
            } else if (id == R.id.expressoes_nav) {
                startActivity(new Intent(this, TelaSugestao.class));
                return true;
            } else if (id == R.id.menu_nav) {
                startActivity(new Intent(this, TelaInicial.class));
                return true;
            }
            return false;
        });


        apelido = (EditText) findViewById(R.id.edit_apelido);
        email = (EditText) findViewById(R.id.edit_email);
        senha = (EditText) findViewById(R.id.edit_senha);
        cidade = (EditText) findViewById(R.id.edit_cidade);
        estado = (EditText) findViewById(R.id.edit_estado);

        btn_expressao = (Button) findViewById(R.id.btn_sugerir_expressao);
        btn_expressao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(TelaPerfil.this,TelaSugestao.class);
                startActivity(it);
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();

        usuario_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db_calingo.collection("Usuarios").document(usuario_id);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    apelido.setText(documentSnapshot.getString("apelido"));
                    email.setText(documentSnapshot.getString("email"));
                    senha.setText(documentSnapshot.getString("senha"));
                    cidade.setText(documentSnapshot.getString("cidade"));
                    estado.setText(documentSnapshot.getString("estado"));
                } else{
                    Toast.makeText(TelaPerfil.this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TelaPerfil.this, TelaCadastro.class));
                }
            }
        });
    }

}