package com.omniticket.reservation_service.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Loglama için eklendi

import com.omniticket.reservation_service.model.Ticket;
import com.omniticket.reservation_service.model.TicketStatus;
import com.omniticket.reservation_service.repository.TicketRepository;
import com.omniticket.reservation_service.exception.ResourceNotFoundException;

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
        return ticketRepository.findAllByOrderByIdAsc();
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

    @Transactional
    public Ticket reserveTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bilet bulunamadı! ID: " + id));

        if (ticket.getStatus() != TicketStatus.AVAILABLE) {
            throw new ResourceNotFoundException("Bu bilet zaten rezerve edilmiş veya satılmış! ❌");
        }

        ticket.setStatus(TicketStatus.RESERVED);
        ticket.setReservedAt(LocalDateTime.now());

        log.info("Bilet rezerve edildi: {}", id);
        return ticketRepository.save(ticket);
    }
}