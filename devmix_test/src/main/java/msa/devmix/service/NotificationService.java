package msa.devmix.service;

import msa.devmix.domain.constant.NotificationType;
import msa.devmix.domain.user.User;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    SseEmitter connect(User user);
    void send(User user, NotificationType notificationType, String content);
}
