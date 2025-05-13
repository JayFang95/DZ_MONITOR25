package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.entity.data.PushTaskRecord;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/6/9
 * @description 推送记录服务即可
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPushTaskRecordService extends IService<PushTaskRecord> {

    /**
     * 验证是否存在相同推送记录
     *
     * @description 验证是否存在相同推送记录
     * @author jing.fang
     * @date 2023/6/9 17:40
     * @param missionId missionId
     * @param recycleNum recycleNum
     * @return: boolean
     **/
    boolean exitPushTaskRecord(Long missionId, Integer recycleNum);
}
