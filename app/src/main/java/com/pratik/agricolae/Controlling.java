package com.pratik.agricolae;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

public class Controlling extends Activity {
    private static final String TAG = "BlueTest5-Controlling";
    private int mMaxChars = 50000;//Default//change this to string..........
    private UUID mDeviceUUID;
    private BluetoothSocket mBTSocket;
    private ReadInput mReadThread = null;
    int speed_bot= 5;
    private boolean mIsUserInitiatedDisconnect = false;
    private boolean mIsBluetoothConnected = false;

    // All controls here
    private TextView mTxtReceive,botstauts;
    private Button mBtnClearInput;
    LottieAnimationView lottieAnimationView;
    private ScrollView scrollView;
    private CheckBox chkScroll;
    private CheckBox chkReceiveText;
    private Button mBtnDisconnect;
    private BluetoothDevice mDevice;

    final static String forward = "1";
    final static String back = "2";
    final static String right = "4";
    final static String left = "3";
    final static String stop = "5";
    final static String drill= "8";
    final static String seed= "6";
    final static String pattern_move= "7";

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlling);
        ActivityHelper.initialize(this);


        hook();
        lottieAnimationView.setAnimation("overall.json");
        lottieAnimationView.playAnimation();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.DEVICE_EXTRA);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        mMaxChars = b.getInt(MainActivity.BUFFER_SIZE);

        Log.d(TAG, "Ready");

        mTxtReceive.setMovementMethod(new ScrollingMovementMethod());

        mBtnClearInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mTxtReceive.setText("");
            }
        });


    }

    private void hook() {
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.lottie);
        mTxtReceive = (TextView) findViewById(R.id.txtReceive);
        botstauts= (TextView)findViewById(R.id.botstatus);
        chkScroll = (CheckBox) findViewById(R.id.chkScroll);
        chkReceiveText = (CheckBox) findViewById(R.id.chkReceiveText);
        scrollView = (ScrollView) findViewById(R.id.viewScroll);
        mBtnClearInput = (Button) findViewById(R.id.btnClearInput);
    }

    public void forward_move(View view) {
        botstauts.setText("Forward Moving");
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setAnimation("forward.json");
        lottieAnimationView.playAnimation();
        try {
            mBTSocket.getOutputStream().write(forward.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void back_move(View view) {
        botstauts.setText("Backward Moving");

        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setAnimation("backward.json");
        lottieAnimationView.playAnimation();
        try
        {
            mBTSocket.getOutputStream().write(back.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void right_move(View view) {
        botstauts.setText("Right Turn");

        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setAnimation("right.json");
        lottieAnimationView.playAnimation();
        try
        {
            mBTSocket.getOutputStream().write(right.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void left_move(View view) {
        botstauts.setText("Left Moving");

        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setAnimation("left.json");
        lottieAnimationView.playAnimation();
        try
        {
            mBTSocket.getOutputStream().write(left.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop_move(View view) {
        botstauts.setText("Idle");

        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setAnimation("overall.json");
        lottieAnimationView.playAnimation();
        try
        {
            mBTSocket.getOutputStream().write(stop.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drill_bot(View view) {
        botstauts.setText("Drilling");

        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setAnimation("seeding.json");
        lottieAnimationView.playAnimation();
        try
        {
            mBTSocket.getOutputStream().write(drill.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void seed_bot(View view) {
        botstauts.setText("Seeding");

        lottieAnimationView.cancelAnimation();
        lottieAnimationView.setAnimation("seeding.json");
        lottieAnimationView.playAnimation();
        try
        {
            mBTSocket.getOutputStream().write(seed.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pattern_move(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter TD & OD: ");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = pattern_move+" "+input.getText().toString();
                botstauts.setText("Pattern Movement");
                lottieAnimationView.cancelAnimation();
                lottieAnimationView.setAnimation("overall.json");
                lottieAnimationView.playAnimation();
                try
                {
                    mBTSocket.getOutputStream().write(str.getBytes());
                   // pattern_text(input.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

//    private void pattern_text(String s) {
//        String[] input = s.split("\\s+");
//        int od= Integer.parseInt(input[1]),td= Integer.parseInt(input[0]);
//        int wait = (int)od / speed_bot;
//        int flag = 1;
//        for (int j = 0; j < 2; j++) {
//            for (int i = 0; i < td; i += od) {
//                botstauts.setText("Forward");
//                delay(wait * 1000);
//
//                botstauts.setText("Drilling");
//                delay(5 * 1000);
//                botstauts.setText("Seeding");
//                delay(500);
//                
//            }
//                if (flag) {
//                turnRight();
//                delay(2000);
//                forword();
//                delay(wait * 1000);
//                turnRight();
//                delay(2000);
//                stop();
//                flag = 0;
//            }
//            else {
//                turnLeft();
//                delay(2000);
//                forword();
//                delay(wait * 1000);
//                turnLeft();
//                delay(2000);
//                stop();
//                flag = 1;
//            }
//        }
//    }

    private class ReadInput implements Runnable {

        private boolean bStop = false;
        private Thread t;
        public ReadInput() {
            t = new Thread(this, "Input Thread");
            t.start();
        }

        public boolean isRunning() {
            return t.isAlive();
        }

        @Override
        public void run() {
            InputStream inputStream;

            try {
                inputStream = mBTSocket.getInputStream();
                while (!bStop) {
                    byte[] buffer = new byte[256];
                    if (inputStream.available() > 0) {
                        inputStream.read(buffer);
                        int i = 0;
                        /*
                         * This is needed because new String(buffer) is taking the entire buffer i.e. 256 chars on Android 2.3.4 http://stackoverflow.com/a/8843462/1287554
                         */
                        for (i = 0; i < buffer.length && buffer[i] != 0; i++) {}
                        final String strInput = new String(buffer, 0, i);

                        if (chkReceiveText.isChecked()) {
                            mTxtReceive.post(new Runnable() {
                                @Override
                                public void run() {
                                    mTxtReceive.append(strInput);
                                    //botstauts.setText(strInput);

                                    int txtLength = mTxtReceive.getEditableText().length();
                                    if (txtLength > mMaxChars) {
                                        mTxtReceive.getEditableText().delete(0, txtLength - mMaxChars);
                                    }

                                    if (chkScroll.isChecked()) { // Scroll only if this is checked
                                        scrollView.post(new Runnable() { // Snippet from http://stackoverflow.com/a/4612082/1287554
                                            @Override
                                            public void run() {
                                                scrollView.fullScroll(View.FOCUS_DOWN);
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    }

                    Thread.sleep(500);
                }
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void stop() {
            bStop = true;
        }

    }

    private class DisConnectBT extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {//cant inderstand these dotss

            if (mReadThread != null) {
                mReadThread.stop();
                while (mReadThread.isRunning())
                    ; // Wait until it stops
                mReadThread = null;

            }

            try {
                mBTSocket.close();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mIsBluetoothConnected = false;
            if (mIsUserInitiatedDisconnect) {
                finish();
            }
        }

    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        if (mBTSocket != null && mIsBluetoothConnected) {
            new DisConnectBT().execute();
        }
        Log.d(TAG, "Paused");
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBTSocket == null || !mIsBluetoothConnected) {
            new ConnectBT().execute();
        }
        Log.d(TAG, "Resumed");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Stopped");
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
// TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean mConnectSuccessful = true;

        @Override
        protected void onPreExecute() {

            progressDialog = ProgressDialog.show(Controlling.this, "Hold on", "Connecting");// http://stackoverflow.com/a/11130220/1287554

        }

        @Override
        protected Void doInBackground(Void... devices) {

            try {
                if (mBTSocket == null || !mIsBluetoothConnected) {
                    if (ActivityCompat.checkSelfPermission(Controlling.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            ActivityCompat.requestPermissions(Controlling.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                        }
                    }
                    mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
// Unable to connect to device`
                // e.printStackTrace();
                mConnectSuccessful = false;



            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (!mConnectSuccessful) {
                Toast.makeText(getApplicationContext(), "Could not connect to device.Please turn on your Hardware", Toast.LENGTH_LONG).show();
                finish();
            } else {
                msg("Connected to device");
                mIsBluetoothConnected = true;
                mReadThread = new ReadInput(); // Kick off input reader
            }

            progressDialog.dismiss();
        }

    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}