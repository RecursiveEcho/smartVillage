package com.backend.media.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import com.backend.media.vo.UploadVO;
import com.backend.media.vo.DetailVO;
import com.backend.media.vo.PageVO;
import com.backend.media.entity.MediaEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author chenyang
 * &#064;date 2026/4/20
 * &#064;description 媒体资源服务
 */
public interface MediaService extends IService<MediaEntity> {
    /*
      上传媒体资源
      @param file 媒体资源文件
     * @param fileType 文件类型
     * @param category 分类
     * @param request 请求
     * @return 上传结果
     */
    /**
     * @param bindTarget 可选，绑定目标，如 FEATURE；与 bindEntityId、bindSlot 同时传入时生效
     * @param bindEntityId 业务主键
     * @param bindSlot 绑定字段语义：COVER / VIDEO / IMAGES_APPEND
     */
    UploadVO upload(
            MultipartFile file,
            String fileType,
            String category,
            String usageRemark,
            String bindTarget,
            Long bindEntityId,
            String bindSlot,
            HttpServletRequest request);

    /**
     * 分页查询媒体资源
     * @param current 当前页
     * @param size 每页条数
     * @param fileType 文件类型
     * @param category 分类
     * @param status 状态
     * @return 分页查询结果
     */
    IPage<PageVO> page(Long current, Long size, String fileType, String category, Integer status);

    /**
     * 删除媒体资源
     * @param id 媒体资源id
     */
    void delete(Integer id);

    /**
     * 获取媒体资源详情
     * @param id 媒体资源id
     * @return 媒体资源详情
     */
    DetailVO getDetail(Integer id);

    /**
     *  启用/禁用媒体资源
     * @param id 媒体资源id
     * @param status 状态 0-禁用 1-启用
     */
    void updateStatus(Integer id, Integer status);

    
}