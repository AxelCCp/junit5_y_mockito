package org.testing.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testing.spring.dao.CuentaDao;
import org.testing.spring.model.Cuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//71 - los metodos test hacen un rollback automatico , para no afectar la data del siguiente metodo test
@DataJpaTest
public class IntegracionJpaTest {

    @Autowired
    CuentaDao cuentaDao;

    @Test
    void testFindById() {
        Optional<Cuenta> cuentaOp= this.cuentaDao.findById(1L);
        assertTrue(cuentaOp.isPresent());
        assertEquals("axel", cuentaOp.orElseThrow().getPersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuentaOp= this.cuentaDao.findByPersona("axel");
        assertTrue(cuentaOp.isPresent());
        assertEquals("axel", cuentaOp.orElseThrow().getPersona());
        assertEquals("1000.00", cuentaOp.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuentaOp= this.cuentaDao.findByPersona("rod");
        assertThrows(NoSuchElementException.class, () -> {
           cuentaOp.orElseThrow();
        });
        assertFalse(cuentaOp.isPresent());
    }


    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaDao.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }


    //72
    @Test
    void testSave() {
        //given
        Cuenta cuentaPepe = new Cuenta(null, "pepe", new BigDecimal("3000"));
        cuentaDao.save(cuentaPepe);
        //when
        Cuenta cuenta = cuentaDao.findByPersona("pepe").orElseThrow();
        //then
        assertEquals("pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        //assertEquals(3, cuenta.getId());
    }

    @Test
    void testSave_2() {
        //given
        Cuenta cuentaPepe = new Cuenta(null, "pepe", new BigDecimal("3000"));
        Cuenta save = cuentaDao.save(cuentaPepe);
        //when
        //Cuenta cuenta = cuentaDao.findByPersona("pepe").orElseThrow();
        Cuenta cuenta = cuentaDao.findById(save.getId()).orElseThrow();
        //then
        assertEquals("pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        //assertEquals(3, cuenta.getId());
    }


    @Test
    void testSave_3() {
        //given
        Cuenta cuentaPepe = new Cuenta(null, "pepe", new BigDecimal("3000"));

        //when
        Cuenta cuenta = cuentaDao.save(cuentaPepe);

        //then
        assertEquals("pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        //assertEquals(3, cuenta.getId());
    }


    //73
    @Test
    void test_update() {
        //given
        Cuenta cuentaPepe = new Cuenta(null, "pepe", new BigDecimal("3000"));

        //when
        Cuenta cuenta = cuentaDao.save(cuentaPepe);

        //then
        assertEquals("pepe", cuenta.getPersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());

        //when
        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaDao.save(cuenta);

        //then
        assertEquals("pepe", cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());
    }


    @Test
    void testDelete () {
        Cuenta cuenta = cuentaDao.findById(2L).orElseThrow();
        assertEquals("john", cuenta.getPersona());
        cuentaDao.delete(cuenta);
        assertThrows(NoSuchElementException.class, () -> {
            cuentaDao.findByPersona("john").orElseThrow();
        });
        assertThrows(NoSuchElementException.class, () -> {
            cuentaDao.findById(2L).orElseThrow();
        });
        assertEquals(1, cuentaDao.findAll().size());
    }




}
