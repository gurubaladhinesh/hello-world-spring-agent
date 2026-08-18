package com.techguru.hello_world_spring_agent.classroom.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class StudentAnswerQuestionAgent {
  private final ChatClient chatClient;

  public StudentAnswerQuestionAgent(ChatClient.Builder builder, SimpleLoggerAdvisor loggerAdvisor) {
    this.chatClient = builder
        .defaultSystem("You are a student trying to answer a question based on a provided lesson.")
        .defaultAdvisors(loggerAdvisor)
        .build();
  }

  @Tool(description = "Answers a question based on the teacher's lesson context.")
  public String answerQuestion(String lesson, String question) {
    return this.chatClient.prompt()
        .user(String.format("Lesson Context: %s\nQuestion: %s", lesson, question))
        .call().content();
  }
}