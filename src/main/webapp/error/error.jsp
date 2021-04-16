<%@ page isErrorPage="true"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.io.*"%>
<%@page import="org.codehaus.jackson.map.JsonMappingException"%>
<%@page import="org.codehaus.jackson.JsonGenerationException"%>
<%@page import="org.codehaus.jackson.map.ObjectMapper"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
response.setStatus(HttpServletResponse.SC_OK);
Map<String, String> retMap = new HashMap<String, String>();
retMap.put("CODE", "444");
retMap.put("MSG", "ERROR");
ObjectMapper om = new ObjectMapper();

try {   
    out.print(om.defaultPrettyPrintingWriter().writeValueAsString(retMap));
} catch (JsonGenerationException e) {   
    e.printStackTrace();
} catch (JsonMappingException e) {  
    e.printStackTrace();
} catch (IOException e) {   
    e.printStackTrace();
}
%>