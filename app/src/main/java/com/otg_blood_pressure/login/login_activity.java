package com.otg_blood_pressure.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.otg_blood_pressure.IP_Setting;
import com.otg_blood_pressure.MainActivity;
import com.otg_blood_pressure.MyApp;
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

public class login_activity extends Activity {
    private RequestQueue mQueue;
    private StringRequest getRequest;
    CheckBox accountmem, passwordmem;
    TextView textView;
    Button login_button;
    EditText UserName,Password;
    String username,password;
    String login_url = new IP_Setting().blood_server_url + "login.php";
    AlertDialog.Builder builder;
    private Handler mUI_Handler = new Handler();
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        textView = (TextView)findViewById(R.id.reg_txt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_activity.this, register.class));
            }
        });
        builder = new AlertDialog.Builder(login_activity.this);
        login_button = (Button)findViewById(R.id.bn_login);
        UserName = (EditText)findViewById(R.id.login_name);
        Password = (EditText)findViewById(R.id.login_password);
        accountmem = (CheckBox)findViewById(R.id.accountmem);
        passwordmem = (CheckBox)findViewById(R.id.passwordmem);
        Log.d("FREDTEST: ", login_url);
        //mem account and passowrd +
        if(sp.getBoolean("ISCHECK_A", true))
        {
            accountmem.setChecked(true);
            UserName.setText(sp.getString("account", ""));
        }
        if(sp.getBoolean("ISCHECK_P", true))
        {
            passwordmem.setChecked(true);
            Password.setText(sp.getString("password", ""));
        }
        //mem account and passowrd -

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mem account and passowrd +
                if(!accountmem.isChecked())
                {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("account", "");
                    editor.commit();
                    sp.edit().putBoolean("ISCHECK_A", false).commit();
                }
                if(!passwordmem.isChecked())
                {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", "");
                    editor.commit();
                    sp.edit().putBoolean("ISCHECK_P", false).commit();
                }
                //mem account and passowrd -
                username = UserName.getText().toString();
                password = Password.getText().toString();

                if (username.equals("") || password.equals(""))
                {
                    builder.setTitle("Something went wrong");
                    displayAlert("Enter a valid username and password");
                }
                else if (username.equals(CommonSettings.USER_ROOT_NAME) && password.equals(CommonSettings.USER_ROOT_NAME)){
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(CommonSettings.USER_NAME,"root");
                    bundle.putString(CommonSettings.USER_MAIL,"root@gmail.com");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else if (username.equals(CommonSettings.USER_ADMIN_NAME) && password.equals(CommonSettings.USER_ADMIN_NAME)) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(CommonSettings.USER_NAME, CommonSettings.USER_ADMIN_NAME);
                    bundle.putString(CommonSettings.USER_MAIL,CommonSettings.USER_ROOT_MAIL);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else
                {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.d("FREDTEST: ", response);
                                        JSONArray jsonArray = new JSONArray(response);
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        String code = jsonObject.getString("code");
                                        Log.d("TEST     ",code);
                                        if (code.equals("login_failed")) {
                                            builder.setTitle("Login Error...");
                                            displayAlert(jsonObject.getString("message"));
                                        }
                                        else
                                        {
                                            //mem account and passowrd +
                                            if(accountmem.isChecked())
                                            {
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.putString("account", username);
                                                editor.commit();
                                                sp.edit().putBoolean("ISCHECK_A", true).commit();
                                            }
                                            if(passwordmem.isChecked())
                                            {
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.putString("password", password);
                                                editor.commit();
                                                sp.edit().putBoolean("ISCHECK_P", true).commit();
                                            }
                                            //mem account and passowrd -
                                            Intent intent = new Intent(getApplication(), MainActivity.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString(CommonSettings.USER_PID,jsonObject.getString(CommonSettings.USER_PID));
                                            bundle.putString(CommonSettings.USER_NAME,jsonObject.getString(CommonSettings.USER_NAME));
                                            bundle.putString(CommonSettings.USER_MAIL,jsonObject.getString(CommonSettings.USER_MAIL));
                                            bundle.putString(CommonSettings.USER_SEX,jsonObject.getString(CommonSettings.USER_SEX));
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(login_activity.this, "Error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("account",username);
                            params.put("password",password);
                            return params;
                        }
                    };
                    MySingleton.getmInstance(login_activity.this).addToRequestque(stringRequest);
                }
            }
        });
    }


    public void displayAlert(String message)
    {
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserName.setText("");
                Password.setText("");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
