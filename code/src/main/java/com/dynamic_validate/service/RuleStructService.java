package com.dynamic_validate.service;

import com.dynamic_validate.entity.SamlType;

public interface RuleStructService {
    boolean TR_dentry(SamlType samlType, String rep_id);

    boolean TR_subsys(SamlType samlType, String rep_id);

    boolean TR_file(SamlType t, String rep_id);

    boolean TR_file_1(SamlType t, String rep_id);

    boolean TR_file_2(SamlType t, String rep_id);

    boolean TR_struct(SamlType samlType, String rep_id);

    boolean TR_struct_1(SamlType s, String rep_id);

    boolean TR_struct_2(SamlType s, String rep_id);

    boolean TR_func(SamlType t, String rep_id);
}
