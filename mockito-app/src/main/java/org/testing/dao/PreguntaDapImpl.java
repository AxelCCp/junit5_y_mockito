package org.testing.dao;

import org.testing.Datos;

import java.util.List;

public class PreguntaDapImpl implements PreguntaDao{
    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println("PreguntaDapImpl.findPreguntasPorExamenId");
        return Datos.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {

    }
}
