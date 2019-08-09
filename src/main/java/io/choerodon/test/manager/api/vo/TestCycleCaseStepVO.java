package io.choerodon.test.manager.api.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.agile.api.vo.IssueLinkDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

public class TestCycleCaseStepVO {

    @ApiModelProperty(value = "执行步骤ID")
    private Long executeStepId;

    @ApiModelProperty(value = "测试执行ID")
    private Long executeId;

    @ApiModelProperty(value = "测试步骤ID")
    private Long stepId;

    @ApiModelProperty(value = "描述")
    private String comment;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "循环ID")
    private Long cycleId;

    @ApiModelProperty(value = "步骤状态ID")
    private Long stepStatus;

    @ApiModelProperty(value = "测试步骤")
    private String testStep;

    @ApiModelProperty(value = "测试数据")
    private String testData;

    @ApiModelProperty(value = "预期结果")
    private String expectedResult;

    @ApiModelProperty(value = "关联issueDTOList")
    private List<IssueLinkDTO> issueLinkDTOS;

    @ApiModelProperty(value = "步骤附件DTOList")
    private List<TestCycleCaseAttachmentRelVO> stepAttachment;

    @ApiModelProperty(value = "用例issueID")
    private Long issueId;

    @ApiModelProperty(value = "缺陷DTOList")
    private List<TestCycleCaseDefectRelVO> defects;

    @ApiModelProperty(value = "issue详情DTO")
    private IssueInfosVO issueInfosVO;

    @ApiModelProperty(value = "状态名")
    private String statusName;

    @ApiModelProperty(value = "循环名称")
    private String cycleName;

    public IssueInfosVO getIssueInfosVO() {
        return issueInfosVO;
    }

    public void setIssueInfosVO(IssueInfosVO issueInfosVO) {
        this.issueInfosVO = issueInfosVO;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public String getTestStep() {
        return testStep;
    }

    public void setTestStep(String testStep) {
        this.testStep = testStep;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public Long getExecuteStepId() {
        return executeStepId;
    }

    public void setExecuteStepId(Long executeStepId) {
        this.executeStepId = executeStepId;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public List<IssueLinkDTO> getIssueLinkDTOS() {
        return issueLinkDTOS;
    }

    public void setIssueLinkDTOS(List<IssueLinkDTO> issueLinkDTOS) {
        this.issueLinkDTOS = issueLinkDTOS;
    }

    public void addIssueLinkDTOS(IssueLinkDTO issueLinkDTO) {
        if (this.issueLinkDTOS == null) {
            this.issueLinkDTOS = new ArrayList<>();
        }
        this.issueLinkDTOS.add(issueLinkDTO);
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


    public void setStepAttachment(List<TestCycleCaseAttachmentRelVO> stepAttachment) {
        this.stepAttachment = stepAttachment;
    }

    public void setDefects(List<TestCycleCaseDefectRelVO> defects) {
        this.defects = defects;
    }

    public List<TestCycleCaseAttachmentRelVO> getStepAttachment() {
        return stepAttachment;
    }

    public List<TestCycleCaseDefectRelVO> getDefects() {
        return defects;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getStepStatus() {
        return stepStatus;
    }

    public void setStepStatus(Long stepStatus) {
        this.stepStatus = stepStatus;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }
}
