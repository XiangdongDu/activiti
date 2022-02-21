package boot.spring.service.impl;

import boot.spring.mapper.LeaveApplyMapper;
import boot.spring.mapper.ParamItemMapper;
import boot.spring.ocr.BaiduOCR;
import boot.spring.pagemodel.MSG;
import boot.spring.po.LeaveApply;
import boot.spring.po.ParamItem;
import boot.spring.service.LeaveService;
import boot.spring.util.AppException;
import boot.spring.util.DateUtils;
import boot.spring.util.excel.ExportExcelWrapper;
import boot.spring.util.mail.BeetHelper;
import boot.spring.util.mail.MailHelper;
import boot.spring.util.mail.MapHolder;
import com.github.pagehelper.PageHelper;
import groovy.util.logging.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.HistoricDetailVariableInstanceUpdateEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 5000)
@Service
@Slf4j
public class LeaveServiceImpl implements LeaveService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);


    @Autowired
    HistoryService histiryservice;

    @Autowired
    LeaveApplyMapper leavemapper;
    @Autowired
    IdentityService identityservice;
    @Autowired
    RuntimeService runtimeservice;
    @Autowired
    TaskService taskservice;

    @Autowired
    LeaveService leaveservice;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    BeetHelper beetHelper;

    @Autowired
    RuntimeService runservice;

    @Autowired
    ParamItemMapper paramItemMapper;

    @Override
    public MSG uploadJKMFile(MultipartFile file, String username) throws AppException {
        try {
            if (file.isEmpty()) {
                return new MSG("文件为空");
            }
            // 获取文件名
            String fileName = file.getOriginalFilename();
            logger.info("上传的文件名为：" + fileName);
            // 获取文件的后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            logger.info("上传的后缀名为：" + suffixName);
            // 创建返回值
            boolean flag = false;
            if (suffixName.toLowerCase().endsWith(".jpg")
                    || suffixName.toLowerCase().endsWith(".png")
                    || suffixName.toLowerCase().endsWith(".bmp")
                    || suffixName.toLowerCase().endsWith(".jpeg")) {
                flag = true;
            }
            if (flag) {
                //获取当前项目的相对路径的根目录
                String rootPath = System.getProperty("user.dir");
                // 文件上传后的路径
                String filePath = rootPath + "/src/main/webapp/uploadfiles/" + username + "/" + fileName;
                logger.info("上传的文件名路径===>{}", filePath);
                File dest = new File(filePath);
                // 检测是否存在目录
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();
                }
                file.transferTo(dest);
                // 高精度版本-调用接口  参数为本地图片路径请求格式支持：PNG、JPG、JPEG、BMP
                JSONObject accurateBasic = BaiduOCR.accurateBasic(filePath);
                //校验健康码是否正常
                String result = BaiduOCR.checkJKM(accurateBasic);
//                String result = "异常";
                logger.info("健康码检验结果=========>{}", result);
                if (result.equals("正常")) {
                    return new MSG("上传文件成功！");
                } else if (result.equals("异常")) {
                    String mail = leaveservice.sendJKMMail(username);
                    return new MSG("上传文件成功！" + mail);
                } else {
                    return new MSG(result);
                }
            } else {
                return new MSG("图片格式不符合！（支持PNG、JPG、JPEG、BMP）请重新上传！");
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            logger.error("上传文件失败{}", e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("上传文件失败,{}", e);
        }
        return new MSG("上传文件失败！");
    }


    /**
     * 健康码邮件
     *
     * @param username
     */
    @Override
    public String sendJKMMail(String username) {
        try {
            //收件人
            String[] toAdder = getToAdder();
            //抄送人
            String[] cc = getToAdderCC();
            Map<String, Object> map = MapHolder.create().put("name", username).get();
            mailHelper.syncSendMailAddFile(toAdder, cc, "健康码警报-【" + username + "】健康码异常", beetHelper.getContent("/templates/mail/checkJKM.txt", map), null, null);
            logger.info("健康码邮件发送成功!");
            return "健康码邮件发送成功!";
        } catch (Exception e) {
            logger.error("健康码邮件发送失败!{}", e);
            e.printStackTrace();
        }
        return "健康码邮件发送失败!";
    }

    /**
     * 考勤邮件
     *
     * @param username
     */
    @Override
    public void sendWorkMail(String username) {
        try {
            //收件人
            String[] toAdder = getToAdder();
            //抄送人
            String[] cc = getToAdderCC();
            List<String> list = new ArrayList<>();
            list.add("a");
            list.add("b");
            list.add("c");
            List<List> listA = new ArrayList<>();
            listA.add(list);
            ExportExcelWrapper<Object> util = new ExportExcelWrapper<>();
            ByteArrayOutputStream baops = util.exportExcel(list);
            Map<String, Object> map = MapHolder.create().put("nodes", listA).put("env", "考勤").put("names", "员工考勤").get();
            mailHelper.syncSendMailAddFile(toAdder, cc, "考勤邮件-" + username + "考勤", beetHelper.getContent("/templates/mail/checkWork.txt", map), "员工考勤.xlsx", baops);
            logger.info("考勤邮件发送成功");
        } catch (Exception e) {
            logger.error("考勤邮件发送失败{}", e);
            e.printStackTrace();
        }
    }

    //收件人
    private String[] getToAdder() {
        String[] toAdder = null;
        List<ParamItem> paramItem = paramItemMapper.getParamItem();
        for (int i = 0; i < paramItem.size(); i++) {
            if (paramItem.get(i).getItem_name().equals("toAdder") &&
                    !paramItem.get(i).getItem_attr().trim().equals("")) {
                //收件人
                toAdder = paramItem.get(i).getItem_attr().split(";");
            }
        }
        return toAdder;
    }

    //抄送人
    private String[] getToAdderCC() {
        //抄送人
        String[] cc = null;
        List<ParamItem> paramItem = paramItemMapper.getParamItem();
        for (int i = 0; i < paramItem.size(); i++) {
            if (paramItem.get(i).getItem_name().equals("toAdder_CC") &&
                    !paramItem.get(i).getItem_attr().trim().equals("")) {
                //抄送人
                cc = paramItem.get(i).getItem_attr().split(";");
            }
        }
        return cc;
    }

    public ProcessInstance startWorkflow(LeaveApply apply, String userid, Map<String, Object> variables) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        apply.setApply_time(sdf.format(new Date()));
        apply.setUser_id(userid);
        leavemapper.save(apply);
        String businesskey = String.valueOf(apply.getId());//使用leaveapply表的主键作为businesskey,连接业务数据和流程数据
        identityservice.setAuthenticatedUserId(userid);
        int days = DateUtils.getDutyDays(apply.getStart_time(), apply.getEnd_time());
        ProcessInstance instance = null;
        logger.info("根据请假天数：" + days + "天，选择对应审批流程。");
        if (days == 1) {//部门领导审批
            instance = runtimeservice.startProcessInstanceByKey("leaveDept", businesskey, variables);

        } else if (days == 2) {//HR审批
            instance = runtimeservice.startProcessInstanceByKey("leave", businesskey, variables);

        } else if (days > 2) {//总经理审批
            instance = runtimeservice.startProcessInstanceByKey("leavePresident", businesskey, variables);
        } else {

            return null;
        }
        String instanceid = instance.getId();
        apply.setProcess_instance_id(instanceid);
        leavemapper.updateByPrimaryKey(apply);
        return instance;
    }

    /**
     * 使用业务号查出业务数据一起返回
     *
     * @param tasks
     * @return
     */
    public List<LeaveApply> getPageTask(List<Task> tasks) {
        List<LeaveApply> results = new ArrayList<>();
        for (Task task : tasks) {
            String instanceid = task.getProcessInstanceId();
            ProcessInstance ins = runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
            String businesskey = ins.getBusinessKey();
            // 使用业务号查出业务数据一起返回
            LeaveApply a = leavemapper.getLeaveApply(Integer.parseInt(businesskey));
            a.setTask(task);
            results.add(a);
        }
        return results;
    }

    /**
     * 部门领导审批
     *
     * @param username
     * @param firstrow
     * @param rowcount
     * @return
     */
    public List<LeaveApply> getpagedepttask(String username, int firstrow, int rowcount) {
        // 使用任务候选人查询待办列表
        List<Task> tasks = taskservice.createTaskQuery().taskAssignee(username).taskName("部门领导审批").orderByTaskCreateTime().desc().listPage(firstrow, rowcount);
        List<LeaveApply> results = getPageTask(tasks);
        return results;
    }

    public int getalldepttask(String username) {
        List<Task> tasks = taskservice.createTaskQuery().taskAssignee(username).taskName("部门领导审批").list();
        return tasks.size();
    }

    /**
     * 获取员工考勤列表
     */
    public List<LeaveApply> employeeAttendanceList(int current, int rowCount) {
        List<LeaveApply> result = new LinkedList<>();
        try {
            List<LeaveApply> list = leavemapper.getAllLeave();
            for (LeaveApply apply : list) {
                HistoricDetailVariableInstanceUpdateEntity his = (HistoricDetailVariableInstanceUpdateEntity) histiryservice.createHistoricDetailQuery()
                        .processInstanceId(apply.getProcess_instance_id()).orderByTime().desc().list().get(0);
                String isLeave = his.getTextValue();
                if (!isLeave.equals("false"))//排除取消休假
                    result.add(apply);
            }
        } catch (Exception e) {
            logger.error("获取员工考勤列表异常...{}", e);
        }
        return result;
    }


    public LeaveApply getleave(int id) {
        LeaveApply leave = leavemapper.getLeaveApply(id);
        return leave;
    }

    /**
     * 人事审批
     *
     * @param username
     * @param firstrow
     * @param rowcount
     * @return
     */
    public List<LeaveApply> getpagehrtask(String username, int firstrow, int rowcount) {
        List<Task> tasks = taskservice.createTaskQuery().taskAssignee(username).taskName("人事审批").orderByTaskCreateTime().desc().listPage(firstrow, rowcount);
        List<LeaveApply> results = getPageTask(tasks);
        return results;
    }


    public int getallhrtask(String username) {
        List<Task> tasks = taskservice.createTaskQuery().taskAssignee(username).taskName("人事审批").list();
        return tasks.size();
    }


    /**
     * 总经理审批
     *
     * @param username
     * @param firstrow
     * @param rowcount
     * @return
     */
    public List<LeaveApply> getPagePresidetTask(String username, int firstrow, int rowcount) {
        List<Task> tasks = taskservice.createTaskQuery().taskAssignee(username).taskName("总经理审批").orderByTaskCreateTime().desc().listPage(firstrow, rowcount);
        List<LeaveApply> results = getPageTask(tasks);
        return results;
    }


    public int getAllPresidetTask(String username) {
        List<Task> tasks = taskservice.createTaskQuery().taskAssignee(username).taskName("总经理审批").list();
        return tasks.size();
    }


    public List<LeaveApply> getpageXJtask(String userid, int firstrow, int rowcount) {
        List<Task> tasks = taskservice.createTaskQuery().taskCandidateOrAssigned(userid).taskName("销假").orderByTaskCreateTime().desc().listPage(firstrow, rowcount);
        List<LeaveApply> results = getPageTask(tasks);
        return results;
    }

    public int getallXJtask(String userid) {
        List<Task> tasks = taskservice.createTaskQuery().taskCandidateOrAssigned(userid).taskName("销假").list();
        return tasks.size();
    }

    public List<LeaveApply> getpageupdateapplytask(String userid, int firstrow, int rowcount) {
        List<Task> tasks = taskservice.createTaskQuery().taskCandidateOrAssigned(userid).taskName("调整申请").orderByTaskCreateTime().desc().listPage(firstrow, rowcount);
        List<LeaveApply> results = getPageTask(tasks);
        return results;
    }

    public int getallupdateapplytask(String userid) {
        List<Task> tasks = taskservice.createTaskQuery().taskCandidateOrAssigned(userid).taskName("调整申请").list();
        return tasks.size();
    }

    public void completereportback(String taskid, String realstart_time, String realend_time) {
        Task task = taskservice.createTaskQuery().taskId(taskid).singleResult();
        String instanceid = task.getProcessInstanceId();
        ProcessInstance ins = runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
        String businesskey = ins.getBusinessKey();
        LeaveApply a = leavemapper.getLeaveApply(Integer.parseInt(businesskey));
        a.setReality_start_time(realstart_time);
        a.setReality_end_time(realend_time);
        leavemapper.updateByPrimaryKey(a);
        taskservice.complete(taskid);
    }

    public void updatecomplete(String taskid, LeaveApply leave, String reapply) {
        Task task = taskservice.createTaskQuery().taskId(taskid).singleResult();
        String instanceid = task.getProcessInstanceId();
        ProcessInstance ins = runtimeservice.createProcessInstanceQuery().processInstanceId(instanceid).singleResult();
        String businesskey = ins.getBusinessKey();
        LeaveApply a = leavemapper.getLeaveApply(Integer.parseInt(businesskey));
        a.setLeave_type(leave.getLeave_type());
        a.setStart_time(leave.getStart_time());
        a.setEnd_time(leave.getEnd_time());
        a.setReason(leave.getReason());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("reapply", reapply);
        if (reapply.equals("true")) {
            leavemapper.updateByPrimaryKey(a);
            taskservice.complete(taskid, variables);
        } else
            taskservice.complete(taskid, variables);
    }

    public List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {

        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size(); i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());// 得 到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
            if ((i + 1) >= historicActivityInstances.size()) {
                break;
            }
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId());// 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);// 后续第二个节点
                if (activityImpl1.getStartTime().equals(
                        activityImpl2.getStartTime())) {// 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {// 有不相同跳出循环
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl
                    .getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) {// 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }

    @Override
    public List<LeaveApply> getPageByApplyer(String username, int current, int rowCount) {
        PageHelper.startPage(current, rowCount);
        List<LeaveApply> list = leavemapper.listLeaveApplyByApplyer(username);
        return list;
    }

    @Override
    public int getAllByApplyer(String username) {
        return leavemapper.listLeaveApplyByApplyer(username).size();
    }
}
