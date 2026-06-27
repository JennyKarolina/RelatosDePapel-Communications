package com.orders.relatosdepapel.communications.events.listener;

import com.orders.relatosdepapel.communications.events.model.OrderCreatedEvent;
import com.orders.relatosdepapel.communications.events.service.EmailService;
import com.orders.relatosdepapel.communications.events.service.EmailServiceWithHtml;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final EmailServiceWithHtml emailService;

    @RabbitListener(queues = "${rabbitmq.queue.mails.order-created}")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) throws MessagingException {
        try {
            log.info("Recibido evento de pedido creado: {} - EventId: {}",
                    event.getBody().getOrderName(),
                    event.getHeader().getEventId());

            // Enviar notificación por correo
            emailService.sendOrderCreatedEmail(event);

            log.info("Evento procesado exitosamente para el pedido: {}",
                    event.getBody().getOrderName());
        } catch (Exception e) {
            log.error("Error al procesar evento de pedido creado: {} - EventId: {}",
                    event.getBody().getOrderName(),
                    event.getHeader().getEventId(), e);
            // Aquí podrías implementar lógica de retry o dead letter queue
            throw e; // Re-lanzar para que RabbitMQ pueda manejar el retry
        }
    }
}
