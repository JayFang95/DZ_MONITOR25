package com.dzkj.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dzkj.biz.project.vo.CustomDisplayVO;
import com.dzkj.biz.vo.FileCondition;
import com.dzkj.common.util.DzBeanUtils;
import com.dzkj.common.util.FileUtil;
import com.dzkj.common.util.ResponseUtil;
import com.dzkj.config.websocket.WebSocketServer;
import com.dzkj.entity.File;
import com.dzkj.entity.project.CustomDisplay;
import com.dzkj.entity.system.Resource;
import com.dzkj.entity.system.RoleResource;
import com.dzkj.service.IFileService;
import com.dzkj.service.project.ICustomDisplayService;
import com.dzkj.service.system.IResourceService;
import com.dzkj.service.system.IRoleResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/3
 * @description 通用业务
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class CommonBizImpl implements ICommonBiz {

    @Autowired
    private IRoleResourceService roleResourceService;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IFileService fileService;
    @Autowired
    private ICustomDisplayService displayService;
    //endregion

    @Override
    public boolean updateRoleMenu() {
        List<Resource> list = resourceService.list();
        List<Long> collect = list.stream().filter(resource -> !resource.getWhiteUrl()).map(Resource::getId).collect(Collectors.toList());
        ArrayList<RoleResource> roleResources = new ArrayList<>();
        for (Long resourceId : collect) {
            RoleResource resource = new RoleResource();
            resource.setResourceId(resourceId);
            resource.setRoleId(1L);
            roleResources.add(resource);
        }
        return roleResourceService.saveBatch(roleResources);
    }

    @Override
    public Map<String, Object> upload(File file, MultipartFile multipartFile) {
        if(0!=file.getCategoryId()){
            file.setTemp(false);
        }
        String url = FileUtil.store(file, multipartFile);
        file.setSize(multipartFile.getSize());
        fileService.save(file);
        Map<String, Object> map = new HashMap<>(16);
        map.put("id", file.getId());
        map.put("url", url);
        map.put("item", fileService.getById(file.getId()));
        return map;
    }

    @Override
    public void download(Long fileId, HttpServletResponse response) {
        File file = fileService.getById(fileId);
        if(file!=null){
            FileUtil.download(file, response);
        }
    }

    @Override
    public void downloadAll(File file, HttpServletResponse response) {
        FileUtil.downloadZip(file, response);
    }

    @Override
    public Boolean deleteFileByIds(List<Long> fileIds) {
        if(fileIds != null && fileIds.size()>0){
            fileIds.forEach(id -> FileUtil.delete(fileService.getById(id)));
        }
        return fileService.removeByIds(fileIds);
    }

    @Override
    public void deleteTemp(FileCondition condition) {
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(condition.getCreatorId()!=null, File::getCreatorId, condition.getCreatorId())
                .eq(condition.getCategoryId()!=null, File::getCategoryId, condition.getCategoryId())
                .eq(StringUtils.isNotEmpty(condition.getCategoryName()), File::getCategoryName, condition.getCategoryName())
                .eq(StringUtils.isNotEmpty(condition.getScopeName()), File::getScopeName, condition.getScopeName())
                .eq(File::getTemp, true);
        List<File> files = fileService.list(wrapper);
        if (files.size() > 0){
            List<Long> fileIds = files.stream().map(File::getId).collect(Collectors.toList());
            deleteFileByIds(fileIds);
        }
    }

    @Override
    public List<File> getByCategoryInfo(File file) {
        if(file == null){
            return new ArrayList<>();
        }
        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(File::getCategoryId, file.getCategoryId())
                .eq(File::getCategoryName, file.getCategoryName());
        return fileService.list(wrapper);
    }

    @Override
    public List<CustomDisplayVO> getDefaultDisplayList() {
        LambdaQueryWrapper<CustomDisplay> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomDisplay::getMissionId, 0).orderByAsc(CustomDisplay::getSeq);
        return DzBeanUtils.listCopy(displayService.list(wrapper), CustomDisplayVO.class);
    }

    @Override
    public List<CustomDisplayVO> getDisplayList(Long missionId) {
        LambdaQueryWrapper<CustomDisplay> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomDisplay::getMissionId, missionId).orderByAsc(CustomDisplay::getSeq);
        return DzBeanUtils.listCopy(displayService.list(wrapper), CustomDisplayVO.class);
    }

    @Override
    public ResponseUtil updateDisplayList(List<CustomDisplayVO> displayList) {
        displayService.updateBatchById(DzBeanUtils.listCopy(displayList, CustomDisplay.class));
        return ResponseUtil.success();
    }

    @Override
    public void deleteOldWs(Long userId, String createTime) {
        WebSocketServer.deleteOldLink(userId, createTime);
    }

}
