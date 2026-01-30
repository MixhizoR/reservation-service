package com.omniticket.reservation_service.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Loglama için eklendi

import com.omniticket.reservation_service.model.Ticket;
import com.omniticket.reservation_service.repository.TicketRepository;
import com.omniticket.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;

    public Ticket createTicket(Ticket ticket) {
        log.info("Yeni bilet oluşturuluyor: {}", ticket.getSeatNumber());
        return ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        // Önce veritabanındaki mevcut bileti buluyoruz
        Ticket existingTicket = getTicket(id);

        // Modelimizdeki alanlara göre güncelliyoruz
        existingTicket.setSeatNumber(ticketDetails.getSeatNumber());
        existingTicket.setPrice(ticketDetails.getPrice());
        existingTicket.setStatus(ticketDetails.getStatus());

        log.info("Bilet güncellendi: {}", id);
        return ticketRepository.save(existingTicket);
    }

    public void deleteTicket(Long id) {
        Ticket ticket = getTicket(id);
        ticketRepository.delete(ticket);
        log.warn("Bilet silindi: {}", id);
    }
}