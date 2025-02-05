package org.testing.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.testing.spring.model.Cuenta;

import java.util.List;
import java.util.Optional;

public interface CuentaDao extends JpaRepository<Cuenta, Long> {

    @Query("select c from Cuenta c where c.persona=?1")
    Optional<Cuenta> findByPersona(String persona);

    /*
    List<Cuenta> findAll();
    Cuenta findById(Long id);
    void update(Cuenta cuenta);
    */

}
