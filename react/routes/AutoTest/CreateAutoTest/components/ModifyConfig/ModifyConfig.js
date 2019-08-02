
import React, { Component } from 'react';
import { Button } from 'choerodon-ui';
import { observer } from 'mobx-react';
import { injectIntl, FormattedMessage } from 'react-intl';

import { YamlEditor } from '../../../../../components';
import CreateAutoTestStore from '../../../AutoTestStore/CreateAutoTestStore';
import { getYaml, checkYaml } from '../../../../../api/AutoTestApi';

@injectIntl
@observer
class ModifyConfig extends Component {
  state = {
    markers: null,
    errorLine: [],
  };

  componentDidMount() {
    this.loadYaml();
  }

  loadYaml=() => {
    const { app, appVersion, env } = CreateAutoTestStore;
    getYaml(app.id, appVersion.id, env.id).then((data) => {
      if (data) {
        CreateAutoTestStore.setConfigValue(data);
      }
    });
  }

  /**
   * 获取values
   * @param value
   */
  handleChangeValue = (value) => {
    CreateAutoTestStore.setNewConfigValue(value);
    checkYaml(value)
      .then((data) => {
        this.setState({ errorLine: data });
      });
  };

  render() {
    const { errorLine, markers } = this.state;
    const data = CreateAutoTestStore.getNewConfigValue;
    return (
      <div className="deployApp-env">
        {data && (
        <YamlEditor
          newLines={data.newLines}
          isFileError={!!data.errorLines}
          totalLine={data.totalLine}
          errorLines={errorLine}
          errMessage={data.errorMsg}
          modifyMarkers={markers}
          value={data.yaml}
          highlightMarkers={data.highlightMarkers}
          onChange={this.handleChangeValue}
          change
        />
        )}
      </div>
    );
  }
}

export default ModifyConfig;
