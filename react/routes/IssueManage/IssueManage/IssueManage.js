import React, { Component, Fragment } from 'react';
import { observer } from 'mobx-react';
import {
  Page, Header, Content, Breadcrumb,
} from '@choerodon/boot';
import { Button, Icon } from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro/lib';
import { FormattedMessage, injectIntl } from 'react-intl';
import IssueStore from '../stores/IssueStore';
import { getParams } from '../../../common/utils';
import RunWhenProjectChange from '../../../common/RunWhenProjectChange';
import Empty from '@/components/Empty';
import CreateIssue from '../components/CreateIssue';
import IssueTree from '../components/IssueTree';
import IssueTable from '../components/IssueTable';
import ExportSide from '../components/ExportSide';
import ImportSide from '../components/ImportIssue';
import TestCaseDetail from '../components/TestCaseDetail';
import openCreateFolder from '../components/CreateFolder';
import './IssueManage.less';
import IssueTreeStore from '../stores/IssueTreeStore';
import empty from '@/assets/empty.png';
@injectIntl
@observer
export default class IssueManage extends Component {
  componentDidMount() {
    RunWhenProjectChange(IssueStore.clearStore);
    RunWhenProjectChange(IssueTreeStore.clearStore);
    this.getInit();
  }

  getInit = () => {
    const Request = getParams(this.props.location.search);
    const { paramName, paramIssueId, folderId } = Request;
    IssueStore.setParamName(paramName);
    IssueStore.setParamIssueId(paramIssueId);
    if (paramName && paramIssueId) {
      IssueStore.setClickIssue({
        caseId: Number(paramIssueId),
      });
    }
    // 当参数中有用例名时，在table的筛选框中加入
    const barFilters = paramName ? [paramName] : [];
    IssueStore.setBarFilters(barFilters);
    this.getTestCase(folderId);
  }

  getTestCase = async (defaultSelectId) => {
    await IssueTreeStore.loadIssueTree(defaultSelectId);
    const { currentFolder } = IssueTreeStore;
    const { id } = currentFolder;
    if (id) {
      IssueStore.loadIssues();
    }
  }

  /**
   *
   * 用例创建后，默认选到目标目录
   * @param {*} issue
   * @param {*} folderId
   * @memberof IssueManage
   */
  handleCreateIssue(issue, folderId) {
    if (folderId) {
      IssueTreeStore.setCurrentFolderById(folderId);
      IssueTreeStore.updateHasCase(folderId, true);// 设置含有用例
    }
    IssueStore.loadIssues();
  }


  handleTableRowClick = (record) => {
    IssueStore.setClickIssue(record);
  }


  saveRef = name => (ref) => {
    this[name] = ref;
  }

  handleClose = (issueInfo) => {
    const { issues } = IssueStore;
    const index = issues.findIndex(item => item.caseId === issueInfo.caseId);
    if (index > -1) {
      issues[index] = { ...issues[index], ...issueInfo };
    }
    IssueStore.setClickIssue({});
    IssueStore.setIssues(issues); // 每次关闭详情时应该设置issues
  }

  handleOpenCreateIssue = () => {
    const { intl } = this.props;
    const { clickIssue } = IssueStore;
    Modal.open({
      key: 'createIssue',
      // title:<FormattedMessage id='issue_create_name'  />,
      title: intl.formatMessage({ id: 'issue_create_name' }),
      drawer: true,
      style: {
        width: 740,
      },
      children: (
        <CreateIssue
          onOk={this.handleCreateIssue.bind(this)}
          intl={intl}
          caseId={clickIssue && clickIssue.caseId}
          defaultFolderValue={IssueTreeStore.getCurrentFolder}
        />
      ),
      okText: '创建',
    });
  }

  handleOpenImportIssue = () => {
    Modal.open({
      key: 'importIssue',
      // title:<FormattedMessage id='issue_create_name'  />,
      title: '导入用例',
      drawer: true,
      destroyOnClose: false,
      style: {
        width: 380,
      },
      children: (
        <ImportSide defaultFolderValue={IssueTreeStore.getCurrentFolder} />
      ),
      footer: () => '',
      className: 'c7ntest-Issue-import-modal',
    });
  }

  handleOpenExportIssue = () => {
    Modal.open({
      key: 'exportIssue',
      title: '导出用例',
      drawer: true,
      style: {
        width: 1090,
      },
      okCancel: false,
      okText: '关闭',
      children: (
        <ExportSide
          folderId={IssueTreeStore.getCurrentFolder.id}
        />
      ),
    });
  }

