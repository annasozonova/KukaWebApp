package com.example.demo.controllers;

import com.example.demo.no.hials.crosscom.CrossComClient;
import com.example.demo.no.hials.crosscom.KRL.structs.KRLPos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;


@Controller
public class TestController {
    public CrossComClient client = null;

    @RequestMapping("/connection")
    public String connection() {
        return "connect.html";
    }

    @RequestMapping(value = "/connection", method = RequestMethod.POST)
    public String connectionPost(@RequestParam String ip_address, @RequestParam String port) {
        int clientPort = -1;
        try {
            clientPort = Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            return "Error on parsing port number!";
        }

        try {
            client = new CrossComClient(ip_address, clientPort);
            return "mainWindow.html";
        } catch (IOException ex) {
            return "Wrong IP";
        }
    }

    @RequestMapping("/mainWindow")
    public String mainWindow() {
        return "mainWindow.html";
    }

    @RequestMapping(value = "/mainWindow", method = RequestMethod.POST)
    public String write (Model model, @RequestParam(required = false) String x , @RequestParam(required = false) String y, @RequestParam(required = false) String z, @RequestParam(required = false) String a, @RequestParam(required = false) String b, @RequestParam(required = false) String  c) throws IOException {
        if(x!="" && y!="" && z!="" && a!="" && b!="" && c!="") {
                double xd = Double.parseDouble(x),
                        yd = Double.parseDouble(y),
                        zd = Double.parseDouble(z),
                        ad = Double.parseDouble(a),
                        bd = Double.parseDouble(b),
                        cd = Double.parseDouble(c);
                KRLPos pos = new KRLPos("COM_FRAME");
                pos.setXToZ(xd, yd, zd);
                pos.setAToC(ad, bd, cd);
                client.writeVariable(pos);
                client.readVariable(pos);
                model.addAttribute("x", x);
                model.addAttribute("y", y);
                model.addAttribute("z", z);
                model.addAttribute("a", a);
                model.addAttribute("b", b);
                model.addAttribute("c", c);
            return "mainWindow.html";
        }
        else {
            String str = client.simpleRead("$POS_ACT");
            System.out.println(str);
            str = str.substring(str.indexOf("X"), str.indexOf(", S"));
            System.out.println(str);
            String[] numbers = str.split(", ");
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = numbers[i].substring(2);
                System.out.println(numbers[i]);
            }
            model.addAttribute("x", numbers[0]);
            model.addAttribute("y", numbers[1]);
            model.addAttribute("z", numbers[2]);
            model.addAttribute("a", numbers[3]);
            model.addAttribute("b", numbers[4]);
            model.addAttribute("c", numbers[5]);
            return "mainWindow.html";
        }
    }
}
