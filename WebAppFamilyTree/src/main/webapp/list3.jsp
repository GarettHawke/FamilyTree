<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

    <table>
        <tr>
            <th><a href="${pageContext.request.contextPath}/people">People</a></th>
            <th><a href="${pageContext.request.contextPath}/marriages">Marriages</a></th>
            <th><a href="${pageContext.request.contextPath}/relations">Relations</a></th>
        </tr>
    </table>

<table border="1">
    <thead>
    <tr>
        <th>Rodič</th>
        <th>Dieťa</th>
    </tr>
    </thead>
    <c:forEach items="${relations}" var="relation">
        <c:forEach items="${relation.value}" var="child">
            <tr>
                <td><c:out value="${relation.key.name}"/></td>
                <td><c:out value="${child.name}"/></td>
                <td><form method="post" action="${pageContext.request.contextPath}/relations/delete?parent_id=${relation.key.id}&child_id=${child.id}"
                          style="margin-bottom: 0;"><input type="submit" value="Vymazať"></form></td>
            </tr>
        </c:forEach>
    </c:forEach>
</table>

<h2>Pridaj vzťah</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/relations/add" method="post">
    <table>
        <tr>
            <th style="text-align: right">Rodič:</th>
            <td>
                <select name="parent">
                    <c:forEach items="${people}" var="person">
                        <option value="${person.id}">${person.name}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <th style="text-align: right">Dieťa:</th>
            <td>
                <select name="child">
                    <c:forEach items="${people}" var="person">
                        <option value="${person.id}">${person.name}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
    </table>
    <input type="Submit" value="Pridaj" />
</form>

</body>
</html>