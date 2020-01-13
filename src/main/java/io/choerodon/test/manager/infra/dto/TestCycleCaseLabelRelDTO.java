package io.choerodon.test.manager.infra.dto;

import javax.persistence.Table;
import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/27
 */
@Table(name = "test_cycle_case_label_rel")
public class TestCycleCaseLabelRelDTO extends BaseDTO {
    private Long executeId;
    private Long caseId;
    private Long labelId;
    private Long projectId;

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
