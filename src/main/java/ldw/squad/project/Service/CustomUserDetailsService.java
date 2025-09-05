package ldw.squad.project.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ldw.squad.project.Entities.ClientModel;
import ldw.squad.project.Repository.ClientRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ClientRepository usuarioRepository;

    public CustomUserDetailsService(ClientRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        ClientModel usuario = usuarioRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + name));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getName())
                .password(usuario.getPassword())
                .roles(usuario.getRole())
                .build();
    }
}
