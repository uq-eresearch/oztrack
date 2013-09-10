<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Spatial Reference Systems">
    <jsp:attribute name="description">
        Update spatial reference systems in OzTrack.
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/settings">Settings</a>
        &rsaquo; <span class="active">Spatial Reference Systems</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <div class="sidebar-actions">
            <div class="sidebar-actions-title">Manage SRSs</div>
            <ul class="icons sidebar-actions-list">
                <li class="create"><a href="${pageContext.request.contextPath}/settings/srs/new">Create new SRS</a></li>
            </ul>
        </div>
    </jsp:attribute>
    <jsp:body>
        <h1>Spatial Reference Systems</h1>
        <table class="table table-bordered">
            <col style="width: 120px;" />
            <col style="width: 260px;" />
            <col style="width: 180px;" />
            <col style="width: 80px;" />
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Title</th>
                    <th>Bounds</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${srsList}" var="srs">
                <tr>
                    <td><c:out value="${srs.identifier}"/></td>
                    <td><c:out value="${srs.title}"/></td>
                    <td>
                        ${srs.bounds.envelopeInternal.minX},
                        ${srs.bounds.envelopeInternal.minY},
                        ${srs.bounds.envelopeInternal.maxX},
                        ${srs.bounds.envelopeInternal.maxY}
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/settings/srs/${srs.id}/edit"><img src="${pageContext.request.contextPath}/img/page_white_edit.png" /></a>
                        <a href="javascript:void(OzTrack.deleteEntity(
                            '${pageContext.request.contextPath}/settings/srs/${srs.id}',
                            '${pageContext.request.contextPath}/settings/srs', 'Are you sure you want to delete this spatial reference system?'
                            ));"><img src="${pageContext.request.contextPath}/img/page_white_delete.png" /></a>
                    </td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </jsp:body>
</tags:page>
