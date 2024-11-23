package org.testing.junit5.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.testing.junit5.exception.DineroInsuficienteException;

import java.math.BigDecimal;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest2 {

    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest() {

        this.cuenta = new Cuenta("axel", new BigDecimal("1000.12345"));

        System.out.println("iniciando el metodo");
    }


    @AfterEach
    void tearDown() {
        System.out.println("finalizando el metodo de prueba");
    }


    @BeforeAll
    static void beforeAll() {
        System.out.println("inicializando el test");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("finalizando el test");
    }


    //alt insert
    @Test
    void test_nombre_cuenta() {
        this.cuenta = new Cuenta("axel", new BigDecimal("1000.12345"));
        //cuenta.setPersona("axel");
        String esperado = "axel";
        String real = cuenta.getPersona();
        Assertions.assertEquals(esperado, real);
        Assertions.assertTrue(real.equals(esperado));
    }


    @Test
    void test_saldo_cuenta() {
        this.cuenta = new Cuenta("axel", new BigDecimal("1000.12345"));
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);                                                  //regla sw saldo mayor q cero.
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);                                                   //misma regla pero con logica inversa.
    }

    @Test
    void nametest_referencia_cuenta() {
        Cuenta cuenta1 = new Cuenta("john doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("john doe", new BigDecimal("8900.9997"));
        //assertNotEquals(cuenta2, cuenta1);                                                                            //comparacion por instancia,  sin sobreescribir el equals.
        assertEquals(cuenta2, cuenta1);                                                                                 //con equals
    }

    @Test
    void test_debito_cuenta() {
        //this.cuenta = new Cuenta("axel", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void test_credito_cuenta() {
        //this.cuenta = new Cuenta("axel", new BigDecimal("1000.12345"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void test_DineroInsuficienteException() {
        //Cuenta cuenta = new Cuenta("axel", new BigDecimal("1000.12345"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
           cuenta.debito(new BigDecimal(1500));
        });
        String actual = exception.getMessage();
        String esperado = "dinero insuficiente";
        assertEquals(esperado, actual);
    }


    @Test
    void test_transferir_dinero_cuentas() {
        Cuenta cuenta1 = new Cuenta("john doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("andres", new BigDecimal("1500.8989"));
        Banco banco = new Banco();
        banco.setNombre("banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());
    }


    //14
    @Test
    void test_relacion_banco_cuentas() {
        Cuenta cuenta1 = new Cuenta("john doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta1.getSaldo().toPlainString());

        assertEquals(2, banco.getCuentas().size());

        assertEquals("banco del estado", cuenta1.getBanco().getNombre());                                               //probando la relacion inversa.

        assertEquals("andres", banco.getCuentas().stream().filter(c -> c.getPersona().equals("andres")).findFirst().get().getPersona());

        assertTrue(banco.getCuentas().stream().filter(c -> c.getPersona().equals("andres")).findFirst().isPresent());

        assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("andres")));
    }


    //15 - assertAll - para obtener los resultados de todas las pruebas unitarias que dieron error.
    @Test
    //@Disabled    //17-es para q se salte la pruba unitaria.
    @DisplayName("obtener los resultados de todas las pruebas unitarias que dieron error")
    void test_relacion_banco_cuentas_2() {

        //fail();   //17-metodo q hace fallar la prueba

        Cuenta cuenta1 = new Cuenta("john doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("banco del estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        assertAll(
                () -> { assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()); },
                () -> { assertEquals("3000", cuenta1.getSaldo().toPlainString()); },
                () -> { assertEquals(2, banco.getCuentas().size()); },
                () -> { assertEquals("banco del estado", cuenta1.getBanco().getNombre()); },
                () -> { assertEquals("andres", banco.getCuentas().stream().filter(c -> c.getPersona().equals("andres")).findFirst().get().getPersona()); },
                () -> { assertTrue(banco.getCuentas().stream().filter(c -> c.getPersona().equals("andres")).findFirst().isPresent());},
                () -> { assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("andres"))); }
        );

    }

    //20
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testSoloWindows() {

    }

    //20
    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testSoloLinuxMac() {

    }

    //20
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testNoWindows() {

    }


    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void soloJdk8() {

    }


    @Test
    @EnabledOnJre(JRE.JAVA_17)
    void soloJdk17() {

    }

    @Test
    @DisabledOnJre(JRE.JAVA_17)
    void noJdk17() {

    }


    @Test
    void imprimirSystemProperties() {
        Properties properties  = System.getProperties();
        properties.forEach((k, v)-> System.out.println(k + " : " + v));
    }


    //20 - se ejecuta si ña version de java es 15.0.1
    @Test
    @EnabledIfSystemProperty(named="java.version", matches = "15.0.1")
    void testJavaVersion() {

    }

    //20 - se ejecuta si ña version de java es 15.0.1 - expresiones regulares
    @Test
    @EnabledIfSystemProperty(named="java.version", matches = ".*15.*")
    void testJavaVersion_2() {

    }


    @Test
    @DisabledIfSystemProperty(named="os.arch", matches = ".*32.*")
    void test_Solo_64() {

    }

    @Test
    @EnabledIfSystemProperty(named="os.arch", matches = ".*32.*")
    void test_NO_64() {

    }


    @Test
    @EnabledIfSystemProperty(named="user.name", matches = "Fanta")
    void test_username() {

    }

    @Test
    @EnabledIfSystemProperty(named="ENV", matches = "dev")
    void test_dev() {

    }
}