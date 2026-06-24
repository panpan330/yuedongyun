package com.yuedongyun.training.utils;

import com.yuedongyun.common.utils.CollUtils;
import com.yuedongyun.common.utils.ObjectUtils;
import com.yuedongyun.common.utils.ReflectUtils;
import com.yuedongyun.common.utils.StringUtils;
import com.yuedongyun.training.domain.po.Assessment;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目工具类
 *
 * @ClassName AssessmentUtils
 * @Author wusongsong
 * @Date 2022/7/15 17:13
 * @Version
 **/
public class AssessmentUtils {

    /**
     * 将选项列表中的选项设置到题目中
     *
     * @param assessment 题目
     * @param options 选项
     */
    public static void setOptions(Assessment assessment, List<String> options) {
        if (CollUtils.isNotEmpty(options)) {
            for (int count = 0; count < options.size(); count++) {
                ReflectUtils.setFieldValue(assessment, "option" + (count + 1), options.get(count));
            }
        }
    }

    /**
     * 从题目中获取选项
     *
     * @param assessment 题目
     * @return 选项
     */
    public static List<String> getOptions(Assessment assessment) {
        List<String> options = new ArrayList<>();
        for (int count = 1; count <= 10; count++) {
            Object option = ReflectUtils.getFieldValue(assessment, "option" + count);
            if (ObjectUtils.isEmpty(option) || StringUtils.isEmpty((String)option)) {
                return options;
            }
            options.add((String) option);
        }
        return options;
    }
}

