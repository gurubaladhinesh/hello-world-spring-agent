package com.techguru.hello_world_spring_agent.classroom.controller;

import com.techguru.hello_world_spring_agent.classroom.StudentTeacherWorkflowOrchestrator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workflow")
public class StudentTeacherWorkflowController {

  private final StudentTeacherWorkflowOrchestrator orchestrator;

  public StudentTeacherWorkflowController(StudentTeacherWorkflowOrchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  @GetMapping
  public void callWorkflow(@RequestParam("type") String type) {
    switch (type) {
      case "sequence":
        orchestrator.sequenceWorkflow();
        break;
      case "loop":
        orchestrator.loopWorkflow();
        break;
      case "parallel":
        orchestrator.parallelWorkflow();
        break;
      case "parallel-mapper":
        orchestrator.parallelMapperWorkflow();
        break;
      case "conditional":
        orchestrator.conditionalWorkflow();
        break;
      case "supervisor":
        orchestrator.supervisorWorkflow();
        break;
      default:
        orchestrator.simpleWorkflow();
        break;
    }
  }

}
