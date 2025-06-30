package org.example.granturismo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.example.granturismo.modelo.Usuario;
import org.example.granturismo.modelo.UsuarioRol;
import org.example.granturismo.repositorio.IUsuarioRepository;
import org.example.granturismo.repositorio.IUsuarioRolRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Importar Collectors

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final IUsuarioRolRepository repo;
    private final IUsuarioRepository repoU;

    // ✅ Cargar la clave secreta desde application.properties o application.yml
    @Value("${jwt.secret}")
    private String secret;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario por email
        Usuario u = repoU.findOneByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        // Busca los roles del usuario
        List<UsuarioRol> userRoles = repo.findByUsuario(u); // Asumiendo que IUsuarioRolRepository tiene un findByUsuario

        // Mapea los roles a GrantedAuthority
        List<GrantedAuthority> roles = userRoles.stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getRol().getNombre().name()))
                .collect(Collectors.toList());

        // Retorna tu CustomUserDetails con el ID del usuario
        return new CustomUserDetails(u.getIdUsuario(), u.getEmail(), u.getClave(), roles);
    }
    // ✅ Método para extraer el ID del usuario desde el token
    public Long extractUserId(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        Claims claims = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("userId", Long.class);
    }
}