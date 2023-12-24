package com.kerwin.gallery.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface WallpaperService {

    Page<Map<String, Object>> searchThumbnailList(Map<String, Object> params, Pageable pageable);

    Page<Map<String, Object>> searchThumbnailListByIntelligentSemantics(Map<String, Object> params, Pageable pageable);

    Map<String, Object> getThumbnailDetail(Long thumbnailId);

    Page<Map<String, Object>> getThumbnailByLabel(String label, Pageable pageable);

    Boolean addWallpaperDownloadCount(Long wallpaperId);

}
