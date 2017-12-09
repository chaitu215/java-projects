package evan.wang.bean;

import lombok.Data;

import java.util.Date;

/**
 * 作者：wangsy
 * 日期：2016/6/23 17:58
 * 描述：评分
 */
@Data
public class Grade {
    //评分日期
    private Date date;
    //评分级别(A B C D)
    private String score;
    //分数
    private String grade;
}
