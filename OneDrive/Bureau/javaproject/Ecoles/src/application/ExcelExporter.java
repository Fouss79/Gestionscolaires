package application;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExporter {

    public static void exporterEleves(ObservableList<Eleve> eleves, Stage stage) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Liste des élèves");

        // ==== 1️⃣ En-tête ====
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Matricule", "Nom", "Prénom", "Classe", "Année scolaire", "Statut"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        // ==== 2️⃣ Contenu ====
        for (int i = 0; i < eleves.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Eleve e = eleves.get(i);

            row.createCell(0).setCellValue(e.getNumeroMatricule());
            row.createCell(1).setCellValue(e.getNom());
            row.createCell(2).setCellValue(e.getPrenom());
            row.createCell(3).setCellValue(e.getClasseNom());
            row.createCell(4).setCellValue(e.getAnneeScolaire() != null ? e.getAnneeScolaire() : "");
            row.createCell(5).setCellValue(e.getStatutId());
        }

        // ==== 3️⃣ Ajuster largeur colonnes ====
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ==== 4️⃣ Choisir fichier à sauvegarder ====
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                workbook.close();
                System.out.println("Fichier Excel créé avec succès !");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

