package evan.wang.dao;

import evan.wang.bean.Restaurants;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 作者：wangsy
 * 日期：2016/6/23 18:06
 * 描述：
 */
public interface RestaurantsRepository extends MongoRepository<Restaurants,String> {


}
