//package io.choerodon.test.manager.api.controller.v1
//
//import com.alibaba.fastjson.JSONObject
//import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.api.vo.TestIssueFolderVO
//import io.choerodon.test.manager.api.vo.TestIssueFolderWithVersionNameVO
//import io.choerodon.test.manager.app.service.TestCaseService
//import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO
//import io.choerodon.test.manager.infra.dto.TestIssueFolderRelDTO
//import io.choerodon.test.manager.infra.enums.TestIssueFolderType
//import io.choerodon.test.manager.infra.exception.IssueFolderException
//import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper
//import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper
//import org.apache.commons.lang.StringUtils
//import org.assertj.core.util.Lists
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.context.annotation.Import
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpMethod
//import spock.lang.Shared
//import spock.lang.Specification
//import spock.lang.Stepwise
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
//
///**
// * Created by zongw.lee@gmail.com
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//@Stepwise
//class TestIssueFolderControllerSpec extends Specification {
//    @Autowired
//    TestCaseService testCaseService
//
//    @Autowired
//    TestIssueFolderRelMapper testIssueFolderRelMapper
//
//    @Autowired
//    TestIssueFolderMapper testIssueFolderMapper
//
//    @Autowired
//    TestRestTemplate restTemplate
//
//    @Shared
//    List foldersId = new ArrayList()
//    @Shared
//    List objectVersionNumbers = new ArrayList()
//    @Shared
//    def projectId = 1L
//    @Shared
//    def versionId = 1L
//
//    def "Insert"() {
//        given:
//        TestIssueFolderVO testIssueFolder = new TestIssueFolderVO()
//        testIssueFolder.setName("测试文件夹")
//        testIssueFolder.setType("temp")
//        testIssueFolder.setVersionId(versionId)
//        testIssueFolder.setObjectVersionNumber(1L)
//
//        TestIssueFolderVO testIssueFolderParamException = new TestIssueFolderVO()
//        testIssueFolderParamException.setName("异常测试")
//        testIssueFolderParamException.setType("错误类型")
//        testIssueFolderParamException.setVersionId(versionId)
//
//        TestIssueFolderVO testIssueFolderIdException = new TestIssueFolderVO()
//        testIssueFolderIdException.setType("temp")
//
//        when: '向testIssueFolder的插入接口发请求'
//        def entity = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolder', testIssueFolder, TestIssueFolderVO, projectId)
//        then:
//        entity.statusCode.is2xxSuccessful()
//
//        and:
//        entity.body != null
//        StringUtils.equals(entity.getBody().name, "测试文件夹")
//
//        and: '设置值'
//        foldersId.add(entity.body.folderId)
//        objectVersionNumbers.add(entity.body.objectVersionNumber)
//        testIssueFolderIdException.setFolderId(foldersId[0])
//
//        when: '向testIssueFolder的插入接口发请求'
//        def resultFailure = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolder', testIssueFolderIdException, String.class, projectId)
//        then: '返回值'
//        resultFailure.statusCode.is2xxSuccessful()
//        JSONObject exceptionInfo = JSONObject.parse(resultFailure.body)
//        assert exceptionInfo.get("failed").toString() == "true"
//        assert exceptionInfo.get("code").toString() == "error.issue.folder.insert.folderId.should.be.null"
//
//        when: '向testIssueFolder的插入接口发请求'
//        resultFailure = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolder', testIssueFolderParamException, String.class, projectId)
//        then: '返回值'
//        resultFailure.statusCode.is2xxSuccessful()
//        JSONObject exceptionInfo2 = JSONObject.parse(resultFailure.body)
//        assert exceptionInfo2.get("failed").toString() == "true"
//        assert exceptionInfo2.get("code").toString() == IssueFolderException.ERROR_FOLDER_TYPE
//
//        when: '向testIssueFolder的插入接口发请求'
//        resultFailure = restTemplate.postForEntity('/v1/projects/{project_id}/issueFolder', testIssueFolder, String.class, projectId)
//        then: '返回值'
//        resultFailure.statusCode.is2xxSuccessful()
//        JSONObject exceptionInfo3 = JSONObject.parse(resultFailure.body)
//        assert exceptionInfo3.get("failed").toString() == "true"
//        assert exceptionInfo3.get("code").toString() == "error.db.duplicateKey"
//    }
//
//    def "Query"() {
//        given:
//        ProductVersionDTO productVersionDTO = new ProductVersionDTO()
//        productVersionDTO.setVersionId(versionId)
//        productVersionDTO.setProjectId(projectId)
//        productVersionDTO.setName("0.1.0")
//        productVersionDTO.setDescription("测试版本")
//        Map map = new HashMap<Long, ProductVersionDTO>()
//        map.put(versionId, productVersionDTO)
//        JSONObject result = new JSONObject()
//        result.put("versions", new ArrayList<>())
//
//        when: '向查询issues的接口发请求'
//        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issueFolder/query', JSONObject.class, projectId)
//
//        then: '返回值'
//        1 * testCaseService.getVersionInfo(_) >> map
//        entity.statusCode.is2xxSuccessful()
//        JSONObject jsonObject = entity.body
//
//        expect: "设置期望值"
//        !jsonObject.isEmpty()
//
//        when: '向testIssueFolder的查询接口发请求'
//        def resultNull = restTemplate.getForEntity('/v1/projects/{project_id}/issueFolder/query', JSONObject.class, 99999L)
//        then: '返回值'
//        1 * testCaseService.getVersionInfo(_) >> new HashMap<>()
//        resultNull.statusCode.is2xxSuccessful()
//        assert resultNull.body == result
//    }
//
//    def "QueryByVersion"() {
//        given:
//        ProductVersionDTO productVersionDTO = new ProductVersionDTO()
//        productVersionDTO.setVersionId(versionId)
//        productVersionDTO.setProjectId(projectId)
//        productVersionDTO.setName("0.1.0")
//        productVersionDTO.setDescription("测试版本")
//        Map map = new HashMap<Long, ProductVersionDTO>()
//        map.put(versionId, productVersionDTO)
//
//        when: '向查询issues的接口发请求'
//        def entity = restTemplate.getForEntity('/v1/projects/{project_id}/issueFolder/query/all?versionId={versionId}', List, projectId, versionId)
//
//        then: '返回值'
//        1 * testCaseService.getVersionInfo(_) >> map
//        entity.statusCode.is2xxSuccessful()
//        List<TestIssueFolderWithVersionNameVO> TestIssueFolderWithVersionNameDTOS = entity.body
//
//        expect: "设置期望值"
//        TestIssueFolderWithVersionNameDTOS.size() > 0
//
//    }
//
//    def "Update"() {
//        given:
//        TestIssueFolderVO testIssueFolderDTO = new TestIssueFolderVO()
//        testIssueFolderDTO.setName("修改名字")
//        testIssueFolderDTO.setFolderId(foldersId[0])
//        testIssueFolderDTO.setVersionId(versionId)
//        testIssueFolderDTO.setProjectId(projectId)
//        testIssueFolderDTO.setType(TestIssueFolderType.TYPE_CYCLE)
//        testIssueFolderDTO.setObjectVersionNumber(objectVersionNumbers[0])
//
//        TestIssueFolderVO testIssueFolderIdException = new TestIssueFolderVO()
//        testIssueFolderIdException.setFolderId(999L)
//        testIssueFolderIdException.setName("修改名字异常")
//        testIssueFolderIdException.setType("temp")
//
//        TestIssueFolderVO testIssueFolderTypeException = new TestIssueFolderVO()
//        testIssueFolderTypeException.setType("1111")
//        testIssueFolderTypeException.setFolderId(foldersId[0])
//
//        when: '向修改issuesFolder的接口发请求'
//        restTemplate.put('/v1/projects/{project_id}/issueFolder/update', testIssueFolderDTO, projectId)
//
//        then: '返回值'
//        TestIssueFolderDTO changedIssueFolder = testIssueFolderMapper.selectByPrimaryKey(foldersId[0])
//
//        and:
//        objectVersionNumbers.add(changedIssueFolder.getObjectVersionNumber())
//
//        expect: '验证更新是否成功'
//        changedIssueFolder.type == TestIssueFolderType.TYPE_CYCLE
//        changedIssueFolder.name == "修改名字"
//        changedIssueFolder.objectVersionNumber == 2L
//
//        when: '向testIssueFolder的修改接口发请求'
//        HttpEntity<JSONObject> jsonObjectHttpEntity = new HttpEntity<>(testIssueFolderTypeException)
//        def resultFailure = restTemplate.exchange("/v1/projects/{project_id}/issueFolder/update",
//                HttpMethod.PUT,
//                jsonObjectHttpEntity,
//                String.class,
//                projectId)
//        then:
//        resultFailure.statusCode.is2xxSuccessful()
//        JSONObject idExceptionInfo = JSONObject.parse(resultFailure.body)
//        idExceptionInfo.get("failed").toString() == "true"
//        idExceptionInfo.get("code").toString() == IssueFolderException.ERROR_FOLDER_TYPE
//    }
//
//    def "MoveFolder"() {
//        given:
//        TestIssueFolderRelDTO testIssueFolderRelDO = new TestIssueFolderRelDTO()
//        testIssueFolderRelDO.setFolderId(foldersId[0])
//        testIssueFolderRelDO.setVersionId(2L)
//        testIssueFolderRelDO.setProjectId(1L)
//        testIssueFolderRelDO.setIssueId(99999L)
//        testIssueFolderRelDO.setObjectVersionNumber(1L)
//        testIssueFolderRelMapper.insert(testIssueFolderRelDO)
//
//        TestIssueFolderVO testIssueFolderDTO = new TestIssueFolderVO()
//        testIssueFolderDTO.setName("修改名字")
//        testIssueFolderDTO.setFolderId(foldersId[0])
//        testIssueFolderDTO.setVersionId(2L)
//        testIssueFolderDTO.setProjectId(projectId)
//        testIssueFolderDTO.setObjectVersionNumber(objectVersionNumbers[1])
//        List<TestIssueFolderVO> testIssueFolderDTOS = Lists.newArrayList(testIssueFolderDTO)
//
//        TestIssueFolderVO testIssueFolderException = new TestIssueFolderVO()
//        testIssueFolderException.setFolderId(999L)
//        testIssueFolderException.setName("修改名字异常")
//        testIssueFolderException.setType("temp")
//        List<TestIssueFolderVO> testIssueFolderDTOSException = Lists.newArrayList(testIssueFolderException)
//        List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = testIssueFolderRelMapper.selectAll()
//        TestIssueFolderRelDTO testIssueFolderRelDTO=new TestIssueFolderRelDTO()
//        def all = testIssueFolderRelMapper.selectAll()
//
//        testIssueFolderRelMapper.updateVersionByFolderWithNoLock(testIssueFolderRelDTO)
//
//        when: '向查询issueFolder的移动接口发请求'
//        restTemplate.put('/v1/projects/{project_id}/issueFolder/move', testIssueFolderDTOS, projectId)
//
//        then: '返回值'
//        1 * testCaseService.batchIssueToVersionTest(_, _, _)
//        TestIssueFolderDTO changedIssueFolder = testIssueFolderMapper.selectByPrimaryKey(foldersId[0])
//
//        and:
//        objectVersionNumbers.add(changedIssueFolder.getObjectVersionNumber())
//
//        expect: '验证移动是否成功'
//        changedIssueFolder.versionId == 2L
//        changedIssueFolder.objectVersionNumber == 3L
//    }
//
//    def "CopyFolder"() {
//        given:
//        Long[] folderIds = new Long[1]
//        folderIds[0] = foldersId[0]
//
//        TestIssueFolderDTO testIssueFolderDO = new TestIssueFolderDTO()
//        testIssueFolderDO.setName("修改名字")
//        testIssueFolderDO.setVersionId(3L)
//        testIssueFolderDO.setProjectId(projectId)
//        testIssueFolderDO.setType(TestIssueFolderType.TYPE_CYCLE)
//
//        List<Long> issuesId = new ArrayList<>()
//        issuesId.add(1L)
//
//        when: '向查询issueFolder的复制接口发请求'
//        restTemplate.put('/v1/projects/{project_id}/issueFolder/copy?versionId={versionId}', folderIds, projectId, 3L)
//
//        then: '返回值'
//        1 * testCaseService.batchCloneIssue(_, _, _) >> issuesId
//        TestIssueFolderDTO changedIssueFolder = testIssueFolderMapper.selectOne(testIssueFolderDO)
//
//        expect: '验证复制是否成功'
//        changedIssueFolder != null
//    }
//
//    def "Delete"() {
//        when: '执行方法'
//        restTemplate.delete('/v1/projects/{project_id}/issueFolder/{folderId}', projectId, folderId)
//
//        then: '返回值'
//        def result = testIssueFolderMapper.selectByPrimaryKey(folderId as Long)
//
//        expect: '期望值'
//        result == null
//
//        where: '判断issue是否删除'
//        folderId << foldersId[0]
//    }
//
//}
