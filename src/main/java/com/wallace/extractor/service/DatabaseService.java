package com.wallace.extractor.service;

import com.wallace.extractor.model.Expense;
import com.wallace.extractor.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public void saveExpenses(List<Expense> expenses) {
        expenseRepository.saveAll(expenses);
    }
}
