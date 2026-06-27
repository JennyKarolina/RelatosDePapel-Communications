package com.orders.relatosdepapel.communications.events.service;

import com.orders.relatosdepapel.communications.events.model.OrderCreatedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceWithHtml {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // inyectado automáticamente por Thymeleaf

    public void sendOrderCreatedEmail(OrderCreatedEvent event) throws MessagingException {

        // 1. Construir el contexto con las variables del template
        Context context = new Context();
        context.setVariable("orderName",  event.getBody().getOrderName());
        context.setVariable("ownerId",    event.getBody().getOwnerId());
        context.setVariable("status",     event.getBody().getStatus());
        context.setVariable("orderItems", event.getBody().getOrderItems());
        context.setVariable("eventId",    event.getHeader().getEventId());
        context.setVariable("eventType",  event.getHeader().getEventType());
        context.setVariable("version",    event.getHeader().getVersion());

        // Formatear fechas y moneda aquí, no en el template
        context.setVariable("orderDate",
                event.getBody().getOrderDate()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        context.setVariable("timestamp",
                event.getHeader().getTimestamp()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        context.setVariable("total",
                NumberFormat.getCurrencyInstance(new Locale("es", "ES"))
                        .format(event.getBody().getTotal()));

        // Formatear subtotales en los items
        List<Map<String, Object>> itemsForTemplate = event.getBody().getOrderItems()
                .stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idCatalog", item.getIdCatalog());
                    map.put("quantity",  item.getQuantity());
                    map.put("subTotal",
                            NumberFormat.getCurrencyInstance(new Locale("es", "ES"))
                                    .format(item.getSubTotal()));
                    return map;
                })
                .toList();

        context.setVariable("orderItems", itemsForTemplate);

        // 2. Procesar el template → genera el HTML final
        String htmlContent = templateEngine.process("emails/order-created", context);

        // 3. Construir el mensaje MIME (soporta HTML)
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(event.getBody().getEmail());
        helper.setSubject("Nuevo Pedido: " + event.getBody().getOrderName());
        helper.setText(htmlContent, true); // true = es HTML
        helper.setFrom("noreply@relatosdepapel.com");

        mailSender.send(message);
    }
}
