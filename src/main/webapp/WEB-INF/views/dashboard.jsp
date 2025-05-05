<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Araka vatsy</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.2/dist/chart.umd.min.js"></script>
    <style>
        .alert-warning {
            background-color: #fff3cd;
            border-color: #ffeeba;
            color: #856404;
        }
        .table th, .table td {
            vertical-align: middle;
        }
    </style>
</head>
<body>
    <!-- Barre de navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
        <div class="container-fluid">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/dashboard">Suivies de Vatsy</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link active" aria-current="page" href="${pageContext.request.contextPath}/dashboard">Tableau de Bord</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/categories">Gérer les Catégories</a>
                    </li>
                </ul>
                <span class="navbar-text me-3">
                    Bonjour, <c:out value="${utilisateur.nom}"/> !
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light">Déconnexion</a>
            </div>
        </div>
    </nav>

    <div class="container">

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

        <h1 class="mb-4">Tableau de Bord</h1>

        <!-- Indicateurs Clés -->
        <div class="row mb-4">
            <div class="col-md-6">
                <div class="card text-center">
                    <div class="card-header">Consommation ${moisCourant}</div>
                    <div class="card-body">
                        <h5 class="card-title"><fmt:formatNumber value="${totalMensuel}" type="number" minFractionDigits="2" maxFractionDigits="2"/> Ariary</h5>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                 <c:if test="${depassementJournalier}">
                    <div class="alert alert-warning text-center" role="alert">
                        ⚠️ Attention : Vous avez dépassé les 9000 Ar !
                    </div>
                 </c:if>
            </div>
        </div>

        <!-- Graphique -->
        <div class="card mb-4">
            <div class="card-header">
                Évolution de depenses (${moisCourant})
            </div>
            <div class="card-body">
                <canvas id="consumptionChart"></canvas>
            </div>
        </div>
        <!-- Liste des consommations récentes -->
        <div class="card mb-4">
            <div class="card-header d-flex justify-content-between align-items-center">
                Consommations Récentes
                <a href="${pageContext.request.contextPath}/consommations/add" class="btn btn-success btn-sm">+ Ajouter</a>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${not empty consommationsRecentes}">
                        <div class="table-responsive">
                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Somme (AR)</th>
                                        <th>Catégorie</th>
                                        <th>Description</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="conso" items="${consommationsRecentes}">
                                        <tr>
                                            <td><fmt:formatDate value="${conso.date}" pattern="dd/MM/yyyy"/></td>
                                            <td><fmt:formatNumber value="${conso.quantite}" type="number" minFractionDigits="2" maxFractionDigits="2"/></td>
                                            <td><c:out value="${conso.categorie.nom != null ? conso.categorie.nom : '-'}"/></td>
                                            <td><c:out value="${conso.description}"/></td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/consommations/edit?id=${conso.id}" class="btn btn-primary btn-sm">Modifier</a>
                                                <!-- Bouton de suppression qui ouvre une modale de confirmation -->
                                                <button type="button" class="btn btn-danger btn-sm" data-bs-toggle="modal" data-bs-target="#deleteModal" data-delete-id="${conso.id}">
                                                    Supprimer
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="text-center">Aucune depenses enregistrée pour le moment.</p>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="mb-3">
            <a href="${pageContext.request.contextPath}/export-pdf" class="btn btn-outline-primary">
                📄 Exporter le mois en PDF
            </a>
            </div>
        </div>

    </div>

    <!-- Modale de Confirmation de Suppression -->
    <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteModalLabel">Confirmer la suppression</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Êtes-vous sûr de vouloir supprimer cette consommation ?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                    <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/consommations/delete" style="display: inline;">
                        <input type="hidden" name="id" id="deleteItemId">
                        <button type="submit" class="btn btn-danger">Supprimer</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS Bundle (includes Popper) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

    <!-- Script pour le graphique Chart.js et la modale -->
    <script>
  // Récupération des données JSON (assurez-vous que labelsGraphique et dataGraphique sont bien des chaînes JSON)
  const labels = ${labelsGraphique};
  const data = ${dataGraphique};

  const ctx = document.getElementById('consumptionChart').getContext('2d');
  const consumptionChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [{
        label: 'Consommation Journalière Ar',
        data: data,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1,
        fill: false
      }]
    },
    options: {
      scales: {
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Ar'
          }
        },
        x: {
          title: {
            display: true,
            text: 'Date'
          }
        }
      },
      responsive: true,
      maintainAspectRatio: false
    }
  });
</script>

</body>
</html>

