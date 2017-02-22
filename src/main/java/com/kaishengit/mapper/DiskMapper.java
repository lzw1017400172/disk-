package com.kaishengit.mapper;

import com.kaishengit.pojo.Disk;

import java.util.List;

/**
 * Created by 刘忠伟 on 2017/2/22.
 */
public interface DiskMapper {
    List<Disk> findByFid(Integer fid);

    Disk findById(Integer fid);

    void save(Disk disk1);

    void delete(Integer id);

    List<Disk> findAll();

    void deleteAll(List<Integer> idList);
}
