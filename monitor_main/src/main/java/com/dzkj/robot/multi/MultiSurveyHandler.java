package com.dzkj.robot.multi;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright(c),2018-2024,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2024/2/28 17:23
 * @description 测站联测处理器
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Slf4j
public class MultiSurveyHandler {

    /**
     * 系统所有的多站联测业务对象
     */
    private static final List<MultiStationAo> ALL_MULTI_STATION = new ArrayList<>();

    /**
     * 获取指定多测站信息对象
     * @param id 测站id
     */
    public static MultiStationAo getStation(Long id){
        for (MultiStationAo multiStation : ALL_MULTI_STATION) {
            if (multiStation.getId().equals(id)){
                return multiStation;
            }
        }
        return null;
    }

    /**
     * 获取所有的多测站集合
     */
    public static List<MultiStationAo> getAllStations(){
        return ALL_MULTI_STATION;
    }

}
