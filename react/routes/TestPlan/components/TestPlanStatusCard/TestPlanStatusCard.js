import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Card, Tooltip } from 'choerodon-ui';
import Progress from '../../../../components/Progress';
import './TestPlanStatusCard.less';
import Store from '../../stores';

export default observer(() => {
  const { testPlanStore } = useContext(Store);
  const { statusRes } = testPlanStore;
  
  const handleQueryExecutesByStatus = (status) => {
    const { filter } = testPlanStore;
    testPlanStore.setFilter({ ...filter, ...{ executionStatus: status.statusId } });
    testPlanStore.loadExecutes();
  };

  return (
    <Card className="c7ntest-testPlan-statusCard" title="测试状态总览"> 
      <div className="c7ntest-testPlan-statusCard-content">
        {
          statusRes && statusRes.statusVOList && statusRes.statusVOList.length > 0 && statusRes.statusVOList.map(item => (
            <Tooltip title={`${item.statusName}：${(item.count && statusRes.total) ? `${Math.round(item.count * 100 / statusRes.total * 100) / 100}%` : 0}`}>
              <div
                style={{
                  flexShrink: 0, overflow: 'hidden', cursor: 'pointer', 
                }}
                role="none"
                onClick={handleQueryExecutesByStatus.bind(this, item)}
              >
                <Progress percent={(item.count && statusRes.total) ? Math.round(item.count * 100 / statusRes.total * 100) / 100 : 0} tip={item.count} title={item.statusName} strokeColor={item.statusColor} />
              </div>
            </Tooltip>
          ))
        }
      </div>
    </Card>
  );
});
