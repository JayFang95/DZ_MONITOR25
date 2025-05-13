package com.dzkj.common.util;

import com.dzkj.biz.data.vo.EchartData;
import com.dzkj.entity.File;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Copyright(c),2018-2021,合肥市鼎足空间技术有限公司
 *
 * @author jing.fang
 * @date 2021/8/16
 * @description 文件操作工具类
 * history
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
@Slf4j
public class FileUtil {

    public static final String BASE_PATH = "File";

    /**
     * 文件保存
     *
     * @date 2021/8/16 10:12
     * @param file file
     * @param multipartFile multipartFile
     * @return java.lang.String
    **/
    public static String store(File file, MultipartFile multipartFile){
        String path = generateFolder(file);
        java.io.File dir = new java.io.File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("创建文件夹失败");
            }
        }
        String originalFilename = multipartFile.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new RuntimeException("上传文件为空");
        }
        String lastName = originalFilename.substring(originalFilename.lastIndexOf("."));
        String firstName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String fileName = firstName + "_" + System.currentTimeMillis() + lastName;
        try {
            FileOutputStream out = new FileOutputStream(new java.io.File(path, fileName));
            out.write(multipartFile.getBytes());
            out.flush();
            out.close();
        } catch (Exception ex) {
            log.warn(ex.getLocalizedMessage());
        }
        file.setName(originalFilename);
        file.setRealName(fileName);

        return BASE_PATH
                + "/" + file.getCategoryName()
                + "/" + file.getCategoryId()
                + "/" + file.getScopeName()
                + "/" + fileName;
    }

    /**
     * 根据前端eChart参数生成图片保存
     *
     * @description 根据前端eChart参数生成图片保存
     * @author jing.fang
     * @date 2022/6/1 14:43
     * @param echartData echartData
     * @return java.lang.String
    **/
    public static String storeEchart(EchartData echartData){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormat = format.format(new Date());
        String path = BASE_PATH + java.io.File.separatorChar + "temp-chart" + java.io.File.separatorChar + dateFormat;
        java.io.File dir = new java.io.File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("创建文件夹失败");
            }
        }
        if (StringUtils.isBlank(echartData.getImagDataUrl())) {
            throw new RuntimeException("上传文件为空");
        }
        String fileName = System.currentTimeMillis() + ".png";
        try {
            //拆分base64编码后部分
            String[] imgUrlArr = echartData.getImagDataUrl().split("base64,");
            byte[] buffer = Base64.decode(imgUrlArr[1]);
            FileOutputStream out = new FileOutputStream(new java.io.File(path, fileName));
            out.write(buffer);
            out.flush();
            out.close();
        } catch (Exception ex) {
            log.warn(ex.getLocalizedMessage());
        }
        return BASE_PATH + "/" + "temp-chart" + "/" + dateFormat + "/" + fileName;
    }

    /**
     * 下载文件
     *
     * @date 2021/8/16 10:43
     * @param file file
     * @param response response
     * @return void
    **/
    public static void download(File file, HttpServletResponse response) {
        Path path = getFilePath(file);
        // 判断是否存在文件
        if (Files.exists(path)) {
            response.setContentType("application/octet-stream;charset=utf8");
            response.addHeader("Content-Disposition"
                    , "attachment; filename=" + new String(file.getRealName().getBytes(StandardCharsets.UTF_8)
                            , StandardCharsets.ISO_8859_1).replace(',', '_'));
            response.addHeader("filesize", String.valueOf(path.toFile().length()));
            try {
                Files.copy(path, response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException ex) {
                log.error(ex.getMessage());
            }
        } else {
            log.error("文件不存在");
        }
    }

    /**
     * 批量下载
     *
     * @date 2021/8/16 10:43
     * @param data file
     * @param response response
     * @return void
    **/
    public static void downloadZip(File data, HttpServletResponse response) {
        response.setContentType("application/octet-stream;charset=utf8");
        response.addHeader("Content-Disposition"
                , "attachment; filename=" + new String("附件.zip".getBytes(StandardCharsets.UTF_8)
                        , StandardCharsets.ISO_8859_1).replace(',', '_'));
        byte[] buffer = new byte[1024];
        String path = generateFolder(data);

        try {
            ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
            java.io.File file = new java.io.File(path);
            java.io.File[] fileList = file.listFiles();
            assert fileList != null;
            for (java.io.File fileTemp : fileList) {
                FileInputStream fis = new FileInputStream(fileTemp);
                out.putNextEntry(new ZipEntry(fileTemp.getName()));
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.closeEntry();
                fis.close();
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException("下载失败!");
        }
    }

    /**
     * 删除文件
     *
     * @description 删除文件
     * @author jing.fang
     * @date 2021/8/16 16:42
     * @param file file
     * @return void
    **/
    public static boolean delete(File file) {
        String path = generateFolder(file) + java.io.File.separatorChar + file.getRealName();
        java.io.File item = new java.io.File(path);
        return item.delete();
    }

    public static String generateFolder(File file){
        return generateCategoryId(file.getCategoryName(), String.valueOf(file.getCategoryId()))
                + java.io.File.separatorChar + file.getScopeName();
    }

    public static String generatePdfTransFolder(){
        return generateCategoryId("trans_pdf", "0")
                + java.io.File.separatorChar + "preview";
    }

    public static String generateCategoryId(String categoryName, String categoryId) {
        return BASE_PATH + java.io.File.separatorChar + categoryName + java.io.File.separatorChar + categoryId;
    }

    public static Path getFilePath(File file){
        return Paths.get(generateFolder(file), file.getRealName());
    }

    public static boolean isExit(String filePath) {
        java.io.File file = new java.io.File(filePath);
        return file.exists();
    }

    /**
     * 保存测量日志到本地
     * @param backInfos 过程日志信息
     */
    public static void exportUserLog(List<String> backInfos, String path){
        StringBuilder sb = new StringBuilder();
        backInfos.forEach(info -> sb.append(info).append("\r\n"));
        java.io.File dir = new java.io.File(path.substring(0, path.lastIndexOf(java.io.File.separatorChar)));
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                log.info("创建文件夹失败");
            }
        }
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(sb.toString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 预览文本和图片文件
     *
     * @description 预览文本和图片文件
     * @author jing.fang
     * @date 2023/8/1 16:33
     * @param fileInfo fileInfo
     * @return: java.lang.String
     **/
    public static String previewTextAndImageFile(File fileInfo) {
        try {
            Path path = getFilePath(fileInfo);
            java.io.File file = path.toFile();
            String fileType = getImageFileType(file);
            if (StringUtils.isNotEmpty(fileType) && isImage(file)) {

                BufferedImage bufferedImage = Thumbnails.of(file).width(500).asBufferedImage();
                InputStream inputStream = bufferedImageToInputStream(bufferedImage, fileType);
                String s = fileToBase64(inputStream);

                return "data:image/" + fileType + ";base64," + s;
            } else {
                // 设置编码格式
                StringBuilder write = new StringBuilder();
                String enter = "&#10;";
                if (Files.exists(path)) {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        write.append(line);
                        write.append(enter);
                    }
                    br.close();
                }
                return write.toString();
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将文件转base64字符串  File转成编码成BASE64
     *
     * @description 将文件转base64字符串  File转成编码成BASE64
     * @author jing.fang
     * @date 2023/8/1 16:41
     * @param inputStream inputStream
     * @return: java.lang.String
     **/
    public static String fileToBase64(InputStream inputStream) {
        String base64 = null;
        InputStream in = null;
        try {
            in = inputStream;
            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            base64 = java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    /**
     * 将BufferedImage转换为InputStream
     *
     * @description 将BufferedImage转换为InputStream
     * @author jing.fang
     * @date 2023/8/1 16:40
     * @param image image
     * @param fileType fileType
     * @return: java.io.InputStream
     **/
    public static InputStream bufferedImageToInputStream(BufferedImage image, String fileType) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, fileType, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取图片文件的具体类型
     *
     * @description 获取图片文件的具体类型
     * @author jing.fang
     * @date 2023/8/1 16:36
     * @param file file
     * @return: java.lang.String
     **/
    public static String getImageFileType(java.io.File file) {
        if (isImage(file)) {
            try {
                ImageInputStream iis = ImageIO.createImageInputStream(file);
                Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
                if (!iter.hasNext()) {
                    return null;
                }
                ImageReader reader = iter.next();
                iis.close();
                return reader.getFormatName();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 判断文件是否是图片类型(bmp/gif/jpg/png)
     * @param file file
     * @return boolean
     */
    public static boolean isImage(java.io.File file){
        try {
            // 通过ImageReader来解码这个file并返回一个BufferedImage对象
            // 如果找不到合适的ImageReader则会返回null，我们可以认为这不是图片文件
            // 或者在解析过程中报错，也返回false
            Image image = ImageIO.read(file);
            return image != null;
        } catch(IOException ex) {
            return false;
        }
    }

    /**
     * pdf格式文件预览
     *
     * @description pdf格式文件预览
     * @author jing.fang
     * @date 2023/8/1 16:44
     * @param fileInfo fileInfo
     * @param response response
     * @return: void
     **/
    public static void previewPdfFile(File fileInfo, HttpServletResponse response) {
        try {
            // 获取pdf文件路径（包括文件名）
            Path path = getFilePath(fileInfo);
            FileInputStream inStream = new FileInputStream(path.toFile());
            // 设置输出的格式
            response.setContentType( "application/pdf;charset=UTF-8");
            OutputStream outputStream= response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 1024];
            while ((count =inStream.read(buffer)) != -1){
                outputStream.write(buffer, 0,count);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
