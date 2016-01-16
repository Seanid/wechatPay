package com.sean.controler;


import com.sean.util.Log;
import com.sean.util.PropertiesUtil;
import com.sean.util.SignatureUtils;
import com.sean.util.WechatOrderUtils;
import com.swetake.util.Qrcode;
import net.sf.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/wechatPay")
public class WechatPayControler extends BaseControler {

    /**
     * 微信公众号调起
     * @param detail    商品描述
     * @param desc      商品详情
     * @param goodSn    商品编号
     * @param openId    用户openid
     * @param orderSn   订单号
     * @param amount    金额
     * @return          返回包装了调起jssdk所需要的函数
     * @throws Exception
     */
    @RequestMapping("/jsOarder.do")
    @ResponseBody
    public String jsOrder(String detail, String desc, String goodSn,String openId, String orderSn, String amount) throws Exception {
        JSONObject result = WechatOrderUtils.createOrder(detail, desc, "oJ__FjkcUtLkWqEDPdSao3vozewA", "10.0.0.1", goodSn, orderSn, amount, "JSAPI");
        return result.toString();
    }


    /**
     * 获取PC端网页支付二维码
     * @param detail    商品描述
     * @param desc      商品详情
     * @param goodSn    商品编号
     * @param orderSn   订单号
     * @param amount    金额
     * @param response
     * @throws IOException
     */
    @RequestMapping("/nativeOrder.do")
    public  void nativeOrder(String detail, String desc, String goodSn, String orderSn, String amount,HttpServletResponse response) throws IOException {

        //调用框架生成二维码
        Qrcode handler = new Qrcode();
        handler.setQrcodeErrorCorrect('M');
        handler.setQrcodeEncodeMode('B');
        handler.setQrcodeVersion(7);

        JSONObject jsonObject = WechatOrderUtils.createOrder(detail, desc, "", "10.0.0.1", goodSn, orderSn, amount, "NATIVE");

       String tmp= jsonObject.getJSONObject("obj").getString("url");
        byte[] contentBytes = tmp.getBytes("UTF-8");

        BufferedImage bufImg = new BufferedImage(140, 140, BufferedImage.TYPE_INT_RGB);

        Graphics2D gs = bufImg.createGraphics();

        gs.setBackground(Color.WHITE);
        gs.clearRect(0, 0, 140, 140);

        //设定图像颜色：BLACK
        gs.setColor(Color.BLACK);

        //设置偏移量  不设置肯能导致解析出错
        int pixoff = 2;
        //输出内容：二维码
        if(contentBytes.length > 0 && contentBytes.length < 124) {
            boolean[][] codeOut = handler.calQrcode(contentBytes);
            for(int i = 0; i < codeOut.length; i++) {
                for(int j = 0; j < codeOut.length; j++) {
                    if(codeOut[j][i]) {
                        gs.fillRect(j * 3 + pixoff, i * 3 + pixoff,3, 3);
                    }
                }
            }
        } else {
            Log.error("QRCode content bytes length = " + contentBytes.length + " not in [ 0,120 ]. ",null);
        }

        gs.dispose();
        bufImg.flush();

        //生成二维码QRCode图片
        ImageIO.write(bufImg, "jpg", response.getOutputStream());

    }



    /**
     * APP调起微信支付接口
     * @param detail    商品描述
     * @param desc      商品详情
     * @param goodSn    商品编号
     * @param openId    用户openid
     * @param orderSn   订单号
     * @param amount    金额
     * @return          返回包装了调起微信APPSDK所需要的函数
     * @throws Exception
     */
    @RequestMapping("/appOarder.do")
    @ResponseBody
    public String appOrder(String detail, String desc, String goodSn,String openId, String orderSn, String amount) throws Exception {
        JSONObject result = WechatOrderUtils.createOrder(detail, desc, "", "10.0.0.1", goodSn, orderSn, amount, "APP");
        return result.toString();
    }




    /**
     * 微信支付回调函数
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/callback.do", method = RequestMethod.POST)
    public void callBack(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        InputStream is = request.getInputStream();
        HashMap<String, String> map = new HashMap<String, String>();
       Log.info("------------微信回调函数----------------", null);
        // 1、读取传入信息并转换为map
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(is);
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }
        String payType = "";
        String memberId = "";
        Element root = document.getRootElement();
        List<Element> list = root.elements();
        for (Element e : list) {
            if (e.getName().trim().equals("payType")) {
                payType = e.getText().trim();
            } else if (e.getName().trim().equals("memberId")) {
                memberId = e.getText().trim();
            } else {
                map.put(e.getName().trim(), e.getText().trim());
            }
        }
        is.close();
        //System.out.println(map.toString());
        // 2、克隆传入的信息并进行验签
        HashMap<String, String> signMap = (HashMap<String, String>) map.clone();
        signMap.remove("sign");
        Log.info(map.toString(), null);
        String key= PropertiesUtil.getValue("wechat.properties","wx_key");
        String sign = SignatureUtils.signature(signMap,key);
        //System.out.println(sign);
        //System.out.println(map.get("sign"));
        if (!sign.equals(map.get("sign"))) {
            Log.error( "微信支付回调函数：验签错误", null);
            return;
        }
        // 信息处理
        String result_code = map.get("result_code");
        try {

            if ("SUCCESS".equals(result_code)) {
                //由于微信后台会同时回调多次，所以需要做防止重复提交操作的判断
                //此处放防止重复提交操作

            } else if ("FAIL".equals(result_code)) {

            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
           //进行业务逻辑操作

        } catch (Exception e) {
            e.printStackTrace();
            Log.error("回调用户中心错误", e);
        }


        // 返回信息，防止微信重复发送报文
        String result = "<xml>"
                + "<return_code><![CDATA[SUCCESS]]></return_code>"
                + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml>";
        PrintWriter out = new PrintWriter(response.getOutputStream());
        out.print(result);
        out.flush();
        out.close();

    }

}
