package com.kerwin.gallery.domain.auto;

import java.io.Serializable;
import java.util.Date;
import javax.annotation.Generated;

public class UploadFile implements Serializable {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.id")
    private Long id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filename")
    private String filename;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filename_server")
    private String filenameServer;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.relative_filename_server")
    private String relativeFilenameServer;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filepath_server")
    private String filepathServer;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.thumbnail")
    private String thumbnail;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.file_type")
    private String fileType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.create_time")
    private Date createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    private static final long serialVersionUID = 1L;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.id")
    public Long getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.id")
    public void setId(Long id) {
        this.id = id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filename")
    public String getFilename() {
        return filename;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filename")
    public void setFilename(String filename) {
        this.filename = filename == null ? null : filename.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filename_server")
    public String getFilenameServer() {
        return filenameServer;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filename_server")
    public void setFilenameServer(String filenameServer) {
        this.filenameServer = filenameServer == null ? null : filenameServer.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.relative_filename_server")
    public String getRelativeFilenameServer() {
        return relativeFilenameServer;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.relative_filename_server")
    public void setRelativeFilenameServer(String relativeFilenameServer) {
        this.relativeFilenameServer = relativeFilenameServer == null ? null : relativeFilenameServer.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filepath_server")
    public String getFilepathServer() {
        return filepathServer;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filepath_server")
    public void setFilepathServer(String filepathServer) {
        this.filepathServer = filepathServer == null ? null : filepathServer.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.thumbnail")
    public String getThumbnail() {
        return thumbnail;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.thumbnail")
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail == null ? null : thumbnail.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.file_type")
    public String getFileType() {
        return fileType;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.file_type")
    public void setFileType(String fileType) {
        this.fileType = fileType == null ? null : fileType.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.create_time")
    public Date getCreateTime() {
        return createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.create_time")
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", filename=").append(filename);
        sb.append(", filenameServer=").append(filenameServer);
        sb.append(", relativeFilenameServer=").append(relativeFilenameServer);
        sb.append(", filepathServer=").append(filepathServer);
        sb.append(", thumbnail=").append(thumbnail);
        sb.append(", fileType=").append(fileType);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}