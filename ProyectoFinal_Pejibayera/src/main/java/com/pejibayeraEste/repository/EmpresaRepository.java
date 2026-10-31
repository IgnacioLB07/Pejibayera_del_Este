package com.pejibayeraEste.repository;


import com.pejibayeraEste.domain.Empresa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    public List<Empresa> findByActivoTrue();

    public List<Empresa> findAll();
    
}