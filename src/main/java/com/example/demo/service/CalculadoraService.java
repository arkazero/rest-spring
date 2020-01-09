package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.Vector;

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

		long start  = System.currentTimeMillis();
		long count = 0l;
		for(long x=0;x<Integer.MAX_VALUE ;x++){
			count+=1;
		}
		long end = System.currentTimeMillis();
		System.out.println(end-start +" ms");

		return resultado;
	}

	public Integer restar(Integer minuendo, Integer sustraendo){

		Integer resultado = minuendo + sustraendo;

		System.out.println("El resultado de la rest es: "+resultado);


		Vector v = new Vector();
		for(int i = 0; i<100;i++)
		{
			byte b[] = new byte[1048576];
			v.add(b);
			Runtime rt = Runtime.getRuntime();
			System.out.println( "free memory: " + rt.freeMemory() );
		}

		return resultado;
	}

	
}
