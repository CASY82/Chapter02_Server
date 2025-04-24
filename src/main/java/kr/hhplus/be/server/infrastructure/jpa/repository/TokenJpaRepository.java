package kr.hhplus.be.server.infrastructure.jpa.repository;

import kr.hhplus.be.server.domain.token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenValue(String tokenValue);
    
    @Query("SELECT t FROM Token t WHERE t.userRefId = :userRefId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<Token> findByUserRefIdWithLock(@Param("userRefId") Long userRefId);
}