package msdemo.hong.com.flowableservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import msdemo.hong.com.flowableservice.service.FlowableService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flowable流程控制器
 * <p>
 * 提供流程定义管理、流程实例管理、任务管理等RESTful API接口
 * </p>
 */
@Slf4j
@RestController
@RequestMapping("/api/flowable")
@RequiredArgsConstructor
@Tag(name = "Flowable流程管理", description = "流程定义、流程实例、任务管理相关接口")
public class FlowableController {

    private final FlowableService flowableService;

    /**
     * 获取所有已部署的流程定义
     *
     * @return ResponseEntity<List<ProcessDefinition>> 流程定义列表
     */
    @GetMapping("/process-definitions")
    @Operation(summary = "获取流程定义列表", description = "获取所有已部署的BPMN流程定义")
    public ResponseEntity<List<ProcessDefinition>> getAllProcessDefinitions() {
        List<ProcessDefinition> definitions = flowableService.getAllProcessDefinitions();
        return ResponseEntity.ok(definitions);
    }

    /**
     * 根据流程定义key获取流程定义
     *
     * @param processDefinitionKey 流程定义key
     * @return ResponseEntity<ProcessDefinition> 流程定义对象
     */
    @GetMapping("/process-definitions/{processDefinitionKey}")
    @Operation(summary = "获取流程定义详情", description = "根据流程定义key获取流程定义详情")
    public ResponseEntity<ProcessDefinition> getProcessDefinitionByKey(
            @Parameter(description = "流程定义key") @PathVariable String processDefinitionKey) {
        ProcessDefinition definition = flowableService.getProcessDefinitionByKey(processDefinitionKey);
        if (definition == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(definition);
    }

    /**
     * 获取流程定义的XML内容
     *
     * @param processDefinitionId 流程定义ID
     * @return ResponseEntity<String> BPMN XML字符串
     */
    @GetMapping("/process-definitions/xml/{processDefinitionId}")
    @Operation(summary = "获取流程定义XML", description = "获取指定流程定义的BPMN XML内容")
    public ResponseEntity<String> getProcessDefinitionXml(
            @Parameter(description = "流程定义ID") @PathVariable String processDefinitionId) {
        String xml = flowableService.getProcessDefinitionXml(processDefinitionId);
        if (xml == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(xml);
    }

    /**
     * 部署流程定义
     *
     * @param processDefinitionKey 流程定义key
     * @param bpmnFilePath         BPMN文件路径
     * @return ResponseEntity<Deployment> 部署信息
     */
    @PostMapping("/deploy")
    @Operation(summary = "部署流程定义", description = "将BPMN流程定义文件部署到Flowable引擎")
    public ResponseEntity<Deployment> deployProcessDefinition(
            @Parameter(description = "流程定义key") @RequestParam String processDefinitionKey,
            @Parameter(description = "BPMN文件路径，如processes/leave-request.bpmn20.xml") @RequestParam String bpmnFilePath) {
        Deployment deployment = flowableService.deployProcessDefinition(processDefinitionKey, bpmnFilePath);
        return ResponseEntity.ok(deployment);
    }

    /**
     * 删除流程定义部署
     *
     * @param deploymentId 部署ID
     * @return ResponseEntity<Void> 无内容响应
     */
    @DeleteMapping("/deployments/{deploymentId}")
    @Operation(summary = "删除流程部署", description = "删除指定的流程定义部署，级联删除相关数据")
    public ResponseEntity<Void> deleteDeployment(
            @Parameter(description = "部署ID") @PathVariable String deploymentId) {
        flowableService.deleteDeployment(deploymentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取所有运行中的流程实例
     *
     * @return ResponseEntity<List<ProcessInstance>> 流程实例列表
     */
    @GetMapping("/process-instances")
    @Operation(summary = "获取运行中的流程实例", description = "获取所有正在运行的流程实例")
    public ResponseEntity<List<ProcessInstance>> getAllRunningProcessInstances() {
        List<ProcessInstance> instances = flowableService.getAllRunningProcessInstances();
        return ResponseEntity.ok(instances);
    }

    /**
     * 根据流程定义key获取运行中的流程实例
     *
     * @param processDefinitionKey 流程定义key
     * @return ResponseEntity<List<ProcessInstance>> 流程实例列表
     */
    @GetMapping("/process-instances/key/{processDefinitionKey}")
    @Operation(summary = "按流程定义获取流程实例", description = "根据流程定义key获取运行中的流程实例")
    public ResponseEntity<List<ProcessInstance>> getProcessInstancesByKey(
            @Parameter(description = "流程定义key") @PathVariable String processDefinitionKey) {
        List<ProcessInstance> instances = flowableService.getRunningProcessInstancesByKey(processDefinitionKey);
        return ResponseEntity.ok(instances);
    }

    /**
     * 获取流程实例详情
     *
     * @param processInstanceId 流程实例ID
     * @return ResponseEntity<ProcessInstance> 流程实例对象
     */
    @GetMapping("/process-instances/{processInstanceId}")
    @Operation(summary = "获取流程实例详情", description = "根据流程实例ID获取流程实例详情")
    public ResponseEntity<ProcessInstance> getProcessInstance(
            @Parameter(description = "流程实例ID") @PathVariable String processInstanceId) {
        ProcessInstance instance = flowableService.getProcessInstance(processInstanceId);
        if (instance == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(instance);
    }

    /**
     * 获取流程实例的流程变量
     *
     * @param processInstanceId 流程实例ID
     * @return ResponseEntity<Map<String, Object>> 流程变量Map
     */
    @GetMapping("/process-instances/{processInstanceId}/variables")
    @Operation(summary = "获取流程变量", description = "获取指定流程实例的所有流程变量")
    public ResponseEntity<Map<String, Object>> getProcessVariables(
            @Parameter(description = "流程实例ID") @PathVariable String processInstanceId) {
        Map<String, Object> variables = flowableService.getProcessVariables(processInstanceId);
        return ResponseEntity.ok(variables);
    }

    /**
     * 设置流程实例的流程变量
     *
     * @param processInstanceId 流程实例ID
     * @param variables         流程变量Map
     * @return ResponseEntity<Void> 无内容响应
     */
    @PutMapping("/process-instances/{processInstanceId}/variables")
    @Operation(summary = "设置流程变量", description = "为指定流程实例设置流程变量")
    public ResponseEntity<Void> setProcessVariables(
            @Parameter(description = "流程实例ID") @PathVariable String processInstanceId,
            @RequestBody Map<String, Object> variables) {
        flowableService.setProcessVariables(processInstanceId, variables);
        return ResponseEntity.noContent().build();
    }

    /**
     * 挂起流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return ResponseEntity<Void> 无内容响应
     */
    @PutMapping("/process-instances/{processInstanceId}/suspend")
    @Operation(summary = "挂起流程实例", description = "将指定的流程实例挂起，暂停执行")
    public ResponseEntity<Void> suspendProcessInstance(
            @Parameter(description = "流程实例ID") @PathVariable String processInstanceId) {
        flowableService.suspendProcessInstance(processInstanceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 激活流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return ResponseEntity<Void> 无内容响应
     */
    @PutMapping("/process-instances/{processInstanceId}/activate")
    @Operation(summary = "激活流程实例", description = "将挂起的流程实例激活，恢复执行")
    public ResponseEntity<Void> activateProcessInstance(
            @Parameter(description = "流程实例ID") @PathVariable String processInstanceId) {
        flowableService.activateProcessInstance(processInstanceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据流程实例ID获取待办任务列表
     *
     * @param processInstanceId 流程实例ID
     * @return ResponseEntity<List<Task>> 任务列表
     */
    @GetMapping("/tasks/process-instance/{processInstanceId}")
    @Operation(summary = "获取流程实例的任务", description = "根据流程实例ID获取相关的所有任务")
    public ResponseEntity<List<Task>> getTasksByProcessInstanceId(
            @Parameter(description = "流程实例ID") @PathVariable String processInstanceId) {
        List<Task> tasks = flowableService.getTasksByProcessInstanceId(processInstanceId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 根据任务负责人获取待办任务列表
     *
     * @param assignee 任务负责人
     * @return ResponseEntity<List<Task>> 任务列表
     */
    @GetMapping("/tasks/assignee/{assignee}")
    @Operation(summary = "获取个人待办任务", description = "根据任务负责人获取待办任务列表")
    public ResponseEntity<List<Task>> getTasksByAssignee(
            @Parameter(description = "任务负责人（用户ID）") @PathVariable String assignee) {
        List<Task> tasks = flowableService.getTasksByAssignee(assignee);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取任务详情
     *
     * @param taskId 任务ID
     * @return ResponseEntity<Task> 任务对象
     */
    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "获取任务详情", description = "根据任务ID获取任务详情")
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "任务ID") @PathVariable String taskId) {
        Task task = flowableService.getTaskById(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(task);
    }

    /**
     * 认领任务
     *
     * @param taskId   任务ID
     * @param assignee 认领人
     * @return ResponseEntity<Void> 无内容响应
     */
    @PutMapping("/tasks/{taskId}/claim")
    @Operation(summary = "认领任务", description = "将任务分配给指定的用户")
    public ResponseEntity<Void> claimTask(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @Parameter(description = "认领人（用户ID）") @RequestParam String assignee) {
        flowableService.claimTask(taskId, assignee);
        return ResponseEntity.noContent().build();
    }

    /**
     * 取消认领任务
     *
     * @param taskId 任务ID
     * @return ResponseEntity<Void> 无内容响应
     */
    @PutMapping("/tasks/{taskId}/unclaim")
    @Operation(summary = "取消认领任务", description = "取消任务认领，使其重新变为待认领状态")
    public ResponseEntity<Void> unclaimTask(
            @Parameter(description = "任务ID") @PathVariable String taskId) {
        flowableService.unclaimTask(taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 完成任务
     *
     * @param taskId    任务ID
     * @param variables 任务完成时的变量
     * @return ResponseEntity<Void> 无内容响应
     */
    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "完成任务", description = "完成指定的任务，并设置任务变量")
    public ResponseEntity<Void> completeTask(
            @Parameter(description = "任务ID") @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        flowableService.completeTask(taskId, variables);
        return ResponseEntity.noContent().build();
    }

    /**
     * 启动请假申请流程
     *
     * @param request 请求参数
     * @return ResponseEntity<ProcessInstance> 流程实例对象
     */
    @PostMapping("/start/leave-request")
    @Operation(summary = "启动请假申请流程", description = "启动一个新的请假申请流程")
    public ResponseEntity<ProcessInstance> startLeaveRequestProcess(@RequestBody LeaveRequestRequest request) {
        ProcessInstance instance = flowableService.startLeaveRequestProcess(
                request.employeeName(),
                request.employeeId(),
                request.leaveType(),
                request.startDate(),
                request.endDate(),
                request.days(),
                request.reason(),
                request.leader(),
                request.manager(),
                request.hr(),
                request.businessKey()
        );
        return ResponseEntity.ok(instance);
    }

    /**
     * 启动费用报销流程
     *
     * @param request 请求参数
     * @return ResponseEntity<ProcessInstance> 流程实例对象
     */
    @PostMapping("/start/expense-reimbursement")
    @Operation(summary = "启动费用报销流程", description = "启动一个新的费用报销流程")
    public ResponseEntity<ProcessInstance> startExpenseReimbursementProcess(@RequestBody ExpenseReimbursementRequest request) {
        ProcessInstance instance = flowableService.startExpenseReimbursementProcess(
                request.applicantName(),
                request.applicantId(),
                request.amount(),
                request.expenseType(),
                request.description(),
                request.receiptCount(),
                request.finance(),
                request.manager(),
                request.businessKey()
        );
        return ResponseEntity.ok(instance);
    }

    /**
     * 通用启动流程实例接口
     *
     * @param request 请求参数
     * @return ResponseEntity<ProcessInstance> 流程实例对象
     */
    @PostMapping("/start")
    @Operation(summary = "启动流程实例", description = "根据流程定义key启动一个新的流程实例")
    public ResponseEntity<ProcessInstance> startProcessInstance(@RequestBody StartProcessRequest request) {
        ProcessInstance instance = flowableService.startProcessInstance(
                request.processDefinitionKey(),
                request.businessKey(),
                request.variables()
        );
        return ResponseEntity.ok(instance);
    }

    /**
     * 获取历史流程实例列表
     *
     * @return ResponseEntity<List<Map<String, Object>>> 历史流程实例列表
     */
    @GetMapping("/history")
    @Operation(summary = "获取历史流程实例", description = "获取所有已结束的流程实例历史记录")
    public ResponseEntity<List<Map<String, Object>>> getHistoricProcessInstances() {
        List<Map<String, Object>> result = flowableService.getHistoricProcessInstances().stream()
                .map(instance -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("processInstanceId", instance.getId());
                    map.put("processDefinitionId", instance.getProcessDefinitionId());
                    map.put("processDefinitionKey", instance.getProcessDefinitionKey());
                    map.put("businessKey", instance.getBusinessKey());
                    map.put("startTime", instance.getStartTime());
                    map.put("endTime", instance.getEndTime());
                    map.put("durationInMillis", instance.getDurationInMillis());
                    map.put("deleteReason", instance.getDeleteReason());
                    return map;
                })
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * 请假申请流程请求参数
     */
    public record LeaveRequestRequest(
            String employeeName,
            String employeeId,
            String leaveType,
            String startDate,
            String endDate,
            Long days,
            String reason,
            String leader,
            String manager,
            String hr,
            String businessKey
    ) {}

    /**
     * 费用报销流程请求参数
     */
    public record ExpenseReimbursementRequest(
            String applicantName,
            String applicantId,
            Double amount,
            String expenseType,
            String description,
            Long receiptCount,
            String finance,
            String manager,
            String businessKey
    ) {}

    /**
     * 通用启动流程请求参数
     */
    public record StartProcessRequest(
            String processDefinitionKey,
            String businessKey,
            Map<String, Object> variables
    ) {}
}