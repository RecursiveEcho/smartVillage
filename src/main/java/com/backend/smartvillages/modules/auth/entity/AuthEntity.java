package com.backend.smartvillages.modules.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @author chenyang
 * @DataAllArgsConstructor
 * @NoArgsConstructor
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("news")
public class AuthEntity {

@TableId(type = IdType.AUTO)
    private Integer id;
@TableField("title")
    private String title;
@TableField("subtitle")
    private String subtitle;
@TableField("source")
    private String source;
@TableField("author")
    private String author;
@TableField("connect")
    private String connect;
@TableField("publish_time")
    private String publishTime;
@TableField("update_time")
    private String updateTime;
@TableField("cover_image")
    private String coverImage;
@TableField("category_id")
    private Integer categoryId;
@TableField("status")
    private Integer status;
@TableField("click_num")
    private Integer clickNum;
@TableField("is_top")
    private Integer isTop;
}