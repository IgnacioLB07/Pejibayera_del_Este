package com.pejibayeraEste.repository;

import com.pejibayeraEste.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByEmpresa_IdEmpresa(Integer idEmpresa);
}
