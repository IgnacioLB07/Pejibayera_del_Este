package com.pejibayeraEste.service;

import com.pejibayeraEste.domain.Empresa;
import com.pejibayeraEste.repository.EmpresaRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaService {

    // El repositorio es final para asegurar la inmutabilidad
    private final EmpresaRepository empresaRepository;    
    private final FirebaseStorageService firebaseStorageService;

    @Transactional(readOnly = true)
    public List<Empresa> getEmpresas(boolean activo) {
        if (activo) { //Sólo activos...            
            return empresaRepository.findByActivoTrue();
        }
        return empresaRepository.findAll();
    }
    
     @Transactional(readOnly = true)
    public Optional<Empresa> getEmpresa(Integer idEmpresa) {
        return empresaRepository.findById(idEmpresa);
    }

    @Transactional
    public void save(Empresa empresa, MultipartFile imagenFile) {
        empresa = empresaRepository.save(empresa);
        if (imagenFile != null && !imagenFile.isEmpty()) { //Si no está vacío... pasaron una imagen...            
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile, "empresa",
                        empresa.getIdEmpresa());
                empresa.setRutaImagen(rutaImagen);
                empresaRepository.save(empresa);
            } catch (IOException e) {
                log.warn("No fue posible almacenar la imagen de la empresa {}",empresa.getIdEmpresa(),e);
            }
        }
    }

    @Transactional
    public void delete(Integer idEmpresa) {
        // Verifica si la categoría existe antes de intentar eliminarlo
        if (!empresaRepository.existsById(idEmpresa)) {
            // Lanza una excepción para indicar que el usuario no fue encontrado
            throw new IllegalArgumentException("La categoría con ID " + idEmpresa + " no existe.");
        }
        try {
            empresaRepository.deleteById(idEmpresa);
            empresaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            // Lanza una nueva excepción para encapsular el problema de integridad de datos
            throw new IllegalStateException("No se puede eliminar la empresa. Tiene datos asociados.", e);
        }
    }
}