package com.example.consommationdeau.controller;

import com.example.consommationdeau.model.Categorie;
import com.example.consommationdeau.model.Consommation;
import com.example.consommationdeau.model.Utilisateur;
import com.example.consommationdeau.service.CategorieService;
import com.example.consommationdeau.service.ConsommationService;
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
import java.util.List;
import java.util.Optional;

@WebServlet(name = "ConsommationServlet", urlPatterns = {"/consommations", "/consommations/add", "/consommations/edit", "/consommations/delete"})
public class ConsommationServlet extends HttpServlet {

    @Inject
    private ConsommationService consommationService;

    @Inject
    private CategorieService categorieService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Long utilisateurId = utilisateur.getId();

        String action = request.getServletPath();
        String idParam = request.getParameter("id");

        try {
            switch (action) {
                case "/consommations/add":
                    // Afficher le formulaire d'ajout
                    List<Categorie> categoriesAdd = categorieService.listerCategoriesParUtilisateur(utilisateurId);
                    request.setAttribute("categories", categoriesAdd);
                    request.setAttribute("consommation", new Consommation()); // Pour un formulaire vide
                    request.setAttribute("action", "add");
                    request.getRequestDispatcher("/WEB-INF/views/consommation_form.jsp").forward(request, response);
                    break;

                case "/consommations/edit":
                    // Afficher le formulaire de modification
                    if (idParam == null) {
                        session.setAttribute("errorMessage", "ID de consommation manquant pour la modification.");
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                        return;
                    }
                    Long idEdit = Long.parseLong(idParam);
                    Optional<Consommation> consoEditOpt = consommationService.trouverConsommationParId(idEdit);
                    if (consoEditOpt.isPresent() && consoEditOpt.get().getUtilisateur().getId().equals(utilisateurId)) {
                        List<Categorie> categoriesEdit = categorieService.listerCategoriesParUtilisateur(utilisateurId);
                        request.setAttribute("categories", categoriesEdit);
                        request.setAttribute("consommation", consoEditOpt.get());
                        request.setAttribute("action", "edit");
                        request.getRequestDispatcher("/WEB-INF/views/consommation_form.jsp").forward(request, response);
                    } else {
                        session.setAttribute("errorMessage", "Consommation non trouvée ou non autorisée.");
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                    }
                    break;

                case "/consommations/delete":
                     // Géré en POST pour la sécurité, mais on pourrait avoir une confirmation en GET
                     // Pour l'instant, on redirige vers le dashboard si on arrive ici en GET
                     response.sendRedirect(request.getContextPath() + "/dashboard");
                     break;

                case "/consommations": // Lister toutes les consommations (pourrait être une page dédiée)
                default:
                    // Par défaut, on redirige vers le dashboard qui affiche les consommations récentes
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    break;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID invalide.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            // Log l'erreur
            e.printStackTrace(); // À remplacer par un logger
            session.setAttribute("errorMessage", "Une erreur est survenue: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        if (utilisateur == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        Long utilisateurId = utilisateur.getId();

        String action = request.getServletPath();
        String idParam = request.getParameter("id");

        try {
            switch (action) {
                case "/consommations/add":
                    handleSave(request, response, utilisateurId, null);
                    break;
                case "/consommations/edit":
                    if (idParam == null) {
                        session.setAttribute("errorMessage", "ID manquant pour la mise à jour.");
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                        return;
                    }
                    Long idEdit = Long.parseLong(idParam);
                    handleSave(request, response, utilisateurId, idEdit);
                    break;
                case "/consommations/delete":
                    if (idParam == null) {
                        session.setAttribute("errorMessage", "ID manquant pour la suppression.");
                        response.sendRedirect(request.getContextPath() + "/dashboard");
                        return;
                    }
                    Long idDelete = Long.parseLong(idParam);
                    handleDelete(request, response, utilisateurId, idDelete);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    break;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID invalide.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (IllegalArgumentException e) {
            // Erreur métier (validation, etc.)
            session.setAttribute("errorMessage", e.getMessage());
            // Rediriger vers le formulaire avec l'erreur
            if (action.equals("/consommations/add") || action.equals("/consommations/edit")) {
                 // Re-préparer les données pour le formulaire
                 List<Categorie> categories = categorieService.listerCategoriesParUtilisateur(utilisateurId);
                 request.setAttribute("categories", categories);
                 // Recréer un objet Consommation avec les données soumises pour pré-remplir
                 Consommation consoWithError = recreateConsommationFromRequest(request, utilisateur);
                 request.setAttribute("consommation", consoWithError);
                 request.setAttribute("action", action.contains("edit") ? "edit" : "add");
                 request.setAttribute("errorMessage", e.getMessage()); // Répéter le message pour la JSP
                 request.getRequestDispatcher("/WEB-INF/views/consommation_form.jsp").forward(request, response);
            } else {
                 response.sendRedirect(request.getContextPath() + "/dashboard");
            }
        } catch (Exception e) {
            // Log l'erreur
            e.printStackTrace(); // À remplacer par un logger
            session.setAttribute("errorMessage", "Une erreur technique est survenue lors du traitement.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    private void handleSave(HttpServletRequest request, HttpServletResponse response, Long utilisateurId, Long consommationId) throws IOException, ServletException {
        String dateStr = request.getParameter("date");
        String quantiteStr = request.getParameter("quantite");
        String description = request.getParameter("description");
        String categorieIdStr = request.getParameter("categorieId");

        Date date = null;
        BigDecimal quantite = null;
        Long categorieId = null;

        // Validation et conversion
        try {
            if (dateStr != null && !dateStr.isEmpty()) {
                date = Date.valueOf(dateStr);
            }
            if (quantiteStr != null && !quantiteStr.isEmpty()) {
                quantite = new BigDecimal(quantiteStr.replace(',', '.')); // Gérer la virgule décimale
            }
            if (categorieIdStr != null && !categorieIdStr.isEmpty() && !categorieIdStr.equals("0")) { // 0 pour "aucune catégorie"
                categorieId = Long.parseLong(categorieIdStr);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Format de date ou de quantité invalide.");
        }

        if (consommationId == null) {
            // Ajout
            consommationService.enregistrerConsommation(date, quantite, description, utilisateurId, categorieId);
            request.getSession().setAttribute("successMessage", "Consommation ajoutée avec succès.");
        } else {
            // Modification
            consommationService.mettreAJourConsommation(consommationId, date, quantite, description, utilisateurId, categorieId);
            request.getSession().setAttribute("successMessage", "Consommation mise à jour avec succès.");
        }
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response, Long utilisateurId, Long consommationId) throws IOException {
        consommationService.supprimerConsommation(consommationId, utilisateurId);
        request.getSession().setAttribute("successMessage", "Consommation supprimée avec succès.");
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    // Helper pour recréer un objet Consommation en cas d'erreur de validation pour pré-remplir le formulaire
    private Consommation recreateConsommationFromRequest(HttpServletRequest request, Utilisateur utilisateur) {
        Consommation conso = new Consommation();
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
             try { conso.setId(Long.parseLong(idParam)); } catch (NumberFormatException e) { /* ignore */ }
        }
        try {
             String dateStr = request.getParameter("date");
             if (dateStr != null && !dateStr.isEmpty()) conso.setDate(Date.valueOf(dateStr));
        } catch (Exception e) { /* ignore */ }
         try {
             String quantiteStr = request.getParameter("quantite");
             if (quantiteStr != null && !quantiteStr.isEmpty()) conso.setQuantite(new BigDecimal(quantiteStr.replace(',', '.')));
        } catch (Exception e) { /* ignore */ }
        conso.setDescription(request.getParameter("description"));
        conso.setUtilisateur(utilisateur); // Important pour la cohérence
        try {
            String categorieIdStr = request.getParameter("categorieId");
            if (categorieIdStr != null && !categorieIdStr.isEmpty() && !categorieIdStr.equals("0")) {
                Long catId = Long.parseLong(categorieIdStr);
                // On ne recharge pas l'objet Categorie ici, on pourrait juste stocker l'ID
                // ou le recharger si nécessaire pour l'affichage
                Categorie tempCat = new Categorie();
                tempCat.setId(catId);
                conso.setCategorie(tempCat);
            }
        } catch (Exception e) { /* ignore */ }
        return conso;
    }
}

