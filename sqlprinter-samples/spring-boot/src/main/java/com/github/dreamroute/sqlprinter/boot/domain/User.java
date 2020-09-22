package com.github.dreamroute.sqlprinter.boot.domain;

import com.github.dreamroute.mybatis.pro.core.annotations.Id;
import com.github.dreamroute.mybatis.pro.core.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w.dehai
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "smart_user")
public class User {

    @Id
    private Integer id;
    private String name;
    private String password;
    private Long version;
}
