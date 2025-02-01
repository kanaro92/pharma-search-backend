package com.pharmasearch.controller;

import com.pharmasearch.model.Message;
import com.pharmasearch.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin("*")
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<Message>> getMessages(
            @RequestParam Long requestId,
            @RequestParam Long otherUserId) {
        List<Message> messages = messageService.getMessagesByRequest(requestId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long requestId,
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        Long receiverId = Long.parseLong(request.get("receiverId"));
        Message message = messageService.sendMessage(requestId, content, receiverId);
        return ResponseEntity.ok(message);
    }
}
