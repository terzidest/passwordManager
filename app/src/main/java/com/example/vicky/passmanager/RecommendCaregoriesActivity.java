package com.example.vicky.passmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecommendCaregoriesActivity extends AppCompatActivity {


    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private ListView myList;
    private String ageStr;
    private double age;
    private String mUserId;
    private DatabaseReference ref;
    private String email;
    private String password;
    private String sexStr;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_caregories);


        //set fb
        ref = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        //get user's info
        email = getIntent().getStringExtra("mail");
        password = getIntent().getStringExtra("pass");
        sexStr = getIntent().getStringExtra("sex");
        ageStr = getIntent().getStringExtra("age");
        age = Double.parseDouble(ageStr);
        btn = (Button)findViewById(R.id.button3);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Intent intent = new Intent(RecommendCaregoriesActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });



        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1);
        myList = (ListView) findViewById(R.id.list);
        myList.setAdapter(adapter);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String elementName = (String) parent.getItemAtPosition(position);
               ref.child("users").child(mUserId).child("categories").child(elementName).child("exampleName").setValue("examplePass");
                Toast.makeText(RecommendCaregoriesActivity.this, "Category has been added", Toast.LENGTH_SHORT).show();
                adapter.remove(elementName);
            }
        });


        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(RecommendCaregoriesActivity.this,  new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            mUserId =  mFirebaseUser.getUid();

                            final User user = new User();
                            user.setAge(age);
                            user.setSex(sexStr);

                            String examplePass = "1234";

                            DatabaseReference userRef = ref.child("users").child(mUserId);
                            userRef.setValue(user);

                            DatabaseReference sexRef = userRef.child("sex");
                            sexRef.setValue(sexStr);

                            DatabaseReference ageRef = userRef.child("age");
                            ageRef.setValue(age);

                            DatabaseReference categoriesRef = userRef.child("categories");

                            DatabaseReference emailsRef = categoriesRef.child("emails");
                            DatabaseReference emailchildRef = emailsRef.child("example(at)gmail(dot)com");
                            emailchildRef.setValue(examplePass);

                            DatabaseReference banksRef = categoriesRef.child("banks");
                            DatabaseReference bankchildRef = banksRef.child("example bank");
                            bankchildRef.setValue(examplePass);

                            DatabaseReference websitesRef = categoriesRef.child("websites");
                            DatabaseReference websiteschildRef = websitesRef.child(" www(dot)example(dot)com(slash)home");
                            websiteschildRef.setValue(examplePass);


                            final double minAge = age-5d;
                            final double maxAge = age+5d;

                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference uRef = database.getReference("users");

                            final ArrayList<DataSnapshot> matches = new ArrayList<DataSnapshot>();
                            final ArrayList<String> allCategories = new ArrayList<String>();
                            final Map<String, Integer> countedCategories = new HashMap<String, Integer>();


                            uRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {



                                    Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                                    Iterator<DataSnapshot> i = dataSnapshotIterable.iterator();

                                        while(i.hasNext()){
                                            DataSnapshot ss = i.next();
                                            String uAgeStr = ss.child("age").getValue().toString();
                                            double uAge = Double.parseDouble(uAgeStr);

                                            if (ss.child("sex").getValue().equals(sexStr))
                                                if( uAge >= minAge && uAge <= maxAge)
                                                    matches.add(ss);
                                        }

                                    final int[] count = {0};

                                    for(DataSnapshot d: matches){

                                        ref.child("users").child(d.getKey()).child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                Iterable<DataSnapshot> dataSnapshotIterable = dataSnapshot.getChildren();
                                                Iterator<DataSnapshot> i = dataSnapshotIterable.iterator();

                                                count[0]++;

                                                if(count[0] == matches.size()){

                                                    for(String s: allCategories){
                                                      if(!s.equals("emails"))
                                                         if(!s.equals("banks"))
                                                             if(!s.equals("websites"))


                                                        if(countedCategories.containsKey(s)){
                                                            countedCategories.put(s, countedCategories.get(s)+1);
                                                        }
                                                        else{
                                                            countedCategories.put(s,1);
                                                        }

                                                    }



                                                    Map<String,Integer> sortedMap =sortByComparator(countedCategories,false);
                                                    ArrayList<String> recommentedCategories= new ArrayList<String>();
                                                    int topk = 0;
                                                    for(String s : sortedMap.keySet()){

                                                        if(topk >= 2)
                                                        break;

                                                        recommentedCategories.add(s);
                                                        topk++;
                                                    }

                                                    for(String s: recommentedCategories) {
                                                        adapter.add(s);
                                                    }


                                                }

                                                while(i.hasNext()) {
                                                    DataSnapshot ss = i.next();
                                                    allCategories.add(ss.getKey());
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("The read failed: " + databaseError.getCode());
                                }

                            });

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecommendCaregoriesActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });


    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFirebaseAuth.signOut();
        Intent intent = new Intent(RecommendCaregoriesActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
