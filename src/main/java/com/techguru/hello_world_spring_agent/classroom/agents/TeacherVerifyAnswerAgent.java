package com.techguru.hello_world_spring_agent.classroom.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class TeacherVerifyAnswerAgent {
  private final ChatClient chatClient;

  public TeacherVerifyAnswerAgent(ChatClient.Builder builder, SimpleLoggerAdvisor loggerAdvisor) {
    this.chatClient = builder
        .defaultSystem("You are an evaluator. Grade the student's answer based on the question. State clearly if it is 'Correct' or 'Incorrect' and give feedback.")
        .defaultAdvisors(loggerAdvisor)
        .build();
  }

  @Tool(description = "Verifies a student's answer against the original question.")
  public String verifyAnswer(String question, String answer) {
    return this.chatClient.prompt()
        .user(String.format("Question: %s\nStudent Answer: %s", question, answer))
        .call().content();
  }
}