package com.drunk.goods.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tb_template")
public class Template implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;//ID

    @Column(name = "name")
    private String name;//模板名称

    @Column(name = "spec_num")
    private Integer specNum;//规格数量

    @Column(name = "para_num")
    private Integer paraNum;//参数数量

}
