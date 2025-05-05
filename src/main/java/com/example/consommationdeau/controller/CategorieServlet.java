package com.example.consommationdeau.controller;

import com.example.consommationdeau.model.Categorie;
import com.example.consommationdeau.model.Utilisateur;
import com.example.consommationdeau.service.CategorieService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "CategorieServlet", urlPatterns = {"/categories", "/categories/add", "/categories/edit", "/categories/delete"})
public class CategorieServlet extends HttpServlet {

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
                case "/categories/add":
                    // Afficher le formulaire d'ajout
                    request.setAttribute("categorie", new Categorie()); // Pour un formulaire vide
                    request.setAttribute("action", "add");
                    request.getRequestDispatcher("/WEB-INF/views/categorie_form.jsp").forward(request, response);
                    break;

                case "/categories/edit":
                    // Afficher le formulaire de modification
                    if (idParam == null) {
                        session.setAttribute("errorMessage", "ID de catégorie manquant pour la modification.");
                        response.sendRedirect(request.getContextPath() + "/categories"); // Rediriger vers la liste
                        return;
                    }
                    Long idEdit = Long.parseLong(idParam);
                    Optional<Categorie> catEditOpt = categorieService.trouverCategorieParId(idEdit);
                    if (catEditOpt.isPresent() && catEditOpt.get().getUtilisateur().getId().equals(utilisateurId)) {
                        request.setAttribute("categorie", catEditOpt.get());
                        request.setAttribute("action", "edit");
                        request.getRequestDispatcher("/WEB-INF/views/categorie_form.jsp").forward(request, response);
                    } else {
                        session.setAttribute("errorMessage", "Catégorie non trouvée ou non autorisée.");
                        response.sendRedirect(request.getContextPath() + "/categories");
                    }
                    break;

                case "/categories/delete":
                     // Géré en POST pour la sécurité
                     response.sendRedirect(request.getContextPath() + "/categories");
                     break;

                case "/categories": // Lister toutes les catégories
                default:
                    List<Categorie> categoriesList = categorieService.listerCategoriesParUtilisateur(utilisateurId);
                    request.setAttribute("categories", categoriesList);
                    // Ajouter message succès/erreur si présent
                    String successMessage = (String) session.getAttribute("successMessage");
                    if (successMessage != null) {
                        request.setAttribute("successMessage", successMessage);
                        session.removeAttribute("successMessage");
                    }
                    String errorMessage = (String) session.getAttribute("errorMessage");
                     if (errorMessage != null) {
                        request.setAttribute("errorMessage", errorMessage);
                        session.removeAttribute("errorMessage");
                    }
                    request.getRequestDispatcher("/WEB-INF/views/categories.jsp").forward(request, response);
                    break;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID invalide.");
            response.sendRedirect(request.getContextPath() + "/categories");
        } catch (Exception e) {
            e.printStackTrace(); // Logger
            session.setAttribute("errorMessage", "Une erreur est survenue: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/categories");
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
        String nom = request.getParameter("nom");

        try {
            switch (action) {
                case "/categories/add":
                    if (nom == null || nom.trim().isEmpty()) {
                        throw new IllegalArgumentException("Le nom de la catégorie est requis.");
                    }
                    categorieService.creerCategorie(nom, utilisateurId);
                    session.setAttribute("successMessage", "Catégorie ajoutée avec succès.");
                    response.sendRedirect(request.getContextPath() + "/categories");
                    break;
                case "/categories/edit":
                    if (idParam == null) {
                        throw new IllegalArgumentException("ID manquant pour la mise à jour.");
                    }
                    if (nom == null || nom.trim().isEmpty()) {
                        throw new IllegalArgumentException("Le nom de la catégorie est requis.");
                    }
                    Long idEdit = Long.parseLong(idParam);
                    categorieService.mettreAJourCategorie(idEdit, nom, utilisateurId);
                    session.setAttribute("successMessage", "Catégorie mise à jour avec succès.");
                    response.sendRedirect(request.getContextPath() + "/categories");
                    break;
                case "/categories/delete":
                    if (idParam == null) {
                        throw new IllegalArgumentException("ID manquant pour la suppression.");
                    }
                    Long idDelete = Long.parseLong(idParam);
                    categorieService.supprimerCategorie(idDelete, utilisateurId);
                    session.setAttribute("successMessage", "Catégorie supprimée avec succès.");
                    response.sendRedirect(request.getContextPath() + "/categories");
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    break;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "ID invalide.");
            response.sendRedirect(request.getContextPath() + "/categories");
        } catch (IllegalArgumentException e) {
            session.setAttribute("errorMessage", e.getMessage());
            // Rediriger vers le formulaire en cas d'erreur d'ajout/modif
            if (action.equals("/categories/add") || action.equals("/categories/edit")) {
                 Categorie catWithError = new Categorie();
                 catWithError.setNom(nom); // Pré-remplir nom
                 if (idParam != null) {
                     try { catWithError.setId(Long.parseLong(idParam)); } catch (NumberFormatException nfe) {/* ignore */} 
                 }
                 request.setAttribute("categorie", catWithError);
                 request.setAttribute("action", action.contains("edit") ? "edit" : "add");
                 request.setAttribute("errorMessage", e.getMessage());
                 request.getRequestDispatcher("/WEB-INF/views/categorie_form.jsp").forward(request, response);
            } else {
                 response.sendRedirect(request.getContextPath() + "/categories");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Logger
            session.setAttribute("errorMessage", "Une erreur technique est survenue.");
            response.sendRedirect(request.getContextPath() + "/categories");
        }
    }
}

