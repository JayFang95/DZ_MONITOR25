package com.dzkj.biz.system.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dzkj.biz.ICommonBiz;
import com.dzkj.biz.project.IProjectBiz;
import com.dzkj.biz.project.vo.ProjectVO;
import com.dzkj.biz.system.ICompanyBiz;
import com.dzkj.biz.system.vo.CompanyCondition;
import com.dzkj.biz.system.vo.Tree;
import com.dzkj.biz.vo.DeleteMemberBatch;
import com.dzkj.biz.vo.DropVO;
import com.dzkj.common.enums.ResponseEnum;
import com.dzkj.common.util.*;
import com.dzkj.entity.Dict;
import com.dzkj.entity.File;
import com.dzkj.entity.system.Company;
import com.dzkj.entity.system.HomePage;
import com.dzkj.entity.system.User;
import com.dzkj.entity.system.UserGroup;
import com.dzkj.service.IDictService;
import com.dzkj.service.IFileService;
import com.dzkj.service.file_store.IFileStoreService;
import com.dzkj.service.project.IProjectService;
import com.dzkj.service.system.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/4
 * @description CompanyBizImpl
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
public class CompanyBizImpl implements ICompanyBiz {

    @Autowired
    private ICompanyService companyService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserGroupService userGroupService;
    @Autowired
    private IGroupService groupService;
    @Autowired
    private IHomePageService homePageService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private IProjectBiz projectBiz;
    @Autowired
    private ICommonBiz commonBiz;
    @Autowired
    private IOperateLogService operateLogService;
    @Autowired
    private QwUtil qwUtil;
    @Autowired
    private IDictService dictService;
    @Autowired
    private IFileService fileService;
    @Autowired
    private IFileStoreService fileStoreService;

    @Override
    public IPage<Company> getPage(int pageIndex, int pageSize, CompanyCondition condition) {
        Integer timeNum = condition.getTimeNum();
        if(timeNum!=null && timeNum>0){
            condition.setCreateTime(DateUtil.getDateOfMonth(new Date(), -timeNum));
        }
        return companyService.getPage(pageIndex, pageSize, condition);
    }

    @Override
    public ResponseUtil add(Company company) {
        if(company.getCode() == null){
            return ResponseUtil.failure(500, "单位代码不能为空");
        }
        company.setCode(StringUtils.upperCase(company.getCode()));
        //验证公司代码
        if(companyService.findCodeExist(company)){
            return ResponseUtil.failure(500, "单位代码重复");
        }
        //新增第一条默认为当前
        if(companyService.count()==0){
            company.setCurrent(true);
        }
        company.setStatusTime(new Date());
        // 设置默认功能
        company.setFunctionConfig("全站仪自动监测(XYZ),支撑轴力,人工巡视,手动三维位移(XYZ),竖向位移(H),水平位移(XY->S),水平位移(HD),水平位移(分层),倾斜位移(%)");
        boolean b = companyService.save(company);
        if(b){
            // 新增成功，添加默认管理员 (单位代码_Admin)
            creatDefaultAdmin(company);
            //新增默认首页信息
            saveHomePage(company);
            if(companyService.count()==1){
                // 2023/7/19 修改超级管理员所属单位
                updateSuperAdminCompany(company.getId());
                // 2023/8/2 清除超级管理员原挂载工作组信息
                deleteSuperAdminGroupInfo();
            }
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.SAVE_ERROR);
    }

