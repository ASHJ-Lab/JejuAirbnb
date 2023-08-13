package com.example.jejuairbnb.repository;

import com.example.jejuairbnb.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    Long countByProductId(Long productId);
}
