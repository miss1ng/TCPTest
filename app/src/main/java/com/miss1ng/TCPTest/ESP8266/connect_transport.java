package com.miss1ng.TCPTest.ESP8266;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class connect_transport {

	public static DataInputStream bInputStream = null;
	public static DataOutputStream bOutputStream =null;
	public static Socket socket = null;
	private byte[] rbyte = new byte[40];
	private Handler reHandler;



	public short TYPE=0xAA;
	public short MAJOR = 0x00;
	public short FIRST = 0x00;
	public short SECOND = 0x00;
	public short THRID = 0x00;
	public short CHECKSUM=0x00;
	private byte[] msg = new byte[6000];

	public void destory(){
		try {
			if(socket!=null&&!socket.isClosed()){
				socket.close();
				bInputStream.close();
				bOutputStream.close();
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void connect(Handler reHandler ,String IP,int port) {
		try {
            this.reHandler =reHandler;
			socket = new Socket(IP, port);
			bInputStream = new DataInputStream(socket.getInputStream());
			bOutputStream = new DataOutputStream(socket.getOutputStream());
			reThread.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Thread reThread = new Thread(new Runnable() {
		@Override
		public void run() {
			// TODO Auto1-generated method stub
			while (socket != null && !socket.isClosed()) {
				try{
					//////////////////////
					String result = bytesToString(readFromInputStream(bInputStream)); //通过从输入流读取数据，返回给result
					if (!result.equals("")) {
						Message msg = new Message();  //获取�?��消息
						msg.what = 1;                 //设置message的what属�?的�?
						msg.obj = rbyte;
						Bundle data = new Bundle();
						data.putString("msg", result);
						msg.setData(data);
						reHandler.sendMessage(msg);   //发�?消息

					}
				} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
		}
	});
/////////////////////////////////////////////////////
public byte[] readFromInputStream(InputStream in) {
	int count = 0;
	byte[] inDatas = null;
	try {
		while (count == 0) {
			count = in.available();
		}
		inDatas = new byte[count];
		in.read(inDatas);
	} catch (Exception e) {
		e.printStackTrace();
	}
	return inDatas ;
}


	public String getHexString(String send) {
		String s = send;
		int i;
		StringBuilder sb = new StringBuilder();
		for ( i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (('0' <= c && c <= '9') || ('a' <= c && c <= 'f') ||
					('A' <= c && c <= 'F')) {
				sb.append(c);
			}
		}
		if ((sb.length() % 2) != 0) {
			sb.deleteCharAt(sb.length());
		}
		return sb.toString();
	}

	public byte[] stringToBytes(String s) {
		byte[] buf = new byte[s.length() / 2];
		for (int i = 0; i < buf.length; i++) {
			try {
				buf[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

		}
		return buf;
	}
	public String bytesToString(byte[] bytes) {
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		StringBuilder sb = new StringBuilder();
		try{
			for (int i = 0; i < bytes.length; i++) {
				int v = bytes[i] & 0xFF;
				hexChars[i * 2] = hexArray[v >>> 4];
				hexChars[i * 2 + 1] = hexArray[v & 0x0F];

				sb.append(hexChars[i * 2]);
				sb.append(hexChars[i * 2 + 1]);
				sb.append(' ');
			}}
		catch (NumberFormatException e) {
			e.printStackTrace();
		};
		return sb.toString();
	}


	public void send( String X)
	{
		CHECKSUM=(short) ((MAJOR+FIRST+SECOND+THRID)%256);
		final byte[] sbyte= stringToBytes( getHexString(X));
		 MainActivity.executorServicetor.execute(new Runnable() {  //开启线程，传输数据
			 @Override
			 public void run() {
				 // TODO Auto-generated method stub
				 try{
					 if (socket != null && !socket.isClosed()) {

						 bOutputStream.write(sbyte, 0, sbyte.length);
						 bOutputStream.flush();
					 }
				 } catch (IOException e) {
					 e.printStackTrace();
				 }
			 }
		 });
	}


	public void send_voice(final byte [] textbyte) {
                    MainActivity.executorServicetor.execute(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								if (socket != null && !socket.isClosed()) {
									bOutputStream.write(textbyte, 0, textbyte.length);
									bOutputStream.flush();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
	}

	// 沉睡
	public void yanchi(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
