package com.dzkj.biz.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.system.IRoleBiz;
import com.dzkj.biz.system.vo.ResourceCondition;
import com.dzkj.biz.system.vo.Tree;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.Dict;
import com.dzkj.entity.system.Resource;
import com.dzkj.entity.system.Role;
import com.dzkj.entity.system.RoleResource;
import com.dzkj.service.IDictService;
import com.dzkj.service.system.IResourceService;
import com.dzkj.service.system.IRoleResourceService;
import com.dzkj.service.system.IRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/6
 * @description 角色业务接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class RoleBizImpl implements IRoleBiz {

    private final static String SUPER_ADMIN = "超级管理员";
    private final static String ADMIN = "管理员";
    private final static String ICON_PREFIX = "anticon-";

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IRoleResourceService roleResourceService;
    @Autowired
    private IDictService dictService;

    @Override
    public List<Role> getList() {
        return roleService.list();
    }

    @Override
    public ResponseUtil addRole(Role role) {
        if(roleService.findByName(role)){
            return ResponseUtil.failure(500, "角色名称已经存在");
        }
        boolean b = roleService.save(role);
        if (b){
            // 2023/7/19 赋值角色默认权限
            LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Dict::getCode, "default_role_resource").eq(Dict::getKey, 0);
            Dict dict = dictService.getOne(wrapper);
            if (dict != null && StringUtils.isNotEmpty(dict.getValue())){
                List<RoleResource> list = Arrays.stream(dict.getValue().split(","))
                        .map(e -> new RoleResource(role.getId(), Long.valueOf(e)))
                        .collect(Collectors.toList());
                roleResourceService.saveBatch(list);
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil updateRole(Role role) {
        if(roleService.findByName(role)){
            return ResponseUtil.failure(500, "角色名称已经存在");
        }
        boolean b = roleService.updateById(role);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil deleteRole(Long id) {
        Role role = roleService.getById(id);
        if(role==null || SUPER_ADMIN.equals(role.getName()) || ADMIN.equals(role.getName())){
            return ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
        }
        boolean b = roleService.removeById(id);
        if (b){
            //删除角色资源
            LambdaQueryWrapper<RoleResource> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(RoleResource::getRoleId, id);
            roleResourceService.remove(wrapper);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public List<Tree> getTree(Long id) {
        List<Resource> allMenu = resourceService.getMenus(new ResourceCondition());
        List<Resource> roleMenus = resourceService.findByRoleId(id);
        List<Tree> list = new ArrayList<>();
        List<Tree> trees = new ArrayList<>();
        allMenu.forEach(menu ->{
            String icon = menu.getIcon();
            if(icon==null || "".equals(icon) || menu.getPid()==null || menu.getPid()==0){
                icon = "bars";
            }else if(icon.contains(ICON_PREFIX)){
                icon = icon.substring(8);
            }
            Tree tree = new Tree(menu.getId(), menu.getName(), menu.getPid(), icon, menu.getIndex());
            trees.add(tree);
        });
        createTree(trees, list);
        List<String> buttonType = Arrays.asList("普通按钮", "消息按钮");
        List<String> menuType = Arrays.asList("普通菜单", "消息菜单");
        trees.forEach(item -> {
            roleMenus.forEach(resource -> {
                if(item.getKey().equals(resource.getId())){
                    if(buttonType.contains(resource.getType())){
                        item.setChecked(true);
                    }else if (menuType.contains(resource.getType())
                            && hasChildren(resource.getId())){
                        item.setChecked(true);
                    }
                }
            });
        });
        findLeaf(trees);
        return list;
    }

    @Override
    public boolean updateTree(Long id, List<Long> menuIds) {
        // 删除角色原有关联菜单
        deleteOldRoleResource(id);
        if(menuIds==null||menuIds.size()==0){
            return true;
        }
        //查询勾选的上级菜单
        List<Long> pidList = getParentIds(menuIds);
        pidList.forEach(menuId -> {
            if(!menuIds.contains(menuId)){
                menuIds.add(menuId);
            }
        });
        // 新增
        List<RoleResource> list = new ArrayList<>();
        menuIds.forEach(item -> {
            RoleResource roleResource = new RoleResource();
            roleResource.setRoleId(id);
            roleResource.setResourceId(item);
            list.add(roleResource);
        });
        if (list.size()==0){
            return true;
        }
        return roleResourceService.saveBatch(list);
    }

    @Override
    public List<DropVO> getRoleDrop() {
        List<Role> list = roleService.list();
        return DzBeanUtils.listCopy(list, DropVO.class);
    }

    //region 私有方法
    /**
     * 获取父菜单id
     */
    private List<Long> getParentIds(List<Long> menuIds) {
        if(menuIds==null || menuIds.size()==0){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Resource::getId,menuIds);
        List<Resource> list = resourceService.list(wrapper);
        list.forEach(item -> {
            if(item.getPid()!=null && item.getPid()!=0 && !menuIds.contains(item.getPid())){
                getParentId(menuIds,item.getPid());
            }
        });
        return menuIds;
    }

    /**
     * 获取父菜单id
     */
    private void getParentId(List<Long> menuIds, Long pid){
        menuIds.add(pid);
        Resource item = resourceService.getById(pid);
        if(item.getPid()!=null && item.getPid()!=0 && !menuIds.contains(item.getPid())){
            getParentId(menuIds,item.getPid());
        }
    }

    private void deleteOldRoleResource(Long roleId) {
        LambdaQueryWrapper<RoleResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleResource::getRoleId, roleId);
        roleResourceService.remove(wrapper);
    }

    /**
     * 判断是否有子菜单
     */
    private boolean hasChildren(Long id){
        LambdaQueryWrapper<Resource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resource::getPid,id);
        wrapper.eq(Resource::getWhiteUrl,false);
        return resourceService.count(wrapper) <= 0;
    }

    /**
     * 设置tree叶子节点
     */
    private void findLeaf(List<Tree> trees){
        trees.forEach(item -> {
            if(item.getChildren()==null || item.getChildren().size()==0){
                item.setIsLeaf(true);
            }else {
                findLeaf(item.getChildren());
            }
        });
    }

    /**
     * 构建当前用户菜单集合
     */
    private void createTree(List<Tree> trees, List<Tree> list) {
        //获取所有一级目录
        trees.forEach(tree -> {
            if (tree.getPkey() == null || tree.getPkey() == 0) {
                list.add(tree);
            }
        });
        //查询子菜单集合
        findChildren(trees, list);
    }

    /**
     * 查询子菜单集合
     */
    private void findChildren(List<Tree> trees, List<Tree> list) {
        list.forEach(tree -> {
            ArrayList<Tree> children = new ArrayList<>();
            trees.forEach(item -> {
                if (tree.getKey() != null && tree.getKey().equals(item.getPkey())) {
                    if (!exists(children, item)) {
                        children.add(item);
                    }
                }
            });
            tree.setChildren(children);
            findChildren(trees, children);
        });
    }

    private boolean exists(List<Tree> list, Tree item) {
        boolean exist = false;
        for (Tree tree : list) {
            if (tree.getKey().equals(item.getPkey())) {
                exist = true;
                break;
            }
        }
        return exist;
    }
    //endregion

}
