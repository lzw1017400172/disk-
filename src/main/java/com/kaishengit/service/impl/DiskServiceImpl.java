package com.kaishengit.service.impl;

import com.google.common.collect.Lists;
import com.kaishengit.exception.NotFountException;
import com.kaishengit.mapper.DiskMapper;
import com.kaishengit.pojo.Disk;
import com.kaishengit.service.DiskService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * Created by 刘忠伟 on 2017/2/22.
 */
@Service
public class DiskServiceImpl implements DiskService {


    @Value("${upload.path}")
    private String filePath;
    @Autowired
    private DiskMapper diskMapper;


    /**
     * 根据fid查找
     * @param fid
     * @return
     */
    @Override
    public List<Disk> getLsit(Integer fid) {
        return diskMapper.findByFid(fid);
    }

    /**
     * 文件上传，
     * @param fid   所属文件夹fid
     * @param file  文件
     * @return
     */
    @Override
    @Transactional//需要保存数据库，还要上传文件
    public String uploader(Integer fid, MultipartFile file) {

            if(!file.isEmpty()) {
                String sourceName = file.getOriginalFilename();
                //创建文件新名字
                String newName = UUID.randomUUID().toString();
                if(sourceName.lastIndexOf(".")!= -1){//没有后缀=-1
                    //有后缀
                    newName += sourceName.substring(sourceName.lastIndexOf("."));
                }
                try{
                    InputStream inputStream = file.getInputStream();

                    //存到磁盘需要新名字，如果原始名字有后缀，这里也要有
                    FileOutputStream outputStream = new FileOutputStream(new File(filePath,newName));
                    IOUtils.copy(inputStream,outputStream);
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();

                    //保存数据库
                    Disk disk1 = new Disk();
                    disk1.setSourceName(sourceName);
                    disk1.setName(newName);
                    disk1.setSize(FileUtils.byteCountToDisplaySize(file.getSize()));
                    disk1.setType(Disk.FILE);
                    disk1.setCreateTime(DateTime.now().toString("yyyy-MM-dd HH:mm"));//现在的时间。来自joda.time.DateTime
                    disk1.setFid(fid);
                    diskMapper.save(disk1);

                    return sourceName;
                } catch (IOException ex){
                    //如果流出现异，catch，catch的话上传失败也不会回滚.在手动抛出运行时
                    throw new RuntimeException("文件上传失败");
                }
            }else{
                throw  new RuntimeException("请选择文件");
            }

    }



    /**
     * 创建新的文件夹
     * @param fid   folderName
     * @return
     */
    @Override
    //只需要更新数据库，不用真正创建这个文件夹，所以只有一个不用事务
    public void saveFolder(Integer fid,String folderName) {

        Disk disk = new Disk();
        disk.setType(Disk.FOLDER);
        disk.setCreateTime(DateTime.now().toString("YYYY-MM-DD HH:mm"));
        disk.setSourceName(folderName);
        disk.setFid(fid);

        diskMapper.save(disk);
    }

    /**
     * 根据id查找资源
     * @param id
     * @return
     */
    @Override
    public Disk findById(Integer id) {
        return diskMapper.findById(id);
    }

    /**
     * 获取文件的输入流
     * @param disk
     * @return
     */
    @Override
    public FileInputStream getInputStream(Disk disk) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(new File(filePath,disk.getName()));

        return inputStream;
    }

    /**
     * 资源的删除根据id。   若果是文件直接删除并且更新数据库，是文件夹就把文件夹内的都删了，并且数据库记录也要删除
     * @param id
     */
    @Override
    @Transactional//删除数据库，还要删除文件
    public void delete(Integer id) {

        Disk disk = diskMapper.findById(id);
        if(disk != null){
            //首先无论是文件还是文件夹这条数据库记录都要删除
            diskMapper.delete(id);
            if(Disk.FILE.equals(disk.getType())){
                //文件，直接删除磁盘中的文件
                File file = new File(filePath,disk.getName());
                file.delete();
            } else {
                //文件夹。 需要对文件夹的子，子子，子子子。。。删除。所以递归
                //删除文件，就只能一个一个删除。删除数据库，为了提高性能，要批量删除。所以创建集合，为需要删除的disk的id
                //集合属于对象，参数传递之后，对迭代的集合改变会影响集合
                List<Integer> idList = Lists.newArrayList();
                //所有disk对象的集合，遍历出fid为此id的对象。
                List<Disk> diskList = diskMapper.findAll();
                findDelId(idList,diskList,id);

                //执行删除
                diskMapper.deleteAll(idList);
            }
        } else {
            throw new NotFountException();
        }
    }

    /**
     * 递归。找出数据库所有需要删除的对象的id。已经删除文件
     * @param idList
     * @param diskList
     * @param id
     */
    private void findDelId(List<Integer> idList, List<Disk> diskList, Integer id) {

        for(Disk disk:diskList){
            if(disk.getFid() == id){
                //从所有diskList中找出属于此id文件夹下的，无论是文件还是文件夹，数据库都是要删除的。
                idList.add(disk.getId());

                if(Disk.FILE.equals(disk.getType())){
                    //直接删除此文件
                    File file = new File(filePath,disk.getName());
                    file.delete();
                } else {
                    //文件夹需要递归。把此文件夹的id，当作fid去遍历
                    findDelId(idList,diskList,disk.getId());
                }
            }
        }

    }


}
