<%--
  Created by IntelliJ IDEA.
  User: 刘忠伟
  Date: 2017/2/22
  Time: 9:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>网盘系统</title>
    <%@include file="include/css.jsp"%>
    <link rel="stylesheet" href="/static/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="/static/plugins/uploader/webuploader.css">
    <style>

    </style>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<!-- Site wrapper -->
<div class="wrapper">

    <%@include file="include/header.jsp"%>
    <jsp:include page="include/sidebar.jsp">
        <jsp:param name="menu" value="disk_list"/>
    </jsp:include>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">

            <!-- Default box -->
            <div class="box box-primary box-solid">
                <div class="box-header with-border">
                    <h3 class="box-title">网盘系统</h3>

                </div>
                <div class="box-body">

                    <div id="pick" type="button" class="btn btn-primary">文 件 上 传</div><%--点击文件上传，需要知道这个文件存到当前文件夹里面，需要当前文件夹id--%>
                    <button type="button" id="newFolder" href="javascript:;" class="btn btn-primary">新建文件夹</button>
                    <%--点击应该弹出创建文件夹的名字--%>
                    <%--新建文件夹，需要知道新建文件夹的位置，由跳转传过来的当前文件夹的id当作创建新文件夹的fid--%>
                    <br/><br/>
                    <table  id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
                        <thead>
                        <tr>
                            <th></th>
                            <th>名称</th>
                            <th>大小</th>
                            <th>创建时间</th>
                            
                            <th>#</th>
                        </tr>
                        </thead>
                        <tbody>

                        <c:if test="${empty requestScope.diskList}">
                            <tr>
                                <td colspan="5">暂无数据</td>
                            </tr>
                        </c:if>
                            <c:forEach items="${requestScope.diskList}" var="disk">
                                <tr>
                                    <c:choose>
                                        <c:when test="${disk.type == 'file'}">
                                            <td><i class="fa fa-file"></i></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><i class="fa fa-folder"></i></td>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:choose>
                                        <c:when test="${disk.type == 'file'}">
                                            <td><a href="/download/${disk.id}">${disk.sourceName}</a></td>
                                            <%--文件下载是跳转，响应输出流--%>
                                        </c:when>
                                        <c:otherwise>
                                            <td><a href="/list?fid=${disk.id}">${disk.sourceName}</a></td>
                                        </c:otherwise>
                                    </c:choose>

                                    <td>${disk.size}</td>
                                    <td>${disk.createTime}</td>
                                    
                                    <td><a href="javascript:;" class="del" rel="${disk.id}"><i class="fa fa-trash-o"></i></a></td>
                                    <%--点击删除发送ajax删除，再刷新本页即可--%>
                                </tr>



                            </c:forEach>

                        </tbody>
                    </table>
                </div>
                <!-- /.box-body -->
            </div>
            <!-- /.box -->

        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->

</div>

<%@include file="include/js.jsp"%>
<script src="/static/plugins/uploader/webuploader.min.js"></script>
<script src="/static/plugins/layer/layer.js"></script>
<script>
    $(function () {

        var uploader = WebUploader.create({
            swf:"/static/plugins/uploader/Uploader.swf",
            server:"/uploader",//文件上传地址
            pick:"#pick",
            auto:true,
            fileVal:"file",
            formData:{//点击文件上传，需要知道上传到哪里。所以来到此页面时，把要上传的fid传过来。保存到fid下
                "fid":${fid}
            }

        });
        /*监听uploader的事件*/
        uploader.on("uploadSuccess",function (file,result) {
            if(result.status == "success"){
                layer.msg("文件"+ result.data +"上传成功！");
                //刷新页面
                window.history.go(0);
            } else{
                layer.msg(result.message);
            }
        });
        uploader.on("uploadError",function (file) {
            layer.msg("服务器繁忙，请稍候再试");
        });


        /*新建文件夹。点击时输入新文件夹名字，并且也需要fid*/
        $("#newFolder").click(function () {

            layer.prompt({title: '输入新文件夹名称，并确认'}, function(text, index) {
                layer.close(index);
                //发送ajax请求，新建成功，刷新页面
                $.post("/saveFolder",{"fid":${requestScope.fid},"folderName":text})
                        .done(function (result) {
                            if(result.status == "success"){
                                layer.msg("新文件夹"+ result.data +"，已经创建成功！");
                                window.history.go(0);
                            } else {
                                layer.msg(result.message);
                            }
                }).error(function () {
                    layer.msg("服务器繁忙，请稍候再试！");
                });

            });
        });

        /*文件删除。rel为需要删除的资源id*/
        $(".del").click(function () {//this代表调用函数的对象。点击谁是哪个dom调用
            var id = $(this).attr("rel");
            layer.confirm('你确定要删除吗', function(index){
                //do something
                $.get("/delete",{"id":id}).done(function (result) {
                    if(result.status == "success"){
                        layer.msg("删除成功！");
                        window.history.go(0);
                    } else {
                        layer.msg(result.message);
                    }
                }).error(function () {
                   layer.msg("服务器繁忙，请稍候再试！");
                });
                layer.close(index);
            });


        });

    });
</script>
</body>
</html>
