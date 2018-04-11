package rushabh.com.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar mToolbar;

    private TextInputEditText mLoginEmail;
    private TextInputEditText mLoginPassword;
    private Button mLoginButton;

    //For login method
    private FirebaseAuth mAuth;
    //Progress bar
    private ProgressDialog mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        //Creating instance of Firebase Auth & initiating progressbar
        mAuth = FirebaseAuth.getInstance();
        mProgressBar=new ProgressDialog(this);

        //Fetching values from the Textinputlayouts
        mLoginEmail=(TextInputEditText)findViewById(R.id.login_email);
        mLoginPassword=(TextInputEditText)findViewById(R.id.login_pwd);

        mLoginButton=(Button)findViewById(R.id.login_btn);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Textinputlayouts return objects,using this we can return the values in texrt format
                String email=mLoginEmail.getText().toString();
                String pwd=mLoginPassword.getText().toString();

                if(!email.isEmpty() && !pwd.isEmpty()){
                    mProgressBar.setTitle("Logging in");
                    mProgressBar.setMessage("Please wait while we check your credentials");
                    mProgressBar.setCanceledOnTouchOutside(false);
                    mProgressBar.show();
                    loginUser(email,pwd);
                }
            }
        });

    }

    private void loginUser(String email, String pwd) {
        //Copy pasted from the firebase auth
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mProgressBar.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            mProgressBar.hide();
                            Toast.makeText(LoginActivity.this,"Can not Sign in",Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }
}
