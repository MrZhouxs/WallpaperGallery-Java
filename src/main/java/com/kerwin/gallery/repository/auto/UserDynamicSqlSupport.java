package com.kerwin.gallery.repository.auto;

import java.sql.JDBCType;
import java.util.Date;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class UserDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: user")
    public static final User user = new User();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: user.id")
    public static final SqlColumn<Long> id = user.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: user.name")
    public static final SqlColumn<String> name = user.name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: user.age")
    public static final SqlColumn<Integer> age = user.age;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: user.create_time")
    public static final SqlColumn<Date> createTime = user.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source field: user.update_time")
    public static final SqlColumn<Date> updateTime = user.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", comments="Source Table: user")
    public static final class User extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);

        public final SqlColumn<String> name = column("`name`", JDBCType.VARCHAR);

        public final SqlColumn<Integer> age = column("age", JDBCType.INTEGER);

        public final SqlColumn<Date> createTime = column("create_time", JDBCType.TIMESTAMP);

        public final SqlColumn<Date> updateTime = column("update_time", JDBCType.TIMESTAMP);

        public User() {
            super("user");
        }
    }
}