package com.foxx.bean;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 作者：wangsy
 * 日期：2016/6/23 17:01
 * 描述：用户类
 */
@Data
@Document
public class User implements Serializable {
    private static final long serialVersionUID = -4770493237851400594L;
    @Id
    private String userId;
    private String userName;
    private String password;
    private Set<String> pets;
    private Map<String, String> favoriteMovies;
    private Date birthday;
}
