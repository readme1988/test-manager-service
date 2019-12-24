package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.ExcelServiceHandler;
import io.choerodon.test.manager.app.service.TestCycleCaseService;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle/case")
public class TestCycleCaseController {

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除测试循环用例")
    @DeleteMapping
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                 Long cycleCaseId) {
        testCycleCaseService.delete(cycleCaseId, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private ExcelServiceHandler excelServiceHandler;

    @Autowired
    public TestCycleCaseController(ExcelServiceHandler excelServiceHandler) {
        this.excelServiceHandler = excelServiceHandler;
    }

    public void setExcelServiceHandler(ExcelServiceHandler excelServiceHandler) {
        this.excelServiceHandler = excelServiceHandler;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("excel")
    @GetMapping("/download/excel/{cycleId}")
    public ResponseEntity downLoad(@PathVariable(name = "project_id") Long projectId,
                                   @PathVariable(name = "cycleId") Long cycleId,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestParam Long organizationId) {
        excelServiceHandler.exportCycleCaseInOneCycle(cycleId, projectId, request, response, organizationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询测试执行详情")
    @PostMapping("/{executeId}/info")
    public ResponseEntity<TestCycleCaseInfoVO> queryCaseInfo(@PathVariable("project_id") Long projectId,
                                                             @RequestParam(name = "cycle_id") Long cycleId,
                                                             @RequestParam(name = "plan_id") Long planId,
                                                             @RequestBody(required = false) SearchDTO searchDTO,
                                                             @PathVariable(name = "executeId", required= true) Long executeId) {
        return new ResponseEntity<>(testCycleCaseService.queryCycleCaseInfo(executeId,projectId,planId,cycleId,searchDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询计划下的执行状态总览")
    @GetMapping("/query/status")
    public ResponseEntity<ExecutionStatusVO> queryExecutionStatus(@PathVariable(name = "project_id") Long projectId,
                                                                  @ApiParam(value = "plan_id", required = false)
                                                                  @RequestParam(name = "plan_id") Long planId,
                                                                  @ApiParam(value = "cycle_id", required = false)
                                                                  @RequestParam(name = "cycle_id") Long cycleId) {
        return Optional.ofNullable(testCycleCaseService.queryExecuteStatus(projectId, planId, cycleId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.plan.status.query"));

    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新测试执行和其对应步骤")
    @PutMapping("/case_step")
    public ResponseEntity updateCaseAndStep(@PathVariable(name = "project_id") Long projectId,
                                 @RequestBody TestCycleCaseUpdateVO testCycleCaseUpdateVO,
                                            @RequestParam(name = "isAsync",required = true,defaultValue = "false") Boolean isAsync) {
        testCycleCaseService.updateCaseAndStep(projectId,testCycleCaseUpdateVO,isAsync);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新测试执行信息")
    @PutMapping("/cycle_case")
    public ResponseEntity update(@PathVariable(name = "project_id") Long projectId,
                                 @RequestBody TestCycleCaseVO testCycleCaseVO) {
        testCycleCaseService.update(testCycleCaseVO);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询当前文件夹下面所有子文件夹中用例")
    @PostMapping("/query/caseList")
    public ResponseEntity<PageInfo<TestFolderCycleCaseVO>> listCaseByCycleId(@PathVariable("project_id") Long projectId,
                                                                              @RequestParam(name = "cycle_id") Long cycleId,
                                                                              @RequestParam(name = "plan_id") Long planId,
                                                                              @SortDefault Pageable pageable,
                                                                              @RequestBody(required = false) SearchDTO searchDTO) {
        return new ResponseEntity<>(testCycleCaseService.listAllCaseByCycleId(projectId, planId, cycleId, pageable, searchDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("批量指派用例")
    @PostMapping("/batchAssign/cycleCase")
    public ResponseEntity batchAssignCase(@PathVariable("project_id") Long projectId,
                                          @RequestParam(name = "assign_user_id") Long assignUserId,
                                          @RequestBody(required = true) List<Long>  cycleCaseIds) {
        testCycleCaseService.batchAssignCycleCase(projectId, assignUserId, cycleCaseIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询一个执行和步骤")
    @GetMapping("/case_step/{execute_id}")
    public ResponseEntity<TestCycleCaseUpdateVO> queryCaseAndStep(@PathVariable(name = "project_id") Long projectId,
                                                    @PathVariable(name = "execute_id") Long executeId) {
        return Optional.ofNullable(testCycleCaseService.queryCaseAndStep(executeId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query.executeId"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("查询更新对比")
    @GetMapping("/{execute_id}/compared")
    public ResponseEntity<CaseChangeVO> selectUpdateCompare(@PathVariable("project_id") Long projectId,
                                          @PathVariable(name = "execute_id") Long executeId) {

        return new ResponseEntity<>(testCycleCaseService.selectUpdateCompare(projectId,executeId),HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("更新用例")
    @PostMapping("/compared")
    public ResponseEntity updateCompare(@PathVariable("project_id") Long projectId,
                                                      @RequestBody CaseCompareRepVO caseCompareRepVO) {
        testCycleCaseService.updateCompare(projectId,caseCompareRepVO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("忽略用例更新")
    @PostMapping("/{execute_id}/ignore/update")
    public ResponseEntity ignoreUpdate(@PathVariable("project_id") Long projectId,
                                        @PathVariable("execute_id") Long executedId) {
        testCycleCaseService.ignoreUpdate(projectId,executedId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("导入用例")
    @PostMapping("/import")
    public ResponseEntity importCase(@PathVariable("project_id") Long projectId,
                                     @RequestParam("cycle_id") Long cycleId,
                                     @RequestParam("plan_id") Long planId,
                                     @RequestBody Map<Long,CaseSelectVO> map) {
        testCycleCaseService.importCase(projectId,cycleId,map,planId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
