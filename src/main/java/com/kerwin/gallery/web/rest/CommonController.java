package com.kerwin.gallery.web.rest;

import com.kerwin.gallery.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * ==============================================================================
 * Author:       Kerwin
 * Created:      2023/11/22
 * Description:  文件上传接口
 * ==============================================================================
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Api(value = "通用API", tags = "系统通用接口")
public class CommonController {

    private final CommonService commonService;

    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }


    @ApiOperation(value = "文件上传", notes = "通用的单文件上传接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", name = "file", value = "二进制文件数据", dataType = "__file", required = true),
            @ApiImplicitParam(paramType = "form", name = "relativeDir", value = "文件在服务器上存储的路径"),
            @ApiImplicitParam(paramType = "form", name = "filename", value = "文件在服务器上存储的名字")
    })
    @PostMapping("/upload_file/_file")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam(value = "file") MultipartFile uploadFile,
                                                          @RequestParam(required = false) String relativeDir,
                                                          @RequestParam(required = false) String filename) throws IOException {
        log.debug("REST request to upload file, relativeDir: {}, filename: {}", relativeDir, filename);

        Map<String, Object> mapResult = this.commonService.uploadFile(uploadFile, relativeDir, filename);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }
}
