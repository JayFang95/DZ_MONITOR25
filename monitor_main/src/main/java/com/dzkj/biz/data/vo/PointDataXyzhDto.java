package com.dzkj.biz.data.vo;

import com.dzkj.entity.data.PointDataXyzh;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Copyright(c),2018-2025,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/6/28 下午9:30
 * @description DataXyzh过程传输数据
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PointDataXyzhDto {

    private List<PointDataXyzh> dataList;

    private boolean hasAlarm;

}
