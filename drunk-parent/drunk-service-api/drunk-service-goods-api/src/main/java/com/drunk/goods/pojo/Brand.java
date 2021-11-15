package com.drunk.goods.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tb_brand")
public class Brand implements Serializable {
    @Id
    private Integer id;//品牌ID
    private String name;//品牌名称
    private String image;//品牌图片地址
    private String letter;//品牌首字母
    private Integer seq;//排序
}
