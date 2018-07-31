package com.leco.rpc.core.filter;

import com.leco.rpc.core.entity.RequestSerialize;
import com.leco.rpc.core.annotations.Filtr;

/**
 * @创建人 leco
 * @创建时间 2018/7/31
 */
@Filtr
public interface IFilter {

    void before(RequestSerialize serialize);


    void After(RequestSerialize serialize, Object result);
}
