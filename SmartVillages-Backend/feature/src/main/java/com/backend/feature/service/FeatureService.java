package com.backend.feature.service;

import com.backend.feature.entity.FeatureEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.backend.feature.dto.HighlightCreateDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.backend.feature.vo.FeatureVO;
/**
 * @author chenyang
 * &#064;date 2026/4/23
 * &#064;description 乡村风采服务接口
 */
public interface FeatureService extends IService<FeatureEntity> {

    /* 创建乡村风采 */
    void createFeature(@Valid@RequestBody HighlightCreateDTO dto, HttpServletRequest request);

    /* 获取乡村风采列表(村民可见) */
    IPage<FeatureVO> getFeatureList(Long current, Long size, String type, Integer getSort, Integer getCreateTime, Integer start, Integer end, HttpServletRequest request);

    /* 获取乡村风采详情 */
    FeatureVO getFeatureDetail(Long id);
}