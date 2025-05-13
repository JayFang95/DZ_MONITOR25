package com.dzkj.biz.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.system.IGroupBiz;
import com.dzkj.biz.system.vo.UserVO;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Groups;
import com.dzkj.entity.system.UserGroup;
import com.dzkj.service.project.IProjectGroupService;
import com.dzkj.service.system.ICompanyService;
import com.dzkj.service.system.IGroupService;
import com.dzkj.service.system.IUserGroupService;
import com.dzkj.service.system.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/6
 * @description 分组业务实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class GroupBizImpl implements IGroupBiz {

    @Autowired
    private IGroupService groupService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IProjectGroupService projectGroupService;
    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IUserService userService;

    @Override
    public List<Groups> getList(Long companyId) {
        LambdaQueryWrapper<Groups> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(companyId != 0,Groups::getCompanyId, companyId);
        wrapper.orderByDesc(Groups::getCreateTime);
        return groupService.list(wrapper);
    }

    @Override
    public ResponseUtil add(Long companyId, Groups groups) {
        groups.setCompanyId(companyId);
        if(groupService.checkName(groups)){
            return ResponseUtil.failure(500, "工作组名重复");
        }
        boolean b = groupService.save(groups);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(Groups groups) {
        if(groupService.checkName(groups)){
            return ResponseUtil.failure(500, "工作组名重复");
        }
        boolean b = groupService.updateById(groups);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        boolean b = groupService.removeById(id);
        if(b){
            userGroupService.removeByGroupId(id);
            projectGroupService.removeByGroupId(id);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public List<UserVO> getListUser(Long id) {
        Groups groups = groupService.getById(id);
        if(groups == null){
            return new ArrayList<>();
        }
        List<UserVO> list = userService.getListUser(groups.getCompanyId(), id);
        list.sort(Comparator.comparing(UserVO::getChecked).reversed());
        return list;
    }

    @Override
    public boolean updateUserList(Long id, List<Long> userIds) {
        LambdaQueryWrapper<UserGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGroup::getGroupId, id);
        userGroupService.remove(wrapper);
        if(userIds!=null && userIds.size()>0){
            ArrayList<UserGroup> list = new ArrayList<>();
            for (Long userId : userIds) {
                UserGroup userGroup = new UserGroup();
                userGroup.setUserId(userId).setGroupId(id);
                list.add(userGroup);
            }
            userGroupService.saveBatch(list);
        }
        return true;
    }

    @Override
    public List<DropVO> getUserGroupListIn(List<Long> ids) {
        if (ids.size()==0){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Groups> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Groups::getId, ids);
        List<Groups> list = groupService.list(wrapper);
        return DzBeanUtils.listCopy(list, DropVO.class);
    }

    @Override
    public List<DropVO> getUserGroupList(Long companyId) {
        if(companyId==null){
            return new ArrayList<>();
        }
        if(companyId==0){
            companyId = companyService.getCurrentCompany();
        }
        LambdaQueryWrapper<Groups> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Groups::getCompanyId, companyId);
        List<Groups> list = groupService.list(wrapper);
        return DzBeanUtils.listCopy(list, DropVO.class);
    }

}
