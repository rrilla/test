<%@page import="java.io.IOException"%>
<%@page import="org.codehaus.jackson.map.JsonMappingException"%>
<%@page import="org.codehaus.jackson.JsonGenerationException"%>
<%@page import="org.codehaus.jackson.map.ObjectMapper"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Map retMap = (Map)request.getAttribute("retMap");

ObjectMapper om = new ObjectMapper();

try {   
    out.print(om.defaultPrettyPrintingWriter().writeValueAsString(retMap));
} catch (JsonGenerationException e) {   
    e.printStackTrace();
} catch (JsonMappingException e) {  
    e.printStackTrace();
} catch (IOException e) {   
    e.printStackTrace();
} catch (Exception e) {   
    e.printStackTrace();
}
%>