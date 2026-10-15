package com.vadimbeer.backendservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ExperimentController {

    @GetMapping("/experiments/{id}")
    public String experiment(
            @PathVariable("id") Long id,
            Model model
    ) {
        model.addAttribute("taskId", id);
        return "experiment";
    }
}