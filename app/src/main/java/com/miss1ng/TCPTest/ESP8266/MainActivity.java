package com.miss1ng.TCPTest.ESP8266;

import android.content.Context;
import android.content.res.Configuration;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private byte[] mByte = new byte[11];
    private TextView Data_show_st =null;
    private EditText ipadress_st = null,ipcom_st=null,Data_send_st=null;
    private Button clear_receive_st,getClear_send_st,send_st,get_ip_st,connection_st,converStr_st;
    private connect_transport sock_con;
    private WifiManager wifiManager;
    // 服务器管理器
    private DhcpInfo dhcpInfo;
    private int wificonnection=0;
    static String IPadress;
    static ExecutorService executorServicetor = Executors.newCachedThreadPool();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        control_init();
        sock_con =new connect_transport();

    }

    // 转化十六进制编码为字符串
    public static String hexStr2Str(String s) {
        s = s.replaceAll(" ","");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "GBK");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }


    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    //WiFi初始化
    private void wifi_Init() {
        // 得到服务器的IP地址
        wifiManager = (WifiManager) getApplicationContext(). getSystemService(Context.WIFI_SERVICE);
        dhcpInfo = wifiManager.getDhcpInfo();
        IPadress = Formatter.formatIpAddress(dhcpInfo.gateway);
        ipadress_st.setText(IPadress);
    }



    private void control_init()
    {
        ipadress_st=(EditText) findViewById(R.id.ipadress);
        ipcom_st=(EditText)findViewById(R.id.com);
        Data_show_st =(TextView) findViewById(R.id.rvdata);
        Data_show_st.setMovementMethod(ScrollingMovementMethod.getInstance());
        Data_send_st =(EditText) findViewById(R.id.senddata);

        clear_receive_st=(Button)findViewById(R.id.reclear);
        converStr_st=(Button)findViewById(R.id.converStr);
        converStr_st.setEnabled(false);

        getClear_send_st=(Button)findViewById(R.id.sendclear);
        send_st=(Button)findViewById(R.id.sendto);

        connection_st=(Button)findViewById(R.id.buttonconnection);
        get_ip_st=(Button)findViewById(R.id.buttonip);

        clear_receive_st.setOnClickListener(new onClickListener());
        converStr_st.setOnClickListener(new onClickListener());
        getClear_send_st.setOnClickListener(new onClickListener());
        send_st.setOnClickListener(new onClickListener());
        connection_st.setOnClickListener(new onClickListener());
        get_ip_st.setOnClickListener(new onClickListener());

    }

    // 接受显示ESP8266发送的数据
    private Handler recvhandler = new Handler() {
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
              // mByte = (byte[]) msg.obj;
                String result = msg.getData().get("msg").toString();
                //result+=" ";
                //Data_show_st.append(result);
                Data_show_st.append(hexStr2Str(result));
                //

                // TODO: 对接收的单条数据分割处理并显示到独立的TextView中
            }
            //Data_show_st.append("\n");
        }
    };


    private class onClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {

            switch(v.getId())
            {
                case R.id.reclear:{
                    Data_show_st.setText(null);
                    //converStr_st.setEnabled(true);
                    break;
                }
                case R.id.converStr:{
                    String str = Data_show_st.getText().toString();
                    if (str==null||str.equals("")) {Toast.makeText(MainActivity.this,"null" , Toast.LENGTH_SHORT).show();break;}
                    converStr_st.setEnabled(false);
                    Toast.makeText(MainActivity.this,str , Toast.LENGTH_SHORT).show();
                    Data_show_st.setText(hexStr2Str(str));
                    break;
                }
                case R.id.sendclear:
                    Data_send_st.setText(null);
                    break;
                case R.id.sendto:
                    sock_con.send(Data_send_st.getText().toString());
                    break;
                case R.id.buttonconnection:
                    executorServicetor.execute(new Runnable() {
                        @Override
                        public void run() {
                         if(wificonnection==0)
                         {
                             try {
                                 sock_con.connect(recvhandler, ipadress_st.getText().toString(), Integer.valueOf(ipcom_st.getText().toString(), 10));

                                 connection_st.setText("断开");
                                 wificonnection=1;
                                 //connection_st.setEnabled(false);
                             }catch (Exception e) {
                                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                                 connection_st.setText("连接");

                             }
                         }
                         else {
                             sock_con.destory();
                             connection_st.setText("连接");
                             wificonnection=0;
                         }
                         }

                    });


                    break;
                case R.id.buttonip:
                    wifi_Init();
                    break;
                default:
                    break;

            }
        }
    }
    private class onLongClickListener2 implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {

            return true;
        }
    }


}
