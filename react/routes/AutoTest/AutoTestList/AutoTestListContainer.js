import React, { Component } from 'react';
import { Choerodon } from '@choerodon/boot';
import { observer } from 'mobx-react';
import _ from 'lodash';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/base16-dark.css';
import {
  getApps, getTestHistoryByApp, reRunTest, getAllEnvs,
} from '../../../api/AutoTestApi';
import { commonLink, TestExecuteLink } from '../../../common/utils';
import AutoTestList from './AutoTestList';
import AutoListStore from '../stores/AutoListStore';
import CreateAutoTestStore from '../stores/CreateAutoTestStore';

const store = AutoListStore;
@observer
class AutoTestListContainer extends Component {
  componentDidMount() {
    this.loadApps();
  }

  componentWillUnmount() {
    store.clear();
    clearTimeout(this.timer);
  }

  requestQueue=[]

  loadApps = (value = '') => {
    const { currentApp } = store;
    let searchParam = {};
    if (value !== '') {
      searchParam = { name: value };
    }
    store.Loading();
    store.setSelectLoading(true);
    Promise.all([
      getApps({
        page: 1,
        size: 10,
        sort: { field: 'id', order: 'desc' },
        postData: { searchParam, params: [] },
      }),
      getAllEnvs(),
    ]).then(([data, envs]) => {
      // 默认取第一个

      if (data.failed) {
        Choerodon.prompt(data.failed);
        return;
      }
      if (!currentApp && !value && data.list.length > 0) {
        this.loadTestHistoryByApp({ appId: data.list[0].id });
        store.setEnvList(envs);
        store.setCurrentApp(data.list[0].id);
        store.setAppList(data.list);
        store.setSelectLoading(false);
      } else {
        this.loadTestHistoryByApp();
        store.setEnvList(envs);
        store.setAppList(data.list);
        store.setSelectLoading(false);
        store.unLoading();
      }
    });
  }

  handleAppChange = (appId) => {
    this.loadTestHistoryByApp({ appId });
    store.setCurrentApp(appId);
  }

  loadTestHistoryByApp = ({ appId = store.currentApp, pagination = store.pagination, filter = store.filter } = {}) => {
    store.Loading();
    store.setFilter(filter);
    getTestHistoryByApp(appId, pagination, filter).then((history) => {
      store.setHistoryList(history.list);
      store.setPagination({
        current: history.pageNum,
        total: history.total,
        pageSize: history.pageSize,
      });
    }).finally(() => {
      store.unLoading();
      store.setSelectLoading(false);
      setTimeout(() => {
        this.handleAutoRefresh();
      }, 3000);
    });
  }

  handleAutoRefresh = ({ appId = store.currentApp, pagination = store.pagination, filter = store.filter } = {}) => {
    if (store.autoRefresh) {
      clearTimeout(this.timer);
      this.timer = setTimeout(() => {
        this.handleAutoRefresh();
      }, 3000);
    }
    getTestHistoryByApp(appId, pagination, filter).then((history) => {
      store.setHistoryList(history.list);

      store.setPagination({
        current: history.pageNum,
        total: history.total,
        pageSize: history.pageSize,
      });
    });
  }

  handleTableChange = (pagination, filter) => {
    this.loadTestHistoryByApp({ pagination, filter });
  }

  handleRerunTest = (record) => {
    store.Loading();
    const { id } = record;
    reRunTest({ historyId: id }).then((res) => {
      this.loadTestHistoryByApp();
    }).catch((err) => {
      store.unLoading();
      Choerodon.prompt('网络出错');
    });
  }

  handleAutoRefreshChange=(checked) => {
    localStorage.setItem('testManager.AutoList.autoRefresh', checked);
    store.setAutoRefresh(checked);
    if (checked) {
      this.handleAutoRefresh();
    }
  }

  toCreateAutoTest = () => {
    CreateAutoTestStore.setVisible(true);
  }

  toReport = (resultId) => {
    this.props.history.push(commonLink(`/AutoTest/report/${resultId}`));
  }

  toTestExecute = (cycleId) => {
    this.props.history.push(TestExecuteLink(cycleId));
  }

  handleItemClick = (record, { item, key, keyPath }) => {
    const {
      id, instanceId, status, resultId, cycleId,
    } = record;
    // console.log(key, record);
    switch (key) {
      case 'log': {
        this.ContainerLog.open(record);
        break;
      }

      case 'retry': {
        this.handleRerunTest(record);
        break;
      }
      case 'report': {
        this.toReport(resultId);
        break;
      }
      default: break;
    }
  }

  saveRef = name => (ref) => {
    this[name] = ref;
  }

  render() {
    const {
      loading, appList,
      selectLoading,
      autoRefresh,
      currentApp,
      historyList,
      envList,
      pagination,
    } = store;
    return (
      <AutoTestList
        loading={loading}
        appList={appList}
        selectLoading={selectLoading}
        autoRefresh={autoRefresh}
        currentApp={currentApp}
        historyList={historyList}
        envList={envList}
        pagination={pagination}
        toCreateAutoTest={this.toCreateAutoTest}
        onRefreshClick={this.loadTestHistoryByApp}
        onItemClick={this.handleItemClick}
        onAppChange={this.handleAppChange}
        onFilterChange={this.loadApps}
        onTableChange={this.handleTableChange}
        onAutoRefreshChange={this.handleAutoRefreshChange}
        onSaveLogRef={this.saveRef}
      />
    );
  }
}

export default AutoTestListContainer;
