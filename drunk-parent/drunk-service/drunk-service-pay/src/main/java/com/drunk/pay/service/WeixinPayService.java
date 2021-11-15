package com.drunk.pay.service;

import java.util.Map;

public interface WeixinPayService {

    /***
     * 关闭支付
     * @param orderId
     * @return
     */
    Map<String,String> closePay(Long orderId) throws Exception;

    /*****
     * 创建二维码
     * @param out_trade_no : 客户端自定义订单编号
     * @param total_fee    : 交易金额,单位：分
     * @return
     */
    public Map createNative(Map<String,String> parameter);

    /*****
     * 创建二维码
     * @param out_trade_no : 客户端自定义订单编号
     * @param total_fee    : 交易金额,单位：分
     * @return
     */
    /*public Map createNative(String out_trade_no, String total_fee);*/

    /***
     * 查询订单状态
     * @param out_trade_no : 客户端自定义订单编号
     * @return
     */
    public Map queryPayStatus(String out_trade_no);
}
