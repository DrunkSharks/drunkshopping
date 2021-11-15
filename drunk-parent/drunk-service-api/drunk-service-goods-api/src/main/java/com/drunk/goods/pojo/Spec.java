package com.drunk.goods.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tb_spec")
public class Spec implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;//ID

    @Column(name = "name")
    private String name;//名称

    @Column(name = "options")
    private String options;//规格选项

    @Column(name = "seq")
    private Integer seq;//排序

    @Column(name = "template_id")
    private Integer templateId;//模板ID
}
