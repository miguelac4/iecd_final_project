package tools;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegistarPorJSP")
public class RegistarPorJSP extends HttpServlet {

    private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nacionalidade = request.getParameter("nacionalidade");
        String idade = request.getParameter("idade");
        String cor = request.getParameter("cor");

        String xml = getServletContext().getRealPath("/xml/lista.xml");
        String xsd = getServletContext().getRealPath("/xml/lista.xsd");
        String fotos = getServletContext().getRealPath("/fotos/cara.jpg");

        User user = new User(xml, xsd, fotos);
        user.setUsername(username);
        try {
			user.setPassword(password);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
        user.setNacionalidade(nacionalidade);
        user.setIdade(Integer.parseInt(idade));
        user.setCor(cor);
        

        boolean jaRegistado = false;
        if (request.getSession().getAttribute("registoFeito") != null) {
            jaRegistado = (boolean) request.getSession().getAttribute("registoFeito");
        }

        if (!jaRegistado) {
			try {
				boolean estado = User.registarJSP(user);
				
				if (estado) {
	                request.getSession().setAttribute("username", username);
	                request.getSession().setAttribute("password", password);
	                request.getSession().setAttribute("registoFeito", true);
	                System.out.println("Registo completo!");
	                RequestDispatcher rd = request.getRequestDispatcher("jogo.jsp");
	    			rd.forward(request, response);
	            } else {
	                System.out.println("Registo falhado!");
	            }
			} catch (Exception e) {
				e.printStackTrace();
			}

            
        }
    }
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
}