package com.drunk.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.drunk.entity.HttpClient;
import com.drunk.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    //微信支付接口
    private static final String WEIXIN_PAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //微信查询订单接口
    private static final String WEIXIN_QUERY = "https://api.mch.weixin.qq.com/pay/orderquery";

    //微信关闭支付接口
    private static final String WEIXIN_CLOSE = "https://api.mch.weixin.qq.com/pay/closeorder";

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.partner}")
    private String partner;

    @Value("${weixin.partnerkey}")
    private String partnerkey;

    @Value("${weixin.notifyurl}")
    private String notifyurl;

    /***
     * 关闭支付
     * @param orderId
     * @return
     */
    @Override
    public Map<String, String> closePay(Long orderId) throws Exception {
        //参数设置
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);        //应用ID
        paramMap.put("mch_id",partner);     //商户编号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr()); //随机字符
        paramMap.put("out_trade_no",String.valueOf(orderId));   //商家唯一编号

        //将Map数据转成XML
        String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);

        //创建HttpClient对象
        HttpClient httpClient = new HttpClient(WEIXIN_CLOSE);
        httpClient.setHttps(true);
        httpClient.setXmlParam(xmlParam);
        //提交请求
        httpClient.post();

        //获取返回数据
        String content = httpClient.getContent();

        return WXPayUtil.xmlToMap(content);
    }

    /*****
     * 创建二维码
     * @param out_trade_no : 客户端自定义订单编号
     * @param total_fee    : 交易金额,单位：分
     * @return
     */
    @Override
    public Map createNative(Map<String,String> parameter) {
        try{
            //封装调用微信服务接口请求参数
            Map param = new HashMap();
            param.put("appid",appid);       //应用ID
            param.put("mch_id",partner);    //商户ID号
            param.put("nonce_str", WXPayUtil.generateNonceStr());   //随机数
            param.put("body","用户订单");       //订单描述
            param.put("out_trade_no",parameter.get("out_trade_no"));     //商户订单号
            param.put("total_fee",parameter.get("total_fee"));       //交易金额
            param.put("spbill_create_ip","127.0.0.1");      //终端IP
            param.put("notify_url",notifyurl);      //回调地址
            param.put("trade_type","NATIVE");       //交易类型
            param.put("attach", JSON.toJSONString(parameter));      //消息需要发送的队列名称

            //2、将参数转为xml字符，并携带签名
            String paramXml = WXPayUtil.generateSignedXml(param,partnerkey);

            //3、执行请求微信接口
            HttpClient httpClient = new HttpClient(WEIXIN_PAY);
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            //4、获取参数
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            System.out.println("resultMap:"+resultMap);

            //5、获取部分页面所需参数
            HashMap<String, String> dataMap = new HashMap<>();
            dataMap.put("code_url",resultMap.get("code_url"));
            dataMap.put("out_trade_no",parameter.get("out_trade_no"));
            dataMap.put("total_fee",parameter.get("total_fee"));

            return dataMap;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 查询订单状态
     * @param out_trade_no : 客户端自定义订单编号
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        try{
            //1、封装参数
            Map param = new HashMap();
            param.put("appid",appid);           //应用ID
            param.put("mch_id",partner);        //商户号
            param.put("out_trade_no",out_trade_no);     //商户订单编号
            param.put("nonce_str",WXPayUtil.generateNonceStr());        //随机字符

            //2、将参数转成xml字符，并携带签名
            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);

            //3、发送请求
            HttpClient httpClient = new HttpClient(WEIXIN_QUERY);
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            //4、获取请求结果
            String content = httpClient.getContent();
            return WXPayUtil.xmlToMap(content);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
