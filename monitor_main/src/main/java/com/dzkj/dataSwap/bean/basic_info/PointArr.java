package com.dzkj.dataSwap.bean.basic_info;

import lombok.Data;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/2 14:30
 * @description 监测点的信息
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
@Data
public class PointArr {
    /**
     *      pCode: String 测点代码,在一个项目中，测点代码必须唯一
     *      height: int 监测点高度，单位0.01mm
     *      mainCatagory: int 监测点监测目标的大分类(1	线上目标 ;2	线下目标; 99	其它)
     *      subCatagory: int 监测点监测目标的小分类(1	轨道; 其他见对接文档)
     *      railwayType: int 监测点监测铁路线的类型(1	普铁;2	高铁-有砟;3	高铁-无砟;99	其它)
     *      funcType: int 监测点的功能类型(1	监测点;2	基准点;4	连接点;8	计算点;16	测站点)
     *      kiloMark: String 监测点铁路里程(K834+954)
     *      funcAttr: int 测点测量信息类型(1	位移测量	OFFSET_INFO; 其他见对接文档)
     */
    private String pCode;
    private int height;
    private int mainCatagory;
    private int subCatagory;
    private int railwayType;
    private int funcType;
    private int funcAttr;
    private String kiloMark;
}
