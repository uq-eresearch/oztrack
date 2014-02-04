<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateTimeFormatPattern" value="yyyy-MM-dd HH:mm:ss"/>
<tags:page title="${project.title}: Data Files">
    <jsp:attribute name="description">
        Upload data files to the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#dataActionsViewFiles').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects">Projects</a>
        &rsaquo; <a href="${pageContext.request.contextPath}/projects/${project.id}">${project.title}</a>
        &rsaquo; <span class="active">Data Files</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar">
        <tags:project-menu project="${project}"/>
        <tags:data-actions project="${project}"/>
        <tags:project-licence project="${project}"/>
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <h2>Data Files</h2>

        <p>
            <c:out value="${fn:length(dataFileList)}"/> data file(s) found.
        </p>
        <p>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/projects/${project.id}/datafiles/new" >Upload data file</a>
            <a class="btn" id="pageRefresh" href="javascript:location.reload(true)">Refresh</a>
        </p>
        <c:if test="${not empty dataFileList}">
            <p><c:out value="${errorStr}"/></p>

            <table id="dataFileStatusTable" class="table table-bordered">
                <col style="width: 230px;" />
                <col style="width: 300px;" />
                <col style="width: 100px;" />
                <col style="width: 70px;" />
                <thead>
                    <tr>
                        <th>File Name</th>
                        <th>Uploaded</th>
                        <th>File Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach items="${dataFileList}" var="dataFile">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles/${dataFile.id}"
                                ><c:out value="${not empty dataFile.userGivenFileName ? dataFile.userGivenFileName : dataFile.dataFilePath}"/></a>
                        </td>
                        <td>
                            <fmt:formatDate pattern="${dateTimeFormatPattern}" value="${dataFile.createDate}"/>
                            by <c:out value="${dataFile.createUser.fullName}"/>
                        </td>
                        <td><c:out value="${not empty dataFile.status ? dataFile.status : 'UNKNOWN'}"/></td>
                        <td>
                            <sec:authorize access="hasPermission(#dataFile, 'delete')">
                            <a href="javascript:void(0);" onclick="OzTrack.deleteEntity(<%--
                                --%>'${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles/${dataFile.id}',<%--
                                --%>'${pageContext.request.contextPath}/projects/${dataFile.project.id}/datafiles',<%--
                                --%>'Are you sure you want to delete this data file?'<%--
                            --%>);"><img src="${pageContext.request.contextPath}/img/page_white_delete.png" /></a>
                            </sec:authorize>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </jsp:body>
</tags:page>
