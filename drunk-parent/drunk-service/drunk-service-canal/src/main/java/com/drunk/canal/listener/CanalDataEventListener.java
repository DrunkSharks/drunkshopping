package com.drunk.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

@CanalEventListener
public class CanalDataEventListener {

    //添加数据监控
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        System.out.println("---------------添加后：-----------------");
        rowData.getAfterColumnsList().forEach(column->{
            System.out.println(column.getName()+"       "+column.getValue());
        });
    }

    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        System.out.println("---------------修改前：-----------------");
        rowData.getBeforeColumnsList().forEach(column -> {
            System.out.println(column.getName()+"   "+column.getValue());
        });
        System.out.println("---------------修改后：-----------------");
        rowData.getAfterColumnsList().forEach(column -> {
            System.out.println(column.getName()+"   "+column.getValue());
        });
    }

    @DeleteListenPoint
    public void onEventDelete(CanalEntry.RowData rowData){
        System.out.println("---------------删除前：-----------------");
        rowData.getBeforeColumnsList().forEach(column->{
            System.out.println(column.getName()+"   "+column.getValue());
        });
    }

    /*
    @ListenPoint(destination = "example",schema="changgou_content",table={"tb_content","tb_content_category"},eventType = CanalEntry.EventType.UPDATE)
    public void onCustomizeUpdate(CanalEntry.RowData rowData){
        System.out.println("---------------修改前：-----------------");
        rowData.getBeforeColumnsList().forEach(column->{
            System.out.println(column.getName()+"   "+column.getValue());
        });
        System.out.println("---------------修改后：-----------------");
        rowData.getAfterColumnsList().forEach(column -> {
            System.out.println(column.getName()+"   "+column.getValue());
        });
    }*/
}
