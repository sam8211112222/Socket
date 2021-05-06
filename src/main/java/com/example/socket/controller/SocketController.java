package com.example.socket.controller;

import com.example.socket.service.MinaSocket;
import com.example.socket.service.SocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;


@Controller
public class SocketController extends HttpServlet {
    @Autowired
    SocketService socketService;
    @Autowired
    MinaSocket minaSocket;
    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);

    @GetMapping("/")
    public String index(HttpSession session) {
        session.setAttribute("status", null);
        return "index";
    }

    @PostMapping("/connect")
    public String connect(@RequestParam(value = "address", required = false) String address,
                          @RequestParam(value = "port", required = false) int port,
                          Model model, HttpSession session) throws UnknownHostException {
        minaSocket.connect(address, port, session);
        return "index";
    }

    @PostMapping("/close")
    public String close(Model model, HttpSession session) throws IOException {
        minaSocket.close(session);
        return "index";
    }

    @PostMapping("/send")
    public String send(@RequestParam(value = "io", required = false) String io, Model model, HttpSession session) throws Exception {
        if (session.getAttribute("status") != null) {
            logger.info("傳送: "+io);
            model.addAttribute("display", minaSocket.send(io));
            logger.info("接收: "+minaSocket.send(io));
        } else {
            model.addAttribute("fault", "  請先連線");
        }
        return "index";
    }

    @PostMapping("/transaction")
    public String transaction(@RequestParam(value = "transaction", required = false) String transaction, Model model, HttpSession session) throws Exception {
        if (session.getAttribute("status") != null) {
            if(!transaction.equals("BD")) {
                session.setAttribute("transaction", transaction);
                minaSocket.transaction(transaction);
                logger.info("交易代碼 : "+transaction);
            }else if(transaction.equals("BD")){
                session.setAttribute("transaction","");
                minaSocket.transaction(transaction);
                logger.info("關閉交易代碼");
            }
        } else {
            model.addAttribute("fault", "  請先連線");
        }
        return "index";
    }
}
