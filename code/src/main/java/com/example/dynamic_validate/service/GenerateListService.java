package com.example.dynamic_validate.service;

import com.example.dynamic_validate.entity.SamlType;

public interface GenerateListService {
    void generListByExp(int pro);

    void listDistinct();

    void generStructListByExp(SamlType curStruct);

    String[] parseStructByExp(SamlType curStruct);

    String parseInnerStruct(String body);

    String[] splitNameBody(String exp);

    void generFuncListByExp(SamlType curFunc);

    String[] parseFuncByExp(String exp);

    String alias2OriginalType(String exp);
}
