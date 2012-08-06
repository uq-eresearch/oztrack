<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="aafEnabled"><%= OzTrackApplication.getApplicationContext().isAafEnabled() %></c:set>
<c:set var="title" value="${(user.id != null) ? 'Edit User Profile' : 'Register'}"/>
<tags:page title="${title}">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
                $('#navHome').addClass('active');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="active">${title}</span>
    </jsp:attribute>
    <jsp:attribute name="sidebar"/>
    <jsp:body>
        <h1>${title}</h1>

        <c:if test="${aafEnabled && (user.id == null) && (empty user.aafId)}">
        <form style="margin: 20px 0; width: 600px;">
        <h2>Register using AAF</h2>
        <div style="margin: 1em 0;">
            Click here to register using your <a href="http://www.aaf.edu.au/">Australian Access Federation (AAF)</a> profile.
        </div>
        <div style="margin: 1em 0;">
            You will be redirected to your home institution's login page.
        </div>
        <div style="margin: 1em 0;">
            <a class="oztrackButton" href="<c:url value="/login/shibboleth"/>">Register using AAF</a>
        </div>
        </form>
        </c:if>

        <form:form
            commandName="user"
            method="${(user.id == null) ? 'POST' : 'PUT'}"
            action="${(user.id == null) ? '/users' : ('/users/' + user.id)}"
            style="margin: 20px 0; width: 600px;">
            
            <c:if test="${aafEnabled && (user.id == null)}">
            <h2>Register new profile</h2>
            </c:if>
            
            <fieldset>
            <legend>Account details</legend>
            <table class="form">
            <col style="width: 120px;" />
            <col style="width: 320px;" />
            <col style="width: 40px;" />
            <c:if test="${aafEnabled && ((not empty user.aafId) || (user.id != null))}">
            <tr>
                <th class="form-label">
                    <label for="aafId">AAF ID:</label>
                </th>
                <td class="form-field">
                    <c:choose>
                    <c:when test="${not empty user.aafId}">
                        <form:input path="aafId" id="aafId" cssStyle="width: 250px;"/>
                        <form:errors path="aafId" cssClass="formErrors"/>
                    </c:when>
                    <c:otherwise>
                        <a class="oztrackButton" style="line-height: 24px;" href="<c:url value="/login/shibboleth"/>">Link profile with AAF ID</a>
                    </c:otherwise>
                    </c:choose>
                </td>
                <td class="form-help">
                    <a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
                    <span>
                        <b>AAF ID:</b><br>
                        <br/>
                        Providing your Australian Access Federation (AAF) ID allows you to login
                        through your home institution as an alternative to using your OzTrack
                        username and password.<br/>
                        <br/>
                        Your AAF ID is made up of your username and the domain name for your
                        institution separated by '@'. For example, if you are a UQ staff member
                        with username 'uqjsmith', your ID is 'uqjsmith@uq.edu.au'.
                    </span>
                    </a>
                </td>
            </tr>
            </c:if>
            <tr>
                <th class="form-label">
                    <label for="username">Username:</label>
                </th>
                <td class="form-field">
                    <form:input path="username" id="username" cssStyle="width: 250px;"/><br>
                    <form:errors path="username" cssClass="formErrors"/>
                </td>
                <td class="form-help">
                    <a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
                    <span>
                        <b>Username:</b><br>
                        This will be the name that you log on to OzTrack with.
                    </span>
                    </a>
                </td>
            </tr>
            <tr>
                <th class="form-label">
                    <label for="password">Password:</label>
                </th>
                <td class="form-field">
                    <form:password path="password" id="password" cssStyle="width: 250px;"/>
                    <form:errors path="password" cssClass="formErrors"/>
                </td>
                <td class="form-help">
                </td>
            </tr>
            </table>
            </fieldset>
            
            <fieldset>
            <legend>Personal details</legend>
            <table class="form">
            <col style="width: 120px;" />
            <col style="width: 320px;" />
            <col style="width: 40px;" />
            <tr>
                <th class="form-label">
                    <label for="title">Title:</label>
                </th>
                <td class="form-field">
                    <form:select path="title">
                        <form:option value="none">--- Select ---</form:option>
                        <form:option value="Dr">Dr</form:option>
                        <form:option value="A/Prof">A/Prof</form:option>
                        <form:option value="Prof">Prof</form:option>
                        <form:option value="Mr">Mr</form:option>
                        <form:option value="Ms">Ms</form:option>
                        <form:option value="none">None</form:option>
                    </form:select>
                </td>
                <td class="form-help">
                </td>
            </tr>
            <tr>
                <th class="form-label">
                    <label for="firstname">First Name:</label>
                </th>
                <td class="form-field">
                    <form:input path="firstName" id="firstname" cssStyle="width: 250px;"/>
                    <form:errors path="firstName" cssClass="formErrors"/>
                </td>
                <td class="form-help">
                </td>
            </tr>

            <tr>
                <th class="form-label">
                    <label for="lastname">Last Name:</label>
                </th>
                <td class="form-field">
                    <form:input path="lastName" id="lastname" cssStyle="width: 250px;"/>
                    <form:errors path="lastName" cssClass="formErrors"/>
                </td>
                <td class="form-help">
                </td>
            </tr>
            <tr>
                <th class="form-label">
                    <label for="organisation">Organisation:</label>
                </th>
                <td class="form-field">
                    <form:input path="organisation" id="organisation" cssStyle="width: 250px;"/>
                    <form:errors path="organisation" cssClass="formErrors"/>
                </td>
                <td class="form-help">
                    <a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
                    <span>
                        <b>Organisation:</b><br>
                        Please give the name of the organisation in full.
                        This field is important when project metadata is syndicated to DataSpace and ANDS.
                    </span>
                    </a>
                </td>
            </tr>
            <tr>
                <th class="form-label">
                    <label for="email">Email:</label>
                </th>
                <td class="form-field">
                    <form:input path="email" id="email" cssStyle="width: 250px;"/>
                    <form:errors path="email" cssClass="formErrors"/>
                </td>
                <td class="form-help">
                </td>
            </tr>
            <tr>
                <th class="form-label">
                    <label for="dataSpaceAgentDescription">Description:</label>
                </th>
                <td class="form-field">
                    <form:textarea path="dataSpaceAgentDescription" id="dataSpaceAgentDescription" cssStyle="width: 320px; height: 60px;"/>
                    <form:errors path="dataSpaceAgentDescription" cssClass="formErrors"/>
                </td>
                <td class="form-help">
                    <a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
                    <span>
                        <b>Description:</b><br>
                        This field is important when project metadata is syndicated to DataSpace and ANDS.
                        See examples at http://dataspace.uq.edu.au/agents.
                    </span>
                    </a>
                </td>
            </tr>
            </table>
            </fieldset>
            <div>
                <input type="submit" value="${(user.id != null) ? 'Update' : 'Register'}" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>