package com.example.jack.security;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventVerify extends AppCompatActivity {

    private EditText PIN;

    DatabaseReference databaseReference;
    TextView event, host, contact,cap;
    Button pinsearch,btnChkIn;
    private ProgressDialog pd;
    String number;
    private RequestQueue mQueue;
    String event1="";
    String event2="";
    String event3="";
    String event4="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_verify);

        databaseReference = FirebaseDatabase.getInstance().getReference("Security/Event");
        pinsearch = (Button) findViewById(R.id.btnVerify);

        event = (TextView) findViewById(R.id.eventname);
        host = (TextView) findViewById(R.id.hostby);
        contact = (TextView) findViewById(R.id.contactno);
        cap = (TextView) findViewById(R.id.capacity);

        mQueue = Volley.newRequestQueue(this);

        btnChkIn = findViewById(R.id.btnEventChkIN);
        PIN = (EditText) findViewById(R.id.PINinput);
        pd = new ProgressDialog(EventVerify.this);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pinsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = PIN.getText().toString().trim();
                if(number.isEmpty())
                {
                    Toast.makeText(EventVerify.this,"Please Enter PIN",Toast.LENGTH_LONG).show();
                }
                else {
                    jsonParse();
                }
            }

        });
        btnChkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(event.getText().toString().isEmpty()||host.getText().toString().isEmpty()||contact.getText().toString().isEmpty()||cap.getText().toString().isEmpty())
                {
                    Toast.makeText(EventVerify.this,"Please Verify Event details",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(cap.getText().toString().trim().equals("0"))
                    {
                        Toast.makeText(EventVerify.this,"Event no more available",Toast.LENGTH_LONG).show();
                    }
                    else{
                        //UPDATE capacity
                    int capacityLeft = Integer.parseInt(cap.getText().toString()) -1;
                    EventDetails eventDetails = new EventDetails(event.getText().toString().trim(),host.getText().toString().trim(),contact.getText().toString().trim(),capacityLeft);
                    databaseReference.child(number).setValue(eventDetails);
                    Toast.makeText(EventVerify.this,"Successfully Verified",Toast.LENGTH_LONG).show();
                    finish();}
                }
            }
        });
    }

    private void jsonParse()
    {
        String url = "https://projecttc.000webhostapp.com/FetchEventData.php?Event_OTP=" + number;

        pd.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                try {

                    pd.hide();
                    JSONArray jsonArray = response.getJSONArray("Event");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject Guest = jsonArray.getJSONObject(i);

                        String event1 = Guest.getString("event_name");
                        String event2 = Guest.getString("host_by");
                        String event3 = Guest.getString("contact_no");
                        String event4 = Guest.getString("capacity");

                        event.setText(event1.toString());
                        host.setText(event2.toString());
                        contact.setText(event3.toString());
                        cap.setText(event4.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
                Toast.makeText(EventVerify.this, "Incorrect PIN", Toast.LENGTH_LONG).show();

                pd.hide();
            }
        });
        mQueue.add(request);
    }
}
