package com.portfolio.controller;


import com.portfolio.entity.ContactMessage;
import com.portfolio.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:3000")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * POST /api/contact
     * Body: { "name": "...", "email": "...", "message": "..." }
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> receiveMessage(
            @Valid @RequestBody ContactMessage message) {

        contactService.save(message);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Thank you! Your message has been received."
        ));
    }

}
