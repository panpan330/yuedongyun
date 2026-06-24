package com.yuedongyun.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuedongyun.api.client.training.TrainingClient;
import com.yuedongyun.api.dto.training.SubNumAndTrainingNumDTO;
import com.yuedongyun.common.domain.dto.PageDTO;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.user.domain.po.UserDetail;
import com.yuedongyun.user.domain.query.UserPageQuery;
import com.yuedongyun.user.domain.vo.CoachPageVO;
import com.yuedongyun.common.enums.UserType;
import com.yuedongyun.user.service.ICoachService;
import com.yuedongyun.user.service.IUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 教练详情表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2022-07-12
 */
@Service
public class CoachServiceImpl implements ICoachService {
    @Autowired
    private IUserDetailService detailService;
    @Autowired
    private TrainingClient trainingClient;

    @Override
    public PageDTO<CoachPageVO> queryCoachPage(UserPageQuery pageQuery) {
        // 1.分页参数
        Page<UserDetail> page = detailService.queryUserDetailByPage(pageQuery, UserType.COACH);
        // 2.处理返回值
        List<UserDetail> records = page.getRecords();
        // 2.1.查询教练的试题数量、训练数量
        List<Long> ids = records.stream().map(UserDetail::getId).collect(Collectors.toList());
        List<SubNumAndTrainingNumDTO> countDTOs = trainingClient.infoByCoachIds(ids);
        Map<Long, SubNumAndTrainingNumDTO> map = countDTOs.stream()
                .collect(Collectors.toMap(SubNumAndTrainingNumDTO::getCoachId, s -> s));
        // 2.2.数据转换
        List<CoachPageVO> list = new ArrayList<>(records.size());
        for (UserDetail record : records) {
            CoachPageVO coachPageVO = BeanUtils.toBean(record, CoachPageVO.class);
            SubNumAndTrainingNumDTO sc = map.get(coachPageVO.getId());
            coachPageVO.setTrainingAmount(sc.getTrainingNum());
            coachPageVO.setExamQuestionAmount(sc.getAssessmentNum());
            list.add(coachPageVO);
        }
        return new PageDTO<>(page.getTotal(), page.getPages(), list);
    }
}

