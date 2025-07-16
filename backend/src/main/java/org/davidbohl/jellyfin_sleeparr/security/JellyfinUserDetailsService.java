package org.davidbohl.jellyfin_sleeparr.security;

import org.davidbohl.jellyfin_sleeparr.jellyfin.api.JellyfinApiConsumer;
import org.davidbohl.jellyfin_sleeparr.jellyfin.api.models.JellyfinUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JellyfinUserDetailsService implements UserDetailsService {

    private final JellyfinApiConsumer jellyfinApiConsumer;

    public JellyfinUserDetailsService(JellyfinApiConsumer jellyfinApiConsumer) {
        this.jellyfinApiConsumer = jellyfinApiConsumer;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        JellyfinUser jellyfinUser = jellyfinApiConsumer.getUserById(username);

        return new SleeparrUserDetails(jellyfinUser.getId(), jellyfinUser.getName(), List.of(new SimpleGrantedAuthority("USER")));
    }
}
