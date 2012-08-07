<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Spatial Reference Systems">
    <jsp:attribute name="head">
        <style type="text/css">
            #content.narrow .dataTable {
                width: 100%;
            }
        </style>
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navSettings').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        <a href="<c:url value="/settings"/>">Settings</a>
        &rsaquo; <span class="active">Spatial Reference Systems</span> 
    </jsp:attribute>
    <jsp:body>
        <h1>Spatial Reference Systems</h1>
        <table class="dataTable">
        <col style="width: 120px;" />
        <col style="width: 260px;" />
        <col style="width: 180px;" />
        <col style="width: 80px;" />
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Bounds</th>
            <th>Actions</th>
        </tr>
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
                <a href="<c:url value="/settings/srs/${srs.id}/edit"/>"><img src="<c:url value="/images/page_white_edit.png"/>" /></a>
                <a href="javascript:void(deleteEntity(
                    '<c:url value="/settings/srs/${srs.id}"/>',
                    '<c:url value="/settings/srs"/>', 'Are you sure you want to delete this spatial reference system?'
                    ));"><img src="<c:url value="/images/page_white_delete.png"/>" /></a>
            </td>
        </tr>
        </c:forEach>
        </table>
        <div class="actions">
        <h2>Manage Spatial Reference Systems</h2>
        <ul>
            <li class="create"><a href="<c:url value="/settings/srs/new"/>">Create new SRS</a></li>
        </ul>
        </div>
    </jsp:body>
</tags:page>