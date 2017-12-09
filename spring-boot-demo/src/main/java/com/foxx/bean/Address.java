package com.foxx.bean;

import lombok.Data;
import java.util.List;


/**
 * 作者：wangsy
 * 日期：2016/6/23 17:51
 * 描述：地址
 */
@Data
public class Address {
    //邮政编码
    private String zipcode;
    //坐标系
    private List<Double> coord;
    //街道
    private String street;
    //楼房
    private String building;
}
