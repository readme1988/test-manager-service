import React, { Component } from 'react';
import { Circle } from 'rc-progress';
import 'rc-progress/assets/index.css';
import './index.less';

class Progress extends Component {
  render() {
    const {
      percent, title, strokeColor, trailColor,
    } = this.props;
    return (
      <div className="c7ntest-progress">
        <div className="c7ntest-progress-circle">
          <Circle
            percent={percent}
            strokeWidth="6"
            trailWidth="6"
            strokeLinecap="square"
            strokeColor={strokeColor}
            trailColor={trailColor || 'rgba(0, 0, 0, 0.08'}
          />
        </div>
        <div className="c7ntest-progress-tip">
          <div>
            <span className="c7ntest-progress-tip-title">{percent}</span>
          </div>
        </div>
        <div className="c7ntest-progress-title">
          {title}
        </div>
      </div>
    );
  }
}
export default Progress;
