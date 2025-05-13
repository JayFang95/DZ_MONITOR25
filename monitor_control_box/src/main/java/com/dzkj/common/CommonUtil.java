package com.dzkj.common;

import com.dzkj.bean.CommandCode;
import com.dzkj.bean.RtCode;
import com.dzkj.enums.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/16
 * @description 通用函数集
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
public class CommonUtil {

    public static final List<CommandCode> M_COMMAND_CODES_LEICA = new ArrayList<>();
    public static final List<CommandCode> M_COMMAND_CODES_TRIMBLE = new ArrayList<>();
    public static final List<CommandCode> M_COMMAND_CODES_SOKKA = new ArrayList<>();

    public static final List<RtCode> M_RT_CODES_LEICA = new ArrayList<>();
    public static final List<RtCode> M_RT_CODES_TRIMBLE = new ArrayList<>();
    public static final List<RtCode> M_RT_CODES_SOKKA = new ArrayList<>();

    /**
     * 从系统文件中读取返回码
     *
     * @description: 从系统文件中读取返回码
     * @author: jing.fang
     * @Date: 2023/2/16 14:37
    **/
    public static void init()
    {
        log.info("开始加载系统配置文件信息。。。");
        //初始化CommandCodes
        List<String> files = new ArrayList<>();
        files.add("static/CommandCode_Leica.txt");
        files.add("static/commandCode_Trimble.txt");
        files.add("static/commandCode_Sokka.txt");
        int validLen = 2;
        for (int i = 0; i < files.size(); i++)
        {
            switch (i)
            {
                case 0:
                    getInfoFromFile(files.get(i), validLen, M_COMMAND_CODES_LEICA, 0);
                    break;
                case 1:
                    getInfoFromFile(files.get(i), validLen, M_COMMAND_CODES_TRIMBLE, 0);
                    break;
                case 2:
                    getInfoFromFile(files.get(i), validLen, M_COMMAND_CODES_SOKKA, 0);
                    break;
                default:
            }
        }

        //初始化RtCodes
        files.clear();
        files.add("static/GRC_Code_Leica.txt");
        files.add("static/GRC_Code_Trimble.txt");
        files.add("static/GRC_Code_Sokka.txt");
        validLen = 3;
        for (int i = 0; i < files.size(); i++)
        {
            switch (i)
            {
                case 0:
                    getInfoFromFile(files.get(i), validLen, M_RT_CODES_LEICA, 1);
                    break;
                case 1:
                    getInfoFromFile(files.get(i), validLen, M_RT_CODES_TRIMBLE, 1);
                    break;
                case 2:
                    getInfoFromFile(files.get(i), validLen, M_RT_CODES_SOKKA, 1);
                    break;
                default:
            }
        }
        log.info("系统配置文件信息加载完成");
    }

