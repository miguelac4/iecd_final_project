<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="tools.User" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<%
/*
String username = request.getParameter("username");
String password = request.getParameter("password");
String nacionalidade = request.getParameter("nacionalidade");
String idade = request.getParameter("idade");

String xml = getServletContext().getRealPath("/xml/lista.xml");
String xsd = getServletContext().getRealPath("/xml/lista.xsd");
String fotos = getServletContext().getRealPath("/fotos/");


boolean jaRegistado = false;
if (session.getAttribute("registoFeito") != null) {
	jaRegistado = (boolean) session.getAttribute("registoFeito");
}

if (!jaRegistado) {
    User utilizador = new User(xml, xsd, fotos);

    boolean estado = User.registarJSP(username, password, nacionalidade, idade);

    if (estado) {
        session.setAttribute("username", username);
	    session.setAttribute("password", password);
	
	    // Set the flag to indicate registration is done
	    session.setAttribute("registoFeito", true);
	
	    System.out.println("Registo completo!");
    } else {
    	out.println("Registo falhado!");
    }
}

getServletContext().getRequestDispatcher("/jogo.jsp").forward(request, response);

*/
%>
</body>
</html>