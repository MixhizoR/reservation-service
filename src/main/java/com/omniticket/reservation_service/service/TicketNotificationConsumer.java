package com.omniticket.reservation_service.service;

import com.omniticket.reservation_service.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketNotificationConsumer {

    /**
     * @RabbitListener sayesinde bu metod, QUEUE_NAME içinde
     *                 mesaj biriktiği anda otomatik tetiklenir. 👂
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleTicketPurchaseMessage(String message) {
        log.info("📩 Kuyruktan yeni mesaj yakalandı!");
        log.info("MESAJ İÇERİĞİ: {}", message);

        // Gerçek dünyada burada PDF oluşturup e-posta atardık. 📧
        processNotification(message);
    }

    private void processNotification(String message) {
        log.info("📄 PDF Fatura simülasyonu başlatıldı...");

        try {
            // Sistemin bir iş yaptığını anlamak için 3 saniye bekletiyoruz.
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("✅ İŞLEM TAMAM: Bilet faturası hazırlandı ve gönderildi.");
    }
}