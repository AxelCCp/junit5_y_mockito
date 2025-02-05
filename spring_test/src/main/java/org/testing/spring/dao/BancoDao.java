package org.testing.spring.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.testing.spring.model.Banco;

import java.util.List;

public interface BancoDao extends JpaRepository<Banco, Long> {

    /*
    List<Banco> findAll();

    Banco findById(Long id);

    void update(Banco banco);
    */

}
