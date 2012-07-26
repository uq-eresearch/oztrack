<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="title">
    <c:choose>
    <c:when test="${user.id != null}">
    Edit User Profile
    </c:when>
    <c:otherwise>
    Register
    </c:otherwise>
    </c:choose>
</c:set>
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
		<c:choose>
		<c:when test="${user.id != null}">
		<c:set var="buttonText" value="Update"/>
		</c:when>
		<c:otherwise>
		<c:set var="buttonText" value="Register"/>
		</c:otherwise>
		</c:choose>

        <c:choose>
            <c:when test="${user.id != null}">
                <c:set var="method" value="PUT"/>
                <c:set var="action" value="/users/${user.id}"/>
            </c:when>
            <c:otherwise>
                <c:set var="method" value="POST"/>
                <c:set var="action" value="/users"/>
            </c:otherwise>
        </c:choose>
		<form:form method="${method}" action="${action}" commandName="user" name="user">
		
			<div class="help">
			<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
			<span><b>Username:</b><br> This will be the name that you log on to OzTrack with.
			</span></a>
			</div>
			
		<div>
		<label for="username">Username:</label>
		<form:input path="username" id="username"/><br>
		<form:errors path="username" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="password">Password:</label>
		<form:password path="password" id="password"/>
		<form:errors path="password" cssClass="formErrors"/>
		</div>
        
            <div class="help">
            <a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
            <span><b>AAF ID:</b><br>
            Providing your Australian Access Federation (AAF) ID
            allows you to login through your home institution as an alternative to using your OzTrack
            username and password.<br/>
            <br/>
            Your AAF ID is made up of your username and the domain name for your
            institution separated by '@'.
            For example, if you are a
            UQ staff member with username 'uqjsmith', your ID is 'uqjsmith@uq.edu.au'.
            </span></a>
            </div>
        
        <div>
        <label for="aafId">AAF ID:</label>
        <form:input path="aafId" id="aafId"/>
        <form:errors path="aafId" cssClass="formErrors"/>
        </div>
		
		<br/>
		
		<div>
		<label for="title">Title:</label>
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
		
		<div>
		<label for="firstname">First Name:</label>
		<form:input path="firstName" id="firstname"/>
		<form:errors path="firstName" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="lastname">Last Name:</label>
		<form:input path="lastName" id="lastname"/>
		<form:errors path="lastName" cssClass="formErrors"/>
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
			<span><b>Description:</b><br> This field is important when project metadata is syndicated to DataSpace and ANDS.
			See examples at http://dataspace.uq.edu.au/agents.
			</span></a>
			</div>
		
		<div>
		<label for="dataSpaceAgentDescription">Description:</label>
		<form:input path="dataSpaceAgentDescription" id="dataSpaceAgentDescription"/>
		<form:errors path="dataSpaceAgentDescription" cssClass="formErrors"/>
		</div>
		
			<div class="help">
			<a class=info href="#"><img src="<c:url value="/images/help.png"/>" border="0">
			<span><b>Organisation:</b><br> Please give the name of the organisation in full. This field is important when project metadata is syndicated to DataSpace and ANDS.
			</span></a>
			</div>
		
		<div>
		<label for="organisation">Organisation:</label>
		<form:input path="organisation" id="organisation"/>
		<form:errors path="organisation" cssClass="formErrors"/>
		</div>
		
		<div>
		<label for="email">Email:</label>
		<form:input path="email" id="email"/>
		<form:errors path="email" cssClass="formErrors"/>
		</div>
		
		<div>
		<label> &nbsp;</label>
		<div class="formButton"><input type="submit" value="<c:out value="${buttonText}"/>"/></div>
		</div>
		
		</form:form>
    </jsp:body>
</tags:page>
