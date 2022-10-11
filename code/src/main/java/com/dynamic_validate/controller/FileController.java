package com.dynamic_validate.controller;

import com.dynamic_validate.data.Data;
import net.minidev.json.JSONObject;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class FileController {

    /**
     * fileName是文件所在的绝对路径
     * 算啦，没办法传.txt的字符串作为参数，还是拼接得了。
     * 下载文件名即报告编号，得嘞！
     * */
    @GetMapping("/download")
    public String fileDownLoad(HttpServletResponse response, @RequestParam("repId") String repId) {
        String fileName = repId + ".txt";
        String filePath = Data.RepTxt + repId + ".txt";
        String info;
        File file = new File(filePath);
        if (!file.exists()) {
            info = "下载文件不存在";
            System.out.println(info);
            return info;
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
            info = "下载成功";
            System.out.println(info);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            info = "下载失败";
            System.out.println(info);
            return info;
        }
        return info;
    }



    //@Value("${file.upload.url}")
    private String uploadFilePath;

    @RequestMapping("/upload")
    public String httpUpload(@RequestParam("files") MultipartFile files[]) {
        JSONObject object = new JSONObject();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getOriginalFilename();  // 文件名
            File dest = new File(uploadFilePath + '/' + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                files[i].transferTo(dest);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                object.put("success", 2);
                object.put("result", "程序错误，请重新上传");
                return object.toString();
            }
        }
        object.put("success", 1);
        object.put("result", "文件上传成功");
        return object.toString();
    }

}
