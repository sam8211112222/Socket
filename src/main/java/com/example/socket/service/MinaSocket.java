package com.example.socket.service;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Service
public class MinaSocket extends IoHandlerAdapter {
    private IoConnector connector;
    private static IoSession iosession;
    private String turn = null;
    private static final Logger logger = Logger.getLogger(SocketService.class);

    //與server連線
    public void connect(String address, int port, HttpSession session) throws UnknownHostException {
        try {
            //新增一個連線
            connector = new NioSocketConnector();
            connector.setHandler(this);
            //連線IP跟port
            ConnectFuture connFuture = connector.connect(new InetSocketAddress(address, port));
            //斷線後不報錯
            connFuture.awaitUninterruptibly();
            iosession = connFuture.getSession();
            session.setAttribute("status", "連線中");
            logger.info("連線成功");
            System.out.println("連線成功");
        } catch (Exception e) {
            session.setAttribute("status", "無法連線到主機");
            logger.info("連線主機失敗");
        }
    }

    //關閉連線
    public void close(HttpSession session) throws IOException {
        if (session.getAttribute("status") != null) {
            session.setAttribute("transaction", null);
            session.setAttribute("status", null);
            session.setAttribute("display",null);
            connector.dispose(true);
            logger.info("關閉連線");
        }
    }

    //送出String，並接收
    public String send(String str,HttpSession session) throws Exception {
        if(str.equals("BD")||str.equals("bd")){
            this.close(session);
        }
        //先將string轉成byte利用IoBuffer傳送
        iosession.write(IoBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)));
        //休息一秒
        Thread.sleep(1000);
        return turn;
    }

    //送出交易代碼
    public void transaction(String type) throws Exception {
        //先將string轉成byte利用IoBuffer傳送
        iosession.write(IoBuffer.wrap(type.getBytes(StandardCharsets.UTF_8)));
        Thread.sleep(1000);
        logger.info("交易代碼: " + type);
    }

    @Override
    public void messageReceived(IoSession iosession, Object message)
            throws Exception {
        IoBuffer bbuf = (IoBuffer) message;
        turn = new String(bbuf.array(), StandardCharsets.UTF_8);
        System.out.println("客户端收到消息" + turn);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        System.out.println("client exception");
        super.exceptionCaught(session, cause);
    }

    @Override
    public void messageSent(IoSession iosession, Object obj) throws Exception {
        System.out.println("client messaging");
        super.messageSent(iosession, obj);
    }

    @Override
    public void sessionClosed(IoSession iosession) throws Exception {
        System.out.println("client session closed");
        super.sessionClosed(iosession);
    }

    @Override
    public void sessionCreated(IoSession iosession) throws Exception {
        System.out.println("Client Session Creation");
        super.sessionCreated(iosession);
    }

    @Override
    public void sessionIdle(IoSession iosession, IdleStatus idlestatus)
            throws Exception {
        System.out.println("client session hibernation");
        super.sessionIdle(iosession, idlestatus);
    }

    @Override
    public void sessionOpened(IoSession iosession) throws Exception {
        System.out.println("client session open");
        super.sessionOpened(iosession);
    }
}
