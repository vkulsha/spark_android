package com.example.bluetooth2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;

import com.example.bluetooth2.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CheckBox;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
  private static final String TAG = "bluetooth2";
   
//  Button btnOn, btnOff;
//  TextView txtArduino;
  ToggleButton b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12;
  CheckBox chClientVersion;
  Handler h;
  byte[] data = new byte[2]; 
  
  private static final int REQUEST_ENABLE_BT = 1;
  final int RECIEVE_MESSAGE = 1;		// Статус для Handler
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private StringBuilder sb = new StringBuilder();
  
  private ConnectedThread mConnectedThread;
   
  // SPP UUID сервиса 
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
 
  // MAC-адрес Bluetooth модуля 000EEACF1D41
//private static String address = "00:0E:EA:CF:1D:41";  
private static String address = "98:D3:31:80:5B:FF";  

  private final int SERVER_PORT = 4567;
  private final String SERVER_ADDRESS = "192.168.0.100";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
 
    setContentView(R.layout.activity_main);

   	new Thread(new Runnable() {
	   public void run() {
	    try {
	     //Create a server socket object and bind it to a port
	     ServerSocket socServer = new ServerSocket(SERVER_PORT);
	     //Create server side client socket reference
	     Socket socClient = null;
	     //Infinite loop will listen for client requests to connect
	     while (true) {
	      //Accept the client connection and hand over communication to server side client socket
	      socClient = socServer.accept();
	      //For each client new instance of AsyncTask will be created
	      ServerAsyncTask serverAsyncTask = new ServerAsyncTask();
	      //Start the AsyncTask execution 
	      //Accepted client socket object will pass as the parameter
	      serverAsyncTask.execute(new Socket[] {socClient});
	     }
	    } catch (IOException e) {
	     e.printStackTrace();
	    }
	   }
   	}).start();  
    
    b1 = (ToggleButton) findViewById(R.id.toggleButton1);					
    b2 = (ToggleButton) findViewById(R.id.toggleButton2);					
    b3 = (ToggleButton) findViewById(R.id.toggleButton3);					
    b4 = (ToggleButton) findViewById(R.id.toggleButton4);					
    b5 = (ToggleButton) findViewById(R.id.toggleButton5);					
    b6 = (ToggleButton) findViewById(R.id.ToggleButton01);					
    b7 = (ToggleButton) findViewById(R.id.ToggleButton02);					
    b8 = (ToggleButton) findViewById(R.id.ToggleButton03);					
    b9 = (ToggleButton) findViewById(R.id.ToggleButton06);					
    b10 = (ToggleButton) findViewById(R.id.ToggleButton05);					
    b11 = (ToggleButton) findViewById(R.id.ToggleButton04);					
    b12 = (ToggleButton) findViewById(R.id.ToggleButton07);
    chClientVersion = (CheckBox) findViewById(R.id.chClientVersion);
    
    h = new Handler() {
    	@Override
		public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
            case RECIEVE_MESSAGE:													// если приняли сообщение в Handler
            	byte[] readBuf = (byte[]) msg.obj;
            	String strIncom = new String(readBuf, 0, msg.arg1);
            	sb.append(strIncom);												// формируем строку
            	int endOfLineIndex = sb.indexOf("\r\n");							// определяем символы конца строки
            	if (endOfLineIndex > 0) { 											// если встречаем конец строки,
            		String sbprint = sb.substring(0, endOfLineIndex);				// то извлекаем строку
                    sb.delete(0, sb.length());										// и очищаем sb
                }
            	break;
    		}
        };
	};
     
    btAdapter = BluetoothAdapter.getDefaultAdapter();		// получаем локальный Bluetooth адаптер
    checkBTState();

    b1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	if (chClientVersion.isChecked()) {
        		ClientAsyncTask clientAST = new ClientAsyncTask();
               	clientAST.execute(new String[] { SERVER_ADDRESS, String.valueOf(SERVER_PORT),"b1"+(isChecked?"on":"off") });
        	} else {
	        	if (isChecked) {
	        		data[0] = 13;
	        		data[1] = 0;
	            } else {
	        		data[0] = 13;
	        		data[1] = 1;
	            }
	           	mConnectedThread.write(data);
        	}

        }
    });       

    b2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	if (chClientVersion.isChecked()) {
        		ClientAsyncTask clientAST = new ClientAsyncTask();
               	clientAST.execute(new String[] { SERVER_ADDRESS, String.valueOf(SERVER_PORT),"b2"+(isChecked?"on":"off") });
        	} else {
	    		if (isChecked) {
	        		data[0] = 12;
	        		data[1] = 0;
	            } else {
	        		data[0] = 12;
	        		data[1] = 1;
	            }
	           	mConnectedThread.write(data);
        	}
        }
    });       

    b3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	if (chClientVersion.isChecked()) {
        		ClientAsyncTask clientAST = new ClientAsyncTask();
               	clientAST.execute(new String[] { SERVER_ADDRESS, String.valueOf(SERVER_PORT),"b3"+(isChecked?"on":"off") });
        	} else {
	    		if (isChecked) {
	        		data[0] = 11;
	        		data[1] = 0;
	            } else {
	        		data[0] = 11;
	        		data[1] = 1;
	            }
	           	mConnectedThread.write(data);
        	}
        }
    });       

    b4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	if (chClientVersion.isChecked()) {
        		ClientAsyncTask clientAST = new ClientAsyncTask();
               	clientAST.execute(new String[] { SERVER_ADDRESS, String.valueOf(SERVER_PORT),"b4"+(isChecked?"on":"off") });
        	} else {
	    		if (isChecked) {
	        		data[0] = 10;
	        		data[1] = 0;
	            } else {
	        		data[0] = 10;
	        		data[1] = 1;
	            }
	           	mConnectedThread.write(data);
        	}
        }
    });       

    b5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    		if (isChecked) {
        		data[0] = 9;
        		data[1] = 0;
            } else {
        		data[0] = 9;
        		data[1] = 1;
            }
           	mConnectedThread.write(data);
        }
    });

    b6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    		if (isChecked) {
    			b7.setChecked(false);
    			b8.setChecked(false);
    			b1.setVisibility(View.VISIBLE);
    			b2.setVisibility(View.VISIBLE);
    			b3.setVisibility(View.VISIBLE);
    			b4.setVisibility(View.VISIBLE);
    			b5.setVisibility(View.INVISIBLE);
    			b12.setVisibility(View.INVISIBLE);
    			b9.setVisibility(View.INVISIBLE);
    			b10.setVisibility(View.INVISIBLE);
    			b11.setVisibility(View.INVISIBLE);
            } else {
            }
        }
    });

    b7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    		if (isChecked) {
    			b6.setChecked(false);
    			b8.setChecked(false);
    			b1.setVisibility(View.INVISIBLE);
    			b2.setVisibility(View.INVISIBLE);
    			b3.setVisibility(View.INVISIBLE);
    			b4.setVisibility(View.INVISIBLE);
    			b5.setVisibility(View.INVISIBLE);
    			b12.setVisibility(View.INVISIBLE);
    			b9.setVisibility(View.VISIBLE);
    			b10.setVisibility(View.VISIBLE);
    			b11.setVisibility(View.VISIBLE);
            } else {
            }
        }
    });

    b8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    		if (isChecked) {
    			b7.setChecked(false);
    			b6.setChecked(false);
    			b1.setVisibility(View.INVISIBLE);
    			b2.setVisibility(View.INVISIBLE);
    			b3.setVisibility(View.INVISIBLE);
    			b4.setVisibility(View.INVISIBLE);
    			b5.setVisibility(View.VISIBLE);
    			b12.setVisibility(View.VISIBLE);
    			b9.setVisibility(View.INVISIBLE);
    			b10.setVisibility(View.INVISIBLE);
    			b11.setVisibility(View.INVISIBLE);
            } else {
            }
        }
    });
   }

  @Override
  public void onResume() {
    super.onResume();
 
//    Log.d(TAG, "...onResume - попытка соединения...");
   
    // Set up a pointer to the remote node using it's address.
    BluetoothDevice device = btAdapter.getRemoteDevice(address);
   
    // Two things are needed to make a connection:
    //   A MAC address, which we got above.
    //   A Service ID or UUID.  In this case we are using the
    //     UUID for SPP.
    try {
      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
    }
   
    // Discovery is resource intensive.  Make sure it isn't going on
    // when you attempt to connect and pass your message.
    btAdapter.cancelDiscovery();
   
    // Establish the connection.  This will block until it connects.
//    Log.d(TAG, "...Соединяемся...");
    try {
      btSocket.connect();
//      Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
    } catch (IOException e) {
      try {
        btSocket.close();
      } catch (IOException e2) {
        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
      }
    }
     
    // Create a data stream so we can talk to server.
//    Log.d(TAG, "...Создание Socket...");
   
    mConnectedThread = new ConnectedThread(btSocket);
    mConnectedThread.start();
  }
 
  @Override
  public void onPause() {
    super.onPause();
 
//    Log.d(TAG, "...In onPause()...");
  
    try     {
      btSocket.close();
    } catch (IOException e2) {
      errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
    }
  }
   
  private void checkBTState() {
    // Check for Bluetooth support and then check to make sure it is turned on
    // Emulator doesn't support Bluetooth and will return null
    if(btAdapter==null) { 
      errorExit("Fatal Error", "Bluetooth не поддерживается");
    } else {
      if (btAdapter.isEnabled()) {
//        Log.d(TAG, "...Bluetooth включен...");
      } else {
        //Prompt user to turn on Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      }
    }
  }
 
  private void errorExit(String title, String message){
    Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
    finish();
  }
 
  private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    @Override
		public void run() {
	        byte[] buffer = new byte[256];  // buffer store for the stream
	        int bytes; // bytes returned from read()

	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	        	try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);		// Получаем кол-во байт и само собщение в байтовый массив "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Отправляем в очередь сообщений Handler
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(String message) {
//	    	Log.d(TAG, "...Данные для отправки: " + message + "...");
	    	byte[] msgBuffer = message.getBytes();
	    	try {
	            mmOutStream.write(msgBuffer);
	        } catch (IOException e) {
//	            Log.d(TAG, "...Ошибка отправки данных: " + e.getMessage() + "...");     
	          }
	    }
	    
	    public void write(byte[] message) {
//	    	Log.d(TAG, "...Данные для отправки: " + message + "...");
	    	try {
	            mmOutStream.write(message);
	        } catch (IOException e) {
//	            Log.d(TAG, "...Ошибка отправки данных: " + e.getMessage() + "...");     
	          }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
  
  class ServerAsyncTask extends AsyncTask<Socket, Void, String> {
	  //Background task which serve for the client
	  @Override
	  protected String doInBackground(Socket... params) {
	   String result = null;
	   //Get the accepted socket object 
	   Socket mySocket = params[0];
	   try {
	    //Get the data input stream comming from the client 
	    InputStream is = mySocket.getInputStream();
	    //Get the output stream to the client
	    PrintWriter out = new PrintWriter(
	      mySocket.getOutputStream(), true);
	    //Write data to the data output stream
	    out.println("Hello from server");
	    //Buffer the data input stream
	    BufferedReader br = new BufferedReader(
	      new InputStreamReader(is));
	    //Read the contents of the data buffer
	    result = br.readLine();
	    //Close the client connection
	    mySocket.close();
	   } catch (IOException e) {
	    e.printStackTrace();
	   }
	   return result;
	  }

	  @Override
	  protected void onPostExecute(String s) {
	   //After finishing the execution of background task data will be write the text view
	   //Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
		  if (s.startsWith("b1"))
			  b1.setChecked(s.startsWith("b1on"));
		  if (s.startsWith("b2"))
			  b2.setChecked(s.startsWith("b2on"));
		  if (s.startsWith("b3"))
			  b3.setChecked(s.startsWith("b3on"));
		  if (s.startsWith("b4"))
			  b4.setChecked(s.startsWith("b4on"));
		  
	  }
  }

  class ClientAsyncTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
     String result = null;
     try {
      //Create a client socket and define internet address and the port of the server
      Socket socket = new Socket(params[0],
        Integer.parseInt(params[1]));
      //Get the input stream of the client socket
      InputStream is = socket.getInputStream();
      //Get the output stream of the client socket
      PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
      //Write data to the output stream of the client socket
      out.println(params[2]); 
      //Buffer the data coming from the input stream
      BufferedReader br = new BufferedReader(
        new InputStreamReader(is));
      //Read data in the input buffer
      result = br.readLine();
      //Close the client socket
      socket.close();
     } catch (NumberFormatException e) {
      e.printStackTrace();
//     } catch (UnknownHostException e) {
//      e.printStackTrace();
     } catch (IOException e) {
      e.printStackTrace();
     }
     return result;
    }
    @Override
    protected void onPostExecute(String s) {
     //Write server message to the text view
    }
   }  
  
}