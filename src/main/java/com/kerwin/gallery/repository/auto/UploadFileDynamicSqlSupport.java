package com.kerwin.gallery.repository.auto;

import java.sql.JDBCType;
import java.util.Date;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UploadFileDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    public static final UploadFile uploadFile = new UploadFile();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.id")
    public static final SqlColumn<Long> id = uploadFile.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filename")
    public static final SqlColumn<String> filename = uploadFile.filename;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filename_server")
    public static final SqlColumn<String> filenameServer = uploadFile.filenameServer;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.relative_filename_server")
    public static final SqlColumn<String> relativeFilenameServer = uploadFile.relativeFilenameServer;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.filepath_server")
    public static final SqlColumn<String> filepathServer = uploadFile.filepathServer;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.thumbnail")
    public static final SqlColumn<String> thumbnail = uploadFile.thumbnail;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.file_type")
    public static final SqlColumn<String> fileType = uploadFile.fileType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: sys_upload_file.create_time")
    public static final SqlColumn<Date> createTime = uploadFile.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    public static final class UploadFile extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> filename = column("filename", JDBCType.VARCHAR);

        public final SqlColumn<String> filenameServer = column("filename_server", JDBCType.VARCHAR);

        public final SqlColumn<String> relativeFilenameServer = column("relative_filename_server", JDBCType.VARCHAR);

        public final SqlColumn<String> filepathServer = column("filepath_server", JDBCType.VARCHAR);

        public final SqlColumn<String> thumbnail = column("thumbnail", JDBCType.VARCHAR);

        public final SqlColumn<String> fileType = column("file_type", JDBCType.VARCHAR);

        public final SqlColumn<Date> createTime = column("create_time", JDBCType.TIMESTAMP);

        public UploadFile() {
            super("sys_upload_file");
        }
    }
}