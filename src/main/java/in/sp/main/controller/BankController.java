package in.sp.main.controller;

import in.sp.main.Entity.Account;
import in.sp.main.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
public class BankController {

    @Autowired
    private AccountService accountService;

    // Utility method to get the currently authenticated user
    private Account getAuthenticatedAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountService.findAccountByUsername(username);
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Account account = getAuthenticatedAccount();
        model.addAttribute("account", account);
        return "dashboard";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerAccount(@RequestParam String username, @RequestParam String password, Model model) {
        try {
            accountService.registerAccount(username, password);
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam BigDecimal amount, Model model) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Amount must be greater than zero.");
            return "redirect:/dashboard";
        }

        Account account = getAuthenticatedAccount();
        accountService.deposit(account, amount);
        return "redirect:/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam BigDecimal amount, Model model) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Amount must be greater than zero.");
            return "redirect:/dashboard";
        }

        Account account = getAuthenticatedAccount();
        try {
            accountService.withdraw(account, amount);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("account", account);
            return "dashboard";
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/transactions")
    public String transactionHistory(Model model) {
        Account account = getAuthenticatedAccount();
        model.addAttribute("transactions", accountService.getTransactionHistory(account));
        return "transactions";
    }

    @PostMapping("/transfer")
    public String transferAmount(@RequestParam String toUsername, @RequestParam BigDecimal amount, Model model) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            model.addAttribute("error", "Amount must be greater than zero.");
            return "redirect:/dashboard";
        }

        Account fromAccount = getAuthenticatedAccount();

        try {
            accountService.transferAmount(fromAccount, toUsername, amount);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("account", fromAccount);
            return "dashboard";
        }

        return "redirect:/dashboard";
    }

}
