package com.example.dynamic_validate.service;

import com.example.dynamic_validate.entity.DemandInvoke;
import com.example.dynamic_validate.entity.SamlType;
import com.example.dynamic_validate.entity.ErrorClassify;

import java.util.List;

public interface DemandInvokeService {

    void finishDemandFuncs();

    List<Integer> getAndfinishExistFuncs(List<ErrorClassify> errList);

    List<SamlType> getFuncsByDemandInvoke(DemandInvoke d, List<ErrorClassify> errList);

    void demandDistinct();
}
