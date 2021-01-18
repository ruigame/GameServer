package com.game.async.asynchttp.example;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 5:08 下午
 */

import com.game.async.asynchttp.AsyncHttpClientUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.concurrent.FutureCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建角色后游戏角色信息上报借口
 */
public class PlayerCreateReportService {

    public void onCreate(Player player) {
        String url = ""; //createplayerReport.properties
        String gameCode = "";
        String key = "";
        String ip = "";
        int serverId = player.getServerId();
        String account = player.getAccount();
        long playerId = player.getPlayerId();
        int time = player.getCreateTime();
        Map<String, Object> params = new HashMap<>();
        params.put("serverId", serverId);
        params.put("account", account);
        params.put("playerId", playerId);
        params.put("time", time);
        params.put("sign", DigestUtils.md5Hex(gameCode + serverId + account + ip + time + key));
        AsyncHttpClientUtils.get(url, params, new FutureCallback<String>() {
            @Override
            public void completed(String result) {
                try {
//                    if ("1".equals(result)) {
//                        log.info("创角成功上报，请求url：{},params:{}", url, params);
//                    } else {
//                        log.info("创角失败上报，请求url：{},params:{}", url, params);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Exception e) {
//                log.info("创角失败上报，请求url：{},params:{} e:{}", url, params, e);
            }

            @Override
            public void cancelled() {

            }
        });
    }
}
