package org.testing.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testing.spring.dao.BancoDao;
import org.testing.spring.dao.CuentaDao;
import org.testing.spring.exception.DineroInsuficienteException;
import org.testing.spring.model.Banco;
import org.testing.spring.model.Cuenta;
import org.testing.spring.service.CuentaService;
import org.testing.spring.service.CuentaServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SpringTestApplicationTests {


	//@Mock	//injeccion de moquito
	@MockitoBean  //injeccion de spring
	CuentaDao cuentaDao;

	//@Mock	//injeccion de moquito
	@MockitoBean  //injeccion de spring
	BancoDao bancoDao;

	//@InjectMocks	//injeccion de moquito
	//CuentaServiceImpl cuentaService;

	@Autowired
	CuentaService cuentaService;


	@BeforeEach
	void setUp() {
		//cuentaDao = mock(CuentaDao.class);
		//bancoDao = mock(BancoDao.class);
		//cuentaService = new CuentaServiceImpl(cuentaDao, bancoDao);
		//Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
		//Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
		//Datos.BANCO.setTotalTransferencia(0);
	}


	@Test
	void contextLoads() {
		when(cuentaDao.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaDao.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoDao.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

	 	assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		cuentaService.transferir(1L, 2L, new BigDecimal("100"), 1L);

		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		int total = cuentaService.revisarTotalTransferencias(1L);
		assertEquals(1, total);

		verify(cuentaDao, times(3)).findById(1L);
		verify(cuentaDao, times(3)).findById(2L);
		verify(cuentaDao, times(2)).save(any(Cuenta.class));

		verify(bancoDao, times(2)).findById(1L);
		verify(bancoDao).save(any(Banco.class));

		verify(cuentaDao, times(6)).findById(anyLong());
		verify(cuentaDao, never()).findAll();
	}

	//61
	@Test
	void contextLoads_2() {
		when(cuentaDao.findById(1L)).thenReturn(Datos.crearCuenta001());
		when(cuentaDao.findById(2L)).thenReturn(Datos.crearCuenta002());
		when(bancoDao.findById(1L)).thenReturn(Datos.crearBanco());

		BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
		BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, () -> {

			cuentaService.transferir(1L, 2L, new BigDecimal("1200"), 1L);

		});


		saldoOrigen = cuentaService.revisarSaldo(1L);
		saldoDestino = cuentaService.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		int total = cuentaService.revisarTotalTransferencias(1L);
		assertEquals(0, total);

		verify(cuentaDao, times(3)).findById(1L);
		verify(cuentaDao, times(2)).findById(2L);
		verify(cuentaDao, never()).save(any(Cuenta.class));

		verify(bancoDao, times(1)).findById(1L);
		verify(bancoDao, never()).save(any(Banco.class));

		verify(cuentaDao, times(5)).findById(anyLong());
		verify(cuentaDao, never()).findAll();
	}


	//62
	@Test
	void contextLoads_3() {
		when(cuentaDao.findById(1L)).thenReturn(Datos.crearCuenta001());

		//verifica q se este devolviendo el mismo obj
		Cuenta cuenta1 = cuentaService.findById(1L);
		Cuenta cuenta2 = cuentaService.findById(1l);
		assertSame(cuenta1, cuenta2);
		assertTrue(cuenta1 == cuenta2);
		assertEquals("axel", cuenta1.getPersona());
		assertEquals("axel", cuenta2.getPersona());
		verify(cuentaDao, times(2)).findById(1L);
	}


	//84
	@Test
	void testFindAll() {

		//given
		List<Cuenta> datos = Arrays.asList(Datos.crearCuenta001().orElseThrow(), Datos.crearCuenta002().orElseThrow());
		when(cuentaDao.findAll()).thenReturn(datos);

		//when
		List<Cuenta>cuentas = cuentaService.findAll();

		//then
		assertFalse(cuentas.isEmpty());
		assertEquals(2, cuentas.size());
		assertTrue(cuentas.contains(Datos.crearCuenta002().orElseThrow()));

		verify(cuentaDao).findAll();

	}

	//85
	@Test
	void testSave() {

		//given
		Cuenta cuentaPepe = new Cuenta(null, "pepe", new BigDecimal("3000"));
		when(cuentaDao.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		//when
		Cuenta cuenta = cuentaService.save(cuentaPepe);

		//then
		assertEquals("pepe", cuenta.getPersona());
		assertEquals(3, cuenta.getId());
		assertEquals("3000", cuenta.getSaldo().toPlainString());
		verify(cuentaDao).save(any());
	}
}
