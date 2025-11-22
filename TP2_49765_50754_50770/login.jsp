<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Iniciar sessão</title>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

</head>
<body>


<div style="text-align:center;" class="container mt-5">
    <div class="row d-flex justify-content-center">
        <div class="col-md-6">
            <div class="card px-5 py-5" id="form1">
                <div class="form-data">
                	<h1 class="mb-5">Iniciar sessão</h1> 
                	
                	<form method="POST" action="jogo.jsp">
	                	<label class="forms-inputs mb-4" for  = "username">Utilizador: </label>
				        <input type = "text" name="username" id="username" placeholder="Username" autocomplete="off" required><br>
				        <label class="forms-inputs mb-4" for  = "password">Palavra-passe: </label>
				        <input type = "password" name="password" id="password" placeholder="Palavra-passe" autocomplete="off" required><br>
				        <input type = "submit" value = "Login" class="btn btn-dark w-100 mb-2">
				        <input type = "reset"  value = "Limpar" class="btn btn-dark w-100 mb-2">
					</form>
					
                </div>
            </div>
        </div>
    </div>
</div>    


</body>
</html>