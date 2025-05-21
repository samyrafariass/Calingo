package com.example.calingo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TelaSugestao extends AppCompatActivity {

    private EditText expressao, significado, aplicacao_frase, fonologia, sinonimo, cidade, estado;
    private Button bt_sugerir_expressao;
    private BottomNavigationView bottomNavigationView;
    String usuario_id;
    String expressao_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_sugestao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_sugestao);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.desafio_nav) {
                startActivity(new Intent(this, TelaDesafio.class));
                return true;
            } else if (id == R.id.menu_nav) {
                startActivity(new Intent(this, TelaInicial.class));
                return true;
            } else if (id == R.id.perfil_nav) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null){
                    usuario_id = user.getUid();
                    startActivity(new Intent(this, TelaPerfil.class));
                    return true;
                } else{
                    Toast.makeText(TelaSugestao.this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TelaSugestao.this, TelaCadastro.class));
                }
            }
            return false;
        });

        expressao = (EditText) findViewById(R.id.nome_expressao);
        significado = (EditText) findViewById(R.id.significado);
        aplicacao_frase = (EditText) findViewById(R.id.aplicacao_frase);
        fonologia = (EditText) findViewById(R.id.fonologia);
        sinonimo = (EditText) findViewById(R.id.sinonimo);
        cidade = (EditText) findViewById(R.id.cidade);
        estado = (EditText) findViewById(R.id.estado);

        bt_sugerir_expressao = (Button) findViewById(R.id.btn_sugerir_expressao);
        bt_sugerir_expressao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expressao_sugerida = expressao.getText().toString();
                String significado_expressao = significado.getText().toString();
                String aplicacao = aplicacao_frase.getText().toString();
                String fonologia_expressao = fonologia.getText().toString();
                String sinonimo_expressao = sinonimo.getText().toString();
                String cidade_expressao = cidade.getText().toString();
                String estado_expressao = estado.getText().toString();

                if (expressao_sugerida.isEmpty() || significado_expressao.isEmpty() || aplicacao.isEmpty() || fonologia_expressao.isEmpty() || sinonimo_expressao.isEmpty() || cidade_expressao.isEmpty() || estado_expressao.isEmpty()){
                    Toast.makeText(TelaSugestao.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    cadastrarExpressao();
                }
            }
        });
    }
    private void cadastrarExpressao(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Toast.makeText(TelaSugestao.this, "Expressão não cadastrada!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TelaSugestao.this, TelaCadastro.class));
            finish();
        } else {
            salvarDadosExpressao();
            Toast.makeText(TelaSugestao.this, "Expresão cadastrada com sucesso!", Toast.LENGTH_SHORT).show();

            // limpar os campos após o cadastro com sucesso
            expressao.setText("");
            significado.setText("");
            aplicacao_frase.setText("");
            fonologia.setText("");
            sinonimo.setText("");
            cidade.setText("");
            estado.setText("");
        }
    }
    private void salvarDadosExpressao() {
        String cad_expressao = expressao.getText().toString();
        String cad_significado = significado.getText().toString();
        String cad_aplicacao = aplicacao_frase.getText().toString();
        String cad_fonologia = fonologia.getText().toString();
        String cad_sinonimo = sinonimo.getText().toString();
        String cad_estado = estado.getText().toString();

        FirebaseFirestore db_calingo = FirebaseFirestore.getInstance();

        Map<String, Object> palavra = new HashMap<>();
        palavra.put("Expressão", cad_expressao);
        palavra.put("Significado", cad_significado);
        palavra.put("Aplicação", cad_aplicacao);
        palavra.put("Fonologia", cad_fonologia);
        palavra.put("Sinonimo", cad_sinonimo);
        palavra.put("Estado", cad_estado);

        expressao_id = FirebaseAuth.getInstance().getUid(); // pega o ID da expressao
        DocumentReference documentReference = db_calingo.collection("Palavra").document(expressao_id);
        documentReference.set(palavra).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db_calingo", "Sucesso ao salvar a expressão"); // debugger
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_calingo_error", "Erro ao salvar a expressão" + e.toString()); // debugger
                    }
                });
    }

}