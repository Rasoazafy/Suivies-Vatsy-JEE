package com.example.consommationdeau.filter;

import com.example.consommationdeau.model.Utilisateur;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtre pour vérifier l'authentification de l'utilisateur avant d'accéder aux pages protégées.
 */
@WebFilter(urlPatterns = {"/dashboard/*", "/consommations/*", "/categories/*"}) // Protéger les URLs commençant par /dashboard, /consommations, /categories
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Ne pas créer de session si elle n'existe pas

        boolean isLoggedIn = (session != null && session.getAttribute("utilisateur") != null);
        String loginURI = httpRequest.getContextPath() + "/login";

        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
        boolean isLoginPage = httpRequest.getRequestURI().endsWith("login.jsp"); // Au cas où

        // Si l'utilisateur est connecté ou si la requête concerne la page de login, continuer.
        // Note: Les ressources statiques (css, js) ne sont pas filtrées ici car le pattern ne les inclut pas.
        // Si elles étaient dans un répertoire protégé, il faudrait ajuster le filtre.
        if (isLoggedIn || isLoginRequest || isLoginPage) {
            // Si connecté et essaie d'accéder à login/register, rediriger vers dashboard?
            // if (isLoggedIn && (isLoginRequest || httpRequest.getRequestURI().equals(httpRequest.getContextPath() + "/register"))) {
            //     httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard");
            // } else {
                 chain.doFilter(request, response); // L'utilisateur est autorisé, continuer la requête.
            // }
        } else {
            // L'utilisateur n'est pas connecté et tente d'accéder à une page protégée.
            // Sauvegarder l'URL demandée pour redirection après login (optionnel)
            // session = httpRequest.getSession(true);
            // session.setAttribute("requestedURI", httpRequest.getRequestURI());
            httpResponse.sendRedirect(loginURI); // Rediriger vers la page de login.
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation du filtre si nécessaire
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}

