package com.otg_blood_pressure;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.otg_blood_pressure.login.CommonSettings;
import com.otg_blood_pressure.login.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * 本APP所有容之智慧財權，均為蝸牛手建館所有，包括文字、照片、圖片、軟體程式碼，皆受中華民國著作權法、商標法、
 * 公平交易法與其他相關法令之保障，請尊重智慧財權並遵守法令規範，請勿自行使用、轉載、修改、重製、發行、公開發表、教學、散佈，
 * 若需此方面的使用或服務，請務必事先與蝸牛手建館人員接洽，並且取得授權。
 */

public class MainActivity extends Activity {

    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";

    private EditText SystolicText, DiastolicText, HRText, CurrentText;
    private boolean isOpen;
    private Handler handler;
    private MainActivity activity;

    private Button clearButton, openButton, updateButton, result_button;


    public byte[] writeBuffer;
    public byte[] readBuffer;

    public int baudRate;
    public byte stopBit;
    public byte dataBit;
    public byte parity;
    public byte flowControl;

    String reg_url =  new IP_Setting().blood_server_url + "input_result_url.php";
    private String mDeviceName = "";
    private String Systolic_global_data = "";
    private String Diastolic_global_data = "";
    private String HR_global_data = "";
    private String Current_global_data = "";
    private String user_pid;

    private IntentFilter usbDeviceStateFilter;

    private String sel_pressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        MyApp.driver = new CH34xUARTDriver(
                (UsbManager) getSystemService(Context.USB_SERVICE), this,
                ACTION_USB_PERMISSION);
        initUI();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(CommonSettings.USER_NAME);
        user_pid = intent.getStringExtra(CommonSettings.USER_PID);

