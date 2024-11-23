package org.testing.dao;

import org.testing.model.Examen;

import java.util.List;

public interface ExamenDao {

    List<Examen> findAll();

    Examen guardar(Examen examen);
}
