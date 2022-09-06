package com.example.dynamic_validate.service;

import com.example.dynamic_validate.entity.SamlList;
import com.example.dynamic_validate.entity.SamlType;

public interface RuleExistService {

    boolean TR01(int id, String rep_id);

    boolean TR01(String name, String rep_id);

    boolean TR01(SamlType samlType, String rep_id);

    boolean TR02(int id, String rep_id);

    boolean TR02(SamlType samlType, String rep_id);

    boolean TR03(int id, String rep_id);

    boolean TR03(String name, String rep_id);

    boolean TR03(SamlType samlType, String rep_id);

    boolean TR04(int id, String rep_id);

    boolean TR04(String name, String rep_id);

    boolean TR04(SamlType samlType, String rep_id);

    boolean TR05(SamlType samlType, String rep_id); // 目录

    boolean TR06(SamlType samlType, String rep_id); // 子系统

    boolean TR07(SamlList samlList, String rep_id);

    boolean TR0X(SamlType samlType, int level);


}
