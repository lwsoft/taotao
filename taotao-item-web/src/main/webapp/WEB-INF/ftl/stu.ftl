<html>
<head>
	<title>测试页面</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
	学生信息：<br>
	学号：${student.id}<br>
	姓名：${student.name}<br>
	年龄：${student.age}<br>
	住址：${student.address}<br>
	
	学生列表：<br>
	<table border="1">
		<tr>
			<th>序号</th>
			<th>学号</th>
			<th>姓名</th>
			<th>年龄</th>
			<th>地址</th>
		</tr>
		<#list stuList as stu>
		<#if stu_index%2==0>
		   <tr bgcolor="red">
		   <#else>
		   <tr bgcolor="blue">
		</#if>
			    <td>${stu_index+1}</td>
			    <td>${stu.id}</td>
				<td>${stu.name}</td>
				<td>${stu.age}</td>
				<td>${stu.address}</td>
		   </tr>
		</#list>
	</table>
	${date?string("yyyy-MM-dd HH:mm:ss")}
	</br>
	${val!"def"}
	</br>
	<#if val??>
		has value
	<#else>
	 	no value
	</#if>
	</br>
	<#include "hello.ftl">
</body>
</html>