package com.yuedongyun.campaign.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yuedongyun.common.enums.BaseEnum;
import com.yuedongyun.campaign.strategy.scope.CategoryScope;
import com.yuedongyun.campaign.strategy.scope.TrainingScope;
import com.yuedongyun.campaign.strategy.scope.NoScope;
import com.yuedongyun.campaign.strategy.scope.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ScopeType implements BaseEnum {
    ALL(0, "全部"){
        @Override
        public Scope buildScope(List<Long> scopeIds) {
            return new NoScope();
        }
    },
    CATEGORY(1, "指定分类"){
        @Override
        public Scope buildScope(List<Long> scopeIds) {
            return new CategoryScope(scopeIds);
        }
    },
    TRAINING(2, "指定训练") {
        @Override
        public Scope buildScope(List<Long> scopeIds) {
            return new TrainingScope(scopeIds);
        }
    },
    ;
    private final int value;
    private final String desc;

    public static final String CATEGORY_HANDLER_NAME = "CATEGORY";
    public static final String TRAINING_HANDLER_NAME = "TRAINING";

    @JsonCreator
    public static ScopeType of(Integer value) {
        if (value == null) {
            return null;
        }
        for (ScopeType status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }

    public static String desc(Integer value) {
        ScopeType status = of(value);
        return status == null ? "" : status.desc;
    }

    public abstract Scope buildScope(List<Long> scopeIds);
}

