<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>${action == 'add' ? 'Ajouter' : 'Modifier'} une Catégorie - Suivi Consommation d'Eau</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">Consommation d'Eau</a>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Tableau de Bord</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/categories">Gérer les Catégories</a></li>
            </ul>
            <span class="navbar-text me-3">Bonjour, <c:out value="${sessionScope.utilisateur.nom}"/> !</span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">Déconnexion</a>
        </div>
    </div>
</nav>

<div class="container">
    <h1 class="mb-4">${action == 'add' ? 'Ajouter une nouvelle' : 'Modifier la'} catégorie</h1>

    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/categories/${action}">
        <c:if test="${action == 'edit'}">
            <input type="hidden" name="id" value="${categorie.id}" />
        </c:if>

        <div class="mb-3">
            <label for="nom" class="form-label">Nom de la catégorie *</label>
            <input type="text" class="form-control" id="nom" name="nom" value="${categorie.nom}" required />
        </div>

        <div class="mb-3">
            <label for="description" class="form-label">Description</label>
            <textarea class="form-control" id="description" name="description" rows="3">${categorie.description}</textarea>
        </div>

        <div class="mt-4">
            <button type="submit" class="btn btn-primary">${action == 'add' ? 'Ajouter' : 'Enregistrer les modifications'}</button>
            <a href="${pageContext.request.contextPath}/categories" class="btn btn-secondary">Annuler</a>
        </div>
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
