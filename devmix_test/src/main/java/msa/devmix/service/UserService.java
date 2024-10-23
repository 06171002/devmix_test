package msa.devmix.service;

import msa.devmix.domain.user.User;

public interface UserService {
    User findById(Long userId);
    User findByUsername(String username);
}
