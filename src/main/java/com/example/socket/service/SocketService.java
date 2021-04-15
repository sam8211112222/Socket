package com.example.socket.service;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
@Service
public class SocketService extends HttpServlet {
    private static boolean open = false;
        Socket socket;
    public void connect(String address,int port,HttpSession session) throws UnknownHostException {
        String data;
        BufferedReader key_in = new BufferedReader(new InputStreamReader(	System.in));
        BufferedReader s_in;
        PrintWriter s_out;
        InetAddress iNetAddress = InetAddress.getLocalHost();
        String hostname = iNetAddress.getHostName();
        try {
            // 呼叫此建構子的同時,即根據參數的IP與PORT去連接ServerSocket
            socket = new Socket(address, port);
            open=true;
            System.out.println(socket);
            s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            s_out = new PrintWriter(socket.getOutputStream());
            if(open) {
                session.setAttribute("status","連線中");
                System.out.println("連線成功");
            }else{
                session.setAttribute("status","無法連線");
            }
        } catch (IOException ioe) {
            System.out.println("無法連接主機...");
        }
    }
    public void close(HttpSession session) throws IOException {
        if(session.getAttribute("status")!=null) {
            socket.close();
            session.setAttribute("transaction",null);
            session.setAttribute("status", null);
            System.out.println("關閉連線");
        }
    }
    public String send(String a) throws IOException {
        BufferedReader s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter s_out = new PrintWriter(socket.getOutputStream());
        s_out.println(a);
        s_out.flush();
        return s_in.readLine();
    }
    public void transaction(String type) throws IOException {
        PrintWriter s_out = new PrintWriter(socket.getOutputStream());
        s_out.println(type);
        s_out.flush();
    }

}
