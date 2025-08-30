package com.chris.websocket;

import com.chris.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chris.constant.JwtClaimsConstant.USER_ID;

@ServerEndpoint("/ws/rider")
@Component
@Slf4j
public class RiderWebSocketServer {

    private static JwtTokenUtil jwtTokenUtil;

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        RiderWebSocketServer.jwtTokenUtil = jwtTokenUtil;
    }

    // 用来保存每个骑手所有WebSocket Session（支持多设备/多tab）
    private static final Map<Long, Map<String, Session>> riderSessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        String query = session.getQueryString();
        String accessToken = parseTokenFromQuery(query);

        if (accessToken == null) {
            closeSession(session, "Missing token");
            return;
        }

        Long userId = null;
        try {
            Claims accessClaims = jwtTokenUtil.parseAccessToken(accessToken);
            userId = accessClaims.get(USER_ID, Long.class);
            if (userId == null) {
                closeSession(session, "Unauthorized");
                return;
            }
        } catch (Exception e) {
            closeSession(session, "Invalid token");
            return;
        }

        session.getUserProperties().put("userId", userId);

        riderSessionMap
                .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(session.getId(), session);

        log.info("WebSocket连接建立: 骑手UserId={}，SessionId={}", userId, session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        Object idObj = session.getUserProperties().get("userId");
        if (idObj == null) return;
        Long userId;
        try {
            userId = (Long) idObj;
        } catch (Exception e) {
            return;
        }
        Map<String, Session> sessions = riderSessionMap.get(userId);
        if (sessions != null) {
            sessions.remove(session.getId());
            if (sessions.isEmpty()) {
                riderSessionMap.remove(userId);
            }
        }
        log.info("WebSocket连接关闭: 骑手UserId={}，SessionId={}", userId, session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 处理前端发来的消息（可选）
    }

    @OnError
    public void onError(Session session, Throwable error) {
        onClose(session);
        log.info("WebSocket发生错误: {}", error.getMessage());
    }

    private String parseTokenFromQuery(String query) {
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals("accessToken")) {
                return kv[1];
            }
        }
        return null;
    }

    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, reason));
        } catch (Exception ignored) {}
    }

    /** 向指定骑手所有在线客户端推送消息 */
    public static void sendToRider(Long userId, String msg) {
        Map<String, Session> sessions = riderSessionMap.get(userId);
        if (sessions != null) {
            sessions.values().forEach(session -> {
                try {
                    log.info("WebSocket推送消息: 骑手UserId={}，SessionId={}", userId, session.getId());
                    session.getBasicRemote().sendText(msg);
                } catch (Exception e) {
                    log.error("WebSocket推送失败: {}", e.getMessage());
                }
            });
        }
    }
}
