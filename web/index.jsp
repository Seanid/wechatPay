<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'index.jsp' starting page</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
<script src="js/jquery-1.11.3.min.js"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script>
	$(function() {
		$.ajax({
			type : "POST",
			url : "jsTickets.do",
			data:{'url':window.location.href},
			dataType : "JSON",
			success : function(data) {
				console.log(data);
				wx.config({
					debug : true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
					appId : data.appId, // 必填，公众号的唯一标识
					timestamp : data.timestamp, // 必填，生成签名的时间戳
					nonceStr : data.nonceStr, // 必填，生成签名的随机串
					signature : data.signature, // 必填，签名，见附录1
					jsApiList : [ 'scanQRCode' ]
				// 必填，需要使用的JS接口列表，所有JS接口列表见附录2
				});
				wx.ready(function(){
					wx.scanQRCode({
					    needResult: 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
					    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
					    success: function (res) {
					    var result = res.resultStr; // 当needResult 为 1 时，扫码返回的结果
					    alert(result);
						}
					});
				});
			}
		});
	});
</script>
</head>

<body>
	This is my JSP page.
	<br>
</body>
</html>
