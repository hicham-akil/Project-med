package org.example.backend_med.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.backend_med.Dto.QueuePositionDto;
import org.example.backend_med.Repository.RendezVousRepo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class QueueWebSocketHandler extends TextWebSocketHandler {

    private final RendezVousRepo rendezVousRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public QueueWebSocketHandler(RendezVousRepo rendezVousRepo) {
        this.rendezVousRepo = rendezVousRepo;
    }

    // =========================
    // HANDSHAKE START
    // =========================
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        log.info("🔥 WS HANDSHAKE STARTED");
        log.info("🌐 URI: {}", session.getUri());

        Long patientId = getParam(session, "patientId");
        Long medecinId = getParam(session, "medecinId");

        log.info("📦 PARSED PARAMS -> patientId={}, medecinId={}", patientId, medecinId);

        if (patientId == null || medecinId == null) {
            log.error("❌ HANDSHAKE FAILED: missing params");
            closeWithError(session, "Missing patientId or medecinId");
            return;
        }

        sessions.put(patientId, session);

        log.info("✅ WS CONNECTED SUCCESSFULLY");
        log.info("🧠 SESSION STORED patientId={} sessionId={}", patientId, session.getId());

        pushPositionToPatient(patientId, medecinId);
    }

    // =========================
    // DISCONNECT
    // =========================
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        Long patientId = getParam(session, "patientId");

        log.warn("⚠️ WS CLOSED sessionId={}, status={}", session.getId(), status);

        if (patientId != null) {
            sessions.remove(patientId);
            log.info("🧹 SESSION REMOVED patientId={}", patientId);
        }
    }

    // =========================
    // NOTIFY API
    // =========================
    public void notifyPatient(Long patientId, Long medecinId) {
        log.info("📢 notifyPatient called patientId={}, medecinId={}", patientId, medecinId);
        pushPositionToPatient(patientId, medecinId);
    }

    public void notifyWaitingPatients(Long medecinId) {
        log.info("📢 notifyWaitingPatients medecinId={}", medecinId);

        rendezVousRepo
                .findWaitingByMedecinAndDate(medecinId, LocalDate.now())
                .forEach(rdv ->
                        pushPositionToPatient(rdv.getPatient().getId(), medecinId)
                );
    }

    public void notifyCalledPatient(Long patientId) {

        log.info("📢 notifyCalledPatient patientId={}", patientId);

        WebSocketSession session = sessions.get(patientId);

        if (session == null) {
            log.warn("❌ No session found for called patient {}", patientId);
            return;
        }

        if (!session.isOpen()) {
            log.warn("❌ Session closed for patient {}", patientId);
            return;
        }

        send(session, new QueuePositionDto(
                null,
                patientId,
                null,
                0,
                "EN_COURS",
                0,
                0,
                true
        ));
    }

    // =========================
    // CORE LOGIC
    // =========================
    private void pushPositionToPatient(Long patientId, Long medecinId) {

        log.info("⚙️ Computing queue position patientId={}, medecinId={}", patientId, medecinId);

        WebSocketSession session = sessions.get(patientId);

        if (session == null) {
            log.warn("❌ NO SESSION FOUND patientId={}", patientId);
            return;
        }

        if (!session.isOpen()) {
            log.warn("❌ SESSION CLOSED patientId={}", patientId);
            sessions.remove(patientId);
            return;
        }

        rendezVousRepo
                .findActiveByPatientAndMedecinAndDate(patientId, medecinId, LocalDate.now())
                .ifPresentOrElse(
                        rdv -> {

                            boolean calledNow = "EN_COURS".equals(rdv.getStatus());

                            long position = calledNow ? 0 :
                                    rendezVousRepo.countPatientsBeforeInQueue(
                                            medecinId,
                                            rdv.getQueueNumber(),
                                            rdv.getHoraire().getDate()
                                    );

                            QueuePositionDto payload = new QueuePositionDto(
                                    rdv.getId(),
                                    patientId,
                                    medecinId,
                                    rdv.getQueueNumber(),
                                    rdv.getStatus(),
                                    position,
                                    (int) (position * 10),
                                    calledNow
                            );

                            log.info("📤 SENDING PAYLOAD: {}", payload);

                            send(session, payload);
                        },
                        () -> log.warn(
                                "⚠️ No active RDV found patientId={} medecinId={}",
                                patientId,
                                medecinId
                        )
                );
    }

    // =========================
    // SEND
    // =========================
    private void send(WebSocketSession session, Object payload) {

        try {
            if (session == null || !session.isOpen()) {
                log.warn("❌ Cannot send, session invalid");
                return;
            }

            String json = objectMapper.writeValueAsString(payload);

            log.info("📡 WS SEND: {}", json);

            session.sendMessage(new TextMessage(json));

        } catch (Exception e) {
            log.error("❌ WS SEND ERROR: {}", e.getMessage());
        }
    }

    // =========================
    // ERROR CLOSE
    // =========================
    private void closeWithError(WebSocketSession session, String reason) {

        try {
            log.error("💥 CLOSING SOCKET: {}", reason);

            session.sendMessage(new TextMessage("{\"error\":\"" + reason + "\"}"));
            session.close(CloseStatus.BAD_DATA);

        } catch (Exception e) {
            log.error("❌ closeWithError failed: {}", e.getMessage());
        }
    }

    // =========================
    // PARAM DEBUGGER
    // =========================
    private Long getParam(WebSocketSession session, String key) {

        try {
            if (session.getUri() == null) {
                log.error("❌ URI is NULL");
                return null;
            }

            String query = session.getUri().getQuery();

            if (query == null) {
                log.error("❌ QUERY STRING IS NULL");
                return null;
            }

            log.info("🔎 QUERY STRING: {}", query);

            for (String part : query.split("&")) {
                String[] kv = part.split("=");

                if (kv.length == 2 && kv[0].equals(key)) {
                    Long value = Long.parseLong(kv[1]);
                    log.info("✅ PARAM FOUND {}={}", key, value);
                    return value;
                }
            }

            log.error("❌ PARAM NOT FOUND: {}", key);

        } catch (Exception e) {
            log.error("❌ getParam error: {}", e.getMessage());
        }

        return null;
    }
}