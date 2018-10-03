package mobileprogramming.unimelb.com.instagramapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    private EditText regEmailText;
    private EditText regPassText;
    private EditText regConfPassText;
    private Button regBtn;
    private Button regLoginBtn;
    private ProgressBar regProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        regEmailText = (EditText) findViewById(R.id.reg_email);
        regPassText = (EditText) findViewById(R.id.reg_pass);
        regConfPassText = (EditText) findViewById(R.id.reg_confirm_pass);
        regBtn = (Button) findViewById(R.id.reg_btn);
        regLoginBtn = (Button) findViewById(R.id.reg_login_btn);
        regProgress = (ProgressBar) findViewById(R.id.reg_progress);

        regLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


                regBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String email = regEmailText.getText().toString();
                        String pass = regPassText.getText().toString();
                        String confirm_pass = regConfPassText.getText().toString();

                        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass)) {

                            if (pass.equals(confirm_pass)) {

                                regProgress.setVisibility(View.VISIBLE);

                                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {

                                            Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                            startActivity(setupIntent);
                                            finish();

                                        } else {

                                            String errorMessage = task.getException().getMessage();
                                            Toast.makeText(RegisterActivity.this, "Error : " + errorMessage,
                                                    Toast.LENGTH_LONG).show();
                                        }

                                        regProgress.setVisibility(View.INVISIBLE);
                                    }
                                });

                            } else {
                                Toast.makeText(RegisterActivity.this, "Password and Confirm Password field doesn't match",
                                        Toast.LENGTH_LONG).show();

                            }

                        }

                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            sendToMain();
        }
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
