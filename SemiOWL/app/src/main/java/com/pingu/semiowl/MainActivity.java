package com.pingu.semiowl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public static final String TERMUX_PATH = "/data/data/com.termux/files/usr/bin/";

    ToggleButton btnAWDL;
    Button btnAirDrop;

    static ArrayList<String> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAWDL = (ToggleButton) findViewById(R.id.btnAWDL);
        btnAirDrop = (Button)findViewById(R.id.btnAirDrop);

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("Check Permission!!")
                .setDeniedMessage("Holy... shit...........")
                .setPermissions(Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        btnAWDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton)view).isChecked();

                if(on) {
                    list = new ArrayList<String>();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runCommand("/data/data/com.termux/files/home/find_interfaces\n");
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                            final String[] values = list.toArray(new String[list.size()]);

                            builder.setTitle("For AWDL Network Interfaces");
                            builder.setItems(values, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final int pos = i;

                                    // Set Interface Monitor Mode
                                    runCommand(TERMUX_PATH + "ifconfig " + values[pos] + " down\n");
                                    runCommand(TERMUX_PATH + "iwconfig " + values[pos] + " mode monitor\n");
                                    runCommand(TERMUX_PATH + "ifconfig " + values[pos] + " up\n");

                                    // Enable awdl0 interface
                                    Intent awdl = new Intent(MainActivity.this, AWDLService.class);
                                    awdl.putExtra("command", TERMUX_PATH + "owl -i " + values[pos] + " -v -N \n");
                                    startService(awdl);

//                                    Intent aa = new Intent(MainActivity.this, NsdTestActivity.class);
////                                    startActivity(aa);
//                                    Intent aa = new Intent(MainActivity.this, AirDropActivity.class);
                                    Intent aa = new Intent(MainActivity.this, AttackOptionActivity.class);
                                    startActivity(aa);
                                }
                            });

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        }
                    }).start();
                } else {
                    runCommand("kill -9 `ps | grep owl | grep -v 'grep' | awk '{print $2}'`\n");
                }
            }
        });

        btnAirDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AirDropActivity.class);
                startActivity(i);
            }
        });

    }

    public static void runCommand(String command) throws RuntimeException {
        try {
            Process p = null;

            p=Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(command);
            dos.writeBytes("exit\n");
            dos.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            Scanner scanner = new Scanner(br);
            scanner.useDelimiter(System.getProperty("line.separator"));

            if(command.equals("/data/data/com.termux/files/home/find_interfaces\n")) {
                while(scanner.hasNext()) {
                    String iface = scanner.next();

                    if(iface.contains("wlan") || iface.contains("br")) {
                        list.add(iface);
                    }
                }
            } else {
                while(scanner.hasNext()) {
                    String result = scanner.next();
                    Log.i("tqtq", result);
                }
            }

            scanner.close();
            br.close();
            dos.close();
            p.waitFor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

}
