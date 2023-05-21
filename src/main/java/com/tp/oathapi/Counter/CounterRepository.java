package com.tp.oathapi.Counter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CounterRepository extends JpaRepository<Counter, Long> {
    Optional<Counter> findCounterById(Long id);
    Optional<Counter> findFirstById(Long id);
}
