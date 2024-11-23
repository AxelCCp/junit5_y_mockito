package org.testing.junit5.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class CuentaTest {

    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {

        this.cuenta = new Cuenta("axel", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("iniciando el metodo");

       testReporter.publishEntry("ejecutando --- " + testInfo.getDisplayName() + " --- metodo --- " + testInfo.getTestMethod().orElse(null).getName() + " --- tags --- " + testInfo.getTags());

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


    @Test
    void imprimir_variables_ambiente() {
        Map<String, String> getenv = System.getenv();
        getenv.forEach((k,v) -> System.out.println(k + " : " + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*openjdk-21.0.5+11-windows-x64.*")
    void test_Java_Home() {}

    @Test
    @EnabledIfEnvironmentVariable(named="NUMBER_IF_PROCESSORS", matches = "12")
    void test_procesadores() {}

    @Test
    @EnabledIfEnvironmentVariable(named="ENVIRONMENT", matches = "dev")
    void test_env() {}

    @Test
    @DisabledIfEnvironmentVariable(named="ENVIRONMENT", matches = "prod")
    void test_env_prod_disabled() {}

    @Test
    void test_saldo_cuenta_dev() {
        Boolean esDEV = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDEV);                                              //22 - si es falso,  se deshabilita la prueba.
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @Disabled
    void test_saldo_cuenta_dev_2() {

        Boolean esDEV = "dev".equals(System.getProperty("ENV"));

        assumingThat(esDEV, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        });

    }

    //23 - clase anidada
    @Tag("sistema_operativo_test")              //para etiquetar las pruebas q esta nentro de la clase
    @Nested
    @DisplayName("clase anidada SistemaOperativotest")
    class SistemaOperativotest {


        @Test
        @DisabledIfSystemProperty(named="os.arch", matches = ".*32.*")
        void test_Solo_64() {

            testReporter.publishEntry(testInfo.getTags().toString());

            if(testInfo.getTags().contains("sistema_operativo_test")) {
                testReporter.publishEntry("hacer algo con la etiqueta --- sistema_operativo_test");
            }

        }

        @Test
        @EnabledIfSystemProperty(named="os.arch", matches = ".*32.*")
        void test_NO_64() {}


        @Test
        @EnabledIfSystemProperty(named="user.name", matches = "Fanta")
        void test_username() {}

        @Test
        @EnabledIfSystemProperty(named="ENV", matches = "dev")
        void test_dev() {}

    }


    //23
    @Tag("java_version_test")
    @Nested
    @DisplayName("clase anidada JavaVersionTest")
    class JavaVersionTest {

        //20 - se ejecuta si ña version de java es 15.0.1
        @Test
        @EnabledIfSystemProperty(named="java.version", matches = "15.0.1")
        void testJavaVersion() {}

        //20 - se ejecuta si ña version de java es 15.0.1 - expresiones regulares
        @Test
        @EnabledIfSystemProperty(named="java.version", matches = ".*15.*")
        void testJavaVersion_2() {}

    }



    @DisplayName("probando debito cuenta repetir")
    @RepeatedTest(value=5, name="{displayName} - Repeticion numero: {currentRepetition} de {totalRepetitions}")    //repite el test 5 veces
    void test_saldo_cuenta_repetir(RepetitionInfo info) {

        if(info.getCurrentRepetition() == 3){
            System.out.println("esta es la repeticion 3");
        }

        Boolean esDEV = "dev".equals(System.getProperty("ENV"));

        assumingThat(esDEV, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        });

    }


    //25 - pruebas parametrizadas
    @Tag("param")
    @ParameterizedTest
    @ValueSource( strings = {"100", "200", "500", "700"} )
    void test_debito_cuenta_parametrizada(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    //25 - pruebas parametrizadas
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @ValueSource( strings = {"100", "200", "500", "700"} )
    void test_debito_cuenta_parametrizada_2(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    //25 - pruebas parametrizadas
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @ValueSource( ints = {100, 200, 500, 700} )
    void test_debito_cuenta_parametrizada_3(Integer monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    //25 - pruebas parametrizadas
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @ValueSource( doubles = {100, 200, 500, 700, 1000} )
    void test_debito_cuenta_parametrizada_4(Double monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    //26 - pruebas parametrizadas - con csv - indice y valor
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvSource({"1,100", "2,200", "3,500", "4,700", "5,1000"} )
    void test_debito_cuenta_parametrizada_5(String index, String monto) {
        System.out.println(index + " -> " + monto);
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    //26 - pruebas parametrizadas - con csv - indice y valor
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvFileSource(resources = "/data.csv")
    void test_debito_cuenta_parametrizada_6(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    //26 - pruebas parametrizadas - method
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void test_debito_cuenta_parametrizada_7(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }
    //26 - pruebas parametrizadas - method
    private static List<String> montoList() {
        return Arrays.asList("100", "200", "500", "700", "1000.123");
    }


    //27 - pruebas parametrizadas - con csv - valor y valor
    @Tag("cuenta")
    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvSource({"200,100", "250,200", "320,300", "515,500", "750,700", "1000.12345, 1000.12345"} )
    void test_debito_cuenta_parametrizada_8(String saldo, String monto) {
        System.out.println(saldo + " -> " + monto);
        cuenta.setSaldo(new BigDecimal(saldo));
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }



    //27 - pruebas parametrizadas - con csv - valor y valor, nombre y nombre
    @Tag("cuenta")
    @Tag("param")
    @Tag("banco")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvSource({"200,100, john, axel", "250,200, pepe, pepe", "320,300,maria,maria", "515,500, pepe, pepa", "750,700,lucas,luca", "1000.12345,1000.12345,cata,cata"} )
    void test_debito_cuenta_parametrizada_9(String saldo, String monto, String esperado, String actual) {
        System.out.println(saldo + " -> " + monto);
        cuenta.setSaldo(new BigDecimal(saldo));
        cuenta.debito(new BigDecimal(monto));
        cuenta.setPersona(actual);

        assertNotNull(cuenta.getSaldo());
        assertNotNull(cuenta.getPersona());
        assertEquals(esperado, actual);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    //27 - pruebas parametrizadas - con csv
    @Tag("param")
    @Tag("cuenta")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @CsvFileSource(resources = "/data2.csv")
    void test_debito_cuenta_parametrizada_10(String saldo, String monto, String esperado, String actual) {

        cuenta.debito(new BigDecimal(monto));
        cuenta.setSaldo(new BigDecimal(saldo));
        cuenta.setPersona(actual);

        System.out.println(saldo + " -> " + monto);

        assertNotNull(cuenta.getSaldo());
        assertNotNull(cuenta.getPersona());
        assertEquals(esperado, actual);

        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }


    //29 - inyeccion de dependencia y componentes test info - test reporter - esto podría ir en los metodos con @AfterEach
    /*
    @Test
    @Tag("param")
    @Tag("cuenta")
    @DisplayName("el nombre!")
    void test_nombre_cuenta(TestInfo testInfo, TestReporter testReporter) {
        System.out.println("ejecutando --- " + testInfo.getDisplayName() + " --- metodo --- " + testInfo.getTestMethod().orElse(null).getName() + " --- tags --- " + testInfo.getTags());
    }
    */


    /*
    //30
    @Nested
    @Tag("timeout")
    class TimeOutClassTest {

        //30 - pruebas de timeout
        @Test
        @Timeout(5)  //se espera q la prueba demore 5 segundos
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(6);
        }

        //30
        @Test
        @Timeout(value=500, unit=TimeUnit.MILLISECONDS)
        void pruebaTimeout_2() throws InterruptedException {
            TimeUnit.SECONDS.sleep(6);
        }

        //30
        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.SECONDS.sleep(5500);
            });
        }

    }

    */







}
