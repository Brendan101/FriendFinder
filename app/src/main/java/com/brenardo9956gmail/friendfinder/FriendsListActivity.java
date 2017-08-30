package com.brenardo9956gmail.friendfinder;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

        //ability to remove friends
        friendsListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String friend = String.valueOf(parent.getItemAtPosition(position));
                        //make sure the "friend" isn't the user's own email
                        if(!friend.equals(fList.get(0))) {
                            fList.remove(friend);
                            updateAdapter();
                        }
                        return true;
                    }
                }
        );

        //get friends list
        Intent intent = getIntent();
        fListString = intent.getStringExtra("fList");
        for (String friendEmail: fListString.split(" ")) {
            fList.add(friendEmail);
        }

        //put it in list view
        updateAdapter();

    }

    private void updateAdapter(){

        //skip over first "friend" (user's own email)
        String[] friend_array = new String[fList.size()-1];
        fListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fList.subList(1, fList.size()).toArray(friend_array));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //send info back when this activity is exited
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //convert list back to single string
            String updatedList = "";
            for(int i = 0; i < fList.size(); i++){
                updatedList += fList.get(i) + " ";
            }

            Intent returnList = new Intent();
            returnList.putExtra("fList", updatedList);
            setResult(Activity.RESULT_OK, returnList);
        }

        return super.onKeyDown(keyCode, event);

    }
}
