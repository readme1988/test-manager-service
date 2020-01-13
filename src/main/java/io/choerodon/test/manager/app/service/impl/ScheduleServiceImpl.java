package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.test.manager.api.vo.asgard.QuartzTask;
import io.choerodon.test.manager.api.vo.asgard.ScheduleMethodDTO;
import io.choerodon.test.manager.api.vo.asgard.ScheduleTaskDTO;
import io.choerodon.test.manager.app.service.ScheduleService;
import io.choerodon.test.manager.infra.feign.ScheduleFeignClient;
import org.springframework.stereotype.Service;

/**
 * Created by zongw.lee@gmail.com on 23/11/2018
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleFeignClient feignClient;

    @Override
    public QuartzTask create(long projectId, ScheduleTaskDTO dto) {
        return feignClient.create(projectId, dto).getBody();
    }

    @Override
    public List<ScheduleMethodDTO> getMethodByService(long projectId, String service) {
        return feignClient.getMethodByService(projectId,service).getBody();
    }
}
