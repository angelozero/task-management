package com.angelozero.task.management.entity.dynamicbean;

import com.angelozero.task.management.usecase.exception.DynamicBeanTypeException;
import lombok.Getter;

@Getter
public enum DynamicBeanType {

    DYNAMIC_BEAN_TYPE_1(1, "dynamic_bean_1"),
    DYNAMIC_BEAN_TYPE_2(2, "dynamic_bean_2"),
    DYNAMIC_BEAN_TYPE_3(3, "dynamic_bean_3");

    private final int code;
    private final String description;

    DynamicBeanType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DynamicBeanType fromCode(int code) {
        for (DynamicBeanType type : DynamicBeanType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new DynamicBeanTypeException("Invalid dynamic type code: " + code);
    }
}
