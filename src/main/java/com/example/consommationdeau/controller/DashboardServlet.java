package com.example.consommationdeau.controller;

import com.example.consommationdeau.model.Categorie;
import com.example.consommationdeau.model.Consommation;
import com.example.consommationdeau.model.Utilisateur;
import com.example.consommationdeau.service.CalculationService;
import com.example.consommationdeau.service.CategorieService;
import com.example.consommationdeau.service.ConsommationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    @Inject
    private ConsommationService consommationService;

    @Inject
    private CategorieService categorieService;

    @Inject
    private CalculationService calculationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long utilisateurId = utilisateur.getId();

        // Récupérer les consommations récentes (par exemple, les 10 dernières)
        List<Consommation> consommationsRecentes = consommationService.listerConsommationsParUtilisateur(utilisateurId)
                .stream()
                //.limit(10) // Limiter si nécessaire
                .collect(Collectors.toList());

        // Récupérer les catégories pour les formulaires
        List<Categorie> categories = categorieService.listerCategoriesParUtilisateur(utilisateurId);

        // Calculer le total mensuel (mois courant)
        YearMonth currentMonth = YearMonth.now();
        BigDecimal totalMensuel = calculationService.calculerTotalMensuel(utilisateurId, currentMonth.getYear(), currentMonth.getMonthValue());

        // Vérifier le dépassement journalier (aujourd'hui)
        boolean depassementJournalier = calculationService.verifierDepassementJournalier(utilisateurId, Date.valueOf(LocalDate.now()));

        // Préparer les données pour le graphique (mois courant)
        Map<Date, BigDecimal> donneesGraphiqueMap = calculationService.preparerDonneesGraphiqueMoisCourant(utilisateurId);

        // Convertir les données pour Chart.js (labels et data)
        List<String> labelsGraphique = donneesGraphiqueMap.keySet().stream()
                .map(Date::toLocalDate)
                .map(d -> d.format(DateTimeFormatter.ofPattern("dd/MM")))
                .collect(Collectors.toList());
        List<BigDecimal> dataGraphique = donneesGraphiqueMap.values().stream().collect(Collectors.toList());

        // Utiliser Jackson pour convertir les listes en JSON pour JavaScript
        ObjectMapper mapper = new ObjectMapper();
        // mapper.registerModule(new JavaTimeModule()); // Pas nécessaire si on formate les dates en String
        // mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String labelsJson = mapper.writeValueAsString(labelsGraphique);
        String dataJson = mapper.writeValueAsString(dataGraphique);

        // Mettre les données dans les attributs de la requête
        request.setAttribute("utilisateur", utilisateur);
        request.setAttribute("consommationsRecentes", consommationsRecentes);
        request.setAttribute("categories", categories);
        request.setAttribute("totalMensuel", totalMensuel);
        request.setAttribute("depassementJournalier", depassementJournalier);
        request.setAttribute("labelsGraphique", labelsJson);
        request.setAttribute("dataGraphique", dataJson);
        request.setAttribute("moisCourant", currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        // Ajouter un message de succès si présent (après ajout/modif/suppr)
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage"); // Effacer après affichage
        }
        String errorMessage = (String) session.getAttribute("errorMessage");
         if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage"); // Effacer après affichage
        }


        // Transférer à la vue JSP
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }

    // doPost pourrait être utilisé pour des actions sur le dashboard, comme changer la période du graphique
    // @Override
    // protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //     doGet(request, response); // Pour l'instant, redirige vers doGet
    // }
}

