package com.yuedongyun.message.service;

import com.yuedongyun.api.dto.sms.SmsInfoDTO;
import com.yuedongyun.api.dto.user.UserDTO;
import com.yuedongyun.message.domain.po.NoticeTemplate;

import java.util.List;

public interface ISmsService {
    void sendMessageByTemplate(NoticeTemplate noticeTemplate, List<UserDTO> users);

    void sendMessage(SmsInfoDTO smsInfoDTO);

    void sendMessageAsync(SmsInfoDTO smsInfoDTO);
}
