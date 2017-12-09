package evan.wang.service.impl;

import evan.wang.bean.User;
import evan.wang.dao.UserRepository;
import evan.wang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 作者：wangsy
 * 日期：2016/6/23 18:10
 * 描述：
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
