<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${action == 'add' ? 'Ajouter' : 'Modifier'} une Consommation - Suivi Consommation d'Eau</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body>
    <!-- Barre de navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">Consommation d'Eau</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Tableau de Bord</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/categories">Gérer les Catégories</a>
                    </li>
                </ul>
                <span class="navbar-text me-3">
                    Bonjour, <c:out value="${sessionScope.utilisateur.nom}"/> !
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">Déconnexion</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <h1 class="mb-4">${action == 'add' ? 'Ajouter une nouvelle' : 'Modifier la'} consommation</h1>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger" role="alert">
                ${errorMessage}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/consommations/${action}">
            <c:if test="${action == 'edit'}">
                <input type="hidden" name="id" value="${consommation.id}">
            </c:if>

            <div class="row mb-3">
                <div class="col-md-6">
                    <label for="date" class="form-label">Date *</label>
                    <%-- Format date for input type="date" (yyyy-MM-dd) --%>
                    <fmt:formatDate value="${consommation.date != null ? consommation.date : java.time.LocalDate.now()}" pattern="yyyy-MM-dd" var="formattedDate"/>
                    <input type="date" class="form-control" id="date" name="date" value="${formattedDate}" required>
                </div>
                <div class="col-md-6">
                    <label for="quantite" class="form-label">Quantité (en litres) *</label>
                    <input type="number" step="0.01" min="0" class="form-control" id="quantite" name="quantite" value="<fmt:formatNumber value='${consommation.quantite}' type='number' pattern='0.##' groupingUsed='false'/>" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="categorieId" class="form-label">Catégorie</label>
                <select class="form-select" id="categorieId" name="categorieId">
                    <option value="0" ${consommation.categorie == null ? 'selected' : ''}>-- Aucune --</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${consommation.categorie != null && consommation.categorie.id == cat.id ? 'selected' : ''}>
                            <c:out value="${cat.nom}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3"><c:out value="${consommation.description}"/></textarea>
            </div>

            <div class="mt-4">
                <button type="submit" class="btn btn-primary">${action == 'add' ? 'Ajouter' : 'Enregistrer les modifications'}</button>
                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">Annuler</a>
            </div>
        </form>
    </div>

    <!-- Bootstrap JS Bundle (includes Popper) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>

