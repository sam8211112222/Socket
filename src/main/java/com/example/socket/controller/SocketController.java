package com.example.socket.controller;

import com.example.socket.service.SocketService;
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

    @GetMapping("/")
    public String index(HttpSession session) {
        session.setAttribute("status", null);
        return "index";
    }

    @PostMapping("/connect")
    public String connect(@RequestParam(value = "address", required = false) String address,
                          @RequestParam(value = "port", required = false) int port,
                          Model model, HttpSession session) throws UnknownHostException {
        socketService.connect(address, port, session);
        return "index";
    }

    @PostMapping("/close")
    public String close(Model model, HttpSession session) throws IOException {
        socketService.close(session);
        return "index";
    }

    @PostMapping("/send")
    public String send(@RequestParam(value = "io", required = false) String io, Model model, HttpSession session) throws IOException {
        if (session.getAttribute("status") != null) {
            model.addAttribute("display", socketService.send(io));
        } else {
            model.addAttribute("fault", "  請先連線");
        }
        return "index";
    }

    @PostMapping("/transaction")
    public String transaction(@RequestParam(value = "transaction", required = false) String transaction, Model model, HttpSession session) throws IOException {
        if (session.getAttribute("status") != null) {
            session.setAttribute("transaction", transaction);
            socketService.transaction(transaction);
            System.out.println(transaction);
        } else {
            model.addAttribute("fault", "  請先連線");
        }
        return "index";
    }
}
