package com.sean.util;

import com.sean.beans.WechatOrder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by seanid on 2016/1/15.
 */
public class WechatOrderUtils {

    @Resource(name = "propertiesUtil")
    private PropertiesUtil propertiesUtil;

    public JSONObject createOrder(JSONObject json) {

        JSONObject result = new JSONObject();
        String detail = json.getString("detail");// 商品详情
        String desc = json.getString("desc");// 商品描述
        String openid = json.getString("openid");//支付方openID
        String ip = json.getString("ip");// 对方ip地址
        String goodSn = json.getString("goodSn");// 商品SN
        String orderSn = json.getString("orderSn");// 订单SN
        String amount = json.getString("amount");// 支付金额
        String type = json.getString("type");// 支付类型,JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付


        // 2、参数校验
        if (StringUtils.isBlank(detail) || StringUtils.isBlank(desc) || StringUtils.isBlank(ip)
                || StringUtils.isBlank(goodSn) || StringUtils.isBlank(orderSn) || StringUtils.isBlank(amount)
                || StringUtils.isBlank(type)) {
            Log.error("微信支付统一下单请求错误：请求参数不足", null);
            result.put("status", "error");
            result.put("msg", "请求参数不足");
            result.put("obj", null);
            return result;
        }

        double relAmount = 0;// 对应微信支付的真实数目
        try {//进行格式转换异常获取，保证数目正确
            relAmount = Double.parseDouble(amount) * 100;
        } catch (Exception e) {
            Log.error("微信支付统一下单请求错误：请求金额格式错误", e);
            result.put("status", "error");
            result.put("msg", "请求金额格式错误");
            result.put("obj", null);
            return result;
        }

        if (relAmount == 0) {//微信支付的支付金额必须为大于0的int类型，单位为分
            Log.error("微信支付统一下单请求错误：请求金额不能为0", null);
            result.put("status", "error");
            result.put("msg", "请求金额不能为0");
            result.put("obj", null);
            return result;
        }

        if (!("JSAPI".equalsIgnoreCase(type) || "NATIVE".equalsIgnoreCase(type) || "APP".equalsIgnoreCase(type))) {
            Log.error("微信支付统一下单请求错误：支付类型为空", null);
            result.put("status", "error");
            result.put("msg", "支付类型为空");
            result.put("obj", null);
            return result;
        }

       /* 公众号调起微信支付的时候，必须要有openID*/
        if ("JSAPI".equalsIgnoreCase(type) && StringUtils.isBlank(openid)) {
            Log.error("微信支付统一下单请求错误：请求参数不足", null);
            result.put("status", "error");
            result.put("msg", "请求参数不足");
            result.put("obj", null);
            return result;
        }


        // 3、获取系统配置信息

        String wx_order = propertiesUtil.getValueByKey("wx_order");//获取统一下单接口地址
        String mchappid = propertiesUtil.getValueByKey("mchappid");// 商户appid
        String mchid = propertiesUtil.getValueByKey("mchid");// 商户ID
        String wx_callback = propertiesUtil.getValueByKey("wx_callback");// 获取微信支付回调接口
        String wx_key = propertiesUtil.getValueByKey("wx_key");//微信商户后台设置的key

        if (StringUtils.isBlank(wx_order) || StringUtils.isBlank(mchappid)
                || StringUtils.isBlank(mchid) || StringUtils.isBlank(wx_callback)) {
            Log.error("微信支付统一下单请求错误：系统配置信息缺失", null);
            result.put("status", "error");
            result.put("msg", "系统配置信息缺失");
            result.put("obj", null);
            return result;
        }


        // 4、发送报文模板,其中部分字段是可选字段
        String xml = "" +
                "<xml>" +
                "<appid>APPID</appid>" +//公众号ID
                "<device_info>WEB</device_info>" +//设备信息
                "<detail>DETAIL</detail>" +//商品详情
                "<body>BODY</body>" +//商品描述
                "<mch_id>MERCHANT</mch_id>" +//微信给的商户ID
                "<nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>" +//32位随机字符串,不改
                "<notify_url><![CDATA[URL_TO]]></notify_url>" +//信息通知页面
                "<openid>UserFrom</openid>" +//支付的用户ID
                "<fee_type>CNY</fee_type>" +//支付货币，不改
                "<spbill_create_ip>IP</spbill_create_ip>" +//用户IP
                "<time_start>START</time_start>" +//订单开始时间
                "<time_expire>STOP</time_expire>" +//订单结束时间
                "<goods_tag>WXG</goods_tag>" +//商品标记，不改
                "<product_id>GOODID</product_id>" +//商品ID
                "<limit_pay>no_credit</limit_pay>" +//支付范围，默认不支持信用卡支付，不改
                "<out_trade_no>PAY_NO</out_trade_no>" +//商城生成的订单号
                "<total_fee>TOTAL</total_fee>" +//总金额
                "<trade_type>TYPE</trade_type>" +//交易类型，JSAPI，NATIVE，APP，WAP
                "<sign>SIGN</sign>" +//加密字符串
                "</xml>";

        //生成订单起始时间，订单7天内有效
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        String start_time = df.format(new Date());
        String stop_time = df.format(new Date().getTime() + 7 * 24 * 60 * 60 * 1000);

        xml = xml.replace("APPID", mchappid);
        xml = xml.replace("DETAIL", detail);
        xml = xml.replace("BODY", desc);
        xml = xml.replace("MERCHANT", mchid);
        xml = xml.replace("URL_TO", wx_callback);

        //非网页调起，不需要openID
        if ("NATIVE".equalsIgnoreCase(type) || "APP".equalsIgnoreCase(type)) {
            xml = xml.replace("<openid>UserFrom</openid>", openid);
        } else {
            xml = xml.replace("UserFrom", openid);
        }


        xml = xml.replace("IP", ip);
        xml = xml.replace("START", start_time);
        xml = xml.replace("STOP", stop_time);
        xml = xml.replace("GOODID", goodSn);
        xml = xml.replace("PAY_NO", orderSn);
        xml = xml.replace("TOTAL", (int) relAmount + "");
        xml = xml.replace("TYPE", type);

        // 5、加密

        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", mchappid);
        map.put("device_info", "WEB");
        map.put("detail", detail);
        map.put("body", desc);
        map.put("mch_id", mchid);
        map.put("nonce_str", "1add1a30ac87aa2db72f57a2375d8fec");
        map.put("notify_url", wx_callback);
        map.put("fee_type", "CNY");
        map.put("spbill_create_ip", ip);
        map.put("time_start", start_time);
        map.put("time_expire", stop_time);
        map.put("goods_tag", "WXG");
        map.put("product_id", goodSn);
        map.put("limit_pay", "no_credit");
        map.put("out_trade_no", orderSn);
        map.put("total_fee", (int) relAmount + "");
        map.put("trade_type", type);
        if (!("NATIVE".equalsIgnoreCase(type))) {
            map.put("openid", openid);
        }


        String sign = SignatureUtils.signature(map, wx_key);
        xml = xml.replace("SIGN", sign);

        // 6、请求
        String response = "";
        try {
            response = HttpUtils.post(wx_order, xml);
        } catch (Exception e) {
            Log.error("微信支付统一下单失败:http请求失败", e);
        }
        System.out.println(response);
        /*处理请求结果*/
        XStream s = new XStream(new DomDriver());
        s.alias("xml", WechatOrder.class);
        WechatOrder order = (WechatOrder) s.fromXML(response);

        if ("SUCCESS".equals(order.getReturn_code()) && "SUCCESS".equals(order.getResult_code())) {
            Log.error("微信支付统一下单请求成功：" + order.getPrepay_id(), null);
        } else {
            Log.error("微信支付统一下单请求错误：" + order.getReturn_msg(), null);
        }

        HashMap<String, String> back = new HashMap<String, String>();

        //生成客户端调时需要的信息对象
        if ("JSAPI".equalsIgnoreCase(type)) {
            //网页调起的时候
            String time = Long.toString(System.currentTimeMillis());
            back.put("appId", mchappid);
            back.put("timeStamp", time);
            back.put("nonceStr", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
            back.put("package", "prepay_id=" + order.getPrepay_id());
            back.put("signType", "MD5");
            String sign2 = SignatureUtils.signature(back, wx_key);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", mchappid);
            jsonObject.put("timeStamp", time);
            jsonObject.put("nonceStr", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
            jsonObject.put("package", "prepay_id=" + order.getPrepay_id());
            jsonObject.put("signType", "MD5");
            jsonObject.put("paySign", sign2);

        } else if ("NATIVE".equalsIgnoreCase(type)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", order.getCode_url());
        } else if ("APP".equalsIgnoreCase(type)) {


            //APP调起的时候
            String time = Long.toString(System.currentTimeMillis());
            back.put("appid", mchappid);
            back.put("timestamp", time);
            back.put("partnerid", mchid);
            back.put("noncestr", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
            back.put("prepayid", order.getPrepay_id());
            back.put("package", "Sign=WXPay");
            String sign2 = SignatureUtils.signature(back, wx_key);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", mchappid);
            jsonObject.put("timeStamp", time);
            jsonObject.put("partnerId", mchid);
            jsonObject.put("nonceStr", "5K8264ILTKCH16CQ2502SI8ZNMTM67VS");
            jsonObject.put("prepayId", order.getPrepay_id());
            jsonObject.put("packageValue", "Sign=WXPay");
            jsonObject.put("sign", sign2);


        }
        return result;
    }


}
