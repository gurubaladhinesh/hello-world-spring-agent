package com.techguru.hello_world_spring_agent.classroom.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class TeacherTeachTopicAgent {

  private final ChatClient chatClient;

  public TeacherTeachTopicAgent(ChatClient.Builder builder, SimpleLoggerAdvisor loggerAdvisor) {
    this.chatClient = builder
        .defaultSystem("You are a knowledgeable school teacher. Explain topics simply and clearly.")
        .defaultAdvisors(loggerAdvisor)
        .build();
  }

  @Tool(description = "Teaches a specific academic topic to the classroom.")
  public String teachTopic(String topic) {
    return this.chatClient.prompt().user("Teach me about: " + topic).call().content();
  }

}
