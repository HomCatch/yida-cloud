package com.etar.purifier.modules.websokect;

import com.alibaba.fastjson.JSONArray;
import com.etar.purifier.modules.dev.controller.DevController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocketServer具体方法实现类
 *
 * @author hzh
 * @date 2018/10/25
 */
@ServerEndpoint(value = "/websocket/{id}")
@Component
@EnableScheduling
public class WebSocketServer {
    private static Logger log = LoggerFactory.getLogger(DevController.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "id") int id) {
        this.session = session;
        Map<String, String> pathParameters = session.getPathParameters();
        System.out.println(JSONArray.toJSONString(pathParameters));
        //加入set中
        webSocketSet.add(this);
        //在线数加1
        addOnlineCount();
        log.info("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage(id + "连接成功");
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        webSocketSet.remove(this);
        //在线数减1
        subOnlineCount();
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息:" + message);
        Map<String, String> pathParameters = session.getPathParameters();

        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(pathParameters.get("id") + "消息" + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param error
     */
    @OnError
    public void onError(Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }


    private void sendMessage(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);

    }


    /**
     * 群发自定义消息
     *
     * @throws IOException
     */
    public static void sendInfo(String msg) throws IOException {
        log.info(msg);
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(msg);
            } catch (IOException e) {
                continue;
            }
        }

    }

    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}