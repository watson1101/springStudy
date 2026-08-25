package msdemo.hong.com.flowableservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flowable流程服务类
 * <p>
 * 封装Flowable引擎的核心操作，包括流程定义管理、流程实例管理、任务管理等
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FlowableService {

    private final ProcessEngine processEngine;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    /**
     * 部署流程定义
     * <p>
     * 将BPMN流程定义文件部署到Flowable引擎中，使其可以被启动执行
     * </p>
     *
     * @param processDefinitionKey 流程定义的key（通常是BPMN文件中process元素的id）
     * @param bpmnFilePath         BPMN文件在classpath中的路径
     * @return Deployment 部署信息对象
     */
    @Transactional
    public Deployment deployProcessDefinition(String processDefinitionKey, String bpmnFilePath) {
        log.info("开始部署流程定义: {}", processDefinitionKey);
        Deployment deployment = repositoryService.createDeployment()
                .name(processDefinitionKey + "流程部署")
                .addClasspathResource(bpmnFilePath)
                .deploy();
        log.info("流程定义部署成功, 部署ID: {}, 流程定义ID: {}",
                deployment.getId(), deployment.getName());
        return deployment;
    }

    /**
     * 获取所有已部署的流程定义列表
     * <p>
     * 查询Flowable引擎中所有已部署的流程定义
     * </p>
     *
     * @return List<ProcessDefinition> 流程定义列表
     */
    public List<ProcessDefinition> getAllProcessDefinitions() {
        return repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionName()
                .asc()
                .list();
    }

    /**
     * 根据流程定义key获取流程定义
     *
     * @param processDefinitionKey 流程定义key
     * @return ProcessDefinition 流程定义对象，如果不存在则返回null
     */
    public ProcessDefinition getProcessDefinitionByKey(String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult();
    }

    /**
     * 删除流程定义部署
     * <p>
     * 删除指定的流程定义部署，级联删除相关的流程实例和历史数据
     * </p>
     *
     * @param deploymentId 部署ID
     */
    @Transactional
    public void deleteDeployment(String deploymentId) {
        log.info("开始删除流程部署: {}", deploymentId);
        repositoryService.deleteDeployment(deploymentId, true);
        log.info("流程部署删除成功: {}", deploymentId);
    }

    /**
     * 启动流程实例
     * <p>
     * 根据流程定义key启动一个新的流程实例，并设置流程变量
     * </p>
     *
     * @param processDefinitionKey 流程定义key
     * @param businessKey          业务主键（用于关联业务数据）
     * @param variables            流程变量（Map格式，包含流程中需要的各种参数）
     * @return ProcessInstance 流程实例对象
     */
    @Transactional
    public ProcessInstance startProcessInstance(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        log.info("开始启动流程实例: {}, 业务主键: {}", processDefinitionKey, businessKey);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        log.info("流程实例启动成功, 流程实例ID: {}, 流程定义ID: {}",
                processInstance.getId(), processInstance.getProcessDefinitionId());
        return processInstance;
    }

    /**
     * 获取流程实例
     *
     * @param processInstanceId 流程实例ID
     * @return ProcessInstance 流程实例对象，如果不存在则返回null
     */
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
    }

    /**
     * 获取所有运行中的流程实例
     *
     * @return List<ProcessInstance> 流程实例列表
     */
    public List<ProcessInstance> getAllRunningProcessInstances() {
        return runtimeService.createProcessInstanceQuery()
                .orderByProcessInstanceId()
                .desc()
                .list();
    }

    /**
     * 根据流程定义key获取运行中的流程实例
     *
     * @param processDefinitionKey 流程定义key
     * @return List<ProcessInstance> 流程实例列表
     */
    public List<ProcessInstance> getRunningProcessInstancesByKey(String processDefinitionKey) {
        return runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .orderByProcessInstanceId()
                .desc()
                .list();
    }

    /**
     * 挂起流程实例
     * <p>
     * 将指定的流程实例挂起，挂起后流程实例不再继续执行
     * </p>
     *
     * @param processInstanceId 流程实例ID
     */
    @Transactional
    public void suspendProcessInstance(String processInstanceId) {
        log.info("开始挂起流程实例: {}", processInstanceId);
        runtimeService.suspendProcessInstanceById(processInstanceId);
        log.info("流程实例挂起成功: {}", processInstanceId);
    }

    /**
     * 激活流程实例
     * <p>
     * 将挂起的流程实例激活，使其可以继续执行
     * </p>
     *
     * @param processInstanceId 流程实例ID
     */
    @Transactional
    public void activateProcessInstance(String processInstanceId) {
        log.info("开始激活流程实例: {}", processInstanceId);
        runtimeService.activateProcessInstanceById(processInstanceId);
        log.info("流程实例激活成功: {}", processInstanceId);
    }

    /**
     * 根据流程实例ID获取待办任务列表
     *
     * @param processInstanceId 流程实例ID
     * @return List<Task> 任务列表
     */
    public List<Task> getTasksByProcessInstanceId(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime()
                .desc()
                .list();
    }

    /**
     * 根据任务负责人获取待办任务列表
     *
     * @param assignee 任务负责人（用户ID）
     * @return List<Task> 任务列表
     */
    public List<Task> getTasksByAssignee(String assignee) {
        return taskService.createTaskQuery()
                .taskAssignee(assignee)
                .orderByTaskCreateTime()
                .desc()
                .list();
    }

    /**
     * 根据任务ID获取任务详情
     *
     * @param taskId 任务ID
     * @return Task 任务对象，如果不存在则返回null
     */
    public Task getTaskById(String taskId) {
        return taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
    }

    /**
     * 完成任务
     * <p>
     * 完成指定的任务，并设置任务完成时的变量（用于流程走向决策）
     * </p>
     *
     * @param taskId    任务ID
     * @param variables 任务完成时的变量（可能影响流程走向）
     */
    @Transactional
    public void completeTask(String taskId, Map<String, Object> variables) {
        log.info("开始完成任务: {}", taskId);
        taskService.complete(taskId, variables);
        log.info("任务完成成功: {}", taskId);
    }

    /**
     * 认领任务
     * <p>
     * 将任务分配给指定的用户，使其成为任务的负责人
     * </p>
     *
     * @param taskId   任务ID
     * @param assignee 认领人（用户ID）
     */
    @Transactional
    public void claimTask(String taskId, String assignee) {
        log.info("开始认领任务: {}, 认领人: {}", taskId, assignee);
        taskService.claim(taskId, assignee);
        log.info("任务认领成功: {}, 认领人: {}", taskId, assignee);
    }

    /**
     * 取消认领任务
     * <p>
     * 将任务的负责人设置为空，使其重新变为待认领状态
     * </p>
     *
     * @param taskId 任务ID
     */
    @Transactional
    public void unclaimTask(String taskId) {
        log.info("开始取消认领任务: {}", taskId);
        taskService.unclaim(taskId);
        log.info("任务取消认领成功: {}", taskId);
    }

    /**
     * 获取流程实例的流程变量
     *
     * @param processInstanceId 流程实例ID
     * @return Map<String, Object> 流程变量Map
     */
    public Map<String, Object> getProcessVariables(String processInstanceId) {
        return runtimeService.getVariables(processInstanceId);
    }

    /**
     * 设置流程实例的流程变量
     *
     * @param processInstanceId 流程实例ID
     * @param variables         流程变量Map
     */
    @Transactional
    public void setProcessVariables(String processInstanceId, Map<String, Object> variables) {
        log.info("开始设置流程变量: {}", processInstanceId);
        runtimeService.setVariables(processInstanceId, variables);
        log.info("流程变量设置成功: {}", processInstanceId);
    }

    /**
     * 获取历史流程实例列表
     * <p>
     * 查询已经结束的流程实例历史记录
     * </p>
     *
     * @return List<HistoricProcessInstance> 历史流程实例列表
     */
    public List<HistoricProcessInstance> getHistoricProcessInstances() {
        return historyService.createHistoricProcessInstanceQuery()
                .finished()
                .orderByProcessInstanceEndTime()
                .desc()
                .list();
    }

    /**
     * 根据流程定义key获取历史流程实例
     *
     * @param processDefinitionKey 流程定义key
     * @return List<HistoricProcessInstance> 历史流程实例列表
     */
    public List<HistoricProcessInstance> getHistoricProcessInstancesByKey(String processDefinitionKey) {
        return historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .finished()
                .orderByProcessInstanceEndTime()
                .desc()
                .list();
    }

    /**
     * 获取流程定义的XML内容
     *
     * @param processDefinitionId 流程定义ID
     * @return String BPMN XML字符串
     */
    public String getProcessDefinitionXml(String processDefinitionId) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        if (processDefinition == null) {
            return null;
        }
        try (InputStream is = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                processDefinition.getResourceName())) {
            if (is == null) {
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取流程定义XML失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 启动请假申请流程（封装方法）
     * <p>
     * 封装请假申请流程的启动逻辑，设置必要的流程变量
     * </p>
     *
     * @param employeeName 员工姓名
     * @param employeeId   员工ID
     * @param leaveType    请假类型（annual/sick/personal/maternity）
     * @param startDate    开始日期
     * @param endDate      结束日期
     * @param days         请假天数
     * @param reason       请假原因
     * @param leader       直属领导
     * @param manager      部门经理
     * @param hr           HR人员
     * @param businessKey  业务主键
     * @return ProcessInstance 流程实例对象
     */
    @Transactional
    public ProcessInstance startLeaveRequestProcess(String employeeName, String employeeId,
                                                    String leaveType, String startDate, String endDate,
                                                    Long days, String reason, String leader,
                                                    String manager, String hr, String businessKey) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employeeName", employeeName);
        variables.put("employeeId", employeeId);
        variables.put("leaveType", leaveType);
        variables.put("startDate", startDate);
        variables.put("endDate", endDate);
        variables.put("days", days);
        variables.put("reason", reason);
        variables.put("leader", leader);
        variables.put("manager", manager);
        variables.put("hr", hr);
        return startProcessInstance("leaveRequest", businessKey, variables);
    }

    /**
     * 启动费用报销流程（封装方法）
     * <p>
     * 封装费用报销流程的启动逻辑，设置必要的流程变量
     * </p>
     *
     * @param applicantName 申请人姓名
     * @param applicantId   申请人ID
     * @param amount        报销金额
     * @param expenseType   费用类型（travel/entertainment/office/other）
     * @param description   费用说明
     * @param receiptCount  票据数量
     * @param finance       财务人员
     * @param manager       部门经理
     * @param businessKey   业务主键
     * @return ProcessInstance 流程实例对象
     */
    @Transactional
    public ProcessInstance startExpenseReimbursementProcess(String applicantName, String applicantId,
                                                            Double amount, String expenseType,
                                                            String description, Long receiptCount,
                                                            String finance, String manager,
                                                            String businessKey) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicantName", applicantName);
        variables.put("applicantId", applicantId);
        variables.put("amount", amount);
        variables.put("expenseType", expenseType);
        variables.put("description", description);
        variables.put("receiptCount", receiptCount);
        variables.put("finance", finance);
        variables.put("manager", manager);
        return startProcessInstance("expenseReimbursement", businessKey, variables);
    }
}