package com.omniticket.reservation_service;

import com.omniticket.reservation_service.model.Ticket;
import com.omniticket.reservation_service.model.TicketStatus;
import com.omniticket.reservation_service.repository.TicketRepository;
import com.omniticket.reservation_service.service.TicketService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // Tüm Spring context'ini (Database, Redis vb.) ayağa kaldırır
class TicketServiceConcurrencyTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void shouldOnlyOneUserReserveTicketWhenMultipleUsersTryAtOnce() throws InterruptedException {
        // 1. HAZIRLIK: Test için bir bilet oluşturup DB'ye kaydediyoruz
        Ticket ticket = new Ticket();
        ticket.setSeatNumber("TEST-101");
        ticket.setPrice(100.0);
        ticket.setStatus(TicketStatus.AVAILABLE);
        Ticket savedTicket = ticketRepository.save(ticket);

        int numberOfThreads = 10; // Aynı anda "saldıracak" kullanıcı sayısı
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // Yarış başlangıç çizgisi: Tüm thread'leri burada bekletip aynı anda salacağız
        // 🏁
        CountDownLatch latch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 2. İŞLEM: 10 farklı iş parçacığı (thread) tanımlıyoruz
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    latch.await(); // İşaret verilene kadar bekle...
                    ticketService.reserveTicket(savedTicket.getId());
                    successCount.incrementAndGet(); // Başarılı olursa artır
                } catch (Exception e) {
                    // "Bilet zaten dolu" veya "Kilit alınamadı" hataları buraya düşer
                    failCount.incrementAndGet();
                }
            });
        }

        // 3. YARIŞI BAŞLAT: Kapıyı açıyoruz! 🔫
        latch.countDown();

        // Tüm thread'lerin bitmesi için maksimum 1 dakika bekle
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // 4. DOĞRULAMA: Çıktıları kontrol et
        System.out.println("------------------------------------");
        System.out.println("Toplam İstek: " + numberOfThreads);
        System.out.println("BAŞARILI (Bileti Alan): " + successCount.get());
        System.out.println("BAŞARISIZ (Hata Alan): " + failCount.get());
        System.out.println("------------------------------------");

        // TESTİN KALBİ: 10 kişiden sadece 1'i almış, 9'u hata almış olmalı!
        assertEquals(1, successCount.get(), "Sadece 1 kişi bilet alabilmeliydi!");
        assertEquals(9, failCount.get(), "9 kişi biletin dolu olduğu hatasını almalıydı!");
    }
}