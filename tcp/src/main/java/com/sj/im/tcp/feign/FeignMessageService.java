/*
 * Copyright (c) ShiJu  2023 - 2023. 适度编码益脑，沉迷编码伤身，合理安排时间，享受快乐生活。
 */

package com.sj.im.tcp.feign;

import com.sj.im.common.model.ResponseVO;
import com.sj.im.common.model.message.CheckSendMessageReq;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author ShiJu
 * @version 1.0
 * @description: Feign 客户端接口，用于调用消息服务中心的 checkSend 接口
 */
@FeignClient(name = "serviceClient", url = "${netty.logicUrl}")
public interface FeignMessageService {
    /**
     * 调用消息服务中心的 checkSend 接口，检查是否可以发送消息
     *
     * @param req CheckSendMessageReq 对象，表示待检查的消息内容
     * @return RestResponse 对象，表示检查结果
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PostMapping("/message/checkSend")
    ResponseVO<Object> checkSendMessage(CheckSendMessageReq req);
}
