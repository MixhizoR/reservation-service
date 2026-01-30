package com.omniticket.reservation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.omniticket.reservation_service.model.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
