package com.dzkj.runner;

import com.dzkj.common.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright(c),2018-2023,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2023/2/18
 * @description 初始化控制器配置函数集
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Component
@Slf4j
public class RobotSurveyInitRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        log.info("开始加载系统配置文件信息...");
        BufferedReader br = null;
        try {
            //初始化CommandCodes
            List<String> files = new ArrayList<>();
            files.add("static/CommandCode_Leica.txt");
            files.add("static/CommandCode_Trimble.txt");
            files.add("static/CommandCode_Sokka.txt");
            int validLen = 2;
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                lines.clear();
                ClassPathResource resource = new ClassPathResource(files.get(i));
                br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                String line;
                while (true) {
                    if((line = br.readLine()) != null){
                        lines.add(line);
                    }else {
                        break;
                    }
                }
                switch (i)
                {
                    case 0:
                        CommonUtil.addCode(validLen, CommonUtil.M_COMMAND_CODES_LEICA,0 , lines);
                        break;
                    case 1:
                        CommonUtil.addCode(validLen, CommonUtil.M_COMMAND_CODES_TRIMBLE, 0, lines);
                        break;
                    case 2:
                        CommonUtil. addCode(validLen, CommonUtil.M_COMMAND_CODES_SOKKA,0 , lines);
                        break;
                    default:
                }
                br.close();
            }

            //初始化RtCodes
            files.clear();
            files.add("static/GRC_Code_Leica.txt");
            files.add("static/GRC_Code_Trimble.txt");
            files.add("static/GRC_Code_Sokka.txt");
            validLen = 3;
            for (int i = 0; i < files.size(); i++) {
                lines.clear();
                ClassPathResource resource = new ClassPathResource(files.get(i));
                br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                String line;
                while (true) {
                    if((line = br.readLine()) != null){
                        lines.add(line);
                    }else {
                        break;
                    }
                }
                switch (i)
                {
                    case 0:
                        CommonUtil.addCode(validLen, CommonUtil.M_RT_CODES_LEICA, 1, lines);
                        break;
                    case 1:
                        CommonUtil.addCode(validLen, CommonUtil.M_RT_CODES_TRIMBLE, 1, lines);
                        break;
                    case 2:
                        CommonUtil. addCode(validLen, CommonUtil.M_RT_CODES_SOKKA, 1, lines);
                        break;
                    default:
                }
                br.close();
            }
            log.info("系统配置文件信息加载完成");
        }catch (Exception e){
            log.error("读取数据发生错误：{}", e.getMessage());
        }finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
