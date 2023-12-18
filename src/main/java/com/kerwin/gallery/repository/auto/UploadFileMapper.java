package com.kerwin.gallery.repository.auto;

import static com.kerwin.gallery.repository.auto.UploadFileDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.kerwin.gallery.domain.auto.UploadFile;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface UploadFileMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    BasicColumn[] selectList = BasicColumn.columnList(id, filename, filenameServer, relativeFilenameServer, filepathServer, thumbnail, fileType, createTime);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    @Options(useGeneratedKeys=true,keyProperty="record.id")
    int insert(InsertStatementProvider<UploadFile> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    @Insert({
        "${insertStatement}"
    })
    @Options(useGeneratedKeys=true,keyProperty="records.id")
    int insertMultiple(@Param("insertStatement") String insertStatement, @Param("records") List<UploadFile> records);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int insertMultiple(MultiRowInsertStatementProvider<UploadFile> multipleInsertStatement) {
        return insertMultiple(multipleInsertStatement.getInsertStatement(), multipleInsertStatement.getRecords());
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("UploadFileResult")
    Optional<UploadFile> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="UploadFileResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        @Result(column="filename", property="filename", jdbcType=JdbcType.VARCHAR),
        @Result(column="filename_server", property="filenameServer", jdbcType=JdbcType.VARCHAR),
        @Result(column="relative_filename_server", property="relativeFilenameServer", jdbcType=JdbcType.VARCHAR),
        @Result(column="filepath_server", property="filepathServer", jdbcType=JdbcType.VARCHAR),
        @Result(column="thumbnail", property="thumbnail", jdbcType=JdbcType.VARCHAR),
        @Result(column="file_type", property="fileType", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.TIMESTAMP)
    })
    List<UploadFile> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, uploadFile, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, uploadFile, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int deleteByPrimaryKey(Long id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int insert(UploadFile record) {
        return MyBatis3Utils.insert(this::insert, record, uploadFile, c ->
            c.map(filename).toProperty("filename")
            .map(filenameServer).toProperty("filenameServer")
            .map(relativeFilenameServer).toProperty("relativeFilenameServer")
            .map(filepathServer).toProperty("filepathServer")
            .map(thumbnail).toProperty("thumbnail")
            .map(fileType).toProperty("fileType")
            .map(createTime).toProperty("createTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int insertMultiple(Collection<UploadFile> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, uploadFile, c ->
            c.map(filename).toProperty("filename")
            .map(filenameServer).toProperty("filenameServer")
            .map(relativeFilenameServer).toProperty("relativeFilenameServer")
            .map(filepathServer).toProperty("filepathServer")
            .map(thumbnail).toProperty("thumbnail")
            .map(fileType).toProperty("fileType")
            .map(createTime).toProperty("createTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int insertSelective(UploadFile record) {
        return MyBatis3Utils.insert(this::insert, record, uploadFile, c ->
            c.map(filename).toPropertyWhenPresent("filename", record::getFilename)
            .map(filenameServer).toPropertyWhenPresent("filenameServer", record::getFilenameServer)
            .map(relativeFilenameServer).toPropertyWhenPresent("relativeFilenameServer", record::getRelativeFilenameServer)
            .map(filepathServer).toPropertyWhenPresent("filepathServer", record::getFilepathServer)
            .map(thumbnail).toPropertyWhenPresent("thumbnail", record::getThumbnail)
            .map(fileType).toPropertyWhenPresent("fileType", record::getFileType)
            .map(createTime).toPropertyWhenPresent("createTime", record::getCreateTime)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default Optional<UploadFile> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, uploadFile, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default List<UploadFile> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, uploadFile, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default List<UploadFile> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, uploadFile, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default Optional<UploadFile> selectByPrimaryKey(Long id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, uploadFile, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    static UpdateDSL<UpdateModel> updateAllColumns(UploadFile record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(filename).equalTo(record::getFilename)
                .set(filenameServer).equalTo(record::getFilenameServer)
                .set(relativeFilenameServer).equalTo(record::getRelativeFilenameServer)
                .set(filepathServer).equalTo(record::getFilepathServer)
                .set(thumbnail).equalTo(record::getThumbnail)
                .set(fileType).equalTo(record::getFileType)
                .set(createTime).equalTo(record::getCreateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(UploadFile record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(filename).equalToWhenPresent(record::getFilename)
                .set(filenameServer).equalToWhenPresent(record::getFilenameServer)
                .set(relativeFilenameServer).equalToWhenPresent(record::getRelativeFilenameServer)
                .set(filepathServer).equalToWhenPresent(record::getFilepathServer)
                .set(thumbnail).equalToWhenPresent(record::getThumbnail)
                .set(fileType).equalToWhenPresent(record::getFileType)
                .set(createTime).equalToWhenPresent(record::getCreateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int updateByPrimaryKey(UploadFile record) {
        return update(c ->
            c.set(filename).equalTo(record::getFilename)
            .set(filenameServer).equalTo(record::getFilenameServer)
            .set(relativeFilenameServer).equalTo(record::getRelativeFilenameServer)
            .set(filepathServer).equalTo(record::getFilepathServer)
            .set(thumbnail).equalTo(record::getThumbnail)
            .set(fileType).equalTo(record::getFileType)
            .set(createTime).equalTo(record::getCreateTime)
            .where(id, isEqualTo(record::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: sys_upload_file")
    default int updateByPrimaryKeySelective(UploadFile record) {
        return update(c ->
            c.set(filename).equalToWhenPresent(record::getFilename)
            .set(filenameServer).equalToWhenPresent(record::getFilenameServer)
            .set(relativeFilenameServer).equalToWhenPresent(record::getRelativeFilenameServer)
            .set(filepathServer).equalToWhenPresent(record::getFilepathServer)
            .set(thumbnail).equalToWhenPresent(record::getThumbnail)
            .set(fileType).equalToWhenPresent(record::getFileType)
            .set(createTime).equalToWhenPresent(record::getCreateTime)
            .where(id, isEqualTo(record::getId))
        );
    }
}