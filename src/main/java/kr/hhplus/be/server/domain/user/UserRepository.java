package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUserId(String userId);
    Optional<User> findById(Long id);
    User save(User user);
}