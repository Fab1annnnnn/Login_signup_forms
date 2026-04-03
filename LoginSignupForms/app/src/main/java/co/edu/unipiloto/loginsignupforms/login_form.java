package co.edu.unipiloto.loginsignupforms;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class login_form extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);

        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this,
                    "Completa usuario y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Bienvenido!", Toast.LENGTH_SHORT).show();
    }

    public void btn_signupForm(View view) {
        startActivity(new Intent(getApplicationContext(), signup_form.class));
    }
}