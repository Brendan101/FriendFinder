package com.brenardo9956gmail.friendfinder;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity implements View.OnClickListener{

    EditText friendEmail;
    Button addButton;
    ListView friendsListView;

    String fListString;
    ArrayList<String> fList = new ArrayList<>();

    ListAdapter fListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        //change size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int)((float)dm.widthPixels * 0.95f);
        int height = (int)((float)dm.heightPixels * 0.8f);
        getWindow().setLayout(width, height);

        friendEmail = (EditText) findViewById(R.id.friendEmailEditText);
        addButton = (Button) findViewById(R.id.friendEmailButton);
        addButton.setOnClickListener(this);
        friendsListView = (ListView) findViewById(R.id.friendListView);

        //get friends list
        Intent intent = getIntent();
        fListString = intent.getStringExtra("fList");
        for (String friendEmail: fListString.split(" ")) {
            fList.add(friendEmail);
        }

        //put it in list view
        updateAdapter();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void updateAdapter(){

        //skip over first "friend" (user's own email)
        String[] friend_array = new String[fList.size()-1];
        fListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fList.subList(1, fList.size()-1).toArray(friend_array));
        friendsListView.setAdapter(fListAdapter);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.friendEmailButton:

                //add the friend
                String fEmail = friendEmail.getText().toString();
                if(!fEmail.equals("") && !fList.contains(fEmail)) {
                    fList.add(fEmail);
                    friendEmail.setText("");
                }

                updateAdapter();
                break;

        }

    }
}
