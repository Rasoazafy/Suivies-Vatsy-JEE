package com.example.consommationdeau.controller;

import com.example.consommationdeau.model.Consommation;
import com.example.consommationdeau.model.Utilisateur;
import com.example.consommationdeau.service.ConsommationService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

@WebServlet("/export-pdf")
public class ExportPdfServlet extends HttpServlet {

    @Inject
    private ConsommationService consommationService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");

        if (utilisateur == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Long utilisateurId = utilisateur.getId();
        YearMonth currentMonth = YearMonth.now();
        List<Consommation> consommations = consommationService.listerConsommationsParUtilisateur(utilisateurId)
                .stream()
                .filter(c -> {
                    YearMonth ym = YearMonth.from(c.getDate().toLocalDate());
                    return ym.equals(currentMonth);
                }).toList();

        // Configuration de la réponse
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"consommation_" + currentMonth + ".pdf\"");

        try (OutputStream out = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Titre
            Font fontTitre = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titre = new Paragraph("Consommation - " + currentMonth.getMonth() + " " + currentMonth.getYear(), fontTitre);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(20);
            document.add(titre);

            // Tableau
            PdfPTable table = new PdfPTable(4); // Date, Quantité, Catégorie, Description
            table.setWidthPercentage(100);
            table.setWidths(new int[]{2, 2, 3, 5});

            // En-têtes
            Stream.of("Date", "Somme (Ar)", "Catégorie", "Description")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setPhrase(new Phrase(headerTitle));
                        table.addCell(header);
                    });

            BigDecimal totalQuantite = BigDecimal.ZERO;

            // Contenu
            for (Consommation c : consommations) {
                table.addCell(c.getDate().toString());
                table.addCell(c.getQuantite().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                table.addCell(c.getCategorie() != null ? c.getCategorie().getNom() : "-");
                table.addCell(c.getDescription() != null ? c.getDescription() : "");
                totalQuantite = totalQuantite.add(c.getQuantite());
            }

            // Ligne de total
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setColspan(1);
            emptyCell.setBorder(Rectangle.NO_BORDER);

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total"));
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(totalQuantite.setScale(2, BigDecimal.ROUND_HALF_UP) + " Ar"));
            totalValueCell.setColspan(2);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            totalValueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);

            table.addCell(emptyCell);
            table.addCell(totalLabelCell);
            table.addCell(totalValueCell);

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la génération du PDF", e);
        }
    }
}
