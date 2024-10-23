package msa.devmix.service.implement;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msa.devmix.domain.constant.NotificationType;
import msa.devmix.domain.notification.Notification;
import msa.devmix.domain.user.User;
import msa.devmix.dto.NotificationDto;
import msa.devmix.repository.EmitterRepository;
import msa.devmix.repository.NotificationRepository;
import msa.devmix.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 1. 알림 이벤트 Listen
 * 2. 이벤트 발생시 Notification 을 DB 에 저장해둔다.
 * 3. 특정 클라이언트가 첫 connect 했을 때, 이전 이벤트를 모두 다 가져온다.
 * (하지만, 연결 끊기는게 잦다면, 다시 연결할 때마다 연결에 대한 비용 및 DB 조회에 대한 비용이 존재한다. 이 문제는 추후 해결)
 * 4.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; //1 hour

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;


    /**
     * 첫 연결 or 재 연결 구독 설정
     */
    public SseEmitter connect(User user) {

        SseEmitter sseEmitter = emitterRepository.save(user.getUsername(), new SseEmitter(DEFAULT_TIMEOUT));

        //Configure callbacks for a specific emitter
        sseEmitter.onCompletion(() -> {
            log.info("onCompletion callback");
            emitterRepository.deleteByUsername(user.getUsername());
        });
        sseEmitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitterRepository.deleteByUsername(user.getUsername());
        });

        //Send dummy event to prevent 503 errors
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("connect") // 해당 이벤트의 이름 지정
                    .data("connected!")); // 503 에러 방지를 위한 더미 데이터
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //해당 회원에 대한 읽지 않은 알림 조회
        List<Notification> notifications = notificationRepository.findAllByUser_Username(user.getEmail())
                .stream()
                .filter(notification -> !notification.isRead())
                .collect(Collectors.toList());

        //읽지 않은 알림 전송
        if (!notifications.isEmpty()) {
            sendNotification(user.getUsername(), sseEmitter, notifications);
        }

        return sseEmitter;
    }

    /**
     * 리스너를 통해 알림을 1개 생성하고 해당 알림을 수신하는 클라이언트에게 바로 전송
     */
    public void send(User user, NotificationType notificationType, String content) {
        //알림 객체 생성
        List<Notification> notifications = new ArrayList<>();
        Notification notification = notificationRepository.save(Notification.createNotification(user, notificationType, content));
        notifications.add(notification);

        String username = user.getUsername();
//        SseEmitter sseEmitter = emitterRepository.findByUsername(username);

//        sendNotification(username, sseEmitter, notifications);
    }


    //해당 SseEmitter 를 통해 이벤트를 실제로 전송
    private void sendNotification(String username, SseEmitter emitter, List<Notification> data) {
        try {
            emitter.send(SseEmitter.event() //SseEmitter.event() 호출하여 SseEventBuilder 객체 생성
                    .name("sse")
                    .data(data.stream()
                            .map(NotificationDto::from)
                            .collect(Collectors.toList())
                    )
            );
        } catch (IOException exception) {
            emitterRepository.deleteByUsername(username);
        }
    }

}