    @Override
    public ResponseUtil update(Company company) {
        String upperCase = StringUtils.upperCase(company.getCode());
        company.setCode(upperCase);
        //验证公司代码
        if(companyService.findCodeExist(company)){
            return ResponseUtil.failure(500, "单位代码重复");
        }
        boolean b = companyService.updateById(company);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil delete(Long id) {
        Company company = companyService.getById(id);
        if(company==null || company.getCurrent()){
            return ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
        }
        boolean b = companyService.removeById(id);
        if(b){
            //删除公司关联数据
            ThreadPoolUtil.getPool().execute(() -> {
                deleteUser(id);
                // 删除分组信息
                groupService.removeByCompanyId(id);
                deleteCompanyFile(id);
                deleteHomePage(id);
                deleteProject(id);
                // 删除日志信息
                operateLogService.removeByCompanyId(id);
            });
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.DELETE_ERROR);
    }

    @Override
    public ResponseUtil updateCurrent(Long id) {
        boolean b = companyService.updateCurrent(id);
        if (b){
            updateSuperAdminCompany(id);
            // 2023/8/2 清除超级管理员原挂载工作组信息
            deleteSuperAdminGroupInfo();
        }
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public ResponseUtil updateStatus(Long id) {
        boolean b = companyService.updateStatus(id);
        return b ? ResponseUtil.success() : ResponseUtil.failure(ResponseEnum.UPDATE_ERROR);
    }

    @Override
    public List<DropVO> getCompanyDrop() {
        List<Company> list = companyService.list();
        list = list.stream().sorted(Comparator.comparing(Company::getCurrent).reversed()).collect(Collectors.toList());
        return DzBeanUtils.listCopy(list, DropVO.class);
    }

    @Override
    public Map<String, Object> getCompanyConfigTree(Long id) {
        Map<String, Object> map = new HashMap<>();
        List<Dict> dictList = dictService.queryList(Collections.singletonList("func_config"));
        List<Long> leafKeyList = dictList.stream().map(Dict::getId).filter(itemId ->
                dictList.stream().noneMatch(e -> itemId.equals(e.getPid()))
        ).collect(Collectors.toList());
        List<Dict> leafDict = dictList.stream().filter(item -> leafKeyList.contains(item.getId())).collect(Collectors.toList());
        List<Tree> trees = new ArrayList<>();
        List<Dict> list = dictList.stream().filter(item -> item.getPid() == null).collect(Collectors.toList());
        for (Dict dict : list) {
            Tree tree = new Tree(dict.getId(), dict.getValue(), dict.getPid(), "bars", dict.getSeq());
            addChildNode(tree, dictList);
            trees.add(tree);
        }
        Company company = companyService.getById(id);
        String configInfo = StringUtils.isNotEmpty(company.getFunctionConfig()) ? company.getFunctionConfig() : "";
        List<Long> checkKey = new ArrayList<>();
        setExtraInfo(trees, configInfo, checkKey);
        map.put("treeData", trees);
        map.put("leafKey", leafKeyList);
        map.put("leafDict", leafDict);
        map.put("checkKey", checkKey);
        return map;
    }

    //region 私有方法
    /**
     * 设置树节点属性
     *
     * @param trees      trees
     * @param configInfo configInfo
     * @param checkKey checkKey
     */
    private void setExtraInfo(List<Tree> trees, String configInfo, List<Long> checkKey) {
        trees.forEach(item -> {
            if(item.getChildren()==null || item.getChildren().size()==0){
                item.setIsLeaf(true);
                item.setChecked(configInfo.contains(item.getTitle()));
                if (configInfo.contains(item.getTitle())){
                    checkKey.add(item.getKey());
                }
            }else {
                setExtraInfo(item.getChildren(), configInfo, checkKey);
            }
        });
    }

    /**
     * 添加子节点
     * @param parent parent
     * @param dictList dictList
     */
    private void addChildNode(Tree parent, List<Dict> dictList){
        if(dictList.size() > 0){
            List<Dict> collect = dictList.stream().filter(e -> parent.getKey().equals(e.getPid()))
                    .collect(Collectors.toList());
            List<Tree> childNodes = new ArrayList<>();
            for (Dict dict : collect) {
                Tree tree = new Tree(dict.getId(), dict.getValue(), dict.getPid(), "bars", dict.getSeq());
                childNodes.add(tree);
                addChildNode(tree, dictList);
            }
            parent.setChildren(childNodes);
        }
    }

    /**
     * 删除超级管理员挂载组信息
     */
    private void deleteSuperAdminGroupInfo(){
        LambdaUpdateWrapper<UserGroup> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserGroup::getUserId, 1);
        userGroupService.remove(wrapper);
    }

    /**
     * 修改超级管理员所属单位
     * @param companyId companyId
     */
    private void updateSuperAdminCompany(Long companyId) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(User::getCompanyId, companyId).eq(User::getUsername, "admin");
        userService.update(wrapper);
    }

    /**
     * 删除工程数据
     */
    private void deleteProject(Long id) {
        List<ProjectVO> projects = projectService.listByCompanyId(id);
        projects.forEach(project -> projectBiz.deleteProject(project.getId(), false));
    }

    /**
     * 删除主页信息
     */
    private void deleteHomePage(Long id) {
        List<HomePage> list = homePageService.listByCompanyId(id);
        ArrayList<Long> picIds = new ArrayList<>();
        list.forEach(item -> {
            if (item.getLeftFullId() != null){
                picIds.add(item.getLeftFullId());
            }
            if (item.getLeftRollId() != null){
                picIds.add(item.getLeftRollId());
            }
        });
        if (picIds.size() > 0 ){
            commonBiz.deleteFileByIds(picIds);
        }
        homePageService.removeByCompanyId(id);
    }

    /**
     * 删除单位下文件
     * @param id id
     */
    private void deleteCompanyFile(Long id) {
        List<File> files = fileService.getByCompanyId(id);
        if (files.size() > 0){
            List<Long> fileIds = files.stream().map(File::getId).collect(Collectors.toList());
            List<Long> categoryIds = files.stream().map(File::getCategoryId).collect(Collectors.toList());
            commonBiz.deleteFileByIds(fileIds);
            fileStoreService.removeByIds(categoryIds);
        }
    }

    /**
     * 删除人员信息
     */
    private void deleteUser(Long id) {
        List<User> list = userService.findByCompanyId(id);
        if (list.size() > 0){
            List<Long> userIds = list.stream().map(User::getId).collect(Collectors.toList());
            userGroupService.removeByUserIds(userIds);
            userService.removeByIds(userIds);
            String[] appIds = list.stream().map(User::getAppId)
                    .filter(StringUtils::isNotEmpty).toArray(String[]::new);
            if (appIds.length > 0){
                qwUtil.deleteUserBatch(new DeleteMemberBatch(appIds));
            }
        }
    }

    /**
     * 创建默认管理员
     */
    private void creatDefaultAdmin(Company company) {
        String username = company.getCode()+"_Admin";
        if (userService.findByUsername(username)==null){
            // 新增成功，添加默认管理员 (单位代码_Admin)
            User user = new User();
            user.setCompanyId(company.getId())
                    .setRoleId(2L)
                    .setName("默认管理员")
                    .setUsername(username)
                    .setDeleteFlg(false)
                    .setPassword(new BCryptPasswordEncoder().encode("888888"));
            userService.save(user);
        }
    }

    /**
     * 新增公司首页信息
     */
    private void saveHomePage(Company company) {
        HomePage page = new HomePage();
        page.setCompanyId(company.getId());
        page.setLabelName(company.getName());
        page.setTitleName(company.getName());
        homePageService.save(page);
    }

    //endregion
}
