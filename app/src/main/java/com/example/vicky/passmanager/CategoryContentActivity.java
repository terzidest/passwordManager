package com.example.vicky.passmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CategoryContentActivity extends AppCompatActivity {


    private ListView myList;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference catRef;
    private String mUserId;
    private String elementPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_content);

        final String categoryName = getIntent().getStringExtra("cName");
        TextView tv1 = (TextView)findViewById(R.id.textView1);
        tv1.setText(categoryName);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        catRef = FirebaseDatabase.getInstance().getReference();
        if (mFirebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogInView();
        }
        mUserId = mFirebaseUser.getUid();

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        final EditText textName = (EditText) findViewById(R.id.nameText);
        final EditText textPassword = (EditText) findViewById(R.id.passText);




        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1);
        myList = (ListView) findViewById(R.id.list);
        myList.setAdapter(adapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String elementName = (String) parent.getItemAtPosition(position);
                catRef.child("users").child(mUserId).child("categories").child(categoryName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        elementPass =  dataSnapshot.child(elementName).getValue().toString();

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CategoryContentActivity.this);
                        alertDialogBuilder.setMessage("The password is: "+elementPass);

                        alertDialogBuilder.setNegativeButton("ok",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        //Toast.makeText(CategoryContentActivity.this, elementPass, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });




            }
        });


        catRef.child("users").child(mUserId).child("categories").child(categoryName).orderByKey().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick (View v){
                if(!textName.getText().toString().equals("") && !textPassword.getText().toString().equals("")) {

                    catRef.child("users").child(mUserId).child("categories").child(categoryName).child(textName.getText().toString()).setValue(textPassword.getText().toString());
                    textName.setText("");
                    textPassword.setText("");
                }
            }

        });


    }

    private void loadLogInView() {
        Intent intent = new Intent(CategoryContentActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mFirebaseAuth.signOut();
            loadLogInView();
        }

        return super.onOptionsItemSelected(item);
    }
}

