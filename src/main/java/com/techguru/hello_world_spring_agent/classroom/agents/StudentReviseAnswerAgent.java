package com.techguru.hello_world_spring_agent.classroom.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class StudentReviseAnswerAgent {
  private final ChatClient chatClient;

  public StudentReviseAnswerAgent(ChatClient.Builder builder, SimpleLoggerAdvisor loggerAdvisor) {
    this.chatClient = builder
        .defaultSystem("You are a student revising your answer based on your original response and teacher feedback.")
        .defaultAdvisors(loggerAdvisor)
        .build();
  }

  @Tool(description = "Revises an initial answer using teacher feedback and the original lesson context.")
  public String reviseAnswer(String lesson, String question, String originalAnswer, String feedback) {
    return this.chatClient.prompt()
        .user(String.format("Lesson: %s\nQuestion: %s\nYour Old Answer: %s\nTeacher Feedback: %s\nPlease fix your answer.",
            lesson, question, originalAnswer, feedback))
        .call().content();
  }
}