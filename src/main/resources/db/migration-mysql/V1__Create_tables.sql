create table if not exists `user`
(
    `id`          bigint(20)      AUTO_INCREMENT COMMENT '自增主键',
    `name`        varchar(255)    NOT NULL COMMENT '姓名',
    `age`         int(11)         NOT NULL COMMENT '年龄',
    `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '添加时间',
    `update_time` timestamp(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT '用户表';

CREATE TABLE if not exists `sys_upload_file`
(
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
   `filename` varchar(255) DEFAULT NULL COMMENT '文件名称',
   `filename_server` varchar(255) DEFAULT NULL COMMENT '上传成功后在服务器上的名称',
   `relative_filename_server` varchar(255) DEFAULT NULL COMMENT '上传成功后再服务器上的相对名称',
   `filepath_server` varchar(255) DEFAULT NULL COMMENT '上传成功后在服务器上的路径',
   `thumbnail` varchar(255) DEFAULT NULL COMMENT '如果是图片，图片的缩略图的相对路径',
   `file_type` varchar(32) DEFAULT NULL COMMENT '文件类型：docx;pptx;pdf;excel;py;zip;',
   `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '添加时间',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='文件上传记录表';

CREATE TABLE if not exists `wallpaper`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `url` varchar(255) DEFAULT NULL COMMENT '图片爬取的URL',
    `width` int(11) DEFAULT NULL COMMENT '图片的宽度',
    `height` int(11) DEFAULT NULL COMMENT '图片的高度',
    `mdsum` varchar(128) DEFAULT NULL COMMENT '图片的MD5',
    `filename` varchar(255) DEFAULT NULL COMMENT '图片文件名称',
    `filename_server` varchar(255) DEFAULT NULL COMMENT '存储在服务器上的名称',
    `filepath_server` varchar(255) DEFAULT NULL COMMENT '存储在服务器上的路径',
    `relative_filepath_server` varchar(255) DEFAULT NULL COMMENT '存储在服务器上的相对路径',
    `size` bigint(20) DEFAULT NULL COMMENT '图片大小，单位Byte',
    `size_ch` varchar (255) DEFAULT NULL COMMENT '图片大小，整理后一眼能看懂的',
    `file_type` varchar(32) DEFAULT NULL COMMENT '图片真实的文件类型',
    `download_count` bigint(20) DEFAULT 0 COMMENT '下载次数统计',
    `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '添加时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='壁纸原图表';

CREATE TABLE if not exists `thumbnail`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `url` varchar(255) DEFAULT NULL COMMENT '图片爬取的URL',
    `width` int(11) DEFAULT NULL COMMENT '图片的宽度',
    `height` int(11) DEFAULT NULL COMMENT '图片的高度',
    `mdsum` varchar(128) DEFAULT NULL COMMENT '图片的MD5',
    `filename` varchar(255) DEFAULT NULL COMMENT '图片文件名称',
    `filename_server` varchar(255) DEFAULT NULL COMMENT '存储在服务器上的名称',
    `filepath_server` varchar(255) DEFAULT NULL COMMENT '存储在服务器上的路径',
    `relative_filepath_server` varchar(255) DEFAULT NULL COMMENT '存储在服务器上的相对路径',
    `file_type` varchar(32) DEFAULT NULL COMMENT '图片真实的文件类型',
    `preview_count` bigint(20) DEFAULT 0 COMMENT '浏览次数统计',
    `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '添加时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='缩略图-图片表';

CREATE TABLE if not exists `thumbnail_wallpaper`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `thumbnail_id` bigint(20) DEFAULT NULL COMMENT '缩略图ID',
    `wallpaper_id` bigint(20) DEFAULT NULL COMMENT '原图ID',
    `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '添加时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='缩略图与原图中间表-1:N';

CREATE TABLE if not exists `dimensions`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `width` int(11) DEFAULT NULL COMMENT '图片的宽度',
    `height` int(11) DEFAULT NULL COMMENT '图片的高度',
    `size` varchar(255) DEFAULT NULL COMMENT 'width*height',
    `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '添加时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='图片分辨率表';

CREATE TABLE if not exists `thumbnail_dimensions`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `thumbnail_id` bigint(20) DEFAULT NULL COMMENT '缩略图ID',
    `dimensions_id` bigint(20) DEFAULT NULL COMMENT '分辨率ID',
    `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '缩略图与分辨率中间表-1:N',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='图片与图片尺寸的中间表';

CREATE TABLE if not exists `category`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `name` varchar(255) DEFAULT NULL COMMENT '标签名',
    `href` varchar(255) DEFAULT NULL COMMENT '前端直接可以使用的URL路由',
    `parent_id` bigint(20) DEFAULT NULL COMMENT '父标签ID',
    `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '添加时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='标签表';

CREATE TABLE if not exists `thumbnail_category`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `thumbnail_id` bigint(20) DEFAULT NULL COMMENT '缩略图ID',
    `category_id` bigint(20) DEFAULT NULL COMMENT '标签ID',
    `create_time` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) COMMENT '添加时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='图片与标签的中间表';
