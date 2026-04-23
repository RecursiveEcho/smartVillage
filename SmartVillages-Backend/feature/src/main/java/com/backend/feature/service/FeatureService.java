package com.backend.feature.service;

import com.backend.feature.entity.FeatureEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.feature.dto.HighlightCreateDTO;
import jakarta.servlet.http.HttpServletRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.backend.feature.vo.FeatureVO;
import java.time.LocalDateTime;
/**
 * @author chenyang
 * &#064;date 2026/4/23
 * &#064;description 乡村风采服务接口
 */
public interface FeatureService extends IService<FeatureEntity> {

    /* 创建乡村风采 */
    void createFeature(HighlightCreateDTO dto, HttpServletRequest request);

    /* 获取乡村风采列表(村民可见) */
    IPage<FeatureVO> getFeatureList(Long current, Long size, String type, Integer getSort, LocalDateTime getCreateTime, LocalDateTime startTime, LocalDateTime endTime, HttpServletRequest request);

    /* 获取乡村风采详情 */
    FeatureVO getFeatureDetail(Long id);

    /* 上下架乡村风采 */
    void updateStatus(Long id, Integer status, HttpServletRequest request);

    /* 管理端获取乡村风采列表 */
    IPage<FeatureVO> getFeatureListByAdmin(Long current, Long size, Integer status, String title, String type, Integer getSort, LocalDateTime getCreateTime, LocalDateTime startTime, LocalDateTime endTime, HttpServletRequest request);

    /* 修改乡村风采 */
    void updateFeature(Long id, HighlightCreateDTO dto, HttpServletRequest request);

    /* 删除乡村风采 */
    void deleteFeature(Long id, HttpServletRequest request);
}