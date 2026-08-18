package com.techguru.hello_world_spring_agent.classroom.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class TeacherAgent {

  private final ChatClient chatClient;

  public TeacherAgent(ChatClient.Builder builder, SimpleLoggerAdvisor loggerAdvisor) {
    // A single base persona works perfectly for both operations
    this.chatClient = builder
        .defaultSystem("You are an expert school teacher. You teach academic topics clearly and evaluate student answers fairly.")
        .defaultAdvisors(loggerAdvisor)
        .build();
  }

  // --- TOOL 1 ---
  @Tool(description = "Teaches a specific academic topic to the classroom.")
  public String teachTopic(String topic) {
    return this.chatClient.prompt()
        .user("Teach me about the following topic in simple terms: " + topic)
        .call()
        .content();
  }

  // --- TOOL 2 ---
  @Tool(description = "Verifies a student's answer against the original question. States clearly if it is Correct or Incorrect and provides feedback.")
  public String verifyAnswer(String question, String answer) {
    return this.chatClient.prompt()
        .user(String.format("Original Question: %s\nStudent's Answer: %s\n\nGrade this answer.", question, answer))
        .call()
        .content();
  }
}

