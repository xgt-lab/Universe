package cn.xgt.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import cn.xgt.entity.User;

/**
 * @author XGT
 * @description TODO
 * @date 2026/4/1
 */
@Mapper
public interface UserMapper {
	@Select("SELECT * FROM user WHERE id = #{id}")
	User findById(Long id);

	@Insert("INSERT INTO user(name, email, creator, create_time, operator, modify_time) VALUES(#{name}, #{email}, #{creator}, #{createTime}, #{operator}, #{modifyTime})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int insert(User user);
}
