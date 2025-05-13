package com.dzkj.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Copyright(c),2018-2020,合肥市鼎足空间技术有限公司
 *
 * @author liao --参照网上帖子,未仔细验证
 * @date 2020-02-23 9:20
 * @description 扩展BeanUtils, 处理List, Map
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
public class DzBeanUtils extends org.springframework.beans.BeanUtils {

    public DzBeanUtils() {
    }

    /**
     * @param source source
     * @param clazz  clazz
     * @return T
     * @throws
     * @description 功能描述:获得复制对象
     * @author liao
     * @date 2020-02-23 09:26
     * @see
     **/
    public static <T> T propertiesCopy(Object source, Class<T> clazz) {
        if (null == source) {
            return null;
        } else {
            try {
                T obj = clazz.newInstance();
                org.springframework.beans.BeanUtils.copyProperties(source, obj);
                return obj;
            } catch (IllegalAccessException | InstantiationException var3) {
                throw new RuntimeException(var3);
            }
        }
    }

    /**
     * @param source source
     * @param clazz  clazz
     * @return java.LoginUtil.List<T>
     * @throws
     * @description 功能描述:list中对象的copy
     * @author liao
     * @date 2020-02-23 09:29
     * @see
     **/
    public static <T> List<T> listCopy(Collection source, Class<T> clazz) {
        if (null == source) {
            return new ArrayList<>();
        } else {
            List<T> list = new ArrayList<>();

            for (Object o : source) {
                list.add(propertiesCopy(o, clazz));
            }

            return list;
        }
    }

    /**
     * IPage中对象的copy
     *
     * @param page  page
     * @param clazz clazz
     * @return com.baomidou.mybatisplus.core.metadata.IPage<T>
     * @throws
     * @description IPage中对象的copy
     * @author luchao
     * @date 2020-03-12 12:02
     * @see
     **/
    public static <T> IPage<T> pageCopy(IPage<?> page, Class<T> clazz) {
        //PO->VO
        //这样拷贝后，pageVO中的Records是Student类型
        IPage<T> pageVO = DzBeanUtils.propertiesCopy(page, Page.class);
        //要再处理一下
        List<T> ts = DzBeanUtils.listCopy(page.getRecords(), clazz);
        pageVO.setRecords(ts);
        return pageVO;
    }


    /**
     * @param obj obj
     * @return java.LoginUtil.Map<java.lang.String, java.lang.Object>
     * @throws
     * @description 将对象转换为map:
     * @author liao
     * @date 2020-02-23 09:28
     * @see
     **/
    public static Map<String, Object> object2Map(Object obj) {
        Map<String, Object> map = new HashMap<>(15);
        if (obj == null) {
            return map;
        } else {
            Class clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();

            try {
                for (Field field : fields) {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(obj));
                }

                return map;
            } catch (Exception var8) {
                throw new RuntimeException(var8);
            }
        }
    }


    /**
     * @param map  map
     * @param clzz clzz
     * @return java.lang.Object
     * @throws
     * @description 将map转换为对象, 必须保证属性名称相同
     * @author liao
     * @date 2020-02-23 09:28
     * @see
     **/
    public static <T> T map2Object(Map<Object, Object> map, Class<T> clzz) {
        try {
            T target = clzz.newInstance();
            if (CollectionUtils.isEmpty(map)) {
                return target;
            }
            Field[] fields = clzz.getDeclaredFields();
            if (!CollectionUtils.isEmpty(Arrays.asList(fields))) {
                Arrays.stream(fields).filter((Field field) -> map.containsKey(field.getName())).forEach(var -> {
                    //获取属性的修饰符
                    int modifiers = var.getModifiers();
                    if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                        //在lambada中结束本次循环是用return,它不支持continue和break
                        return;
                    }
                    //设置权限
                    var.setAccessible(true);
                    try {
                        var.set(target, map.get(var.getName()));
                    } catch (IllegalAccessException e) {
                        //属性类型不对,非法操作,跳过本次循环,直接进入下一次循环
                        throw new RuntimeException(e);
                    }
                });
            }
            return target;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}//end class