        if (!MyApp.driver.UsbFeatureSupported())
        {
            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("��ʾ")
                    .setMessage("USB HOST")
                    .setPositiveButton("ȷ��",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    System.exit(0);
                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// ���ֳ�������Ļ��״̬
        writeBuffer = new byte[512];
        readBuffer = new byte[512];
        isOpen = false;
        clearButton.setEnabled(false);
        activity = this;



        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                OpenUART();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                SystolicText.setText("");
                DiastolicText.setText("");
                HRText.setText("");
                CurrentText.setText("");
                byte[] to_send = toByteArray("64");
                MyApp.driver.WriteData(to_send, to_send.length);
                updateButton.setEnabled(false);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date dt = new Date();
                String day = sdf.format(dt);

                String date = day;

                Add_data_to_sql(date, Systolic_global_data, Diastolic_global_data, HR_global_data);

                SystolicText.setText("");
                DiastolicText.setText("");
                HRText.setText("");
                CurrentText.setText("");
                byte[] to_send = toByteArray("64");
                MyApp.driver.WriteData(to_send, to_send.length);
                
                updateButton.setEnabled(false);
            }
        });

        result_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), Result_activity.class);
                Bundle bundle = new Bundle();
                bundle.putString(CommonSettings.USER_PID, user_pid);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });



        handler = new Handler() {
            public void handleMessage(Message msg) {
                String get_data = (String) msg.obj;
                Log.d("FREDTEST ", get_data);
                String type_data = get_data.substring(0,5);
                if(type_data.trim().equals("aa 11")) {
                    String stop_data = get_data.substring(13,17);
                    if(stop_data.trim().equals("2 03")) {
                        String blood_data_0 = get_data.substring(6, 8);
                        String blood_data_1 = get_data.substring(9, 11);
                        int value1 = Integer.decode("0x" + blood_data_0);
                        int value0 = Integer.decode("0x" + blood_data_1);
                        int sum = (value1 * 256) + value0;
                        Systolic_global_data = String.valueOf(sum);
                        SystolicText.setText(Systolic_global_data);
                    }
                }

                if(type_data.trim().equals("aa 33")) {
                    String stop_data = get_data.substring(13,17);
                    if(stop_data.trim().equals("2 03")) {
                        String blood_data_0 = get_data.substring(6, 8);
                        String blood_data_1 = get_data.substring(9, 11);
                        int value1 = Integer.decode("0x" + blood_data_0);
                        int value0 = Integer.decode("0x" + blood_data_1);
                        int sum = (value1 * 256) + value0;
                        Diastolic_global_data = String.valueOf(sum);
                        DiastolicText.setText(Diastolic_global_data);
                    }
                }

                if(type_data.trim().equals("aa 55")) {
                    String stop_data = get_data.substring(13,17);
                    if(stop_data.trim().equals("2 03")) {
                        String blood_data_0 = get_data.substring(6, 8);
                        String blood_data_1 = get_data.substring(9, 11);
                        int value1 = Integer.decode("0x" + blood_data_0);
                        int value0 = Integer.decode("0x" + blood_data_1);
                        int sum = (value1 * 256) + value0;
                        HR_global_data = String.valueOf(sum);
                        HRText.setText(HR_global_data);
                    }
                }



                Log.d("FREDTEST", "111:"+ SystolicText.getText());
                Log.d("FREDTEST", "222:"+ DiastolicText.getText());
                Log.d("FREDTEST", "333:"+ HRText.getText());
                if(!SystolicText.getText().toString().trim().equals("") && !DiastolicText.toString().trim().equals("") && !HRText.toString().trim().equals("")) {
                    updateButton.setEnabled(true);
                }
                //String hex = (String) msg.obj;
                //readText.append(hexToASCII(hex.replace(" ","")));
            }
        };

    }

    private static String hexToASCII(String hexValue)
    {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        isOpen = false;
        MyApp.driver.CloseDevice();
        super.onDestroy();
    }

    //�������
    private void initUI() {
        DiastolicText = (EditText) findViewById(R.id.DiastolicText);
        SystolicText = (EditText) findViewById(R.id.SystolicText);
        HRText = (EditText) findViewById(R.id.HRText);
        CurrentText  = (EditText) findViewById(R.id.current_pressure);
        clearButton = (Button) findViewById(R.id.ClearButton);
        updateButton = (Button) findViewById(R.id.UpdateButton);
        openButton = (Button) findViewById(R.id.open_device);
        result_button = (Button) findViewById(R.id.result);






        DiastolicText.setText("");
        SystolicText.setText("");
        HRText.setText("");

        updateButton.setEnabled(false);

        baudRate = 9600;
        stopBit = 1;
        dataBit = 8;
        parity = 0;
        flowControl = 0;

        return;
    }

    private void OpenUART() {
        if (!isOpen) {
            int retval = MyApp.driver.ResumeUsbPermission();
            if (retval == 0) {
                //Resume usb device list
                retval = MyApp.driver.ResumeUsbList();
                if (retval == -1)// ResumeUsbList��������ö��CH34X�豸�Լ�������豸
                {
                    Toast.makeText(MainActivity.this, "Open failed!",
                            Toast.LENGTH_SHORT).show();
                    MyApp.driver.CloseDevice();
                } else if (retval == 0){
                            if (MyApp.driver.mDeviceConnection != null) {
                                if (!MyApp.driver.UartInit()) {//�Դ����豸���г�ʼ������
                            Toast.makeText(MainActivity.this, "Initialization failed!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(MainActivity.this, "Device opened",
                                Toast.LENGTH_SHORT).show();
                        isOpen = true;
                        if (MyApp.driver.SetConfig(baudRate, dataBit, stopBit, parity,
                                flowControl)) {
                            Toast.makeText(MainActivity.this, "Config successfully",
                                    Toast.LENGTH_SHORT).show();
                            openButton.setText("斷線");
                            clearButton.setEnabled(true);
                        } else {
                            Toast.makeText(MainActivity.this, "Config failed!",
                                    Toast.LENGTH_SHORT).show();
                            updateButton.setEnabled(false);
                        }
                        new readThread().start();
                    } else {
                        Toast.makeText(MainActivity.this, "Open failed!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setIcon(R.drawable.icon);
                    builder.setTitle("δ��Ȩ��");
                    builder.setMessage("ȷ���˳���");
                    builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    builder.setNegativeButton("����", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    });
                    builder.show();

                }
            }
        } else {
            openButton.setText("連線");
            clearButton.setEnabled(false);
            isOpen = false;
            try {
                sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            MyApp.driver.CloseDevice();
        }
    }


    private class readThread extends Thread {

        public void run() {

            byte[] buffer = new byte[4096];

            while (true) {

                Message msg = Message.obtain();
                if (!isOpen) {
                    break;
                }
                int length = MyApp.driver.ReadData(buffer, 4096);
                if (length > 0) {
                    String recv = toHexString(buffer, length);		//��16�������
                    msg.obj = recv;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private String toHexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }

    private void Add_data_to_sql(final String date, final String sp_str, final String dp_str, final String hr_str){
        //FREDTEST SQL+++
        StringRequest stringRequest = new StringRequest(Request.Method.POST, reg_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("FREDTEST1: ", response);
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");
                        } catch (JSONException e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //Log.d("FREDTEST","Add data and temp");

                if(!Systolic_global_data.equals("") && !Diastolic_global_data.equals("") && !HR_global_data.equals("")) {
                    params.put("name", mDeviceName);

                    //params.put("id",id);

                    params.put("pid", user_pid);
                    params.put("date", date);
                    //params.put("pressure",pressure);
                    params.put("sp", sp_str);//test
                    params.put("dp", dp_str);//test
                    params.put("hr", hr_str);//test
                }
                return params;
            }
        };
        MySingleton.getmInstance(MainActivity.this).addToRequestque(stringRequest);
        //FREDTEST SQL---
    }

    private byte[] toByteArray(String arg) {
        if (arg != null) {
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[] {};
    }
}
