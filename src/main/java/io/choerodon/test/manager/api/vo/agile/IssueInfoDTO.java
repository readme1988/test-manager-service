package io.choerodon.test.manager.api.vo.agile;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/7/11.
 * Email: fuqianghuang01@gmail.com
 */
public class IssueInfoDTO {

	private Long issueId;

	private String issueNum;

	private String summary;

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}

	public String getIssueNum() {
		return issueNum;
	}

	public void setIssueNum(String issueNum) {
		this.issueNum = issueNum;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}
}
