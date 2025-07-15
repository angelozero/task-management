package com.angelozero.task.management.adapter.controller;

import com.angelozero.task.management.usecase.services.dynamicbean.DynamicBeanUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dynamic-bean")
@AllArgsConstructor
public class DynamicBeanController {


    private final DynamicBeanUseCase dynamicBeanUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<String> executeDynamicBeans(@PathVariable Integer id) {
        return ResponseEntity.ok(dynamicBeanUseCase.execute(id));
    }
}
