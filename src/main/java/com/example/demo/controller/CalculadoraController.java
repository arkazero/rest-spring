package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.CalculadoraService;

@RestController
public class CalculadoraController {
	
	@Autowired
	CalculadoraService calculadoraService;
	
	@Value( "${app.enviroment}" )
	private String nameApp;
	
	
	@GetMapping
	public String home() {
		return "Prueba Calculadora 3.0 - Env: "+nameApp;
	}
	
	@RequestMapping("/sumar")
    public String sumar(@RequestParam Integer sumando1, @RequestParam Integer sumando2){
    	Integer resultado = 0;
    	resultado = calculadoraService.sumar(sumando1, sumando2); 	
        return ("Resultado: "+resultado);
    }

}
