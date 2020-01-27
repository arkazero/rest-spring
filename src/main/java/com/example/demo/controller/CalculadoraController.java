package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
	
	
	@GetMapping("/version")
	public String home() {
		return "Calculadora 1.0 - Env: "+nameApp;
	}

	@GetMapping("/healthcheck")
	public String healthcheck() {
		return "OK";
	}

	@GetMapping("/sumar")
    public String sumar(@RequestParam Integer sumando1, @RequestParam Integer sumando2){
    	Integer resultado = 0;
    	resultado = calculadoraService.sumar(sumando1, sumando2);
    	
    	try {
			createFiles();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        return ("Resultado: "+resultado);
    }

	@GetMapping("/restar")
	public String restar(@RequestParam Integer minuendo, @RequestParam Integer sustraendo){
		Integer resultado = 0;
		resultado = calculadoraService.restar(minuendo, sustraendo);
		return ("Resultado: "+resultado);
	}
	
	private void createFiles() throws IOException {
		for(int i = 0;i<10; i++) {
			String data = "Test data";
			String nameFile = "File"+Math.random();
			Files.write(Paths.get("/deployments/data/"+nameFile), data.getBytes());
		}
	}

}
