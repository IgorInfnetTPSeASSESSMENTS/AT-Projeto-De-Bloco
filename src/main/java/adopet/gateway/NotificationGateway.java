package adopet.gateway;

public interface NotificationGateway {
    void notifyCreation(Long requestId, String email);
    void notifyApproval(Long requestId, String email);
    void notifyRejection(Long requestId, String email);
    void notifyCancellation(Long requestId, String email);
}