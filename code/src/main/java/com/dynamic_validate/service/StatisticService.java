package com.dynamic_validate.service;

import com.dynamic_validate.entity.SamlType;

import java.util.List;

public interface StatisticService {
    /**
     * 获取所有DI中的函数
     */
    StringBuilder getAllFunc();

    void copyAllDps(String ids);

    List<SamlType> getAllDepends();

    List<List<SamlType>> splitByLevel(List<SamlType> dpList);

    List<String> getTypeDeps(List<SamlType> dpList);

    List<String> getReList(String tdpList);

    void generFuncExp(List<SamlType> funcs);

    void generFiles(List<SamlType> list);

    void generSubsysDentry();
}
