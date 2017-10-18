package in.ac.amu.zhcet.service.user;

import in.ac.amu.zhcet.data.model.user.UserAuth;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.misc.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class UserDetailService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth user = userService.findById(username);

        if (user == null)
            user = userService.getUserByEmail(username);

        if (user == null)
            throw new UsernameNotFoundException(username);

        return new CustomUser(user.getUserId(), user.getPassword(), getAuthorities(user.getRoles()))
                .name(user.getName())
                .avatar(user.getDetails().getAvatarUrl())
                .type(user.getType())
                .department(user.getDepartment());
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(String... roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String role: roles)
            grantedAuthorities.add(new SimpleGrantedAuthority(role));

        return grantedAuthorities;
    }

    private void updatePrincipal(UserAuth userAuth) {
        // Update the principal for use throughout the app
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loadUserByUsername(userAuth.getUserId()), userAuth.getPassword(), UserDetailService.getAuthorities(userAuth.getRoles())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public UserAuth getLoggedInUser() {
        return userService.getLoggedInUser();
    }

    @Transactional
    public void updateAvatar(UserAuth user, ImageService.Avatar avatar) {
        user.getDetails().setAvatarUrl(avatar.getAvatarUrl());
        user.getDetails().setOriginalAvatarUrl(avatar.getOriginalAvatarUrl());
        user.getDetails().setAvatarUpdated(ZonedDateTime.now());
        userService.save(user);
        updatePrincipal(user);
    }
}