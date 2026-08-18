package com.techguru.hello_world_spring_agent.classroom;

import com.techguru.hello_world_spring_agent.classroom.agents.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class StudentTeacherWorkflowOrchestrator {

  private final ChatClient supervisorModel;
  private final TeacherAgent teacher;
  private final StudentAgent student;

  public StudentTeacherWorkflowOrchestrator(ChatClient.Builder builder, TeacherAgent teacher, StudentAgent student) {
    this.teacher = teacher;
    this.student = student;

    // Supervisor Client configured to autonomously call sub-agents as tools
    this.supervisorModel = builder
        .defaultSystem("""
                You are an intelligent academic supervisor managing a team consisting
                of a teacher, a student answering, a student revising, and a teacher verifying. 
                Coordinate them to ensure the student correctly masters the topic and answers the question accurately.
                """)
        .defaultAdvisors()
        .defaultTools(teacher, student)
        .build();
  }

  // ==========================================
  // 1. SIMPLE LINEAR WORKFLOW
  // ==========================================
  public void simpleWorkflow() {
    System.out.println("---------- SIMPLE LINEAR WORKFLOW (SPRING AI) ------------");
    String topic = "Circulatory system";
    String question = "What is an atrium?";

    String lesson = teacher.teachTopic(topic);
    String answer = student.answerQuestion(lesson, question);
    String verification = teacher.verifyAnswer(question, answer);

    System.out.println("Teacher's Lesson: " + lesson);
    System.out.println("Student Answer: " + answer);
    System.out.println("Teacher Verification: " + verification);
  }

  // ==========================================
  // 2. SEQUENCE WORKFLOW (Context Map Chaining)
  // ==========================================
  public void sequenceWorkflow() {
    System.out.println("---------- SEQUENCE WORKFLOW (SPRING AI) ------------");
    Map<String, Object> scope = new HashMap<>();
    scope.put("topic", "Circulatory system");
    scope.put("question", "What is an atrium?");

    // Step-by-step pipeline execution
    scope.put("lesson", teacher.teachTopic((String) scope.get("topic")));
    scope.put("answer", student.answerQuestion((String) scope.get("lesson"), (String) scope.get("question")));
    scope.put("verificationResult", teacher.verifyAnswer((String) scope.get("question"), (String) scope.get("answer")));

    System.out.println("Verification result: " + scope.get("verificationResult"));
  }

  // ==========================================
  // 3. LOOP WORKFLOW
  // ==========================================
  public void loopWorkflow() {
    System.out.println("---------- LOOP WORKFLOW (SPRING AI) ------------");
    String topic = "Circulatory system";
    String question = "What is an atrium?";

    String lesson = teacher.teachTopic(topic);
    String currentAnswer = student.answerQuestion(lesson, question);
    String feedback = teacher.verifyAnswer(question, currentAnswer);

    int maxIterations = 3;
    int currentIteration = 0;

    // Loop until condition matches or max iterations are breached
    while (currentIteration < maxIterations &&
        !(feedback.toLowerCase().contains("correct") && !feedback.toLowerCase().contains("incorrect"))) {

      System.out.println("Iteration " + (currentIteration + 1) + " - Feedback: " + feedback);
      currentAnswer = student.reviseAnswer(lesson, question, currentAnswer, feedback);
      feedback = teacher.verifyAnswer(question, currentAnswer);
      currentIteration++;
    }

    System.out.println("Final Answer after Loop: " + currentAnswer);
    System.out.println("LOOP Workflow completed successfully");
  }

  // ==========================================
  // 4. PARALLEL WORKFLOW (Concurrently executing branches)
  // ==========================================
  public void parallelWorkflow() {
    System.out.println("---------- PARALLEL WORKFLOW (SPRING AI) ------------");
    String lesson = teacher.teachTopic("Circulatory system");
    String question = "What is an atrium?";

    // Execute student responses concurrently using CompletableFuture
    CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> student.answerQuestion(lesson, question));
    CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> student.answerQuestion(lesson, question)); // Replicating Student B

    CompletableFuture.allOf(task1, task2).join();

    System.out.println("Student_1 response: " + task1.join());
    System.out.println("Student_2 response: " + task2.join());
  }

  // ==========================================
  // 5. PARALLEL MAPPER WORKFLOW (Batch Processor)
  // ==========================================
  public void parallelMapperWorkflow() {
    System.out.println("---------- PARALLEL MAPPER WORKFLOW (SPRING AI) ------------");
    String lesson = teacher.teachTopic("Circulatory system");
    String question = "What is an atrium?";
    List<String> studentList = List.of("Alice", "Bob", "Charlie");

    // Scale dynamically using managed thread pool
    try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
      List<CompletableFuture<String>> futures = studentList.stream()
          .map(student -> CompletableFuture.supplyAsync(() -> {
            // Prepend student persona dynamically if needed
            return this.student.answerQuestion(lesson, "(" + student + ") " + question);
          }, executor))
          .toList();

      List<String> combinedAnswers = futures.stream().map(CompletableFuture::join).toList();

      for (int i = 0; i < studentList.size(); i++) {
        System.out.println(studentList.get(i) + "'s Answer: " + combinedAnswers.get(i));
      }
    }
  }

  // ==========================================
  // 6. CONDITIONAL WORKFLOW (State Router)
  // ==========================================
  public void conditionalWorkflow() {
    System.out.println("---------- CONDITIONAL WORKFLOW (SPRING AI) ------------");
    Map<String, Object> scope = new HashMap<>();

    // 1. Initial Assessment Phase
    String question = "What is ventricle";
    String lesson = teacher.teachTopic("Circulatory system");
    String answer = student.answerQuestion(lesson, question);
    String verificationResult = teacher.verifyAnswer(question, answer);

    scope.put("lesson", lesson);
    scope.put("question", question);
    scope.put("answer", answer);
    scope.put("verificationResult", verificationResult);

    // 2. Conditional Routing block
    boolean needsCorrection = !scope.get("verificationResult").toString().toLowerCase().contains("correct");
    if (needsCorrection) {
      System.out.println("--- Revising Answer based on Teacher Feedback ---");
      String revisedAnswer = student.reviseAnswer(
          (String) scope.get("lesson"),
          (String) scope.get("question"),
          (String) scope.get("answer"),
          (String) scope.get("verificationResult")
      );
      scope.put("revisedAnswer", revisedAnswer);
    }

    // 3. Print Results
    String finalAnswer = (String) scope.getOrDefault("revisedAnswer", scope.get("answer"));
    System.out.println("Final Answer after conditional workflow: " + finalAnswer);
  }

  // ==========================================
  // 7. SUPERVISOR WORKFLOW (Autonomous Loop)
  // ==========================================
  public void supervisorWorkflow() {
    System.out.println("---------- SUPERVISOR WORKFLOW (SPRING AI) ------------");

    // Hand complete autonomy to the Supervisor LLM tool-calling loop
    String goalPrompt = "Topic: Circulatory system. Question: What is an atrium? " +
        "Goal: Teach the topic, get the student's answer, verify it, and revise it if necessary.";

    String result = supervisorModel.prompt()
        .user(goalPrompt)
        .call()
        .content();

    System.out.println("Supervisor Orchestrated Result: " + result);
  }
}
