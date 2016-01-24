package edu.buffalo.cse.cse486586.groupmessenger1;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT_BASE = "11108";
    static final int REMOTE_NUM = 5;
    static final int REMOTE_PORTGAP = 4;
    static final int SERVER_PORT = 10000;
    static int recvNum = 0;
    static Uri mUri;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * Calculate the port number that this AVD listens on.
         * It is just a hack that I came up with to get around the networking limitations of AVDs.
         * The explanation is provided in the PA1 spec.
         */

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority("edu.buffalo.cse.cse486586.groupmessenger1.provider");
        uriBuilder.scheme("content");
        mUri = uriBuilder.build();
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        try {
            /*
             * Create a server socket as well as a thread (AsyncTask) that listens on the server
             * port.
             *
             * AsyncTask is a simplified thread construct that Android provides. Please make sure
             * you know how it works by reading
             * http://developer.android.com/reference/android/os/AsyncTask.html
             */
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        } catch (IOException e) {
            /*
             * Log is a good way to debug your code. LogCat prints out all the messages that
             * Log class writes.
             *
             * Please read http://developer.android.com/tools/debugging/debugging-projects.html
             * and http://developer.android.com/tools/debugging/debugging-log.html
             * for more information on debugging.
             */
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        final EditText editText = (EditText) findViewById(R.id.editText1);
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString() + "\n";
                editText.setText("");
                tv.append("SendToAll:"+msg);
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
                Log.e(TAG,msg);
                return;
            }
        });
    }
    //all network parts are based on PA1
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        @Override
        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];
            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            try {
                while (true) {
                    Socket mSocket = serverSocket.accept();
                    BufferedReader mBufferReader = new BufferedReader(new InputStreamReader(
                            mSocket.getInputStream()));
                    String msg;
                    if ((msg = mBufferReader.readLine()) != null) {
                        publishProgress(msg);
                    }
                    mSocket.close();
                }
            }catch (IOException e){
                Log.e(TAG, "Can't accept a client");
            }



            return null;
        }

        protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String strReceived = strings[0].trim();
            tv.append("RECV:" + "key(" + recvNum + "), " + "value(" + strReceived + ")\n");

            /*
             * TODO:Content Provider insert
             */
            ContentValues cv = new ContentValues();
            cv.put("key",Integer.toString(recvNum));
            cv.put("value",strReceived);
            getContentResolver().insert(mUri,cv);
            recvNum++;
            return;
        }
    }

    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            String remotePort = REMOTE_PORT_BASE;
            for(int i=0;i<REMOTE_NUM;i++){
                try {
                    Log.e(TAG, "sendto:"+remotePort);

                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(remotePort));

                    String msgToSend = msgs[0];
                    /*
                     * TODO: Fill in your client code that sends out a message.
                     */

                    PrintWriter mPrintWriter = new PrintWriter(socket.getOutputStream());
                    mPrintWriter.print(msgToSend);
                    mPrintWriter.flush();
                    socket.close();
                    remotePort = String.valueOf((Integer.parseInt(remotePort) + REMOTE_PORTGAP));
                } catch (UnknownHostException e) {
                    Log.e(TAG, "ClientTask UnknownHostException");
                    remotePort = String.valueOf((Integer.parseInt(remotePort) + REMOTE_PORTGAP));
                } catch (IOException e) {
                    Log.e(TAG, "ClientTask socket IOException");
                    remotePort = String.valueOf((Integer.parseInt(remotePort) + REMOTE_PORTGAP));
                }
            }

            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
