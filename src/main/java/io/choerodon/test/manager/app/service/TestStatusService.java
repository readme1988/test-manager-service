package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestStatusVO;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
public interface TestStatusService {
    List<TestStatusVO> query(Long projectId,TestStatusVO testStatusVO);

    TestStatusVO insert(TestStatusVO testStatusVO);

    void delete(TestStatusVO testStatusVO);

    TestStatusVO update(TestStatusVO testStatusVO);

    void populateStatus(TestCycleCaseVO testCycleCaseVO);

    Long getDefaultStatusId(String type);
}
