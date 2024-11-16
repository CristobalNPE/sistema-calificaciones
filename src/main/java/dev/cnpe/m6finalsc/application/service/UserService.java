package dev.cnpe.m6finalsc.application.service;

import dev.cnpe.m6finalsc.application.web.security.SecurityUser;
import dev.cnpe.m6finalsc.domain.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                             .map(SecurityUser::new)
                             .orElseThrow(() -> new UsernameNotFoundException("No se encuentra usuario: " + username));
    }


}
