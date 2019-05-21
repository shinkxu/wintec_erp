<%--
  Created by IntelliJ IDEA.
  User: liuyandong
  Date: 2017/5/16
  Time: 11:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link rel="stylesheet" type="text/css" href="../easyui/themes/bootstrap/easyui.css">
    <script type="text/javascript" src="../easyui/jquery.min.js"></script>
    <script type="text/javascript" src="../easyui/jquery.easyui.min.js"></script>
    <style type="text/css">
        #ok-button {
            background: #20a0ff;
            margin-top: 20px;
            width: 80%;
            height: 40px;
            border-radius: 3px;
            font-size: 16px;
            line-height: 1;
            color: #fff;
            border: none;
            cursor: pointer;
            margin-left: auto;
            margin-right: auto;
        }
        #shopId {
            width: 80%;
            height: 40px;
            font-size: 14px;
            border: 1px solid #dfdfdf;
            border-radius: 3px;
        }
    </style>
    <script type="text/javascript">
        function handleOkButtonOnClick() {
            $("#ok-button").attr("disabled", true);
            var shopId = $("#shopId").val();
            if (!shopId) {
                $.messager.alert("提示", "请输入饿了么店铺号！", "warning");
                $("#ok-button").attr("disabled", false);
                return;
            }
            var tenantId = $("#tenantId").val();
            var branchId = $("#branchId").val();
            $.post("../eleme/doBindingRestaurant", {"tenantId": tenantId, "branchId": branchId, "shopId": shopId}, function(data) {
                if (data.isSuccess) {
                    $.messager.alert("提示", data.message, "info");
                    $("#ok-button").attr("disabled", false);
                } else {
                    $.messager.alert("提示", data.error, "error");
                    $("#ok-button").attr("disabled", false);
                }
            }, "json");
        }
    </script>
</head>
<body>
<div class="header" style="background: #20a0ff;padding: 15px 0;color: #fff;text-align: center;">饿了么店铺绑定</div>
<div style="text-align: center;margin-top: 60px;">
    <input type="hidden" name="tenantId" value="${tenantId}" id="tenantId">
    <input type="hidden" name="branchId" value="${branchId}" id="branchId">
    <input type="text" name="shopId" id="shopId" value="" placeholder="请输入饿了么门店ID">
    <input type="button" value="确定" onclick="handleOkButtonOnClick();" id="ok-button">
</div>
</body>
</html>
