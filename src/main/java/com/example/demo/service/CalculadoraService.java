package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class CalculadoraService {
	
	/**
	 * Metodo encargado de sumar 2 valores
	 * @param sumando1
	 * @param sumando2
	 * @return
	 */
	public Integer sumar(Integer sumando1, Integer sumando2) {
		Integer resultado = sumando1 + sumando2;
		
		if(resultado.intValue()==6) {
			System.out.println("El resultado es 6");
		}
		return resultado;
	}
	
	
	/**
	 * Metodo encargado de realizar la resta de dos numeros
	 * @param minuendo
	 * @param sustraendo
	 * @return
	 */
	public Integer restar(Integer minuendo, Integer sustraendo) {
		Integer resultado = minuendo - sustraendo;
		return resultado;
	}
	
	
		
}
