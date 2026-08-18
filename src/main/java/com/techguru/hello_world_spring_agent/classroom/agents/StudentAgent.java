package com.techguru.hello_world_spring_agent.classroom.agents;

import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.annotation.Tool;

@Service
public class StudentAgent {

  private final ChatClient chatClient;

  public StudentAgent(ChatClient.Builder builder, SimpleLoggerAdvisor loggerAdvisor) {
    // A unified student persona that applies to both studying and incorporating feedback
    this.chatClient = builder
        .defaultSystem("You are an attentive student. You try your best to answer questions based on lessons and carefully revise your work when given feedback.")
        .defaultAdvisors(loggerAdvisor)
        .build();
  }

  // --- TOOL 1: Initial Answering ---
  @Tool(description = "Answers an academic question based strictly on the provided lesson context.")
  public String answerQuestion(String lesson, String question) {
    return this.chatClient.prompt()
        .user(String.format("Lesson Context: %s\n\nQuestion to answer: %s", lesson, question))
        .call()
        .content();
  }

  // --- TOOL 2: Revision ---
  @Tool(description = "Revises an initial answer using specific teacher feedback and the original lesson context.")
  public String reviseAnswer(String lesson, String question, String originalAnswer, String feedback) {
    return this.chatClient.prompt()
        .user(String.format("""
                Lesson Context: %s
                Question: %s
                My Previous Answer: %s
                Teacher Feedback: %s
                
                Please fix and rewrite my answer to address the teacher's feedback accurately.
                """, lesson, question, originalAnswer, feedback))
        .call()
        .content();
  }
}

