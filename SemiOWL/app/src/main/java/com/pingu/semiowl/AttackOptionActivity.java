package com.pingu.semiowl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class AttackOptionActivity extends AppCompatActivity {

    InetAddress awdl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attack_option);

        new Thread(new Runnable() {
            @Override
            public void run() {
                startMDNS();
            }
        }).start();

    }

    private void startMDNS() {
        try {
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

//            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
//
//            // Register a service
//            ServiceInfo serviceInfo = ServiceInfo.create("_airdrop._tcp.local.", "airdrop", 5353, "path=index.html");
//            jmdns.registerService(serviceInfo);
//
//            // Wait a bit
//            Thread.sleep(25000);
//
//            // Unregister all services
//            jmdns.unregisterAllServices();

            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(awdl);

            // Add a service listener
            jmdns.addServiceListener("_airdrop._tcp.local.", new SampleListener());

            // Wait a bit
            Thread.sleep(3000);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
        }
    }
}
