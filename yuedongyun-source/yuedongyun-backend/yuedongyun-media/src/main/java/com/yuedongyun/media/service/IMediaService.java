package com.yuedongyun.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.media.domain.dto.MediaDTO;
import com.yuedongyun.media.domain.dto.MediaUploadResultDTO;
import com.yuedongyun.media.domain.po.Media;
import com.yuedongyun.media.domain.query.MediaQuery;
import com.yuedongyun.media.domain.vo.MediaVO;
import com.yuedongyun.media.domain.vo.VideoPlayVO;

/**
 * <p>
 * 媒资表，主要是视频文件 服务类
 * </p>
 *
 * @author 虎哥
 * @since 2022-06-30
 */
public interface IMediaService extends IService<Media> {

    String getUploadSignature();

    VideoPlayVO getPlaySignatureBySessionId(Long fileId);

    MediaDTO save(MediaUploadResultDTO mediaResult);

    void updateMediaProcedureResult(Media media);

    void deleteMedia(String fileId);

    VideoPlayVO getPlaySignatureByMediaId(Long mediaId);

    PageDTO<MediaVO> queryMediaPage(MediaQuery query);
}
