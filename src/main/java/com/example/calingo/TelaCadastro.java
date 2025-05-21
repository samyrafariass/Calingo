package com.example.calingo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TelaCadastro extends AppCompatActivity {

    private EditText edit_apelido, edit_email, edit_senha, edit_cidade, edit_estado;
    private Button bt_login, bt_cadastro;
    String usuario_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edit_apelido = (EditText) findViewById(R.id.edit_apelido);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_senha = (EditText) findViewById(R.id.edit_senha);
        edit_cidade = (EditText) findViewById(R.id.edit_cidade);
        edit_estado = (EditText) findViewById(R.id.edit_estado);

        bt_login = (Button) findViewById(R.id.btn_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(TelaCadastro.this,TelaLogin.class);
                startActivity(it);
            }
        });

        bt_cadastro = (Button) findViewById(R.id.btn_cadastro);
        bt_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // gera uma caixinha de alerta
                String apelido = edit_apelido.getText().toString();
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();
                String cidade = edit_cidade.getText().toString();
                String estado = edit_estado.getText().toString();

                if (apelido.isEmpty() || email.isEmpty() || senha.isEmpty() || cidade.isEmpty() || estado.isEmpty()){
                    Toast.makeText(TelaCadastro.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    cadastrarUsuario(v);
                    Intent it = new Intent(TelaCadastro.this,TelaInicial.class);
                    startActivity(it);
                }
            }

        });

    }
    private void cadastrarUsuario(View v) {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){ // se o cadastro for um sucesso vai mostrar a mensagem
                    salvarDadosUsuario();
                    Toast.makeText(TelaCadastro.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                    // limpar os campos após o cadastro com sucesso
                    edit_apelido.setText("");
                    edit_email.setText("");
                    edit_senha.setText("");
                    edit_cidade.setText("");
                    edit_estado.setText("");

                } else{
                    String erro;
                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e){
                        erro = "Digite uma senha com no mímino 6 caracteres";
                    } catch (FirebaseAuthUserCollisionException e){
                        erro = "Email já cadastrado!";
                    } catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "Email incorreto, incompleto ou inválido!";
                    } catch (Exception e){
                        erro = "Erro ao cadastrar o usuário!";
                    }

                    Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }
            }
        });

    }
    private void salvarDadosUsuario(){
        String apelido = edit_apelido.getText().toString();
        String cidade = edit_cidade.getText().toString();
        String estado = edit_estado.getText().toString();

        FirebaseFirestore db_calingo = FirebaseFirestore.getInstance();

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("apelido", apelido);
        usuarios.put("cidade", cidade);
        usuarios.put("estado", estado);

        usuario_id = FirebaseAuth.getInstance().getCurrentUser().getUid(); // pega o ID do usuário
        DocumentReference documentReference = db_calingo.collection("Usuarios").document(usuario_id);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db_calingo", "Sucesso ao salvar os dados"); // debugger
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("db_calingo_error", "Erro ao salvar os dados" + e.toString()); // debugger
                    }
                });
    }
}