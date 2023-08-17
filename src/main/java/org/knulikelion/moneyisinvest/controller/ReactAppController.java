package org.knulikelion.moneyisinvest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactAppController {
    @GetMapping(value = {"/company/**", "/news/**"})
    public String reactApp() {
        return "forward:/";
    }
}
