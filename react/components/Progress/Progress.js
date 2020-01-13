import React, { Component } from 'react';
import { Circle } from 'rc-progress';
import 'rc-progress/assets/index.css';
import './index.less';

class Progress extends Component {
  render() {
    const {
      percent, title, strokeColor, trailColor, tip,
    } = this.props;
    return (
      <div className="c7ntest-progress">
        <div className="c7ntest-progress-circle">
          <div style={{
            transform: 'matrix(-1, 0, 0, 1, 0, 0)', width: 40, height: 40, marginLeft: 16, marginRight: 16, 
          }}
          >
            <Circle
              percent={percent}
              strokeWidth="8"
              trailWidth="8"
              strokeLinecap="square"
              strokeColor={strokeColor}
              trailColor={trailColor || 'rgba(0, 0, 0, 0.08'}
            />
          </div>
          <div className="c7ntest-progress-tip">
            <span className="c7ntest-progress-tip-title">{tip}</span>
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
