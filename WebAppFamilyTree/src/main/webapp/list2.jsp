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
        <th>Partner 1</th>
        <th>Partner 2</th>
        <th>Od</th>
        <th>Do</th>
    </tr>
    </thead>
    <c:forEach items="${marriages}" var="marriage">
        <tr>
            <td><c:out value="${marriage.spouse1.name}"/></td>
            <td><c:out value="${marriage.spouse2.name}"/></td>
            <td><c:out value="${marriage.from}"/></td>
            <td><c:out value="${marriage.to}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/marriages/delete?id=${marriage.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Vymazať"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Pridaj manželstvo</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/marriages/add" method="post">
    <table>
        <tr>
            <th style="text-align: right">Partner 1:</th>
            <td>
                <select name="spouse1">
                    <c:forEach items="${people}" var="person">
                        <option value="${person.id}">${person.name}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <th style="text-align: right">Partner 2:</th>
            <td>
                <select name="spouse2">
                    <c:forEach items="${people}" var="person">
                        <option value="${person.id}">${person.name}</option>
                    </c:forEach>
                </select>
            </td>
        </tr>
        <tr>
            <th style="text-align: right">Od:</th>
            <td><input type="date" name="from" value="<c:out value='${param.from}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Do:</th>
            <td><input type="date" name="to" value="<c:out value='${param.to}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Pridaj" />
</form>

</body>
</html>