  handleCreateFolder = () => {
    this.getTestCase();
  }

  handleAddFolderClick = () => {
    if (IssueTreeStore.treeRef && IssueTreeStore.treeRef.current) {
      IssueTreeStore.treeRef.current.addFirstLevelItem();
    } else {
      openCreateFolder({
        onCreate: this.handleCreateFolder,
      });
    }
  }

  render() {
    const { clickIssue } = IssueStore;
    const currentFolder = IssueTreeStore.getCurrentFolder;
    const treeData = IssueTreeStore.getTreeData;
    const { loading } = IssueTreeStore;
    const noFolder = treeData.rootIds.length === 0;
    // const noFolder = true;
    return (
      <Page
        className="c7ntest-Issue c7ntest-region"
        service={[
          'test-manager-service.test-issue-folder.query',
          'test-manager-service.test-case.listCaseByFolderId',
          'test-manager-service.test-file-load-history.queryLatestLoadHistory',
          'test-manager-service.test-case.downloadImportTemplate',
          'test-manager-service.test-file-load-history.cancelUpLoad',
          'test-manager-service.test-case.importIssues',
          'test-manager-service.test-file-load-history.queryIssues',
          'test-manager-service.test-case.downLoadByFolder',
          'test-manager-service.test-case.downExcelFail',
          'test-manager-service.test-issue-folder.create',
          'test-manager-service.test-issue-folder.moveFolder',
          'test-manager-service.test-issue-folder.update',
          'test-manager-service.test-issue-folder.delete',
          'test-manager-service.test-case.createTestCase',
          'test-manager-service.test-attachment.uploadFile',
          'agile-service.issue-attachment.uploadForAddress',
          'test-manager-service.test-case-link.queryLinkIssues',
          'test-manager-service.test-case.queryCaseInfo',
          'test-manager-service.test-case-step.query',
          'test-manager-service.test-case-data-log.queryByCaseId',
          'test-manager-service.test-case.updateCase',
          'test-manager-service.test-case-attachment.uploadAttachment',
          'test-manager-service.test-case.batchCloneCase',
          'test-manager-service.test-case.batchMoveCase',
          'test-manager-service.test-case.deleteCase',
          'test-manager-service.test-case-step.changeOneStep',
          'test-manager-service.test-case-step.clone',
          'test-manager-service.test-case-step.removeStep',
        ]}
      >
        <Header
          title={<FormattedMessage id="issue_name" />}
        >
          {!noFolder && (
            <Button className="leftBtn" onClick={() => this.handleOpenCreateIssue()}>
              <Icon type="playlist_add icon" />
              <FormattedMessage id="issue_createTestIssue" />
            </Button>
          )}
          <Button icon="playlist_add" onClick={this.handleAddFolderClick}>
            创建一级目录
          </Button>
          <Button icon="unarchive" onClick={this.handleOpenExportIssue}>
            <FormattedMessage id="issue_export" />
          </Button>
          <Button className="leftBtn" onClick={this.handleOpenImportIssue}>
            {/* <Icon type="file_upload icon" /> */}
            <Icon type="archive" />
            <FormattedMessage id="issue_import" />
          </Button>
        </Header>
        <Breadcrumb />
        <Content style={{ display: 'flex', padding: '0', borderTop: '0.01rem solid rgba(0,0,0,0.12)' }}>
          {noFolder ? (
            <Empty
              loading={loading}
              pic={empty}
              title="暂无目录"
              description="当前项目下无目录，请创建"
              extra={<Button type="primary" funcType="raised" onClick={this.handleAddFolderClick}>创建一级目录</Button>}
            />
          ) : (
            <Fragment>
              <IssueTree />
              {currentFolder.id && (
              <div
                className="c7ntest-content-issue"
                style={{
                  flex: 1,
                  display: 'block',
                  overflowY: 'auto',
                  overflowX: 'hidden',
                  padding: '0 20px',
                }}
              >
                <div className="c7ntest-content-issueFolderName">
                  {currentFolder.data.name}
                </div>
                <IssueTable
                  onClick={this.handleTableRowClick}
                />
              </div>
              )}
            </Fragment>
          )}
          <TestCaseDetail visible={clickIssue && clickIssue.caseId} onClose={this.handleClose} />
        </Content>
      </Page>
    );
  }
}
