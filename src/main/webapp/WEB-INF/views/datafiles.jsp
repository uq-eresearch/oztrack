<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="dateFormatPattern" value="dd/MM/yyyy"/>
<tags:page title="${project.title}: Data Files">
    <jsp:attribute name="description">
        Upload data files to the ${project.title} project.
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            $(document).ready(function() {
                $('#navTrack').addClass('active');
                $('#projectMenuUploads').addClass('active');
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
    </jsp:attribute>
    <jsp:body>
        <h1 id="projectTitle"><c:out value="${project.title}"/></h1>
        <h2>Data Files</h2>

        <p>
            <c:out value="${fn:length(dataFileList)}"/> data file(s) found.
        </p>
        <p>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/projects/${project.id}/datafiles/new" >Add a data file</a>
            <a class="btn" id="pageRefresh" href="javascript:location.reload(true)">Refresh</a>
        </p>
        <c:if test="${not empty dataFileList}">
            <p><c:out value="${errorStr}"/></p>

            <table id="dataFileStatusTable" class="table table-bordered">
                <col style="width: 200px;" />
                <col style="width: 100px;" />
                <col style="width: 100px;" />
                <thead>
                    <tr>
                        <th>File Name</th>
                        <th>Upload Date</th>
                        <th>File Status</th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach items="${dataFileList}" var="dataFile">
                    <tr>
                        <td><c:out value="${dataFile.userGivenFileName}"/></td>
                        <td><fmt:formatDate pattern="${dateFormatPattern}" value="${dataFile.createDate}"/>
                        <td><a href="${pageContext.request.contextPath}/datafiles/${dataFile.id}"><c:out value="${dataFile.status}"/></a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
    </jsp:body>
</tags:page>
