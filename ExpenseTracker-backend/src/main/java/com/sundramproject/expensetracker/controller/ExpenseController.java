package com.sundramproject.expensetracker.controller;

import com.sundramproject.expensetracker.model.dto.ApiResponse;
import com.sundramproject.expensetracker.model.dto.DashboardDTO;
import com.sundramproject.expensetracker.model.dto.ExpenseDTO;
import com.sundramproject.expensetracker.model.entity.Expense;
import com.sundramproject.expensetracker.model.entity.User;
import com.sundramproject.expensetracker.service.ExpenseService;
import com.sundramproject.expensetracker.service.ReportGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/expenses")
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ReportGenerationService reportGenerationService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardDTO>> getDashboardData(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        DashboardDTO data = expenseService.getDashboardData(year, month);
        return ResponseEntity.ok(ApiResponse.success("Dashboard data fetched", data));
    }

    @PutMapping("/budget")
    public ResponseEntity<ApiResponse<User>> updateBudget(@RequestParam Double amount) {
        User user = expenseService.updateBudget(amount);
        return ResponseEntity.ok(ApiResponse.success("Budget updated successfully", user));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ExpenseDTO>>> getAllExpenses() {
        List<ExpenseDTO> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(ApiResponse.success("Expenses fetched successfully", expenses));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<ExpenseDTO>> addExpense(@RequestBody Expense expense) {
        ExpenseDTO saved = expenseService.addExpense(expense);
        return ResponseEntity.ok(ApiResponse.success("Expense added successfully", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }

    @GetMapping("/report/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "Income vs Expense") String reportType,
            Principal principal) throws Exception {

        User user = expenseService.getLoggedInUser();

        byte[] pdf = reportGenerationService.generatePdfReport(
                user,
                reportType,
                LocalDate.parse(from),
                LocalDate.parse(to)
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.pdf")
                .body(pdf);
    }

    @GetMapping("/report/excel")
    public ResponseEntity<byte[]> downloadExcel(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "Income vs Expense") String reportType,
            Principal principal) throws Exception {

        User user = expenseService.getLoggedInUser();

        byte[] excel = reportGenerationService.generateExcelReport(
                user,
                reportType,
                LocalDate.parse(from),
                LocalDate.parse(to)
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report.xlsx")
                .body(excel);
    }

    @GetMapping("/search-sort")
    public ResponseEntity<ApiResponse<Page<ExpenseDTO>>> getSearchedExpense(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "") String search
    ) {
        Page<ExpenseDTO> expensePage = expenseService.getAllSearchedExpenses(page, size, sortBy, direction, search);
        return ResponseEntity.ok(ApiResponse.success("Expenses searched and sorted", expensePage));
    }

    @PutMapping("/update-expense/{id}")
    public ResponseEntity<ApiResponse<ExpenseDTO>> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO) {
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", updatedExpense));
    }

}
