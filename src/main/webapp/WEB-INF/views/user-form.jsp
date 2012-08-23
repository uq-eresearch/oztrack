<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="aafEnabled"><%= OzTrackApplication.getApplicationContext().isAafEnabled() %></c:set>
<c:set var="title" value="${(user.id != null) ? 'User Profile' : 'Register'}"/>
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
        <form class="form-vertical form-bordered" style="margin: 18px 0; width: 600px;">
            <fieldset>
                <legend>Register using AAF</legend>
                <div class="control-group">
                    <p>
                        Click here to register using your <a href="http://www.aaf.edu.au/">Australian Access Federation (AAF)</a> profile.
                    </p>
                    <p>
                        You will be redirected to your home institution's login page.
                    </p>
                </div>
            </fieldset>
            <div class="form-actions">
                <a class="btn btn-primary" href="<c:url value="/login/shibboleth"/>">Register using AAF</a>
            </div>
        </form>
        </c:if>

        <form:form
            cssClass="form-horizontal form-bordered"
            commandName="user"
            method="${(user.id == null) ? 'POST' : 'PUT'}"
            action="${(user.id == null) ? '/users' : '/users/'}${(user.id == null) ? '' : user.id}"
            style="margin: 18px 0; width: 600px;">
            <fieldset>
                <legend>${(user.id != null) ? 'Update user profile' : 'Register new profile'}</legend>
                <c:if test="${aafEnabled && ((not empty user.aafId) || (user.id != null))}">
                <div class="control-group">
                    <label class="control-label" for="aafId">AAF ID:</label>
                    <div class="controls">
                        <c:choose>
                        <c:when test="${not empty user.aafId}">
                            <form:input path="aafId" id="aafId"/>
                            <form:errors element="div" path="aafId" cssClass="formErrors"/>
                        </c:when>
                        <c:otherwise>
                            <a class="btn" href="<c:url value="/login/shibboleth"/>">Link profile with AAF ID</a>
                        </c:otherwise>
                        </c:choose>
                        <div class="help-inline">
                            <a class=info href="#"><img src="<c:url value="/img/help.png"/>" border="0">
                            <span>
                                <b>AAF ID:</b><br>
                                <br>
                                Providing your Australian Access Federation (AAF) ID allows you to login
                                through your home institution as an alternative to using your OzTrack
                                username and password.<br>
                                <br>
                                Your AAF ID is made up of your username and the domain name for your
                                institution separated by '@'. For example, if you are a UQ staff member
                                with username 'uqjsmith', your ID is 'uqjsmith@uq.edu.au'.
                            </span>
                            </a>
                        </div>
                        <c:if test="${not empty user.aafId}">
                        <form:errors path="aafId" element="div" cssClass="help-block formErrors"/>
                        </c:if>
                    </div>
                </div>
                </c:if>
                <div class="control-group">
                    <label class="control-label" for="username">Username:</label>
                    <div class="controls">
                        <form:input path="username" id="username"/>
                        <div class="help-inline">
                            <a class=info href="#"><img src="<c:url value="/img/help.png"/>" border="0">
                            <span>
                                <b>Username:</b><br>
                                <br>
                                This will be the name that you log on to OzTrack with.
                            </span>
                            </a>
                        </div>
                        <form:errors path="username" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password">Password:</label>
                    <div class="controls">
                        <form:password path="password" id="password"/>
                        <form:errors path="password" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="title">Title:</label>
                    <div class="controls">
                        <form:select path="title">
                            <form:option value="none">--- Select ---</form:option>
                            <form:option value="Dr">Dr</form:option>
                            <form:option value="A/Prof">A/Prof</form:option>
                            <form:option value="Prof">Prof</form:option>
                            <form:option value="Mr">Mr</form:option>
                            <form:option value="Ms">Ms</form:option>
                            <form:option value="none">None</form:option>
                        </form:select>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="firstname">First Name:</label>
                    <div class="controls">
                        <form:input path="firstName" id="firstname"/>
                        <form:errors path="firstName" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="lastname">Last Name:</label>
                    <div class="controls">
                        <form:input path="lastName" id="lastname"/>
                        <form:errors path="lastName" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="organisation">Organisation:</label>
                    <div class="controls">
                        <form:input path="organisation" id="organisation"/>
                        <div class="help-inline">
                            <a class=info href="#"><img src="<c:url value="/img/help.png"/>" border="0">
                            <span>
                                <b>Organisation:</b><br>
                                <br>
                                Please give the name of the organisation in full.
                                This field is important when project metadata is syndicated to DataSpace and ANDS.
                            </span>
                            </a>
                        </div>
                        <form:errors path="organisation" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="email">Email:</label>
                    <div class="controls">
                        <form:input path="email" id="email"/>
                        <form:errors path="email" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="dataSpaceAgentDescription">Description:</label>
                    <div class="controls">
                        <form:textarea path="dataSpaceAgentDescription" id="dataSpaceAgentDescription" cssStyle="width: 320px; height: 60px;"/>
                        <div class="help-inline">
                            <a class=info href="#"><img src="<c:url value="/img/help.png"/>" border="0">
                            <span>
                                <b>Description:</b><br>
                                <br>
                                This field is important when project metadata is syndicated to DataSpace and ANDS.
                                See examples at http://dataspace.uq.edu.au/agents.
                            </span>
                            </a>
                        </div>
                        <form:errors path="dataSpaceAgentDescription" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="${(user.id != null) ? 'Update' : 'Register'}" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>
