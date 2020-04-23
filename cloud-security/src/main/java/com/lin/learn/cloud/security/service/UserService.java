package com.lin.learn.cloud.security.service;

import com.lin.learn.cloud.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService implements UserDetailsService {

    private List<User> users;

    @Autowired
    private PasswordEncoder encoder;

    @PostConstruct
    public void initData() {
        String password = encoder.encode("123456");
        users = Stream.of(
                new User("Zeus", password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin")),
                new User("Hera", password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin")),
                new User("Apollo", password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin"))
        ).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> userList = users.stream()
                .filter(user -> user.getUsername().contentEquals(username))
                .collect(Collectors.toList());
        if (!userList.isEmpty()) {
            return userList.get(0);
        } else {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
    }
}
