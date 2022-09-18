package com.gcc.reggie.controller;

import com.gcc.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //这个参数名要注意和前端的保持一致
        //file是一个临时文件，需要转存到其他的位置，否则本次请求完之后就会被删除了
        log.info(file.toString());

        //原始文件名
        String originalFileName = file.getOriginalFilename();
        //截取文件名的后缀名
        String suffix = originalFileName != null ? originalFileName.substring(originalFileName.lastIndexOf(".")) : null;

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String newFileName = UUID.randomUUID() + suffix;

        //创建一个目录对象
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }

        try {
            //将临时的file文件保存到目录对象
            file.transferTo(new File(basePath+File.separator+newFileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(newFileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+File.separator+name));

            //通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len =0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
