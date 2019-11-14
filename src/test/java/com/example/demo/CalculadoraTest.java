package com.example.demo;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RestAppApplication.class})
public class CalculadoraTest {
	
	@Autowired
	com.example.demo.service.CalculadoraService calculadoraService;
	
	
	@Before
	public void setUp() {
		
		
    }
	
	@Test
	public void sumarPositivos() {
		Integer resul =0;
		resul = calculadoraService.sumar(2, 4);
		
		assertEquals(new Integer(6), resul);
	}
	
	@Test
	public void sumarNegativos() {
		Integer resul =0;
		resul = calculadoraService.sumar(-2, -3);
		
		assertEquals(new Integer(-5), resul);
	}
	
	@Test
	public void sumarNegativoYPositivo() {
		Integer resul =0;
		resul = calculadoraService.sumar(-2, 3);
		
		assertEquals(new Integer(1), resul);
	}

	
	

}
