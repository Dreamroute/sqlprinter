package com.github.dreamroute.sqlprinter.boot.domain;

import com.github.dreamroute.mybatis.pro.base.EnumMarker;
import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author w.dehai
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("smart_user")
public class User {

    @Id
    private Integer id;
    private String name;
    private String password;
    private Long version;
    private Date birthday;
    private Gender gender;

    @Getter
    @AllArgsConstructor
    public enum Gender implements EnumMarker {
        MALE(1, "男"),
        FEMALE(2, "女");

        private Integer value;
        private String desc;
    }

}
