package io.choerodon.test.manager.domain.aop;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseHistoryEFactory;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/28/18.
 */
@Aspect
@Component
public class TestCycleCaseHistoryRecordAOP {

	private final String FIELD_STATUS = "执行状态";
	private final String FIELD_ASSIGNED = "已指定至";
	private final String FIELD_ATTACHMENT = "附件";
	private final String FIELD_DEFECT = "缺陷";
	private final String FIELD_COMMENT = "注释";

	private final String FIELD_NULL = " ";
	@Autowired
	TestCycleCaseHistoryService testCycleCaseHistoryService;

	@Autowired
	RedisTemplate redisTemplate;

	@Autowired
	ITestCycleCaseService testCycleCaseService;

	@Autowired
	UserService userService;


	@Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.changeOneCase(..)) && args(testCycleCaseDTO,projectId)")
	public Object afterTest(ProceedingJoinPoint pjp, TestCycleCaseDTO testCycleCaseDTO, Long projectId) throws Throwable {
		TestCycleCaseE case1 = TestCycleCaseEFactory.create();
		case1.setExecuteId(testCycleCaseDTO.getExecuteId());
		TestCycleCaseE before = testCycleCaseService.queryOne(case1);
		Object o = pjp.proceed();
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setExecuteId(before.getExecuteId());

		if (!testCycleCaseDTO.getExecutionStatus().equals(before.getExecutionStatus())) {
			historyDTO.setField(FIELD_STATUS);
			historyDTO.setNewValue(testCycleCaseDTO.getExecutionStatus());
			historyDTO.setOldValue(before.getExecutionStatus());
			LocalDateTime time = LocalDateTime.ofInstant(((TestCycleCaseDTO) o).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
			countCaseToRedis(String.valueOf(projectId), time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), before.getExecutionStatus(), testCycleCaseDTO.getExecutionStatus(), testCycleCaseDTO.getExecuteId());
		} else if (!testCycleCaseDTO.getAssignedTo().equals(before.getAssignedTo())) {
			historyDTO.setField(FIELD_ASSIGNED);
			Long after_as = testCycleCaseDTO.getAssignedTo();
			Long before_as = before.getAssignedTo();
			Long[] para = new Long[]{before_as, after_as};
			List<UserDO> users = userService.query(para);
			int count = 0;
			if (before_as != 0) {
				historyDTO.setOldValue(users.get(count).getLoginName() + users.get(count).getRealName());
				count++;
			} else {
				historyDTO.setOldValue(FIELD_NULL);
			}
			if (after_as != 0) {
				historyDTO.setNewValue(users.get(count).getLoginName() + users.get(count).getRealName());
			} else {
				historyDTO.setNewValue(FIELD_NULL);
			}

		} else if (!StringUtils.equals(testCycleCaseDTO.getComment(), before.getComment())) {
			historyDTO.setField(FIELD_COMMENT);
			historyDTO.setNewValue(testCycleCaseDTO.getComment());
			historyDTO.setOldValue(before.getComment());
		} else {
			return o;
		}
		testCycleCaseHistoryService.insert(historyDTO);
		return o;
	}

	private void countCaseToRedis(String projectId, String date, String oldStatus, String newStatus, Long executeId) {
		if (StringUtils.equals(oldStatus, TestStatusE.STATUS_UN_EXECUTED)) {
			RedisAtomicLong entityIdCounter = new RedisAtomicLong(projectId + ":" + date, redisTemplate.getConnectionFactory());
			entityIdCounter.incrementAndGet();
		} else if (StringUtils.equals(newStatus, TestStatusE.STATUS_UN_EXECUTED)) {
			TestCycleCaseHistoryE e = TestCycleCaseHistoryEFactory.create();
			e.setExecuteId(executeId);
			e.setOldValue(TestStatusE.STATUS_UN_EXECUTED);
			e.setField(FIELD_STATUS);
			PageRequest pageRequest = new PageRequest();
			pageRequest.setPage(0);
			pageRequest.setSize(1);
			pageRequest.setSort(new Sort(Sort.Direction.DESC, new String[]{"id"}));
			LocalDateTime time = LocalDateTime.ofInstant(e.querySelf(pageRequest).get(0).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
			RedisAtomicLong entityIdCounter = new RedisAtomicLong(projectId + ":" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), redisTemplate.getConnectionFactory());
			entityIdCounter.decrementAndGet();
		}
	}

	@After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.upload(..))")
	public void recordAttachUpload(JoinPoint jp) {

		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(FIELD_ATTACHMENT);
		historyDTO.setExecuteId((Long) jp.getArgs()[3]);
		historyDTO.setOldValue(FIELD_NULL);
		historyDTO.setNewValue(jp.getArgs()[1].toString());
		testCycleCaseHistoryService.insert(historyDTO);

	}

	@Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.delete(..))&& args(bucketName,attachId)")
	public Object recordAttachDelete(ProceedingJoinPoint pjp, String bucketName, Long attachId) throws Throwable {
		TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
		attachmentRelE.setId(attachId);
		List<TestCycleCaseAttachmentRelE> lists = attachmentRelE.querySelf();
		if (lists.size() != 1) {
			throw new CommonException("error.attach.notFound");
		}
		attachmentRelE = lists.get(0);
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setExecuteId(attachmentRelE.getAttachmentLinkId());
		historyDTO.setField(FIELD_ATTACHMENT);
		historyDTO.setOldValue(attachmentRelE.getAttachmentName());
		historyDTO.setNewValue(FIELD_NULL);
		Object o = pjp.proceed();
		testCycleCaseHistoryService.insert(historyDTO);
		return o;
	}

	@After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.insert(..))&& args(testCycleCaseDefectRelDTO)")
	public void recordDefectAdd(JoinPoint jp, TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(FIELD_DEFECT);
		historyDTO.setExecuteId(testCycleCaseDefectRelDTO.getDefectLinkId());
		historyDTO.setOldValue(FIELD_NULL);
		historyDTO.setNewValue(testCycleCaseDefectRelDTO.getDefectName());

	}

	@After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.delete(..))&& args(testCycleCaseDefectRelDTO)")
	public void recordDefectDelete(JoinPoint jp, TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {

		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(FIELD_DEFECT);
		historyDTO.setExecuteId(testCycleCaseDefectRelDTO.getDefectLinkId());
		historyDTO.setOldValue(testCycleCaseDefectRelDTO.getDefectName());
		historyDTO.setNewValue(FIELD_NULL);
	}
}
