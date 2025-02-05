package org.testing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.testing.Datos;
import org.testing.dao.ExamenDao;
import org.testing.dao.ExamenRepositoryImpl;
import org.testing.dao.PreguntaDao;
import org.testing.dao.PreguntaDapImpl;
import org.testing.model.Examen;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//37

@ExtendWith(MockitoExtension.class) //41 - FORMA 2 - para habilitar la inyeccion de dependencias con mockito. con esto se extiende la clase test de junit, para que extienda junto a la extension de mockito y habilite las anotaciones de inyeccion de dependencias. para esto es la dependencia mockito-junit-jupiter q esta en el pom.
class ExamenServiceImplTest {

    //41-usando inyeccion de dependencias desde el contexto e inyeccion con mockito:

    @Mock // 41 - crea el mock.
    private ExamenDao examenDao;

    @Mock
    private PreguntaDao preguntaDao;

    @InjectMocks // 41 - injecta y crea el mock. Pero debe usarse el tipo de la clase que implementa la interfaz
    private ExamenServiceImpl examenService;
    //private ExamenService examenService;

    //38
    @BeforeEach
    void setUp() {
        //creacion de referencias de forma manual:
        /*
        this.examenDao = Mockito.mock(ExamenDao.class);                                                                 //examenDao - esto es un mock,  no un obj real.
        this.preguntaDao = Mockito.mock(PreguntaDao.class);
        this.examenService = new ExamenServiceImpl(examenDao, preguntaDao);
        */

        //41 - usando inyeccion de dependencias desde el contexto e inyeccion con mockito.
        //FORMA 1
        MockitoAnnotations.openMocks(this);     //41 - se habilita el uso de anotaciones para esta clase.
    }


    @Test
    void findExamenPorNombre() {

        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);

        Optional<Examen> examen = examenService.findExamenPorNombre("matematicas");

        assertTrue(examen.isPresent());

        assertEquals(5L, examen.orElseThrow().getId());

        assertEquals("matematicas", examen.get().getNombre());
    }


    @Test
    void findExamenPorNombre_lista_vacia() {

        List<Examen> datos = Collections.emptyList();

        when(examenDao.findAll()).thenReturn(datos);

        Optional<Examen> examen = examenService.findExamenPorNombre("matematicas");

        assertFalse(examen.isPresent());

    }

//39
    @Test
    void testPreguntasExamen() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaDao.findPreguntasPorExamenId(5L)).thenReturn(Datos.PREGUNTAS);
        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = examenService.findExamenPorNombreConPreguntas("matematicas");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }

