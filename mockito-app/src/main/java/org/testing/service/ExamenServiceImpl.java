package org.testing.service;

import org.testing.dao.ExamenDao;
import org.testing.dao.PreguntaDao;
import org.testing.model.Examen;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService{

    private ExamenDao examenDao;
    private PreguntaDao preguntaDao;

    public ExamenServiceImpl(ExamenDao examenDao, PreguntaDao preguntaDao) {

        this.examenDao = examenDao;
        this.preguntaDao = preguntaDao;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {

        Optional<Examen> examenOp = this.examenDao.findAll().stream().filter(e -> e.getNombre().contains(nombre)).findFirst();

        return examenOp;
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {

        Optional<Examen>examenOptional = this.findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()) {
            examen = examenOptional.orElseThrow();
            List<String>preguntas = this.preguntaDao.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()) {
            preguntaDao.guardarVarias(examen.getPreguntas());
        }
        return examenDao.guardar(examen);
    }

}
