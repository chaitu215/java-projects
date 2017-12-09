package evan.wang.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 作者：wangsy
 * 日期：2016/6/23 17:45
 * 描述：餐厅
 */
@Data
@Document(collection = "restaurants")
public class Restaurants {
    @Id
    private long id;
    //名称
    private String name;
    //餐厅编号
    @Field("restaurant_id")
    private String restaurantId;
    //地址
    private Address address;
    //镇
    private String borough;
    //特色菜
    private String cuisine;
    //评分
    private List<Grade> grades;
}
