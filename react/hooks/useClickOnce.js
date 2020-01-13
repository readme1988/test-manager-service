import { useRef, useCallback } from 'react';
/**
 *  防止多次点击
 * useClickOnce((reset)=>()=>{
 * ...
 *  reset();
 * })
 *
 * @export
 * @param {*} fn (reset)=>()=>
 * @returns
 */
export default function useClickOnce(fn) {
  const clickRef = useRef(false);
  const reset = useCallback(() => {
    clickRef.current = false;
  }, []);
  const wrapper = useCallback(async (...args) => {
    if (!clickRef.current) {     
      clickRef.current = true; 
      await fn(reset)(...args);
    }
  }, [fn, reset]);  
  return wrapper;
}
