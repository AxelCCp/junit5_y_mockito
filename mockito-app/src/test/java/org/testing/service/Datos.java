package org.testing.service;

import org.testing.model.Examen;

import java.util.Arrays;
import java.util.List;

public class Datos {

    public final static List<Examen> EXAMENES = Arrays.asList(new Examen(5L, "matematicas"), new Examen(6L, "lenguaje"),  new Examen(7L, "historia"));

    public final static List<Examen> EXAMENES_ID_NULL = Arrays.asList(new Examen(null, "matematicas"), new Examen(null, "lenguaje"),  new Examen(null, "historia"));

    public final static List<Examen> EXAMENES_ID_NEGATIVOS = Arrays.asList(new Examen(-5L, "matematicas"), new Examen(-6L, "lenguaje"), new Examen(null, "historia"));

    public final static List<String> PREGUNTAS = Arrays.asList("aritmetica", "integrales", "derivadas", "trigonometria", "geometria");

    public final static Examen EXAMEN = new Examen(8L, "fisica");
}
