package com.kaishengit.contorller;

import com.kaishengit.dto.AjaxResult;
import com.kaishengit.exception.NotFountException;
import com.kaishengit.pojo.Disk;
import com.kaishengit.service.DiskService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by 刘忠伟 on 2017/2/22.
 */
@Controller

public class DiskContprller {


    @Autowired
    private DiskService diskService;


    /**
     * 显示网盘的页面。思路：网盘的显示是按照文件夹，最开始在根下，点击文件夹进入。根据数据库的设计，想找哪个文件夹下面的，只需要
     * 只需要把当前资源的id当作fid查询。返回到前端。最开始在根下，fid=0。第一次不传值默认为0.根下
     * @param fid
     * @return
     */
    @GetMapping("/list")
    public String pan(@RequestParam(required = false,defaultValue = "0") Integer fid,Model model){
        //根据fid查找数据，传过来的是资源的id
        List<Disk> diskList = diskService.getLsit(fid);
        model.addAttribute("diskList",diskList);
        model.addAttribute("fid",fid);
        return "list";
    }

    /**
     * 文件上传
     * @param fid   文件的所属文件夹即fid
     * @return
     */
    @PostMapping("/uploader")
    @ResponseBody
    public AjaxResult uploader(Integer fid, MultipartFile file){

        try {
            String soucerName = diskService.uploader(fid, file);
            return new AjaxResult(AjaxResult.SUCCESS,soucerName);

        }catch (RuntimeException ex){
            return new AjaxResult(ex.getMessage());

        }
    }


    /**
     * 创箭新的文件夹，文件夹是虚拟的，所有文件都在一个盘。所以只用添加数据库关系
     * @param fid   folderName
     * @return
     */
    @PostMapping("/saveFolder")
    @ResponseBody
    public AjaxResult saveFolder(Integer fid,String folderName){

        diskService.saveFolder(fid,folderName);

        return new AjaxResult(AjaxResult.SUCCESS,folderName);
    }


    /**
     * 文件下载，单个（不是zip打包下载）
     * @param id
     */
    @GetMapping("/download/{id:\\d+}")
    public void download(@PathVariable Integer id, HttpServletResponse response) throws UnsupportedEncodingException {
        Disk disk = diskService.findById(id);

        if(disk != null){
            //设置文件下载类型为浏览器不能识别的
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String fileName = disk.getSourceName();
            //显示到客户端要反转码
            fileName = new String(fileName.getBytes("UTF-8"),"ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            //根据获取文件的输入流
            try {
                FileInputStream fileInputStream = diskService.getInputStream(disk);
                OutputStream outputStream = response.getOutputStream();
                IOUtils.copy(fileInputStream, outputStream);
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
            }catch (IOException ex){
                throw new RuntimeException("文件下载失败");
            }
        } else {
            throw new RuntimeException("文件不存在！");
        }

    }


    /**
     * 资源的删除
     * @param id    资源id
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public AjaxResult delete(Integer id){
        try {
            diskService.delete(id);
            return new AjaxResult(AjaxResult.SUCCESS,"");
        }catch (NotFountException ex) {
            return new AjaxResult(ex.getMessage());
        }
    }

}
