<%@ tag pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ attribute name="projectUsers" type="java.util.List" required="true" %>
<%@ attribute name="role" type="java.lang.String" required="true" %>
<c:choose>
<c:when test="${empty projectUsers}">
<div style="font-style: italic;">
    No users with ${role} role
</div>
</c:when>
<c:otherwise>
<ul class="users">
    <c:forEach items="${projectUsers}" var="projectUser">
    <li class="${role}">${projectUser.user.firstName}&nbsp;${projectUser.user.lastName} (${projectUser.user.username})</li>
    </c:forEach>
</ul>
</c:otherwise>
</c:choose>