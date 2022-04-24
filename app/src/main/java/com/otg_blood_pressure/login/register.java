package com.otg_blood_pressure.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.otg_blood_pressure.IP_Setting;
import com.otg_blood_pressure.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 本APP所有容之智慧財權，均為蝸牛手建館所有，包括文字、照片、圖片、軟體程式碼，皆受中華民國著作權法、商標法、
 * 公平交易法與其他相關法令之保障，請尊重智慧財權並遵守法令規範，請勿自行使用、轉載、修改、重製、發行、公開發表、教學、散佈，
 * 若需此方面的使用或服務，請務必事先與蝸牛手建館人員接洽，並且取得授權。
 */

public class register extends Activity {
    Button reg_bn;
    EditText Name,Phone,UserName,Password,ConPassword,Age;
    String name,phone,username,password,conpass, age, sex;
    AlertDialog.Builder builder;
    String reg_url = new IP_Setting().blood_server_url  + "/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_bn = (Button)findViewById(R.id.btn_reg);
        Name = (EditText)findViewById(R.id.reg_name);
        Phone = (EditText)findViewById(R.id.reg_phone);
        UserName = (EditText)findViewById(R.id.reg_user_name);
        Password = (EditText)findViewById(R.id.reg_password);
        Age = (EditText)findViewById(R.id.reg_age);
        ConPassword = (EditText)findViewById(R.id.reg_con_password);
        builder = new AlertDialog.Builder(register.this);
        RadioButton male = (RadioButton) findViewById(R.id.radioButtonM);
        RadioButton female = (RadioButton) findViewById(R.id.radioButtonF);
        sex = "M";
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "M";
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sex = "F";
            }
        });


        reg_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Name.getText().toString();
                phone = Phone.getText().toString();
                username = UserName.getText().toString();
                password = Password.getText().toString();
                conpass = ConPassword.getText().toString();
                age = Age.getText().toString();

                if(name.equals("") || phone.equals("") || username.equals("") || password.equals("") || conpass.equals("") || age.equals(""))
                {
                    builder.setTitle("錯誤");
                    builder.setMessage("請輸入完整資料");
                    displayAlert("請輸入完整資料");
                }
                else
                {
                    if(!(password.equals(conpass)))
                    {
                        builder.setTitle("錯誤");
                        builder.setMessage("密碼不相同");
                        displayAlert("密碼不相同");
                    }
                    else
                    {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(response);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            String message = jsonObject.getString("message");
                                            builder.setTitle("伺服器回應");
                                            builder.setMessage(message);
                                            Log.d("fredtest123","message: " + message);
                                            displayAlert(code);
                                        } catch (JSONException e){

                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("fredtest456","message: " +error);
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                Log.d("fredtest","message: " +name + "  " + phone + " " + age + "  " + sex );
                                params.put("name",name);
                                params.put("email",phone);
                                params.put("age",age);
                                params.put("sex",sex);
                                params.put("account",username);
                                params.put("password",password);

                                return params;
                            }
                        };
                        MySingleton.getmInstance(register.this).addToRequestque(stringRequest);
                        //////////
                    }
                }
            }
        });

    }

    public void displayAlert(final String code)
    {
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(code.equals("input_error"))
                {
                    Password.setText("");
                    ConPassword.setText("");
                }
                else if(code.equals("reg_success"))
                {
                    finish();
                }
                else if(code.equals("reg_failed"))
                {
                    Name.setText("");
                    Phone.setText("");
                    UserName.setText("");
                    Password.setText("");
                    ConPassword.setText("");
                }
            }
        });
        AlertDialog alertDialog  = builder.create();
        alertDialog.show();
    }
}
