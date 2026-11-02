package com.pejibayeraEste.service;

import com.pejibayeraEste.domain.Permiso;
import com.pejibayeraEste.domain.Rol;
import com.pejibayeraEste.domain.Usuario;
import com.pejibayeraEste.repository.UsuarioRepository;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga los usuarios almacenados en MySQL para la autenticacion de Spring Security.
 * El correo electronico funciona como nombre de usuario.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe un usuario registrado con el correo indicado."));

        Rol rol = usuario.getRol();
        if (rol == null || rol.getNombre() == null || rol.getNombre().isBlank()) {
            throw new UsernameNotFoundException(
                    "El usuario no tiene un rol válido asignado.");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(
                "ROLE_" + normalizarAutoridad(rol.getNombre())));

        // Los permisos del rol tambien quedan disponibles para reglas hasAuthority().
        if (rol.getPermisos() != null) {
            for (Permiso permiso : rol.getPermisos()) {
                if (permiso != null && permiso.getNombre() != null
                        && !permiso.getNombre().isBlank()) {
                    authorities.add(new SimpleGrantedAuthority(
                            "PERM_" + normalizarAutoridad(permiso.getNombre())));
                }
            }
        }

        return User.withUsername(usuario.getCorreo())
                .password(usuario.getPassword())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }

    private String normalizarAutoridad(String valor) {
        String sinAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.trim().toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }
}
