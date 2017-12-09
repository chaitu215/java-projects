package evan.wang.dao;

import evan.wang.bean.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 作者：wangsy
 * 日期：2016/6/23 17:15
 * 描述：用户操作类
 */
public interface UserRepository extends MongoRepository<User, String> {

}
