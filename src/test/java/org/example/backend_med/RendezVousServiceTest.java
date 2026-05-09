package org.example.backend_med;

import org.example.backend_med.Dto.CreateRendezVousRequest;
import org.example.backend_med.Dto.RendezVousResponseDto;
import org.example.backend_med.Models.*;
import org.example.backend_med.Repository.*;
import org.example.backend_med.Services.NotificationService;
import org.example.backend_med.Services.RendezVousService;
import org.example.backend_med.websocket.QueueWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for RendezVousService
 *
 * Level 1 — Unit tests  (this file)     : mock everything, test logic only
 * Level 2 — Integration tests            : see RendezVousServiceIT.java
 * Level 3 — Race condition test          : see RendezVousRaceConditionTest.java
 */
@ExtendWith(MockitoExtension.class)
class RendezVousServiceTest {

    // ── Mocks ────────────────────────────────────────────────────────────────
    @Mock
    private QueueWebSocketHandler wsHandler;
    @Mock
    private RendezVousRepo rendezVousRepo;
    @Mock
    private NotificationService notificationService;
    @Mock
    private PatientRepo patientRepo;
    @Mock
    private MedecinRepo medecinRepo;
    @Mock
    private HoraireRepo horaireRepo;
    @Mock
    private SpecialiteRepo specialiteRepo;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RendezVousService service;

    // ── Fixtures ─────────────────────────────────────────────────────────────
    private Patient patient;
    private Medecin medecin;
    private Horaire horaire;
    private Specialite specialite;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setNom("Dupont");

        medecin = new Medecin();
        medecin.setId(2L);
        medecin.setNom("Dr. Martin");

        horaire = new Horaire();
        horaire.setIdHoraire(3L);
        horaire.setDate(LocalDate.now());

