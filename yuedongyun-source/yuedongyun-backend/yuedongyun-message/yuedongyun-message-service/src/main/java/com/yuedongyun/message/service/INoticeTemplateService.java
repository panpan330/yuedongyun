package com.yuedongyun.message.service;

import com.yuedongyun.message.domain.dto.NoticeTemplateDTO;
import com.yuedongyun.message.domain.dto.NoticeTemplateFormDTO;
import com.yuedongyun.message.domain.query.NoticeTemplatePageQuery;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.message.domain.po.NoticeTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 通知模板 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-08-19
 */
public interface INoticeTemplateService extends IService<NoticeTemplate> {

    Long saveNoticeTemplate(NoticeTemplateFormDTO noticeTemplateFormDTO);

    void updateNoticeTemplate(NoticeTemplateFormDTO noticeTemplateFormDTO);

    PageDTO<NoticeTemplateDTO> queryNoticeTemplates(NoticeTemplatePageQuery pageQuery);

    NoticeTemplateDTO queryNoticeTemplate(Long id);

    NoticeTemplate queryByCode(String code);
}
