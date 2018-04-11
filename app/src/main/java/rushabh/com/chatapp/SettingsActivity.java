package rushabh.com.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mcircleImageView;
    private TextView mName;
    private TextView mStatus;

    private Button mChangeStatus;
    private Button mChangeImage;

    private static final int galleryPick =1;

    //Storage Referance
    private StorageReference mImageStorage;

    //Progress Dialogue
    private ProgressDialog mProcessDialogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mName=(TextView)findViewById(R.id.settings_display_name);
        mStatus=(TextView)findViewById(R.id.settings_status);
        String uid=mCurrentUser.getUid();

        mChangeStatus=(Button)findViewById(R.id.settings_status_btn);
        mChangeImage=(Button)findViewById(R.id.settings_image_btn);
        /*mChangeImage=(Button)findViewById(R.id.save_changes);*/
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(uid);

        //Getting image id
        mcircleImageView=(CircleImageView)findViewById(R.id.profile_image);

        //Image Storage thing
        mImageStorage= FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This fucntion is used when the data is changes or retrived

                //Retriving the value
                String name=dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                Toast.makeText(SettingsActivity.this,status,Toast.LENGTH_LONG).show();
                String thumbnail=dataSnapshot.child(("thumbnail")).getValue().toString();

                //Setting the new values

                mName.setText(name);
                mStatus.setText(status);

                Picasso.with(SettingsActivity.this).load(image).into(mcircleImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_current=mStatus.getText().toString();
                Intent mChangeStatuss=new Intent(SettingsActivity.this,StatusActivity.class);
                mChangeStatuss.putExtra("status_val",status_current);
                startActivity(mChangeStatuss);
            }
        });

       mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent mChangeImage = new Intent(SettingsActivity.this,MainActivity.class);
                Intent gallerIntent = new Intent();
                gallerIntent.setType("image/*");
                gallerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallerIntent,"Select Image"),galleryPick);

                //startActivity(mChangeImage);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == galleryPick && resultCode==RESULT_OK){
            //We will get image url in this phase
            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
            //Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_LONG).show();
        }
        //Copied code from the arthur Hub-->github
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProcessDialogue = new ProgressDialog(SettingsActivity.this);
                mProcessDialogue.setTitle("Uploading Image..");
                mProcessDialogue.setMessage("Please wait while Uploading");
                mProcessDialogue.setCanceledOnTouchOutside(false);
                mProcessDialogue.show();

                Uri resultUri = result.getUri();
                String current_User=mCurrentUser.getUid();
                final StorageReference filePath=mImageStorage.child("profile_images").child(current_User+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            //Getting the download url--> dont forget getDownload function
                            String download_url=task.getResult().getDownloadUrl().toString();

                            mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        mProcessDialogue.dismiss();
                                        Toast.makeText(SettingsActivity.this,"Success",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(SettingsActivity.this,"Cant add the file",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            //Toast.makeText(SettingsActivity.this,filePath.toString(),Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(SettingsActivity.this,"Cant add the file",Toast.LENGTH_LONG).show();
                            mProcessDialogue.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
