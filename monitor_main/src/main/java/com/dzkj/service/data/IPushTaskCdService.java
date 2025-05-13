package com.dzkj.service.data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzkj.dataSwap.vo.PushTaskCdVO;
import com.dzkj.entity.data.PushTaskCd;

import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2025/2/21
 * @description 推送任务服务接口-成都局
 * history
 * <author>    <time>    <version>    <desc>
 * 作者姓名     修改时间     版本号        描述
 */
public interface IPushTaskCdService extends IService<PushTaskCd> {

    List<PushTaskCd> queryList(Long companyId);

    boolean add(PushTaskCdVO data);

    boolean edit(PushTaskCdVO data);

    boolean updateStatus(Long id, int status);
}
