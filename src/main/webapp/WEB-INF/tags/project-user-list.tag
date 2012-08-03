<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="projectUsers" type="java.util.List" required="true" %>
<%@ attribute name="role" type="java.lang.String" required="true" %>
<%@ attribute name="showDeleteLinks" type="java.lang.Boolean" required="true" %>
<div id="${role}ProjectUsersNone" style="font-style: italic; margin: 0.3em 0; ${not empty projectUsers ? 'display: none;' : ''}">
    No users with ${role} role
</div>
<ul id="${role}ProjectUsersList" class="users" style="${empty projectUsers ? 'display: none;' : ''}">
    <c:forEach items="${projectUsers}" var="projectUser">
    <c:set var="userFullName">${projectUser.user.firstName}&nbsp;${projectUser.user.lastName}</c:set>
    <li id="${role}ProjectUser-${projectUser.user.id}" class="${role}">
        <span title="${projectUser.user.username}">${userFullName}</span>
        <c:if test="${showDeleteLinks}">
        <a href="javascript:void(0)" onclick="deleteProjectUser(${projectUser.user.id}, '${role}', '${userFullName}');"
            ><img src="<c:url value="/images/bullet_delete.png"/>" /></a>
        </c:if>
    </li>
    </c:forEach>
</ul>