package com.procurement.evaluation.controller;

import com.procurement.evaluation.model.dto.DataDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/update")
public class MainController {

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    public ResponseEntity<String> insertTender(@RequestBody DataDto data) {
        return null;
    }
}
