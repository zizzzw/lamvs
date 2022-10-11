package com.dynamic_validate.service;

import com.dynamic_validate.entity.DemandInvoke;
import com.dynamic_validate.entity.DemandPath;
import com.dynamic_validate.entity.SamlType;

public interface RuleService {
    //void DEV_IR_ByRule(List<ErrorClassify> errList);

    //boolean VERI_FUNC(SamlType f, List<ErrorClassify> errList);

    void VERI_Demand(int pro, String rep_id);

    boolean VERI_DP(DemandPath dp, String rep_id, int pro);

    boolean DEV_SR(SamlType t, String rep_id);

    boolean DEV_IR(DemandInvoke di, String rep_id);

    boolean TR21(DemandInvoke samlDemand, String rep_id);
}
