package com.otg_blood_pressure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.otg_blood_pressure.login.CommonSettings;
import com.otg_blood_pressure.login.MySingleton;
import com.otg_blood_pressure.login.register;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 本APP所有容之智慧財權，均為蝸牛手建館所有，包括文字、照片、圖片、軟體程式碼，皆受中華民國著作權法、商標法、
 * 公平交易法與其他相關法令之保障，請尊重智慧財權並遵守法令規範，請勿自行使用、轉載、修改、重製、發行、公開發表、教學、散佈，
 * 若需此方面的使用或服務，請務必事先與蝸牛手建館人員接洽，並且取得授權。
 */

public class Result_activity extends Activity {
    TextView sp_textv, dp_textv, hr_textv;
    String username;
    Spinner spinner;
    ArrayList<String> spin_list;
    String get_data_url = new IP_Setting().blood_server_url  + "/get_data.php";
    private String user_pid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_result_layout);
        spinner = (Spinner) findViewById(R.id.spinner);
        sp_textv = (TextView) findViewById(R.id.sp);
        dp_textv = (TextView) findViewById(R.id.dp);
        hr_textv = (TextView) findViewById(R.id.hr);
        spin_list = new ArrayList<>();
        spin_init();
        final Intent intent = getIntent();
        user_pid = intent.getStringExtra(CommonSettings.USER_PID);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(Result_activity.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
            }

            @Override

            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {

                final String spin_date = spinner.getSelectedItem().toString();


                StringRequest stringRequest = new StringRequest(Request.Method.POST, get_data_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.d("FREDTEST: ", response);
                                    JSONArray jsonArray = new JSONArray(response);
                                    Log.d("FREDTEST length: ", String.valueOf(jsonArray.length())); //總共n-1筆
                                    for(int i=0; i<jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String date = jsonObject.getString("date");
                                        String sp = jsonObject.getString("sp");
                                        String dp = jsonObject.getString("dp");
                                        String hr = jsonObject.getString("hr");

                                        if(date.trim().equals(spin_date)){
                                            Log.d("TEST     ", +i + "  " + date + "  "+ sp+ "  "+ dp+ "  "+ hr);
                                            sp_textv.setText("收縮壓: " + sp);
                                            dp_textv.setText("舒張壓: " + dp);
                                            hr_textv.setText("心律: " + hr);
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Result_activity.this, "Error", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("pid", user_pid);
                        return params;
                    }
                };
                MySingleton.getmInstance(Result_activity.this).addToRequestque(stringRequest);
            }
        });
    }
    void spin_init() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, get_data_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Log.d("FREDTEST: ", response);
                            JSONArray jsonArray = new JSONArray(response);
                            //Log.d("FREDTEST lengthhh: ", String.valueOf(jsonArray.length())); //總共n-1筆
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String date = jsonObject.getString("date");
                                Log.d("FREDTEST: ", date);

                                spin_list.add(date);
                                Collections.reverse(spin_list);
                                ArrayAdapter<String> lunchList = new ArrayAdapter<>(Result_activity.this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        spin_list);
                                spinner.setAdapter(lunchList);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Result_activity.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("pid",user_pid);
                return params;
            }
        };
        MySingleton.getmInstance(Result_activity.this).addToRequestque(stringRequest);
    }
}
