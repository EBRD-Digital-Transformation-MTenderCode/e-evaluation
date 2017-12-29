package com.procurement.evaluation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/selections")
public class SelectionsController {

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<String> insertTender(@RequestBody SelectionsDataDto data) {
        return null;
    }
}
