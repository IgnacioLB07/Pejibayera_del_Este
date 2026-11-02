package com.pejibayeraEste.service;

import com.pejibayeraEste.domain.Empresa;
import com.pejibayeraEste.repository.EmpresaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public List<Empresa> getEmpresas(boolean soloActivas) {
        return soloActivas ? empresaRepository.findByActivoTrue() : empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> getEmpresa(Integer idEmpresa) {
        return empresaRepository.findById(idEmpresa);
    }

    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    public void delete(Integer idEmpresa) {
        if (!empresaRepository.existsById(idEmpresa)) {
            throw new IllegalArgumentException("La empresa con ID " + idEmpresa + " no existe.");
        }
        try {
            empresaRepository.deleteById(idEmpresa);
            empresaRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                    "No se puede eliminar la empresa porque tiene información asociada.", ex);
        }
    }
}
