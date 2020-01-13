//package io.choerodon.test.manager.app.service.impl
//
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.api.vo.TestStatusVO
//import io.choerodon.test.manager.app.service.TestStatusService
//import io.choerodon.test.manager.infra.dto.TestStatusDTO
//import io.choerodon.test.manager.infra.mapper.TestStatusMapper
//import org.modelmapper.ModelMapper
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import spock.lang.Specification
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// * Created by 842767365@qq.com on 7/27/18.
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//class TestStatusServiceImplSpec extends Specification {
//    @Autowired
//    TestStatusService service
////    ITestStatusService iService
//    @Autowired
//    ModelMapper modelMapper
//
//    @Autowired
//    TestStatusMapper testStatusMapper
//
//
//    void setup() {
////        iService=Mock(ITestStatusService)
////        service=new TestStatusServiceImpl(iTestStatusService:iService)
//    }
//
//    def "Insert"() {
//        given:
//        TestStatusVO statusDTO = new TestStatusVO(statusColor: "red")
//        when:
//        def result = service.insert(statusDTO)
//        then:
//        result.getStatusColor() == "red"
//    }
//
//    def "Query"() {
//        given:
//        TestStatusVO statusDTO = new TestStatusVO(statusId: 6)
//        statusDTO.setStatusColor("rgba(244,67,54,1)")
//        statusDTO.setStatusType("CASE_STEP")
//        when:
//        def result = service.query(statusDTO)
//        then:
//        result.size() == 3
//    }
//
//    def "Delete"() {
//        given:
//        TestStatusVO statusDTO = new TestStatusVO(statusId: 7)
//        List<TestStatusDTO> selectAllPre = testStatusMapper.selectAll()
//        when:
//        service.delete(statusDTO)
//        then:
//        List<TestStatusDTO> selectAllAft = testStatusMapper.selectAll()
//        selectAllAft.size() == selectAllPre.size() - 1
//
//    }
//
//    def "Update"() {
//        given:
//        TestStatusVO statusDTO = new TestStatusVO(statusId: 2, statusColor: "red")
//        statusDTO.setObjectVersionNumber(1L)
//        statusDTO.setProjectId(1L)
//        statusDTO.setStatusType("CASE_STEP")
//        statusDTO.setStatusName("statusName")
//        when:
//        def result = service.update(statusDTO)
//        then:
//        //1 * iService.update(_) >> new TestStatusE(statusId: 1, statusColor: "red")
//
//        result.getStatusColor() == "red"
//    }
//}
