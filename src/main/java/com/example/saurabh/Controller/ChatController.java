package com.example.saurabh.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.saurabh.entity.Chatmsg;

@Controller
public class ChatController {

    private final TranslationService translationService;

    // Store mechanic's detected language by mechanic name
    private final Map<String, String> mechanicLanguageMap = new HashMap<>();

    public ChatController(TranslationService translationService) {
        this.translationService = translationService;
    }

    // Reset mechanic language jab use karna hoga tab abhi aiese hi rehn ddiaya
    public void resetLanguageFor(String mechanicName) {
        mechanicLanguageMap.remove(mechanicName);
        System.out.println("Language reset for: " + mechanicName);
    }

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public Chatmsg handleMessage(Chatmsg message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();
        String content = message.getContent();

        if (sender.equalsIgnoreCase("Saurabh")) {
            // User -> Mechanic
            String mechLang = mechanicLanguageMap.get(receiver);
            if (mechLang != null) {
                String translatedContent = translationService.translate(content, mechLang);
                return new Chatmsg(sender, receiver, translatedContent);
            } else {
                return message; // send original if language unknown
            }

        } else if (sender.equalsIgnoreCase("Mohit")) {
            // Mechanic's first message: detect language & save
            if (!mechanicLanguageMap.containsKey(sender)) {
                String detectedLang = translationService.detectLanguage(content);
                mechanicLanguageMap.put(sender, detectedLang);
                System.out.println("Detected and saved mechanic language: " + detectedLang);
            }
            // Mechanic -> User, translate to English
            String translatedContent = translationService.translate(content, "en");
            return new Chatmsg(sender, receiver, translatedContent);
        }

        return message;
    }
}