    /**
     * 从文件中读取有效信息列表
     *
     * @description: 从文件中读取有效信息列表
     * @author: jing.fang
     * @Date: 2023/2/16 14:43
     * @param file 文件名
     * @param validLen 有效长度
     * @param results 结果列表
     * @param type 泛型类型
    **/
    public static <T> void getInfoFromFile(String file, int validLen, List<T> results, int type)
    {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(file);
            List<String> lines = Files.readAllLines(Paths.get(resources[0].getFile().getAbsolutePath()));
            addCode(validLen, results, type, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取有效信息列表
     *
     * @description 从文件中读取有效信息列表
     * @author jing.fang
     * @date 2023/3/7 15:14
     * @param resource 文件资源
     * @param validLen 有效长度
     * @param results 结果列表
     * @param type 泛型类型
     * @return: void
    **/
    public static <T> void getInfoFromFile(Resource resource, int validLen, List<T> results, int type)
    {
        try {
            List<String> lines = Files.readAllLines(Paths.get(resource.getFile().getAbsolutePath()));
            addCode(validLen, results, type, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取有效信息列表
     *
     * @description 从文件中读取有效信息列表
     * @author jing.fang
     * @date 2023/3/7 15:26
     * @return: void
    **/
    public static <T> void addCode(int validLen, List<T> results, int type, List<String> lines) {
        for (String line : lines)
        {
            String[] strArray = line.split(";");
            if (strArray.length != validLen) {
                continue;
            }
            if (type == 0)
            {
                CommandCode commandCode = new CommandCode(strArray[0], strArray[1]);
                results.add((T) commandCode);
            }

            if (type == 1)
            {
                RtCode rtCode = new RtCode(strArray[0], Integer.parseInt(strArray[1]), strArray[2]);
                results.add((T) rtCode);
            }
        }
    }

    /**
     * 从RPC指令中返回的消息中，提取返回码信息
     *
     * @description: 从RPC指令中返回的消息中，提取返回码信息
     * @author: jing.fang
     * @Date: 2023/2/16 15:16
     * @param deviceType deviceType
     * @param codeValue  codeValue
     * @return com.dzkj.bean.RtCode
    **/
    public static RtCode getRtCodeByCodeValue(DeviceType deviceType, int codeValue)
    {
        Optional<RtCode> optional;
        switch (deviceType)
        {
            case LEI_CA:
                optional = M_RT_CODES_LEICA.stream().filter(it -> it.getValue() == codeValue).findAny();
                if (optional.isPresent())
                {
                    return optional.get();
                }
                break;
            case TRIMBLE:
                optional = M_RT_CODES_TRIMBLE.stream().filter(it -> it.getValue() == codeValue).findAny();
                if (optional.isPresent())
                {
                    return optional.get();
                }
                break;
            case SOKKA:
                optional = M_RT_CODES_SOKKA.stream().filter(it -> it.getValue() == codeValue).findAny();
                if (optional.isPresent())
                {
                    return optional.get();
                }
                break;
            default:
                return SYS_GRC_OK;
        }
        return SYS_GRC_OK;
    }

    /**
     * 根据指令名称从指令集中返回指令对象
     *
     * @description: 根据指令名称从指令集中返回指令对象
     * @author: jing.fang
     * @Date: 2023/2/16 15:15
     * @param deviceType  deviceType
     * @param name  name
     * @return com.dzkj.bean.CommandCode
    **/
    public static CommandCode getCommandCodeByName(DeviceType deviceType, String name)
    {
        Optional<CommandCode> optional;
        switch (deviceType)
        {
            case LEI_CA:
                optional = M_COMMAND_CODES_LEICA.stream().filter(it -> Objects.equals(it.getName(), name)).findAny();
                break;
            case TRIMBLE:
                optional = M_COMMAND_CODES_TRIMBLE.stream().filter(it -> Objects.equals(it.getName(), name)).findAny();
                break;
            case SOKKA:
                optional = M_COMMAND_CODES_SOKKA.stream().filter(it -> Objects.equals(it.getName(), name)).findAny();
                break;
            default:
                optional = Optional.empty();
        }
        return optional.orElse(null);
    }

    /**
     * 默认
     */
    public static RtCode SYS_GRC_DEFAULT = new RtCode("SYS_GRC_DEFAULT", 90000, "系统默认");

    /**
     * 执行成功
     */
    public static RtCode SYS_GRC_OK = new RtCode("SYS_GRC_OK", 90001, "系统执行成功");

    /**
     * 执行取消
     */
    public static RtCode SYS_GRC_CANCEL = new RtCode("SYS_GRC_CANCEL", 90002, "系统执行取消");

    /**
     * 执行超时
     */
    public static RtCode SYS_GRC_TIMEOUT = new RtCode("SYS_GRC_TIMEOUT", 90003, "系统执行超时");

    /**
     * 控制器掉线
     */
    public static RtCode SYS_GRC_OFFLINE = new RtCode("SYS_GRC_OFFLINE", 90004, "控制器掉线");

    /**
     * 执行异常
     */
    public static RtCode SYS_GRC_ERR = new RtCode("SYS_GRC_ERR", 90005, "系统执行异常");

}
