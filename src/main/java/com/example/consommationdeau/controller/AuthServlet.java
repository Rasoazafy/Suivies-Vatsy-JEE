package com.example.consommationdeau.controller;

import com.example.consommationdeau.model.Utilisateur;
import com.example.consommationdeau.service.AuthService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    @Inject
    private AuthService authService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        switch (action) {
            case "/login":
                // Si déjà connecté, rediriger vers le tableau de bord
                if (request.getSession(false) != null && request.getSession(false).getAttribute("utilisateur") != null) {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                break;
            case "/register":
                 // Si déjà connecté, rediriger vers le tableau de bord
                if (request.getSession(false) != null && request.getSession(false).getAttribute("utilisateur") != null) {
                    response.sendRedirect(request.getContextPath() + "/dashboard");
                    return;
                }
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
                break;
            case "/logout":
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate(); // Invalider la session
                }
                response.sendRedirect(request.getContextPath() + "/login?logout=true"); // Rediriger vers la page de login avec un message
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();

        switch (action) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register":
                handleRegister(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "L\'email et le mot de passe sont requis.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        Optional<Utilisateur> userOpt = authService.login(email, password);

        if (userOpt.isPresent()) {
            HttpSession session = request.getSession(true); // Crée une session si elle n'existe pas
            session.setAttribute("utilisateur", userOpt.get()); // Stocker l'objet Utilisateur entier ou juste l'ID/nom
            session.setMaxInactiveInterval(30 * 60); // 30 minutes d'inactivité max
            response.sendRedirect(request.getContextPath() + "/dashboard"); // Rediriger vers le tableau de bord
        } else {
            request.setAttribute("errorMessage", "Email ou mot de passe incorrect.");
            request.setAttribute("email", email); // Pré-remplir l'email
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validation simple
        if (nom == null || nom.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty() ||
            confirmPassword == null || confirmPassword.isEmpty()) {
            request.setAttribute("errorMessage", "Tous les champs sont requis.");
            request.setAttribute("nom", nom);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Les mots de passe ne correspondent pas.");
            request.setAttribute("nom", nom);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        try {
            authService.register(nom, email, password);
            // Rediriger vers la page de login avec un message de succès
            response.sendRedirect(request.getContextPath() + "/login?register=success");
        } catch (IllegalArgumentException e) {
            // Afficher l'erreur (ex: email déjà utilisé)
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("nom", nom);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
}

