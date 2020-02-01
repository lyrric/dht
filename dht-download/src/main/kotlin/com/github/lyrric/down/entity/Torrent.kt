package com.github.lyrric.down.entity;

import java.util.Date;
import javax.persistence.*;

public class Torrent extends BaseEntity {
    /**
     * 种子hash
     */
    @Id
    @Column(name = "info_hash")
    private String infoHash;

    /**
     * 类型
     */
    @Column(name = "file_type")
    private String fileType;

    /**
     * 名称
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 文件大小
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 创建时间
     */
    @Column(name = "torrent_create_time")
    private Date torrentCreateTime;

    /**
     * 入库时间
     */
    @Column(name = "add_time")
    private Date addTime;

    /**
     * 文件列表
     */
    private String files;

    /**
     * 获取种子hash
     *
     * @return info_hash - 种子hash
     */
    public String getInfoHash() {
        return infoHash;
    }

    /**
     * 设置种子hash
     *
     * @param infoHash 种子hash
     */
    public void setInfoHash(String infoHash) {
        this.infoHash = infoHash == null ? null : infoHash.trim();
    }

    /**
     * 获取类型
     *
     * @return file_type - 类型
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * 设置类型
     *
     * @param fileType 类型
     */
    public void setFileType(String fileType) {
        this.fileType = fileType == null ? null : fileType.trim();
    }

    /**
     * 获取名称
     *
     * @return file_name - 名称
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置名称
     *
     * @param fileName 名称
     */
    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    /**
     * 获取文件大小
     *
     * @return file_size - 文件大小
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * 设置文件大小
     *
     * @param fileSize 文件大小
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 获取创建时间
     *
     * @return torrent_create_time - 创建时间
     */
    public Date getTorrentCreateTime() {
        return torrentCreateTime;
    }

    /**
     * 设置创建时间
     *
     * @param torrentCreateTime 创建时间
     */
    public void setTorrentCreateTime(Date torrentCreateTime) {
        this.torrentCreateTime = torrentCreateTime;
    }

    /**
     * 获取入库时间
     *
     * @return add_time - 入库时间
     */
    public Date getAddTime() {
        return addTime;
    }

    /**
     * 设置入库时间
     *
     * @param addTime 入库时间
     */
    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    /**
     * 获取文件列表
     *
     * @return files - 文件列表
     */
    public String getFiles() {
        return files;
    }

    /**
     * 设置文件列表
     *
     * @param files 文件列表
     */
    public void setFiles(String files) {
        this.files = files == null ? null : files.trim();
    }
}