//40
    @Test
    void testPreguntasExamen_verify() {

        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);

        //when(preguntaDao.findPreguntasPorExamenId(5L)).thenReturn(Datos.PREGUNTAS);
        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("matematicas");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        //verificando que se esta llamando correctamente a los dao's  -  si comento estos metodos en el ExamenService, va a fallar el verify ya q no se estarian llamando los metodos.
        verify(examenDao).findAll();
        verify(preguntaDao).findPreguntasPorExamenId(anyLong());
    }

    //40
    @Test
    void test_no_existe_examen_verify() {

        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);

        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("matematicas2");

        assertNull(examen);

        //verificando que se esta llamando correctamente a los dao's  -  si comento estos metodos en el ExamenService, va a fallar el verify ya q no se estarian llamando los metodos.

        verify(examenDao).findAll();

        verify(preguntaDao).findPreguntasPorExamenId(anyLong());
    }


    //42
    @Test
    void testGuardarExamen() {

        //GIVEN
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        //when(examenDao.guardar(any(Examen.class))).thenReturn(Datos.EXAMEN);                  //cuando se invoque del repository, se pasan datos. se pasa cualquier tipo de examen. y retorna un examen.

        when(examenDao.guardar(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;
            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        //WHEN
        Examen examen = examenService.guardar(Datos.EXAMEN);

        //THEN
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("fisica", examen.getNombre());

        verify(examenDao).guardar(any(Examen.class));                                        //verifica si se está llamando al guardar de examendao
        verify(preguntaDao).guardarVarias(anyList());                                       //si se pasa un examen sin preguntas, esta prueba va a fallar.
    }


    //44
    @Test
    void testManejoException () {

        when(examenDao.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaDao.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);                //cuando se recibe cualquier parametro de tipo long,  debe lanzar una exception.

        // se prueba si devuelve esta exception
        Exception exception =  assertThrows(IllegalArgumentException.class, () -> {
            examenService.findExamenPorNombreConPreguntas("matematicas");
        });

        assertEquals(IllegalArgumentException.class,  exception.getClass());

        verify(examenDao).findAll();
        verify(preguntaDao).findPreguntasPorExamenId(null);
    }

    //45 - validaciones personalizadas de los argumentos. argumentMatcher sirve para ver si coinciden los argumentos que se pasan a un metodo , con los valores definidos en el mock.
    @Test
    void test_argument_matchers() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        verify(examenDao).findAll();
        verify(preguntaDao).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));                  //se valida el paso de argumento.
    }

    //45 - este no pasa pq llega un id null
    @Test
    void test_argument_matchers_2() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        verify(examenDao).findAll();
        verify(preguntaDao).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));                  //se valida el paso de argumento.
    }

    //45
    @Test
    void test_argument_matchers_3() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        verify(examenDao).findAll();
        //verify(preguntaDao).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));                  //se valida el paso de argumento.
        verify(preguntaDao).findPreguntasPorExamenId(eq(5L));                                                           //esto es equivalente a la linea de arriba.
    }


    //46 - argument matcher personalizado con una clase anidada o separada
    public static class MyArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return  "es para un mensaje personalizado de error que imprime mockito en caso de que falle el test. " + argument + " debe ser un numero entero positivo";
        }
    }
    //46
    @Test
    void test_argument_matchers_4() {
        //when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        verify(examenDao).findAll();
        verify(preguntaDao).findPreguntasPorExamenId(argThat(new MyArgsMatchers()));                  //se valida el paso de argumento.
    }


    //47 - capturar los argumentos q se pasan en los metodos mock y poder probarlos. ArgumentCaptor
    @Test
    void testArgumentCaptor() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        ArgumentCaptor<Long>captor = ArgumentCaptor.forClass(Long.class);                           //obj para capturar el argumento, de tipo generico Long
        verify(preguntaDao).findPreguntasPorExamenId(captor.capture());                             //se captura argumento.
        assertEquals(5L, captor.getValue());                                                        //compara el 5L con el arg capturado.
    }

    @Captor //obj para capturar el argumento, de tipo generico Long
    ArgumentCaptor<Long> captor;

    @Test
    void testArgumentCaptor_con_inyeccion() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaDao.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        verify(preguntaDao).findPreguntasPorExamenId(captor.capture());                             //se captura argumento.
        assertEquals(5L, captor.getValue());                                                        //compara el 5L con el arg capturado.
    }


    //48 - doThrow para comprobar exceptions en metodos void
    @Test
    void test_doThrow() {

        Examen examen = Datos.EXAMEN;

        examen.setPreguntas(Datos.PREGUNTAS);

        doThrow(IllegalArgumentException.class).when(preguntaDao).guardarVarias(anyList());         //lanza la illegal exception cuando se llama al metodo guardarVarias.

        //se espera una IllegalArgumentException y se imvoca al guardar(examen);
        assertThrows(IllegalArgumentException.class, () -> {
            examenService.guardar(examen);
        });
    }

    //49
    @Test
    void test_doAnswer() {

        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);

        //se captura el id q se está pasando. se usa el cero, ya que solo se está pasando un argumento, el id.
        //cuando se invoque con el mock preguntaRepository, el metodo findPreguntasPorExamenId(), se va a ejecutar el evento qe está dentro de las llaves {} de la lambda.

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Datos.PREGUNTAS : null;
        }).when(preguntaDao).findPreguntasPorExamenId(anyLong());

        //luego se invoca el service
        Examen examen = examenService.findExamenPorNombreConPreguntas("matematicas");

        //validaciones
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("geometria"));
        assertEquals(5L, examen.getId());
        assertEquals("matematicas", examen.getNombre());
        verify(preguntaDao).findPreguntasPorExamenId(anyLong());
    }

    @Mock
    ExamenRepositoryImpl examenRepositoryImpl;
    @Mock
    PreguntaDapImpl preguntaDaoImpl;

    //50
    //se quiere invocar el metodo real,  no un mock con el valor inventado. En este ejemplo, se requerira una implementacion real de los repositories.
    //se invoca al metodo real
    @Test
    void testDoCallRealMethod() {
        when(examenRepositoryImpl.findAll()).thenReturn(Datos.EXAMENES);
        doCallRealMethod().when(preguntaDaoImpl).findPreguntasPorExamenId(anyLong());
        Examen examen = examenService.findExamenPorNombreConPreguntas("matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("matematicas", examen.getNombre());
    }

    /*
    @Spy
    ExamenRepositoryImpl examenRepositoryImpl;
    @Spy
    PreguntaDapImpl preguntaDaoImpl;
    */

    //51
    @Test
    void testSpy() {
        ExamenDao examenDao1 = spy(ExamenRepositoryImpl.class);
        PreguntaDao preguntaDao1 = spy(PreguntaDapImpl.class);
        ExamenService examenService1 = new ExamenServiceImpl(examenDao1, preguntaDao1);
        Examen examen = examenService1.findExamenPorNombreConPreguntas("matematicas");
        assertEquals(5, examen.getId());
        assertEquals("matematicas", examen.getNombre());
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }


    //52 - verificacion de orden de invocacion de los metodos
    @Test
    void test_orden_invocaciones() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        examenService.findExamenPorNombreConPreguntas("lenguaje");
        InOrder inOrder = inOrder(preguntaDao);
        inOrder.verify(preguntaDao).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaDao).findPreguntasPorExamenId(6L);
    }


    @Test
    void test_orden_invocaciones_2() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        examenService.findExamenPorNombreConPreguntas("lenguaje");
        InOrder inOrder = inOrder(examenDao, preguntaDao);
        inOrder.verify(examenDao).findAll();
        inOrder.verify(preguntaDao).findPreguntasPorExamenId(5L);
        inOrder.verify(examenDao).findAll();
        inOrder.verify(preguntaDao).findPreguntasPorExamenId(6L);
    }


    //53 - contando el numero de invocaciones de los mocks
    @Test
    void testNumeroDeInvocaciones() {
        when(examenDao.findAll()).thenReturn(Datos.EXAMENES);
        examenService.findExamenPorNombreConPreguntas("matematicas");
        verify(preguntaDao, times(1)).findPreguntasPorExamenId(5L);                 //times() : recibe por argumento la cantidad de veces que se espera que  se llame un metodo.
        verify(preguntaDao, atLeast(1)).findPreguntasPorExamenId(5L);               //atLeast() : se espera que se ejecute almenos el numero pasado por parametro.
        verify(preguntaDao, atLeastOnce()).findPreguntasPorExamenId(5L);            //atLeastOnce() : esperamos a q se ejecute almenos una vez.
        verify(preguntaDao, atMost(1)).findPreguntasPorExamenId(5L);                //atMost() : esperamos a q se ejecute a lo más, la cantidad pasada por parametro.
        verify(preguntaDao, atMostOnce()).findPreguntasPorExamenId(5L);             //atMostOnce() : esperamos q se ejecute como maximo una vez.
    }


    @Test
    void testNumeroDeInvocaciones_2() {
        when(examenDao.findAll()).thenReturn(Collections.emptyList());
        examenService.findExamenPorNombreConPreguntas("matematicas");
        verify(preguntaDao, never()).findPreguntasPorExamenId(5L);                  //never() : se espera que nunca se llame al metodo findPreguntasPorExamenId.
        verifyNoInteractions(preguntaDao);                                          //verifica q no hay interacciones con el mock preguntaDao
    }





}