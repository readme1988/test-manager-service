/* eslint-disable no-console */
import React, {
  Component, useState, useEffect, useMemo,
} from 'react';
import TestStepTable from '../TestStepTable';

function CreateTestStepTable(props) {
  const { name, pDataSet } = props;
  const [testStepData, setTestStepData] = useState([]);
  //   useEffect(() => {
  //     pDataSet.current.set(name, testStepData);
  //     console.log('pDataSet', pDataSet.current, pDataSet.current.get(name));
  //   }, [name, pDataSet, testStepData]);
  useEffect(() => {
    console.log(' useEffect pDataSet', testStepData);
  }, [testStepData]);
  useEffect(() => {
    console.log(' useEffect ');
  }, []);
  return (
    <TestStepTable
      disabled={false}
      data={testStepData}
      setData={setTestStepData}
    />
  );
}
export default CreateTestStepTable;
