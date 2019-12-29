package com.pingu.semiowl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Scanner;

public class AirDropActivity extends AppCompatActivity {

    ListView lvUsers;
    Button btnFind;
    TextView tvTest;

    NsdManager.RegistrationListener registrationListener;
    NsdManager.DiscoveryListener discoveryListener;
    NsdManager.ResolveListener resolveListener;
    NsdManager nsdManager;
    NsdServiceInfo mService;
    String serviceName;
    String SERVICE_TYPE = "_airdrop._tcp.";
    String TAG = "airdrop";
    InetAddress awdl = null;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airdrop_drop);

        lvUsers = (ListView)findViewById(R.id.lvUsers);
        btnFind = (Button)findViewById(R.id.btnFind);

        try{
            Enumeration<NetworkInterface> interfaceEnum = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface element : Collections.list(interfaceEnum)) {
//                System.out.println("" + element.getName() + " : " + element.getDisplayName());
                Enumeration<InetAddress> address = element.getInetAddresses();
                for(InetAddress inetAddress : Collections.list(address)) {
                    if(inetAddress.getHostAddress().contains("awdl0")) {
                        awdl = inetAddress;
//                        System.out.println("InetAddress : " + inetAddress.getHostAddress() + ", host : " + inetAddress.getHostName());
                    }
                }
            }
        }catch(SocketException ex) {}

        initializeRegistrationListener();
        initializeDiscoveryListener();
        initializeResolveListener();
        registerService(5353, awdl);

        adapter = new UserAdapter();
        lvUsers.setAdapter(adapter);

        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User)adapterView.getAdapter().getItem(i);
                Intent option = new Intent(AirDropActivity.this, AttackOptionActivity.class);
                option.putExtra("ip", user.getName());
                startActivity(option);
            }
        });

//        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);

        //click find
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                device_find();
            }
        });
    }

    private void awdlTCPTest(final InetAddress addr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkInterface nif = null;
                try {
                    nif = NetworkInterface.getByName("awdl0");
                    Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();
                    Socket soc = new Socket();
                    soc.bind(new InetSocketAddress(nifAddresses.nextElement(), 50481));
                    soc.connect(new InetSocketAddress(addr, 8771));
                    if(soc.isConnected()) {
                        Log.i("tqtq", "1");
                    } else {
                        Log.i("tqtq", "2");
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void setUsers() {

//        adapter.addUser(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mush), "비둘기");
//        adapter.addUser(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mush), "갈매기");
//        adapter.addUser(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mush), "닭");
//        adapter.addUser(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mush), "꿩");
//        adapter.addUser(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mush), "참새");
    }

     private void device_find() {
//         new Thread(new Runnable() {
//             @Override
//             public void run() {
//                    runCommand("/system/bin/opendrop find\n");
//             }
//         }).start();
//         adapter.addUser(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mush), "ssss");
//         adapter.notifyDataSetChanged();
         nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void initializeRegistrationListener() {
        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                serviceName = NsdServiceInfo.getServiceName();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed! Put debugging code here to determine why.
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed. Put debugging code here to determine why.
            }
        };
    }

    public void registerService(int port, InetAddress host) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName("airdrop");
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setHost(host);
        serviceInfo.setPort(port);

        nsdManager = (NsdManager)getSystemService(Context.NSD_SERVICE);
        nsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        discoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(final NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                Log.d(TAG, "sm: " + service.getServiceName());
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(serviceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + serviceName);
                } else if (!service.getServiceName().contains("airdrop")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            nsdManager.resolveService(service, resolveListener);
                        }
                    }).start();
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void initializeResolveListener() {
        resolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(serviceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                int port = mService.getPort();
                final InetAddress host = mService.getHost();


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0; i<adapter.getCount(); i++) {
                            User user = (User)adapter.getItem(i);
                            if(user.getName().equals(host.getHostAddress())) {
                                return;
                            }
                        }
                        adapter.addUser(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_icon), host.getHostAddress());
                        adapter.notifyDataSetChanged();
                        awdlTCPTest(host);
                    }
                });

                Log.i("tqtq", "host: " + host.getHostAddress() + ", port: " + port + ", name: " + host.getHostName());

            }
        };
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
                final String result = scanner.next();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTest.append(result);
                    }
                });
                Log.i("opendrop_log", result);
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
