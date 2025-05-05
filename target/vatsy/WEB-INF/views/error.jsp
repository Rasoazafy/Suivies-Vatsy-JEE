<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Erreur - Suivi Consommation d'Eau</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <style>
        body {
            padding-top: 5rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="alert alert-danger" role="alert">
            <h4 class="alert-heading">Une erreur est survenue !</h4>
            <p>Désolé, une erreur inattendue s'est produite lors du traitement de votre demande.</p>
            <hr>
            <p class="mb-0">Veuillez réessayer plus tard ou contacter l'administrateur si le problème persiste.</p>
            
            <%-- Optionnel : Afficher les détails de l'erreur pour le débogage (à commenter/supprimer en production) --%>
            <%-- 
            <hr>
            <p><strong>Détails de l'erreur :</strong></p>
            <p>Exception : <%= exception %></p>
            <p>Message : <%= exception.getMessage() %></p>
            <pre>
            <% exception.printStackTrace(new java.io.PrintWriter(out)); %>
            </pre>
             --%>
        </div>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">Retour au tableau de bord</a>
    </div>

    <!-- Bootstrap JS Bundle (includes Popper) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>

