package com.wallace.extractor.service;
import com.wallace.extractor.model.Expense;
import com.wallace.extractor.repository.ExpenseRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


@Service
public class ExcelGeneratorService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public void generateExcel(String filePath) throws IOException {
        List<Expense> expenses = expenseRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Expenses");

        String[] headers = {"ID", "Data", "Descrição", "Cidade", "USD Rate", "Valor", "Parcelado",
                "Parcelas pagas", "Parcelas restantes"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        sheet.setColumnWidth(0, 5 * 256);
        sheet.setColumnWidth(1, 20 * 256);
        sheet.setColumnWidth(2, 60 * 256);
        sheet.setColumnWidth(3, 30 * 256);
        sheet.setColumnWidth(4, 8 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 25 * 256);
        sheet.setColumnWidth(7, 25 * 256);
        sheet.setColumnWidth(8, 25 * 256);

        int rowNum = 1;
        double totalAmountToPay = 0.0;
        for (Expense expense : expenses) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(expense.getId());
            row.createCell(1).setCellValue(expense.getDate());
            row.createCell(2).setCellValue(expense.getDescription());
            row.createCell(3).setCellValue(expense.getCity());
            row.createCell(4).setCellValue(expense.getUsdRate());
            row.createCell(5).setCellValue(expense.getAmount());
            row.createCell(6).setCellValue(expense.getIsInstallment() ? "Sim" : "Não");
            row.createCell(7).setCellValue(expense.getInstallmentPaid());
            row.createCell(8).setCellValue(expense.getInstallmentRemaining());

            totalAmountToPay += expense.getAmount();
        }

        int lastRowIndex = sheet.getLastRowNum();
        Row totalRow = sheet.createRow(lastRowIndex + 2);

        Cell totalLabelCell = totalRow.createCell(5);
        totalLabelCell.setCellValue("Total a Pagar:");

        Cell totalAmountCell = totalRow.createCell(6);
        totalAmountCell.setCellValue(totalAmountToPay);

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            workbook.write(outputStream);
        }

        workbook.close();
    }
}
