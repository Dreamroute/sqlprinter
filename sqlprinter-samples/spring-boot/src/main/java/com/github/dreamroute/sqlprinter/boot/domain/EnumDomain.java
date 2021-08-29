package com.github.dreamroute.sqlprinter.boot.domain;

import com.github.dreamroute.mybatis.pro.base.EnumMarker;
import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("smart_typehandler")
public class EnumDomain {

    @Id
    private Long id;
    private Gender gender;

    @Getter
    @AllArgsConstructor
    public enum  Gender implements EnumMarker {

        MALE(1, "男", 1), FEMALE(2, "女", 2);

        private final Integer value;
        private final String desc;
        private final Integer sort;

    }
}
