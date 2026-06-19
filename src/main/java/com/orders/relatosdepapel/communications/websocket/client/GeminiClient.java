package com.orders.relatosdepapel.communications.websocket.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GeminiClient {

    private final WebClient webClient;
    private final String apiUrl;
    private final String apiKey;

    public GeminiClient(
            @Value("${gemini.api.url}") String apiUrl,
            @Value("${gemini.api.key}") String apiKey) {
        this.webClient = WebClient.builder().build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    @SuppressWarnings("rawtypes")
    public String generateResponse(String userMessage) {
        try {
            // Contexto del catálogo de libros de Relatos de Papel orientado a guía de plataforma
            String systemInstruction = 
                "Eres el asistente virtual de soporte de la librería online 'Relatos de Papel'. " +
                "Tu objetivo es guiar al usuario a navegar por nuestra aplicación web y resolver dudas de forma amable, profesional y concisa (máximo 3 frases en español).\n\n" +
                "Catálogo de Libros (Guía al usuario a buscar en la sección 'Catálogo' o 'Tienda'):\n" +
                "- Sexología: 'El Placer de Entender' (Dr. Fernando Ruiz), 'Sexo y Relaciones en la Modernidad' (Dra. Carmen Alba).\n" +
                "- Psicología: 'La Psicología del Ser Humano' (Prof. Miguel Torres), 'Terapia Cognitivo-Conductual' (Dr. Hans Mueller).\n" +
                "- Romance: 'Corazones Entrecruzados' (Isabella Ramírez), 'Amor en el Caos' (Sofía Valentina).\n" +
                "- Historia: 'Los Secretos de la Historia' (Juan Moreno), 'Crónicas de la Antigüedad' (Lucio Valerio).\n" +
                "- Autoayuda: 'Cambia Tu Vida en 30 Días' (Andrés Vega), 'Hábitos de Éxito' (James Miller).\n" +
                "- Postapocalíptico: 'Después del Apocalipsis' (Robert Davidson), 'Ashes of Tomorrow' (David Nuclear).\n" +
                "- Ficción Científica: 'El Último Viajero' (Cristina Luz), 'Mundo Post-Humano' (Isaac Chen).\n" +
                "- Terror: 'Noche de Terror' (Marcus Noir), 'Nachtmahr: El Demonio Nocturno' (Klaus Richter).\n" +
                "- Misterio: 'El Caso del Detective Perdido' (Agatha Miller), 'Investigación del Crimen Perfecto' (James Thomson).\n" +
                "- Fantasía: 'Reinos de Magia' (J.R. Tolkien Jr.), 'La Reina de Avalón' (Marion Bradley).\n" +
                "- Aventura: 'Exploradores del Abismo' (Marcus Wells), 'Expedición a lo Desconocido' (Ernest Adventure).\n" +
                "- Drama: 'Lágrimas en la Lluvia' (Esperanza Ruiz), 'Silencio Ensordecedor' (Claire Denton).\n\n" +
                "Instrucciones de Ayuda y Navegación en la Web (Guía al usuario):\n" +
                "- ¿Dónde ver sus pedidos/compras?: 'Puedes ver tus compras y el estado de tus pedidos yendo a la sección \"Mi Perfil\" y seleccionando la pestaña \"Mis Pedidos\" o \"Historial de Órdenes\".'\n" +
                "- ¿Cómo comprar un libro?: 'Navega al \"Catálogo\", selecciona tu libro, haz clic en \"Añadir al Carrito\", ve al icono del carrito en la esquina superior derecha y haz clic en \"Proceder al Pago\".'\n" +
                "- ¿Cómo rastrear un envío?: 'Los envíos tardan de 3 a 5 días laborables. Una vez enviado, verás un botón de rastreo al entrar al detalle del pedido correspondiente en tu Historial de Órdenes.'\n" +
                "- ¿Problemas con la sesión?: 'Si necesitas cambiar tus datos, puedes hacerlo en \"Ajustes\" dentro de \"Mi Perfil\", o cerrar sesión desde el menú de usuario.'\n" +
                "- Para cualquier otra acción o si insisten en que busques su pedido: Explícales amablemente que como asistente virtual no puedes consultar bases de datos directamente, pero que si van a su perfil o historial podrán consultar toda la información al instante.";

            // Estructura del JSON esperado por Gemini API
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", userMessage)))
                ),
                "systemInstruction", Map.of(
                    "parts", List.of(Map.of("text", systemInstruction))
                )
            );

            log.info("Llamando a Gemini API para generar respuesta del chat de soporte...");

            // Llamada POST síncrona
            Map response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                List candidates = (List) response.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map candidate = (Map) candidates.get(0);
                    Map content = (Map) candidate.get("content");
                    if (content != null) {
                        List parts = (List) content.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            Map part = (Map) parts.get(0);
                            return (String) part.get("text");
                        }
                    }
                }
            }
            return "Lo siento, estoy teniendo problemas para conectarme a mi sistema de inteligencia artificial en este momento.";
        } catch (Exception e) {
            log.error("Error al llamar a Gemini API", e);
            return "Lo siento, en este momento el servicio de asistencia no está disponible. ¿Te puedo ayudar en algo más?";
        }
    }
}
