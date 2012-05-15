<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<tags:page title="Contacts">
    <jsp:attribute name="head">
        <script type="text/javascript"> 
            $(document).ready(function() {
            	$('#navContact').css('color','#f7a700');
            });
        </script>
    </jsp:attribute>
    <jsp:attribute name="breadcrumbs">
        <a href="<c:url value="/"/>">Home</a>
        &rsaquo; <span class="aCrumb">Contact</span> 
    </jsp:attribute>
    <jsp:body>
		<h1>Contacts</h1>
		<div style="width:80%">
		    <div style="position:relative;float:left;width:59%;">
		        <h2>eResearch Lab, UQ</h2>
		        <p>
		            Jane Hunter<br>
		            <a href="mailto:j.hunter@uq.edu.au">j.hunter@uq.edu.au</a>
		        </p>
		        <p>
		            Wilfred Brimblecombe<br>
		            <a href="mailto:w.brimblecombe@uq.edu.au">w.brimblecombe@uq.edu.au</a>
		        </p>
		        <p>
		            Charles Brooking<br>
		            <a href="mailto:c.brooking@uq.edu.au">c.brooking@uq.edu.au</a>
		        </p>
		        <p>
		            Peggy Newman (former member)<br>
		            <a href="mailto:peggy.newman@uq.edu.au">peggy.newman@uq.edu.au</a>
		        </p>
		        <h2>Environmental Decisions Hub, UQ</h2>
		        <p>
		            Matthew Watts<br>
		            <a href="mailto:m.watts@uq.edu.au">m.watts@uq.edu.au</a>
		        </p>
		    </div>
		    <div style="position:relative;float:right;width:39%;">
		        <h2>Eco-Lab, UQ</h2>
		        <p>
		            Craig Franklin<br>
		            <a href="mailto:c.franklin@uq.edu.au">c.franklin@uq.edu.au</a>
		        </p>
		        <p>
		            Hamish Campbell<br>
		            <a href="mailto:Hamish.Campbell@uq.edu.au">Hamish.Campbell@uq.edu.au</a>
		        </p>
		        <p>
		            Ross Dwyer<br>
		            <a href="mailto:ross.dwyer@uq.edu.au">ross.dwyer@uq.edu.au</a>
		        </p>
		    </div>
		</div>
    </jsp:body>
</tags:page>