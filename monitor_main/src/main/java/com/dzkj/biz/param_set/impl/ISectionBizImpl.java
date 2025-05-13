package com.dzkj.biz.param_set.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dzkj.biz.base.Angle;
import com.dzkj.biz.base.BaseFunction;
import com.dzkj.biz.param_set.ISectionBiz;
import com.dzkj.biz.param_set.vo.SectionVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.entity.param_set.Point;
import com.dzkj.entity.param_set.Section;
import com.dzkj.service.param_set.IPointService;
import com.dzkj.service.param_set.ISectionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/9/7
 * @description 断面业务接口实现
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class ISectionBizImpl implements ISectionBiz {

    @Autowired
    private ISectionService sectionService;
    @Autowired
    private IPointService pointService;

    @Override
    public List<SectionVO> getList(Long groupId) {
        LambdaQueryWrapper<Section> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Section::getGroupId, groupId)
                .orderByDesc(Section::getCreateTime);
        return DzBeanUtils.listCopy(sectionService.list(wrapper), SectionVO.class);
    }

    @Override
    public ResponseUtil add(SectionVO section) {
        Section copy = DzBeanUtils.propertiesCopy(section, Section.class);
        if(sectionService.findByName(copy)){
           return ResponseUtil.failure(500, "断面名称重复");
        }
        boolean b = sectionService.save(copy);
        // 更新测点方位角
        updatePtAzimuth(section, b);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(SectionVO section) {
        Section copy = DzBeanUtils.propertiesCopy(section, Section.class);
        if(sectionService.findByName(copy)){
            return ResponseUtil.failure(500, "断面名称重复");
        }
        boolean b = sectionService.updateById(copy);
        // 更新测点方位角
        updatePtAzimuth(section, b);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        Section section = sectionService.getById(id);
        boolean b = sectionService.removeById(id);
        if (b && StringUtils.isNotEmpty(section.getPidStr())){
            List<Long> pidList = Arrays.stream(section.getPidStr().split(",")).map(Long::valueOf)
                    .collect(Collectors.toList());
            LambdaUpdateWrapper<Point> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(Point::getId, pidList).set(Point::getAzimuth, 0);
            pointService.update(wrapper);
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public ResponseUtil calculate(SectionVO section) {
        double x1 = section.getStartX() == null ? 0 : section.getStartX();
        double y1 = section.getStartY() == null ? 0 : section.getStartY();
        double x2 = section.getEndX() == null ? 0 : section.getEndX();
        double y2 = section.getEndY() == null ? 0 : section.getEndY();
        double azimuth = BaseFunction.alphaAB(x1, y1, x2, y2);
        String dms = new Angle(azimuth, 1).showDMS(4);
        return ResponseUtil.success(dms);
    }

    //region
    /**
     * 更新断面方位角
     */
    private void updatePtAzimuth(SectionVO section, boolean b) {
        if(b && StringUtils.isNotEmpty(section.getPidStr())){
            List<Long> pidList = Arrays.stream(section.getPidStr().split(",")).map(Long::valueOf)
                    .collect(Collectors.toList());
            LambdaUpdateWrapper<Point> wrapper = new LambdaUpdateWrapper<>();
            wrapper.in(Point::getId, pidList).set(Point::getAzimuth, section.getAzimuth());
            pointService.update(wrapper);
        }
    }
    //endregion

}
