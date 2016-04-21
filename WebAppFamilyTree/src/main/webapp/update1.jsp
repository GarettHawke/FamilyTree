<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<h2>Uprav osobu</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/people/update?id=${person.id}" method="post">
    <table>
        <tr>
            <th style="text-align: right">Meno:</th>
            <td><input type="text" name="name" value="<c:out value='${person.name}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Pohlavie:</th>
            <td><input type="radio" name="gender" value="MAN" <c:if test="${person.gender eq 'MAN'}">checked</c:if>>Muž</td>
        </tr>
        <tr>
            <th></th>
            <td><input type="radio" name="gender" value="WOMAN" <c:if test="${person.gender eq 'WOMAN'}">checked</c:if>>Žena</td>
        </tr>
        <tr>
            <th style="text-align: right">Dátum narodenia:</th>
            <td><input type="date" name="birthdate" value="<c:out value='${person.dateOfBirth}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Miesto narodenia:</th>
            <td><input type="text" name="birthplace" value="<c:out value='${person.placeOfBirth}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Dátum úmrtia:</th>
            <td><input type="date" name="deathdate" value="<c:out value='${person.dateOfDeath}'/>"/></td>
        </tr>
        <tr>
            <th style="text-align: right">Miesto úmrtia:</th>
            <td><input type="text" name="deathplace" value="<c:out value='${person.placeOfDeath}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Uprav" />
</form>

<a href="${pageContext.request.contextPath}/people">Back</a>

</body>
</html>
