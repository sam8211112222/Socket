package com.example.socket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    BufferedReader s_in;
    PrintWriter s_out;
    private static final Logger logger = LoggerFactory.getLogger(SocketService.class);
    //與server連線
    public void connect(String address, int port, HttpSession session) throws UnknownHostException {
        String data;
        BufferedReader key_in = new BufferedReader(new InputStreamReader(System.in));
        InetAddress iNetAddress = InetAddress.getLocalHost();
        String hostname = iNetAddress.getHostName();
        try {
            // 呼叫此建構子的同時,即根據參數的IP與PORT去連接ServerSocket
            socket = new Socket(address, port);
            open = true;
            if (open) {
                session.setAttribute("status", "連線中");
                logger.info("連線成功");
                System.out.println("連線成功");
            } else {
                session.setAttribute("status", "無法連線");
                logger.info("無法連線");
            }
        } catch (IOException ioe) {
            System.out.println("無法連接主機...");
        }
    }
    //關閉連線
    public void close(HttpSession session) throws IOException {
        if (session.getAttribute("status") != null) {
            socket.close();
            session.setAttribute("transaction", null);
            session.setAttribute("status", null);
            System.out.println("關閉連線");
            logger.info("關閉連線");
        }
    }
    //送出String
    public String send(String a) throws IOException {
        s_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        s_out = new PrintWriter(socket.getOutputStream());
        s_out.println(a);
        logger.info("傳送字串: "+a);
        s_out.flush();
        logger.info("接收資料: "+s_in.readLine());
        return s_in.readLine();
    }
    //送出交易代碼
    public void transaction(String type) throws IOException {
        s_out = new PrintWriter(socket.getOutputStream());
        s_out.println(type);
        logger.info("交易代碼: "+type);
        s_out.flush();
    }
}
