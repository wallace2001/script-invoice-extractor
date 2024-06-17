package com.wallace.extractor.service;

import com.wallace.extractor.model.Expense;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExpenseService {
    private static final Pattern EXPENSE_PATTERN = Pattern.compile("(\\d{2}/\\d{2})\\s+(.*?)\\s+(.*?)\\s+([\\d,.]*)(?:$|\\d{2},\\d)");

    public List<Expense> parseExpenses(String text) {
        List<Expense> expenses = new ArrayList<>();

        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {
            Matcher matcher = EXPENSE_PATTERN.matcher(line);
            if (matcher.find()) {
                Expense expense = getExpense(matcher);
                expenses.add(expense);
            }
        }
        return expenses;
    }

    private Expense getExpense(Matcher matcher) {
        Expense expense = new Expense();
        expense.setDate(matcher.group(1));
        expense.setDescription(extractDescription(matcher.group(2).trim() + " " + matcher.group(3).trim()));
        expense.setCity(extractCity(matcher.group(3)));
        expense.setUsdRate(0D);
        String amountStr = matcher.group(4).replace(",", ".");
        if (!amountStr.isEmpty()) {
            expense.setAmount(Double.parseDouble(amountStr));
        } else {
            expense.setAmount(0.0); // Pode definir um valor padrão ou tratar como necessário
        }

        String installmentInfo = extractInstallmentInfo(matcher.group(2).trim() + " " + matcher.group(3).trim());
        if (installmentInfo != null) {
            String[] parts = installmentInfo.split("/");
            int paid = Integer.parseInt(parts[0]);
            int total = Integer.parseInt(parts[1]);

            expense.setIsInstallment(true);
            expense.setInstallmentPaid(paid);
            expense.setInstallmentRemaining(total - paid);
        } else {
            expense.setIsInstallment(false);
            expense.setInstallmentRemaining(0);
            expense.setInstallmentPaid(1);

        }
        return expense;
    }

    private String extractDescription(String fullDescription) {
        return fullDescription.trim();
    }

    private String extractCity(String fullDescription) {
        String[] parts = fullDescription.split("\\s+");
        String lastPart = parts[parts.length - 1].replaceAll("[,.]", "");


        if (lastPart.matches("[\\d]+")) {
            if (parts.length >= 2 && isCity(parts[parts.length - 2])) {
                return parts[parts.length - 2];
            }
        }
        if (parts.length >= 2 && isCity(parts[parts.length - 2] + " " + lastPart)) {
            return parts[parts.length - 2] + " " + lastPart;
        }
        if (isCity(lastPart)) {
            return lastPart;
        }
        return lastPart;
    }

    private boolean isCity(String cityName) {
        String[] compositeCities = {"SAO PAULO", "RIO DE JANEIRO", "BELO HORIZONTE", "PORTO ALEGRE"};

        for (String city : compositeCities) {
            if (cityName.equalsIgnoreCase(city)) {
                return true;
            }
        }
        return false;
    }

    private static String extractInstallmentInfo(String description) {
        Pattern pattern = Pattern.compile("(\\d{2}/\\d{2})");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
