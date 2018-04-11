package rushabh.com.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mSavebtn;
    private TextInputEditText mStatus;
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mToolbar=(Toolbar)findViewById(R.id.status_app_bar);
        mStatus=(TextInputEditText)findViewById(R.id.change_status);
        mSavebtn=(Button)findViewById(R.id.save_changes);

        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid=mCurrentUser.getUid();

        mStatusDatabase= FirebaseDatabase.getInstance().getReference().child("User").child(uid);

        String status_val= getIntent().getStringExtra("status_val");
        mStatus.setText(status_val);



        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress=new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Changing the status");
                mProgress.setMessage("Please wait for a file ");
                mProgress.show();
                String status=mStatus.getEditableText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                            Intent goBack=new Intent(StatusActivity.this,SettingsActivity.class);
                            startActivity(goBack);
                        }else{
                            mProgress.hide();
                            Toast.makeText(StatusActivity.this,"There is an error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
