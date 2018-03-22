package org.xiaoheshan.hallo.boxing.pi.looper.executor;

/**
 * @author : _Chf
 * @since : 03-18-2018
 */
public interface IExecutor<T> {

    T execute(String... parameters);
}
