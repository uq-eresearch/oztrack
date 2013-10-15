<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ page import="org.oztrack.app.OzTrackApplication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:set var="aafEnabled"><%= OzTrackApplication.getApplicationContext().isAafEnabled() %></c:set>
<c:set var="dataSpaceEnabled"><%= OzTrackApplication.getApplicationContext().isDataSpaceEnabled() %></c:set>
<c:set var="title" value="${(user.id != null) ? 'User Profile' : 'Register'}"/>
<tags:page title="${title}">
    <jsp:attribute name="description">
        <c:choose>
        <c:when test="${user.id != null}">
            Update your OzTrack account.
        </c:when>
        <c:otherwise>
            Register a new OzTrack account.
        </c:otherwise>
        </c:choose>
    </jsp:attribute>
    <jsp:attribute name="tail">
        <script type="text/javascript">
            function addInstitution(institution) {
                $('#old-institutions').show();
                $('#old-institutions').append($('<li class="institution old-institution">')
                    .append($('<input type="hidden" name="institutions" class="input-xlarge">').val(institution.id))
                    .append($('<input type="text" class="input-xlarge" readonly="readonly">').val(institution.title))
                    .append(' ')
                    .append($('<div class="btn-group">')
                        .append($('<a class="btn" href="#up">')
                            .append('<i class="icon-arrow-up"></i>')
                            .click(function(e) {
                                e.preventDefault();
                                var prev = $(this).closest('.institution').prev();
                                if (prev.size() != 0) {
                                    $(this).closest('.institution').fadeOut({complete: function() {
                                        $(this).insertBefore(prev).fadeIn();
                                    }});
                                }
                            })
                        )
                        .append($('<a class="btn" href="#down">')
                            .append('<i class="icon-arrow-down"></i>')
                            .click(function(e) {
                                e.preventDefault();
                                var next = $(this).closest('.institution').next();
                                if (next.size() != 0) {
                                    $(this).closest('.institution').fadeOut({complete: function() {
                                        $(this).insertAfter(next).fadeIn();
                                    }});
                                }
                            })
                        )
                        .append($('<a class="btn" href="#remove">')
                            .append('<i class="icon-trash"></i>')
                            .click(function(e) {
                                e.preventDefault();
                                $(this).closest('.old-institution').fadeOut({
                                    complete: function() {
                                        $(this).remove();
                                        $('#old-institutions:not(:has(.old-institution))').slideUp();
                                    }
                                });
                            })
                        )
                    )
                );
            }
            $(document).ready(function() {
                $('#navHome').addClass('active');
                <c:forEach var="institution" items="${user.institutions}">
                addInstitution({id: '${institution.id}', title: '${institution.title}'});
                </c:forEach>
                $('#new-institution-toggle').click(function(e) {
                    e.preventDefault();
                    $('#new-institution-form').fadeToggle();
                });
                $('#add-affiliation-btn').click(function(e) {
                    e.preventDefault();
                    var institutionId = $('#new-affiliation').val();
                    if (institutionId !== '') {
                        var institutionTitle = $('#new-affiliation :selected').text()
                        addInstitution({id: institutionId, title: institutionTitle});
                    }
                });
                $('#new-institution-btn').click(function(e) {
                    e.preventDefault();
                    $.ajax({
                        url: '${pageContext.request.contextPath}/institutions',
                        type: 'POST',
                        data: $.param({
                            title: $('#new-institution-title').val(),
                            domainName: $('#new-institution-domainName').val(),
                            country: $('#new-institution-country').val()
                        }),
                        success: function(affiliation, textStatus, jqXHR) {
                            addInstitution(affiliation);
                            $('#new-affiliation').append($('<option>').attr('value', affiliation.id).text(affiliation.title));
                            $('#new-institution-form :input').val('');
                            $('#new-institution-form').fadeOut();
                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            alert(errorThrown);
                        }
                    });
                });
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="${pageContext.request.contextPath}/">Home</a>
        &rsaquo; <span class="active">${title}</span>
    </jsp:attribute>
    <jsp:body>
        <h1>${title}</h1>

        <c:if test="${aafEnabled && (user.id == null) && (empty user.aafId)}">
        <form class="form-vertical form-bordered" style="margin: 18px 0;">
            <fieldset>
                <div class="legend">Register using AAF</div>
                <div style="margin: 18px 0;">
                    <p>
                        Click here to register using your <a target="_blank" href="http://www.aaf.edu.au/">Australian Access Federation (AAF)</a> profile.
                    </p>
                    <p>
                        You will be redirected to your home institution's login page.
                    </p>
                </div>
            </fieldset>
            <div class="form-actions">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/login/shibboleth">Register using AAF</a>
            </div>
        </form>
        </c:if>

        <form:form
            cssClass="form-horizontal form-bordered"
            commandName="user"
            method="${(user.id == null) ? 'POST' : 'PUT'}"
            action="${(user.id == null) ? '/users' : '/users/'}${(user.id == null) ? '' : user.id}"
            style="margin: 18px 0;">
            <c:if test="${not empty param.person}">
            <input name="person" type="hidden" value="${param.person}" />
            </c:if>
            <fieldset>
                <div class="legend">${(user.id != null) ? 'Update user profile' : 'Register new profile'}</div>
                <c:if test="${aafEnabled && ((not empty user.aafId) || (user.id != null))}">
                <div class="control-group">
                    <label class="control-label" for="aafId">AAF ID:</label>
                    <div class="controls">
                        <c:choose>
                        <c:when test="${not empty user.aafId}">
                            <input type="text" name="aafId" id="aafId" readonly="readonly" value="${user.aafId}" />
                            <a class="btn" href="javascript:void($('#aafId').val(''));">Remove AAF ID</a>
                        </c:when>
                        <c:otherwise>
                            <a class="btn" href="${pageContext.request.contextPath}/login/shibboleth">Link profile with AAF ID</a>
                        </c:otherwise>
                        </c:choose>
                        <div class="help-inline">
                            <div class="help-popover" title="AAF ID">
                                Providing your Australian Access Federation (AAF) ID allows you to login
                                through your home institution as an alternative to using your OzTrack
                                username and password.<br>
                                <br>
                                Your AAF ID is made up of your username and the domain name for your
                                institution separated by '@'. For example, if you are a UQ staff member
                                with username 'uqjsmith', your ID is 'uqjsmith@uq.edu.au'.
                            </div>
                        </div>
                        <form:errors path="aafId" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                </c:if>
                <div class="control-group required">
                    <label class="control-label" for="username">Username:</label>
                    <div class="controls">
                        <form:input path="username" id="username"/>
                        <div class="help-inline">
                            <div class="help-popover" title="Username">
                                This will be the name that you log on to OzTrack with.
                            </div>
                        </div>
                        <form:errors path="username" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password">Password:</label>
                    <div class="controls">
                        <input type="password" name="password" id="password"/>
                        <div class="help-inline">
                            <div class="help-popover" title="Password">
                                You must enter a password unless you're linking your profile with an AAF ID.
                            </div>
                        </div>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="password">Password (confirm):</label>
                    <div class="controls">
                        <input type="password" name="password2" id="password2"/>
                        <form:errors path="password" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="title">Title:</label>
                    <div class="controls">
                        <form:input path="title" id="title" />
                        <form:errors path="title" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group required">
                    <label class="control-label" for="firstname">First Name:</label>
                    <div class="controls">
                        <form:input path="firstName" id="firstname"/>
                        <form:errors path="firstName" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group required">
                    <label class="control-label" for="lastname">Last Name:</label>
                    <div class="controls">
                        <form:input path="lastName" id="lastname"/>
                        <form:errors path="lastName" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="new-affiliation">Affiliations:</label>
                    <div class="controls">
                        <ul id="old-institutions" class="icons icons-input" style="margin-bottom: 0px; display: none;">
                        </ul>
                        <ul class="icons icons-input">
                            <li class="create-institution">
                                <select id="new-affiliation" style="width: 284px;">
                                    <option value="">Select institution</option>
                                    <c:forEach var="institution" items="${institutions}">
                                    <option value="${institution.id}">${institution.title}</option>
                                    </c:forEach>
                                </select>
                                <button id="add-affiliation-btn" class="btn">Add affiliation</button>
                            </li>
                        </ul>
                        <div style="margin-top: 18px;">
                            <a id="new-institution-toggle" class="btn" href="#new-institution-form">Can't find your institution?</a>
                        </div>
                        <div id="new-institution-form" style="margin-top: 18px; display: none;">
                            <div style="display: inline-block; padding: 12px; border: 1px solid #ccc; border-radius: 4px; background-color: #F0F0E2; box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.08);">
                                <div style="margin-bottom: 5px;">
                                    <label for="new-institution-title" style="display: inline-block; width: 90px;">Title<i class="required-marker">*</i></label>
                                    <input type="text" id="new-institution-title" class="input-xlarge" placeholder="e.g. The University of Queensland">
                                </div>
                                <div style="margin-bottom: 5px;">
                                    <label for="new-institution-domainName" style="display: inline-block; width: 90px;">Domain name</label>
                                    <input type="text" id="new-institution-domainName" class="input-xlarge" placeholder="e.g. uq.edu.au">
                                </div>
                                <div style="margin-bottom: 5px;">
                                    <label for="new-institution-country" style="display: inline-block; width: 90px;">Country</label>
                                    <select id="new-institution-country" style="width: 284px;">
                                        <option value="">Select country</option>
                                        <c:forEach var="country" items="${countries}">
                                        <option value="${country.id}">${country.title}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div>
                                    <label for="new-institution-btn" style="display: inline-block; width: 90px;"></label>
                                    <button id="new-institution-btn" class="btn">Add institution</button>
                                </div>
                            </div>
                        </div>
                        <form:errors path="institutions" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="email">Nationality:</label>
                    <div class="controls">
                        <form:select path="country">
                            <form:option value="">Select country</form:option>
                            <form:options items="${countries}" itemValue="id" itemLabel="title"/>
                        </form:select>
                        <div class="help-inline">
                            <div class="help-popover" title="Nationality">
                                <p>
                                    This field is optional. If provided, it will be used to show statistics for the nationality
                                    of contributors to OzTrack and also to allow the metadata for projects to be harvested
                                    for a specific nation. For example, the Australian National Data Service's
                                    <a href="http://researchdata.ands.org.au/">Research Data Australia</a> repository
                                    harvests records produced by Australian researchers.
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="control-group required">
                    <label class="control-label" for="email">Email:</label>
                    <div class="controls">
                        <form:input path="email" id="email"/>
                        <form:errors path="email" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="description">Short Bio:</label>
                    <div class="controls">
                        <form:textarea path="description" id="description" cssStyle="width: 400px; height: 100px;"/>
                        <div class="help-inline">
                            <div class="help-popover" title="Short Bio">
                                <p>Briefly describe yourself and your research interests.</p>
                                <c:if test="${dataSpaceEnabled}">
                                <p>This field is used when project metadata are syndicated to UQ DataSpace and ANDS.
                                See examples at <a target="_blank" href="http://dataspace.uq.edu.au/agents">http://dataspace.uq.edu.au/agents</a>.</p>
                                </c:if>
                            </div>
                        </div>
                        <form:errors path="description" element="div" cssClass="help-block formErrors"/>
                    </div>
                </div>
                <c:if test="${not empty recaptchaHtml}">
                <div class="control-group required">
                    <label class="control-label" for="recaptcha_response_field">Verification:</label>
                    <div class="controls">
                        ${recaptchaHtml}
                        <c:if test="${not empty recaptchaError}">
                        <div class="help-block formErrors">
                            ${recaptchaError}
                        </div>
                        </c:if>
                    </div>
                </div>
                </c:if>
            </fieldset>
            <div class="form-actions">
                <input class="btn btn-primary" type="submit" value="${(user.id != null) ? 'Update' : 'Register'}" />
            </div>
        </form:form>
    </jsp:body>
</tags:page>
