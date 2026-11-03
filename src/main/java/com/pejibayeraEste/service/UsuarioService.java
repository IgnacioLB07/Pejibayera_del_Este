package com.pejibayeraEste.service;

import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosPorEmpresa(Integer idEmpresa) {
        return usuarioRepository.findByEmpresa_IdEmpresa(idEmpresa);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuario(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    /** Desactiva el usuario para conservar sus pedidos e historial. */
    public void eliminarUsuario(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "El usuario con ID " + id + " no existe."));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        
    }
}
