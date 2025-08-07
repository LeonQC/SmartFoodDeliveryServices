package com.chris.websocket;

import com.chris.utils.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.chris.constant.JwtClaimsConstant.USER_ID;

@ServerEndpoint("/ws/merchant")
@Component
public class MerchantWebSocketServer {

    private static JwtTokenUtil jwtTokenUtil;

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        MerchantWebSocketServer.jwtTokenUtil = jwtTokenUtil;
    }

    // 用来保存每个商家的所有WebSocket Session（支持多设备/多tab）
    private static final Map<Long, Map<String, Session>> merchantSessionMap = new ConcurrentHashMap<>();
    // key: userId, value: (key: sessionId, value: session对象)

    // 建立连接时
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // 通过 session.getQueryString() 获取 URL 参数
        String query = session.getQueryString(); // "accessToken=xxx"
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

        // 把 userId 放到 session 属性里，方便后续消息、推送使用
        session.getUserProperties().put("userId", userId);

        // 注册 session 到全局 map
        merchantSessionMap
                .computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                .put(session.getId(), session);

        // 日志
        System.out.println("WebSocket连接建立: 商家UserId=" + userId + "，SessionId=" + session.getId());
    }

    // 关闭连接时
    @OnClose
    public void onClose(Session session) {
        // 推荐统一用"userId" 作为属性名
        Object idObj = session.getUserProperties().get("userId");
        if (idObj == null) {
            return; // 兜底：没有身份信息则直接返回
        }
        Long userId;
        try {
            userId = (Long) idObj;
        } catch (Exception e) {
            return; // 类型错误直接返回
        }

        Map<String, Session> sessions = merchantSessionMap.get(userId);
        if (sessions != null) {
            sessions.remove(session.getId());
            if (sessions.isEmpty()) {
                merchantSessionMap.remove(userId);
            }
        }

        System.out.println("WebSocket连接关闭: 商家UserId=" + userId + "，SessionId=" + session.getId());
    }

    // 收到消息时（可选）
    @OnMessage
    public void onMessage(String message, Session session) {
        // 处理前端发来的消息（可选）
    }

    // 发生错误时
    @OnError
    public void onError(Session session, Throwable error) {
        onClose(session);
        System.err.println("WebSocket发生错误: " + error.getMessage());
    }

    // 辅助方法：解析 token
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

    // 辅助方法：优雅关闭连接
    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, reason));
        } catch (Exception ignored) {}
    }

    /** 向指定商家所有在线客户端推送消息 */
    public static void sendToMerchant(Long userId, String msg) {
        Map<String, Session> sessions = merchantSessionMap.get(userId);
        if (sessions != null) {
            sessions.values().forEach(session -> {
                try {
                    session.getBasicRemote().sendText(msg);
                } catch (Exception e) {
                    System.err.println("WebSocket发生错误: " + e.getMessage());
                }
            });
        }
    }
}