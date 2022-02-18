package boot.spring.service;

import java.util.List;
import java.util.Map;
import boot.spring.pagemodel.MSG;
import boot.spring.util.AppException;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import boot.spring.po.LeaveApply;
import org.springframework.web.multipart.MultipartFile;


public interface LeaveService {

    MSG uploadJKMFile(MultipartFile file, String username) throws AppException;

    void sendWorkMail(String username);

    String sendJKMMail(String username) throws AppException;

    ProcessInstance startWorkflow(LeaveApply apply, String userid, Map<String, Object> variables);

    List<LeaveApply> getpagedepttask(String userid, int firstrow, int rowcount);

    List<LeaveApply> employeeAttendanceList( int current, int rowCount);

    int getalldepttask(String userid);

    LeaveApply getleave(int id);

    List<LeaveApply> getpagehrtask(String username, int firstrow, int rowcount);

    int getallhrtask(String username);

    List<LeaveApply> getPagePresidetTask(String username, int firstrow, int rowcount);

    int getAllPresidetTask(String username);

    List<LeaveApply> getpageXJtask(String userid, int firstrow, int rowcount);

    int getallXJtask(String userid);

    List<LeaveApply> getpageupdateapplytask(String userid, int firstrow, int rowcount);

    int getallupdateapplytask(String userid);

    void completereportback(String taskid, String realstart_time, String realend_time);

    void updatecomplete(String taskid, LeaveApply leave, String reappply);

    List<String> getHighLightedFlows(ProcessDefinitionEntity deployedProcessDefinition, List<HistoricActivityInstance> historicActivityInstances);

    List<LeaveApply> getPageByApplyer(String username, int current, int rowCount);

    int getAllByApplyer(String username);
}