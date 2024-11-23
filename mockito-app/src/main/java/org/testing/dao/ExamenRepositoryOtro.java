package org.testing.dao;

import org.testing.model.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryOtro implements ExamenDao {

    @Override
    public List<Examen> findAll() {
        try {
            //simulacion de trabajo
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Examen guardar(Examen examen) {
        return null;
    }

}
