<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gérer les Catégories - Suivi Consommation d'Eau</title>
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
                        <a class="nav-link active" aria-current="page" href="${pageContext.request.contextPath}/categories">Gérer les Catégories</a>
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
        <h1 class="mb-4">Gestion des Catégories</h1>

        <!-- Messages Succès/Erreur -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${successMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${errorMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                Liste des Catégories
                <a href="${pageContext.request.contextPath}/categories/add" class="btn btn-success btn-sm">+ Ajouter une catégorie</a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty categories}">
                        <ul class="list-group">
                            <c:forEach var="cat" items="${categories}">
                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                    <c:out value="${cat.nom}"/>
                                    <div>
                                        <a href="${pageContext.request.contextPath}/categories/edit?id=${cat.id}" class="btn btn-primary btn-sm">Modifier</a>
                                        <button type="button" class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#deleteCategoryModal" data-delete-id="${cat.id}" data-delete-name="<c:out value='${cat.nom}'/>">
                                            Supprimer
                                        </button>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p class="text-center">Aucune catégorie définie pour le moment.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Modale de Confirmation de Suppression Catégorie -->
    <div class="modal fade" id="deleteCategoryModal" tabindex="-1" aria-labelledby="deleteCategoryModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteCategoryModalLabel">Confirmer la suppression</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Êtes-vous sûr de vouloir supprimer la catégorie "<span id="deleteCategoryName"></span>" ?
                    <br><small>(Les consommations associées ne seront plus catégorisées)</small>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                    <form id="deleteCategoryForm" method="post" action="${pageContext.request.contextPath}/categories/delete" style="display: inline;">
                        <input type="hidden" name="id" id="deleteCategoryId">
                        <button type="submit" class="btn btn-danger">Supprimer</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS Bundle (includes Popper) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    <script>
        // Modale de suppression catégorie
        const deleteCategoryModal = document.getElementById('deleteCategoryModal');
        const deleteCategoryIdInput = document.getElementById('deleteCategoryId');
        const deleteCategoryNameSpan = document.getElementById('deleteCategoryName');
        deleteCategoryModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const itemId = button.getAttribute('data-delete-id');
            const itemName = button.getAttribute('data-delete-name');
            deleteCategoryIdInput.value = itemId;
            deleteCategoryNameSpan.textContent = itemName;
        });
    </script>
</body>
</html>

