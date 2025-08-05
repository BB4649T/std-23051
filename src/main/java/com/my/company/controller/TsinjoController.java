package com.my.company.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.company.school.tsinjo_std21001.domain.*;
import com.company.school.tsinjo_std21001.dto.DonationForm;
import com.company.school.tsinjo_std21001.service.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TsinjoController {
    
    private final DonationService donationService;
    private final HelpService helpService;
    private final VolaPaymentVerificationService volaService;
    
    @GetMapping("/")
    public String index(Model model) {
        List<Donation> donations = donationService.getAllDonationsOrderByDateDesc();
        List<Help> helps = helpService.getAllHelpsOrderByDateDesc();
        
        model.addAttribute("donations", donations);
        model.addAttribute("helps", helps);
        model.addAttribute("donationForm", new DonationForm());
        
        log.debug("Displaying {} donations and {} helps", donations.size(), helps.size());
        return "index";
    }
    
    @PostMapping("/donations")
    public String submitDonation(@Valid @ModelAttribute DonationForm form, 
                                 BindingResult result, 
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            log.warn("Validation errors in donation form: {}", result.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "Veuillez corriger les erreurs dans le formulaire");
            return "redirect:/";
        }
        
        try {
            // Soumettre le paiement à Vola pour vérification
            Payment payment = volaService.submitPaymentForVerification(form.getPaymentId());
            
            // Créer ou récupérer le donateur
            Donor donor = donationService.getOrCreateDonor(form.getDonorEmail(), form.getDonorName());
            
            // Créer la donation
            donationService.createDonation(donor, payment);
            
            redirectAttributes.addFlashAttribute("success", "Don soumis avec succès! Le paiement est en cours de vérification.");
            log.info("New donation submitted by {} for payment {}", donor.getEmail(), payment.getId());
            
        } catch (Exception e) {
            log.error("Error submitting donation: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la soumission du don: " + e.getMessage());
        }
        
        return "redirect:/";
    }
}
