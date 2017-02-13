package com.example.inclass11;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CommentActivity extends AppCompatActivity {
EditText commentt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentt=(EditText) findViewById(R.id.editText);
    }

    public void submitComment(View view) {
        String comment=commentt.getText().toString();
        if(comment.length()!=0) {
            Intent i = new Intent();
            i.putExtra(ChatRoomActivity.VALUE_KEY, comment);
            setResult(RESULT_OK, i);
            finish();
        }
        else{

        }

    }
}
