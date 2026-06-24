package com.yuedongyun.campaign.service.impl;

import com.yuedongyun.campaign.domain.po.Campaign;
import com.yuedongyun.campaign.mapper.CampaignMapper;
import com.yuedongyun.campaign.service.ICampaignService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 促销活动，形式多种多样，例如：优惠券 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-09-06
 */
@Service
public class CampaignServiceImpl extends ServiceImpl<CampaignMapper, Campaign> implements ICampaignService {

}

