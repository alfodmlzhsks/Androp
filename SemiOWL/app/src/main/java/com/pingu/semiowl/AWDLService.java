package com.pingu.semiowl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class AWDLService extends Service {
    public AWDLService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String command = intent.getExtras().getString("command");

        new Thread(new Runnable() {
            @Override
            public void run() {
                runCommand(command);
            }
        }).start();

        return START_NOT_STICKY;
    }

    public void runCommand(String command) throws RuntimeException {
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

            while(scanner.hasNext()) {
                String result = scanner.next();
                Log.i("tqtq", result);
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
}
