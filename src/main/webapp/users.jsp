<%@ include file="header.jsp" %>

<h1>Users</h1>

<table class="dataTable">
    <tr>
        <th>Name</th>
        <th>Username</th>
        <th>Email</th>
    </tr>
    <c:forEach items="${userList}" var="user">
        <tr>
            <td><c:out value="${user.firstName}"/></td>
            <td><c:out value="${user.username}"/></td>
            <td><c:out value="${user.email}"/></td>
        </tr>
    </c:forEach>
</table>

<%@ include file="footer.jsp" %>
