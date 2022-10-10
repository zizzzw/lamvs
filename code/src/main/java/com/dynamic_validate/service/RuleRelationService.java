package com.dynamic_validate.service;

import com.dynamic_validate.entity.SamlList;
import com.dynamic_validate.entity.SamlType;

public interface RuleRelationService {
    boolean TR15_1(SamlType t, SamlList l);

    boolean TR15_2(SamlType t, SamlList l);

    boolean TR23_1(String t, String f, String rep_id, int pro);

    boolean TR23_1(int t, int f, String rep_id);

    boolean TR23_1(SamlType t, SamlType f, String rep_id);

    boolean TR11_1(int tid, int f, String rep_id);

    boolean TR11_1(int tid, SamlType f, String rep_id);

    boolean TR11_2(int tid, SamlType f, String rep_id);

    boolean TR11_2(SamlType t, SamlType f, String rep_id);

    boolean TR12_1(int tid, SamlType struct, String rep_id);

    boolean TR12_1(SamlType t, SamlType struct, String rep_id);

    /**
     * FuncStruct关系判断。
     */
    boolean TR12_2(int f, int s, String rep_id);

    boolean TR12_2(int f, SamlType s, String rep_id);

    boolean TR12_2(String f, String s, String rep_id, int pro);

    boolean TR12_2(SamlType f, SamlType struct, String rep_id);

    /**
     * FuncFile关系判断。
     */
    boolean TR13_1(int f, int file, String rep_id);

    boolean TR13_1(String f, String file, String rep_id, int pro);

    boolean TR13_1(SamlType f, SamlType file, String rep_id);

    boolean TR13_2(SamlType s, SamlType file, String rep_id);
}