        specialite = new Specialite();
        specialite.setId(4L);
        specialite.setNomspecialite("Cardiologie");
    }

    // =========================================================================
    // CREATE RENDEZ-VOUS
    // =========================================================================
    @Nested
    @DisplayName("createRendezVous()")
    class CreateRendezVous {

        private CreateRendezVousRequest validRequest() {
            CreateRendezVousRequest req = new CreateRendezVousRequest();
            req.setPatientId(1L);
            req.setMedecinId(2L);
            req.setHoraireId(3L);
            req.setSpecialiteId(4L);
            req.setDate(LocalDate.now());
            return req;
        }

        @Test
        @DisplayName("✅ Happy path — crée le RDV avec queueNumber correct")
        void shouldCreateRendezVousWithCorrectQueueNumber() {
            // ARRANGE
            when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
            when(rendezVousRepo.existsActiveRendezVous(1L, 2L)).thenReturn(false);
            when(medecinRepo.findById(2L)).thenReturn(Optional.of(medecin));
            when(horaireRepo.findById(3L)).thenReturn(Optional.of(horaire));
            when(specialiteRepo.findById(4L)).thenReturn(Optional.of(specialite));
            when(rendezVousRepo.countByMedecinAndDateWithLock(2L, LocalDate.now())).thenReturn(4L);

            RendezVous saved = new RendezVous();
            saved.setId(99L);
            saved.setPatient(patient);
            saved.setMedecin(medecin);

            saved.setQueueNumber(5);
            saved.setStatus("EN_ATTENTE");
            when(rendezVousRepo.save(any())).thenReturn(saved);

            // ACT
            RendezVous result = service.createRendezVous(validRequest());

            // ASSERT
            assertThat(result.getQueueNumber()).isEqualTo(5);    // existingCount(4) + 1
            assertThat(result.getStatus()).isEqualTo("EN_ATTENTE");

            // Verify event published (WebSocket/notification via AFTER_COMMIT listener)
            ArgumentCaptor<RendezVousService.RendezVousCreatedEvent> captor =
                    ArgumentCaptor.forClass(RendezVousService.RendezVousCreatedEvent.class);
            verify(eventPublisher).publishEvent(captor.capture());
            assertThat(captor.getValue().patientId()).isEqualTo(1L);
            assertThat(captor.getValue().medecinId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("✅ Premier RDV du médecin — queueNumber = 1")
        void shouldAssignQueueNumberOneForFirstAppointment() {
            when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
            when(rendezVousRepo.existsActiveRendezVous(1L, 2L)).thenReturn(false);
            when(medecinRepo.findById(2L)).thenReturn(Optional.of(medecin));
            when(horaireRepo.findById(3L)).thenReturn(Optional.of(horaire));
            when(specialiteRepo.findById(4L)).thenReturn(Optional.of(specialite));
            when(rendezVousRepo.countByMedecinAndDateWithLock(2L, LocalDate.now())).thenReturn(0L);

            RendezVous saved = new RendezVous();
            saved.setId(1L);
            saved.setPatient(patient);
            saved.setMedecin(medecin);
            saved.setQueueNumber(1);
            saved.setStatus("EN_ATTENTE");
            when(rendezVousRepo.save(any())).thenReturn(saved);

            RendezVous result = service.createRendezVous(validRequest());

            assertThat(result.getQueueNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("❌ Patient introuvable → IllegalArgumentException")
        void shouldThrowWhenPatientNotFound() {
            when(patientRepo.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.createRendezVous(validRequest()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Patient introuvable");
        }

        @Test
        @DisplayName("❌ Médecin introuvable → IllegalArgumentException")
        void shouldThrowWhenMedecinNotFound() {
            when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
            when(rendezVousRepo.existsActiveRendezVous(1L, 2L)).thenReturn(false);
            when(medecinRepo.findById(2L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.createRendezVous(validRequest()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Médecin introuvable");
        }

        @Test
        @DisplayName("❌ Patient a déjà un RDV actif → IllegalStateException")
        void shouldRejectDuplicateActiveRendezVous() {
            when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
            when(rendezVousRepo.existsActiveRendezVous(1L, 2L)).thenReturn(true);

            assertThatThrownBy(() -> service.createRendezVous(validRequest()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("rendez-vous actif");

            // Ensure nothing was saved
            verify(rendezVousRepo, never()).save(any());
        }

        @Test
        @DisplayName("✅ Événement publié contient les bons IDs")
        void shouldPublishEventWithCorrectIds() {
            when(patientRepo.findById(1L)).thenReturn(Optional.of(patient));
            when(rendezVousRepo.existsActiveRendezVous(1L, 2L)).thenReturn(false);
            when(medecinRepo.findById(2L)).thenReturn(Optional.of(medecin));
            when(horaireRepo.findById(3L)).thenReturn(Optional.of(horaire));
            when(specialiteRepo.findById(4L)).thenReturn(Optional.of(specialite));
            when(rendezVousRepo.countByMedecinAndDateWithLock(any(), any())).thenReturn(0L);

            RendezVous saved = new RendezVous();
            saved.setId(42L);
            saved.setPatient(patient);
            saved.setMedecin(medecin);
            saved.setQueueNumber(1);
            saved.setStatus("EN_ATTENTE");
            when(rendezVousRepo.save(any())).thenReturn(saved);

            service.createRendezVous(validRequest());

            ArgumentCaptor<RendezVousService.RendezVousCreatedEvent> captor =
                    ArgumentCaptor.forClass(RendezVousService.RendezVousCreatedEvent.class);
            verify(eventPublisher).publishEvent(captor.capture());

            RendezVousService.RendezVousCreatedEvent event = captor.getValue();
            assertThat(event.rdvId()).isEqualTo(42L);
            assertThat(event.patientId()).isEqualTo(1L);
            assertThat(event.medecinId()).isEqualTo(2L);
            assertThat(event.patientNom()).isEqualTo("Dupont");
            assertThat(event.medecinNom()).isEqualTo("Dr. Martin");
        }
    }

    // =========================================================================
    // CALL NEXT PATIENT
    // =========================================================================
    @Nested
    @DisplayName("callNextPatient()")
    class CallNextPatient {

        @Test
        @DisplayName("✅ Appelle le prochain patient et complete le précédent")
        void shouldCompleteCurrentAndCallNext() {
            RendezVous inProgress = new RendezVous();
            inProgress.setId(10L);
            inProgress.setStatus("EN_COURS");
            inProgress.setPatient(patient);
            inProgress.setMedecin(medecin);

            RendezVous waiting = new RendezVous();
            waiting.setId(11L);
            waiting.setStatus("EN_ATTENTE");
            waiting.setPatient(patient);
            waiting.setHoraire(horaire);
            waiting.setMedecin(medecin);
            waiting.setQueueNumber(2);

            when(rendezVousRepo.findInProgressByMedecin(2L)).thenReturn(Optional.of(inProgress));
            when(rendezVousRepo.findWaitingByMedecinAndDate(eq(2L), any())).thenReturn(List.of(waiting));
            when(rendezVousRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.callNextPatient(2L);

            assertThat(inProgress.getStatus()).isEqualTo("COMPLETED");
            assertThat(waiting.getStatus()).isEqualTo("EN_COURS");
            verify(eventPublisher).publishEvent(any(RendezVousService.PatientCalledEvent.class));
        }

        @Test
        @DisplayName("✅ Aucun patient EN_COURS — fonctionne quand même")
        void shouldWorkEvenWhenNoPatientInProgress() {
            RendezVous waiting = new RendezVous();
            waiting.setId(11L);
            waiting.setStatus("EN_ATTENTE");
            waiting.setPatient(patient);
            waiting.setMedecin(medecin);
            waiting.setHoraire(horaire);

            when(rendezVousRepo.findInProgressByMedecin(2L)).thenReturn(Optional.empty());
            when(rendezVousRepo.findWaitingByMedecinAndDate(eq(2L), any())).thenReturn(List.of(waiting));
            when(rendezVousRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            assertThatNoException().isThrownBy(() -> service.callNextPatient(2L));
        }

        @Test
        @DisplayName("❌ File vide → IllegalStateException")
        void shouldThrowWhenQueueEmpty() {
            when(rendezVousRepo.findInProgressByMedecin(2L)).thenReturn(Optional.empty());
            when(rendezVousRepo.findWaitingByMedecinAndDate(eq(2L), any())).thenReturn(List.of());

            assertThatThrownBy(() -> service.callNextPatient(2L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Aucun patient en attente");

            verify(eventPublisher, never()).publishEvent(any());
        }
    }

    // =========================================================================
    // UPDATE STATUS
    // =========================================================================
    @Nested
    @DisplayName("updateStatus()")
    class UpdateStatus {

        @Test
        @DisplayName("✅ Statut valide → mis à jour")
        void shouldUpdateToValidStatus() {
            RendezVous rdv = new RendezVous();
            rdv.setId(1L);
            rdv.setStatus("EN_ATTENTE");
            rdv.setPatient(patient);
            rdv.setMedecin(medecin);
            rdv.setHoraire(horaire);
            rdv.setSpecialite(specialite);
            rdv.setQueueNumber(1);

            when(rendezVousRepo.findById(1L)).thenReturn(Optional.of(rdv));
            when(rendezVousRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.updateStatus(1L, "EN_COURS");

            assertThat(rdv.getStatus()).isEqualTo("EN_COURS");
            verify(eventPublisher).publishEvent(any(RendezVousService.StatusUpdatedEvent.class));
        }

        @Test
        @DisplayName("✅ Statut en minuscules → normalisé en majuscules")
        void shouldNormalizeStatusToUpperCase() {
            RendezVous rdv = new RendezVous();
            rdv.setId(1L);
            rdv.setPatient(patient);
            rdv.setMedecin(medecin);
            rdv.setHoraire(horaire);
            rdv.setSpecialite(specialite);
            rdv.setQueueNumber(1);

            when(rendezVousRepo.findById(1L)).thenReturn(Optional.of(rdv));
            when(rendezVousRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.updateStatus(1L, "annule");

            assertThat(rdv.getStatus()).isEqualTo("ANNULE");
        }

        @Test
        @DisplayName("❌ Statut invalide → IllegalArgumentException")
        void shouldRejectInvalidStatus() {
            RendezVous rdv = new RendezVous();
            rdv.setId(1L);
            rdv.setPatient(patient);
            rdv.setMedecin(medecin);
            when(rendezVousRepo.findById(1L)).thenReturn(Optional.of(rdv));

            assertThatThrownBy(() -> service.updateStatus(1L, "INVALIDE"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Statut invalide");

            verify(rendezVousRepo, never()).save(any());
        }

        @Test
        @DisplayName("❌ RDV introuvable → RuntimeException")
        void shouldThrowWhenRendezVousNotFound() {
            when(rendezVousRepo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.updateStatus(99L, "ANNULE"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("introuvable");
        }
    }

    // =========================================================================
    // CANCEL
    // =========================================================================
    @Nested
    @DisplayName("cancelRendezVous()")
    class CancelRendezVous {

        @Test
        @DisplayName("✅ Annule le RDV et publie un événement")
        void shouldCancelAndPublishEvent() {
            RendezVous rdv = new RendezVous();
            rdv.setId(1L);
            rdv.setStatus("EN_ATTENTE");
            rdv.setPatient(patient);
            rdv.setMedecin(medecin);

            when(rendezVousRepo.findById(1L)).thenReturn(Optional.of(rdv));
            when(rendezVousRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            RendezVous result = service.cancelRendezVous(1L);

            assertThat(result.getStatus()).isEqualTo("ANNULE");
            verify(eventPublisher).publishEvent(any(RendezVousService.StatusUpdatedEvent.class));
        }

        @Test
        @DisplayName("❌ RDV introuvable → IllegalArgumentException")
        void shouldThrowWhenNotFound() {
            when(rendezVousRepo.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.cancelRendezVous(99L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("introuvable");
        }
    }

    // =========================================================================
    // AFTER_COMMIT LISTENERS
    // =========================================================================
    @Nested
    @DisplayName("onRendezVousCreated() — AFTER_COMMIT listener")
    class OnRendezVousCreated {

        @Test
        @DisplayName("✅ Notification réussie — WebSocket appelé")
        void shouldCallWebSocketAndNotify() throws Exception {
            when(patientRepo.getReferenceById(1L)).thenReturn(patient);
            when(medecinRepo.getReferenceById(2L)).thenReturn(medecin);

            service.onRendezVousCreated(new RendezVousService.RendezVousCreatedEvent(
                    42L, 1L, 2L, "Dupont", "Dr. Martin"
            ));

            verify(wsHandler).notifyPatient(1L, 2L);
            verify(notificationService, times(2)).notify(any(), anyString(), any());
        }

        @Test
        @DisplayName("✅ Notification échoue — WebSocket toujours appelé, pas d'exception")
        void shouldNotThrowWhenNotificationFails() {
            when(patientRepo.getReferenceById(1L)).thenReturn(patient);
            when(medecinRepo.getReferenceById(2L)).thenReturn(medecin);
            doThrow(new RuntimeException("SMTP down"))
                    .when(notificationService).notify(any(), anyString(), any());

            assertThatNoException().isThrownBy(() ->
                    service.onRendezVousCreated(new RendezVousService.RendezVousCreatedEvent(
                            42L, 1L, 2L, "Dupont", "Dr. Martin"
                    ))
            );

            verify(wsHandler).notifyPatient(1L, 2L);
        }

        // =========================================================================
        // UNSUPPORTED OPERATIONS
        // =========================================================================
        @Nested
        @DisplayName("Méthodes non implémentées")
        class UnimplementedMethods {

            @Test
            @DisplayName("getAllRendezVous() → UnsupportedOperationException")
            void getAllShouldThrow() {
                assertThatThrownBy(() -> service.getAllRendezVous())
                        .isInstanceOf(UnsupportedOperationException.class);
            }

            @Test
            @DisplayName("getRendezVousByDate() → UnsupportedOperationException")
            void getByDateShouldThrow() {
                assertThatThrownBy(() -> service.getRendezVousByDate(LocalDate.now()))
                        .isInstanceOf(UnsupportedOperationException.class);
            }
        }
    }
}