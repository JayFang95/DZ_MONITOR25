package com.dzkj.biz.system.impl;

import com.dzkj.biz.system.IResourceBiz;
import com.dzkj.biz.system.vo.ResourceCondition;
import com.dzkj.biz.system.vo.ResourceVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.system.Resource;
import com.dzkj.service.system.IResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/9
 * @description 资源业务实现接口
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class ResourceBizImpl implements IResourceBiz {

    @Autowired
    private IResourceService iResourceService;

    @Override
    public List<ResourceVO> getList(ResourceCondition condition) {
        List<ResourceVO> menuList = DzBeanUtils.listCopy(iResourceService.getMenus(condition), ResourceVO.class);
        ArrayList<ResourceVO> sysMenus = new ArrayList<>();
        if(StringUtils.isAllEmpty(condition.getName(), condition.getType())){
            createTreeMenu(menuList, sysMenus);
        }else {
            return menuList;
        }
        return sysMenus;
    }


    @Override
    public ResponseUtil updateResource(Resource resource) {
        boolean b = iResourceService.updateById(resource);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    // region 私有方法
    /**
     * 构建当前用户菜单集合
     */
    private void createTreeMenu(List<ResourceVO> menuList, List<ResourceVO> userMenus) {
        //获取所有一级目录
        menuList.forEach(menuVo -> {
            if (menuVo.getPid() == null || menuVo.getPid() == 0) {
                userMenus.add(menuVo);
            }
        });
        //查询子菜单集合
        findChildrenMenu(menuList, userMenus);
    }

    /**
     * 查询子菜单集合
     */
    private void findChildrenMenu(List<ResourceVO> menuList, List<ResourceVO> userMenus) {
        userMenus.forEach(menuVo -> {
            ArrayList<ResourceVO> children = new ArrayList<>();
            menuList.forEach(menu -> {
                if (menuVo.getId() != null && menuVo.getId().equals(menu.getPid())) {
                    if (!exists(children, menu)) {
                        children.add(menu);
                    }
                }
            });
            menuVo.setChildrenMenu(children);
            findChildrenMenu(menuList, children);
        });
    }

    /**
     * 判断是否资源存在
     */
    private boolean exists(List<ResourceVO> list, ResourceVO menuVo) {
        boolean exist = false;
        for (ResourceVO menu : list) {
            if (menu.getId().equals(menuVo.getId())) {
                exist = true;
                break;
            }
        }
        return exist;
    }
    // endregion

}
