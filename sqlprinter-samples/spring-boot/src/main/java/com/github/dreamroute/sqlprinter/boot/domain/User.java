package com.github.dreamroute.sqlprinter.boot.domain;

import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
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

}
