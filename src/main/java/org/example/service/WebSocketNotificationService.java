package org.example.service;


import org.example.model.HumanBeing;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyHumanBeingCreated(HumanBeing humanBeing) {
        messagingTemplate.convertAndSend("/topic/humans/created", humanBeing);
    }

    public void notifyHumanBeingUpdated(HumanBeing humanBeing) {
        messagingTemplate.convertAndSend("/topic/humans/updated", humanBeing);
    }

    public void notifyHumanBeingDeleted(Integer id) {
        messagingTemplate.convertAndSend("/topic/humans/deleted", id);
    }

    public void notifyImportCompleted(String username, Integer count) {
        ImportNotification notification = new ImportNotification(username, count);
        messagingTemplate.convertAndSend("/topic/import/completed", notification);
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ImportNotification {
        private String username;
        private Integer objectsImported;
    }
}
