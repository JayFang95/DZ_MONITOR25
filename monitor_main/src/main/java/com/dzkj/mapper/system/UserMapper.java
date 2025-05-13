package com.dzkj.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzkj.biz.system.vo.UserVO;
import com.dzkj.entity.system.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/2
 * @description 人员mapper
 * history
 * <author>    <time>    <version>    <desc>
 *  作者姓名     修改时间     版本号        描述
 */
public interface UserMapper extends BaseMapper<User> {

    List<UserVO> getList(@Param("companyId") Long companyId);

    List<UserVO> getListUser(@Param("companyId") Long companyId, @Param("groupId") Long groupId);

    List<UserVO> getSuperList();

    List<User> listByGroupIds(@Param("list")List<Long> list);

    List<User> findByIdAndPhone(@Param("user") User user);
}
