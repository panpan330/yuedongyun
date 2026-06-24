package com.yuedongyun.training.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuedongyun.api.client.user.UserClient;
import com.yuedongyun.api.dto.user.UserDTO;
import com.yuedongyun.common.constants.ErrorInfo;
import com.yuedongyun.common.exceptions.DbException;
import com.yuedongyun.common.utils.BeanUtils;
import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.training.domain.po.TrainingCoach;
import com.yuedongyun.training.domain.vo.TrainingCoachVO;
import com.yuedongyun.training.mapper.TrainingCoachMapper;
import com.yuedongyun.training.service.ITrainingCoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 训练教练关系表草稿 服务实现类
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Service
public class TrainingCoachServiceImpl extends ServiceImpl<TrainingCoachMapper, TrainingCoach> implements ITrainingCoachService {

    @Autowired
    private UserClient userClient;

    @Override
    public List<TrainingCoachVO> queryCoachs(Long couserId) {
        //1.查询条件
        LambdaQueryWrapper<TrainingCoach> queryWrapper =
                Wrappers.lambdaQuery(TrainingCoach.class)
                        .eq(TrainingCoach::getTrainingId, couserId);
        //2.查询数据
        List<TrainingCoach> trainingCoachs = baseMapper.selectList(queryWrapper);
        //2.1.数据判空
        if (CollUtils.isEmpty(trainingCoachs)) {
            return null;
        }
        //3.查询教练信息
        List<UserDTO> coachDetailDTOS =
                userClient.queryUserByIds(
                        trainingCoachs
                                .stream()
                                .map(TrainingCoach::getCoachId)
                                .collect(Collectors.toList()));
        //3.1.教练id+教练信息map
        Map<Long, UserDTO> coachDetailDTOMap =
                coachDetailDTOS
                        .stream()
                        .collect(Collectors.toMap(UserDTO::getId,
                                CoachDetailDTO -> CoachDetailDTO));
        //4.组装数据
        return BeanUtils.copyList(trainingCoachs, TrainingCoachVO.class,
                (trainingCoach, trainingCoachVO) -> {
                    //4.1.教练信息
                    UserDTO coachDetailDTO = coachDetailDTOMap.get(trainingCoach.getCoachId());
                    if (coachDetailDTO != null) {
                        //4.2设置教练形象照
                        trainingCoachVO.setIcon(coachDetailDTO.getPhoto());
                        //4.3设置教练姓名
                        trainingCoachVO.setName(coachDetailDTO.getName());
                        //4.4.设置教练简介
                        trainingCoachVO.setIntroduce(coachDetailDTO.getIntro());
                        //4.5.设置教练职业
                        trainingCoachVO.setJob(coachDetailDTO.getJob());
                    }
                    //4.6.设置教练id
                    trainingCoachVO.setId(trainingCoach.getCoachId());
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {DbException.class, Exception.class})
    public void deleteByTrainingId(Long trainingrId) {
        //1.删除教练-训练关系
        if (baseMapper.deleteByTrainingId(trainingrId) <= 0) {
            throw new DbException(ErrorInfo.Msg.DB_DELETE_EXCEPTION);
        }
    }

    @Override
    public List<Long> getCoachIdOfTraining(Long trainingId) {
        //1.查询条件
        LambdaQueryWrapper<TrainingCoach> queryWrapper =
                Wrappers.lambdaQuery(TrainingCoach.class)
                        .eq(TrainingCoach::getTrainingId, trainingId)
                        .orderByAsc(TrainingCoach::getCIndex);
        //2.查询数据
        List<TrainingCoach> trainingCoachs = baseMapper.selectList(queryWrapper);
        //3.组装数据
        return CollUtils.isEmpty(trainingCoachs) ?
                new ArrayList<>() : trainingCoachs
                .stream()
                .map(TrainingCoach::getCoachId)
                .collect(Collectors.toList());
    }

}

