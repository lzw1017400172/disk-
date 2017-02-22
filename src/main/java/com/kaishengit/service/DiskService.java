package com.kaishengit.service;

import com.kaishengit.pojo.Disk;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by 刘忠伟 on 2017/2/22.
 */
public interface DiskService {
    List<Disk> getLsit(Integer fid);

    String uploader(Integer fid, MultipartFile file);

    void saveFolder(Integer fid,String folderName);

    Disk findById(Integer id);

    FileInputStream getInputStream(Disk disk) throws FileNotFoundException;

    void delete(Integer id);
}
