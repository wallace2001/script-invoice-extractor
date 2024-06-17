package com.wallace.extractor.controller;

import com.wallace.extractor.model.Expense;
import com.wallace.extractor.service.DatabaseService;
import com.wallace.extractor.service.ExcelGeneratorService;
import com.wallace.extractor.service.ExpenseService;
import com.wallace.extractor.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
public class ExpenseController {
    @Autowired
    private PdfService pdfService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private ExcelGeneratorService excelGeneratorService;

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded", ".pdf");
        file.transferTo(tempFile);

        String text = pdfService.extractTextFromPdf(tempFile.getAbsolutePath());
        List<Expense> expenses = expenseService.parseExpenses(text);
        databaseService.saveExpenses(expenses);
        tempFile.delete();

        return "Upload and processing successful!";
    }

    @PostMapping("/generate-excel")
    public String generateExcel() {
        try {
            String filePath = "expenses.xlsx";
            excelGeneratorService.generateExcel(filePath);
            return "Arquivo Excel gerado com sucesso: " + filePath;
        } catch (IOException e) {
            return "Erro ao gerar o arquivo Excel: " + e.getMessage();
        }
    }
}
