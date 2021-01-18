package com.game.async.asynchttp.example;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.async.asynchttp.AsyncHttpClientUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.concurrent.FutureCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liguorui
 * @Date: 2020/9/20 5:34 下午
 */
public class ChatReport {

    public void chatReport(Player player) {
        String url = ""; //chatReportCheck.properties
        String gameCode = "";
        String key = "";
        String ip = "";
        String content = "";
        int serverId = player.getServerId();
        String account = player.getAccount();
        long playerId = player.getPlayerId();
        int time = player.getCreateTime();
        Map<String, Object> params = new HashMap<>();
        params.put("serverId", serverId);
        params.put("account", account);
        params.put("playerId", playerId);
        params.put("time", time);
        params.put("content", content);
        params.put("sign", DigestUtils.md5Hex(gameCode + serverId + account + ip + time + key));
        AsyncHttpClientUtils.post(url, params, new FutureCallback<String>() {
            @Override
            public void completed(String result) {
                try {
//                    log.debug("请求url:{}, 参数：{}, 结果:{}", url, params, result);
                    JSONObject json = JSON.parseObject(result);
                    int state = json.getInteger("state");
                    if (state == 1) {
                        //成功
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Exception e) {

            }

            @Override
            public void cancelled() {

            }
        });
    }
}
