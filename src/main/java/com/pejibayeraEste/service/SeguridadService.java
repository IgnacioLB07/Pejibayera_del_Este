package com.pejibayeraEste.service;

import com.pejibayeraEste.domain.Permiso;
import com.pejibayeraEste.domain.Rol;
import com.pejibayeraEste.repository.PermisoRepository;
import com.pejibayeraEste.repository.RolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SeguridadService {

    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;

    public SeguridadService(RolRepository rolRepository, PermisoRepository permisoRepository) {
        this.rolRepository = rolRepository;
        this.permisoRepository = permisoRepository;
    }

    // Roles
    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> buscarRol(Integer id) {
        return rolRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Rol> buscarRolPorNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    public Rol guardarRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public void eliminarRol(Integer id) {
        try {
            rolRepository.deleteById(id);
            rolRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                    "No se puede eliminar el rol porque está asociado a usuarios o permisos.", ex);
        }
    }

    // Permisos
    @Transactional(readOnly = true)
    public List<Permiso> listarPermisos() {
        return permisoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Permiso> buscarPermiso(Integer id) {
        return permisoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Permiso> buscarPermisoPorNombre(String nombre) {
        return permisoRepository.findByNombre(nombre);
    }

    public Permiso guardarPermiso(Permiso permiso) {
        return permisoRepository.save(permiso);
    }

    public void eliminarPermiso(Integer id) {
        try {
            permisoRepository.deleteById(id);
            permisoRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                    "No se puede eliminar el permiso porque está asociado a roles.", ex);
        }
    }
}
