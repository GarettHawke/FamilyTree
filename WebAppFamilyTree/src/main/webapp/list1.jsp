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
        <th>Meno</th>
        <th>Pohlavie</th>
        <th>Dátum narodenia</th>
        <th>Miesto narodenia</th>
        <th>Dátum úmrtia</th>
        <th>Miesto úmrtia</th>
    </tr>
    </thead>
    <c:forEach items="${people}" var="person">
        <tr>
            <td><c:out value="${person.name}"/></td>
            <c:if test="${person.gender eq 'MAN'}"><td>Muž</td></c:if>
            <c:if test="${person.gender eq 'WOMAN'}"><td>Žena</td></c:if>
            <td><c:out value="${person.dateOfBirth}"/></td>
            <td><c:out value="${person.placeOfBirth}"/></td>
            <td><c:out value="${person.dateOfDeath}"/></td>
            <td><c:out value="${person.placeOfDeath}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/people/getUpdate?id=${person.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Upraviť"></form></td>
            <td><form method="post" action="${pageContext.request.contextPath}/people/delete?id=${person.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Vymazať"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Pridaj osobu</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<c:if test="${not empty error}">
    <div>
        <c:out value="${error}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/people/add" method="post">
    <table>
        <tr>
            <th style="text-align: right">Meno:</th>
            <td><input type="text" name="name" value="<c:out value='${param.name}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Pohlavie:</th>
            <td><input type="radio" name="gender" value="MAN" checked>Muž</td>
        </tr>
        <tr>
            <th></th>
            <td><input type="radio" name="gender" value="WOMAN">Žena</td>
        </tr>
        <tr>
            <th style="text-align: right">Dátum narodenia:</th>
            <td><input type="date" name="birthdate" value="<c:out value='${param.birthdate}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Miesto narodenia:</th>
            <td><input type="text" name="birthplace" value="<c:out value='${param.birthplace}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Dátum úmrtia:</th>
            <td><input type="date" name="deathdate" value="<c:out value='${param.deathdate}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Miesto úmrtia:</th>
            <td><input type="text" name="deathplace" value="<c:out value='${param.deathplace}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Pridaj" />
</form>
    
</body>
</html>