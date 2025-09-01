package com.example.PortfolioProject.Controller;

import com.example.PortfolioProject.Models.Role;
import com.example.PortfolioProject.Models.User;
import com.example.PortfolioProject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String adminPanel() {
        return "admin/admin";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("allRoles", Role.values());
            return "admin/edit-user";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) List<String> roles,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                // Обновляем email
                user.setEmail(email);

                // Обновляем пароль только если он был введен
                if (password != null && !password.trim().isEmpty()) {
                    user.setPassword(passwordEncoder.encode(password));
                }

                // Обновляем роли
                if (roles != null && !roles.isEmpty()) {
                    Set<Role> userRoles = roles.stream()
                            .map(Role::valueOf)
                            .collect(Collectors.toSet());
                    user.setRoles(userRoles);
                } else {
                    // Если роли не выбраны, устанавливаем роль USER по умолчанию
                    user.setRoles(Set.of(Role.USER));
                }

                userService.updateUser(user);
                redirectAttributes.addFlashAttribute("successMessage",
                        "User updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) {
        try {
            // Проверяем, не пытается ли админ удалить сам себя
            User currentUser = userService.getCurrentUser();
            if (currentUser != null && currentUser.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "You cannot delete your own account!");
                return "redirect:/admin/users";
            }

            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}