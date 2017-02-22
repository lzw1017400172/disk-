<%--
  Created by IntelliJ IDEA.
  User: 刘忠伟
  Date: 2017/1/12
  Time: 21:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<!-- Left side column. contains the sidebar -->
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">

        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu">

                <li class="header">开发模块</li>
                <li class="treeview ${param.menu == "disk_list" ? "active":""}">
                    <a href="/list">
                        <i class="fa fa-folder"></i>
                        <span>公司网盘</span>
                    </a>
                </li>


        </ul>
    </section>
    <!-- /.sidebar -->
</aside>
