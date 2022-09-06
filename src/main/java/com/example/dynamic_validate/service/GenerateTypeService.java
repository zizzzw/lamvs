package com.example.dynamic_validate.service;

import com.example.dynamic_validate.entity.SamlType;

public interface GenerateTypeService {
    void generLevel4_6ByPath(int pro);

    void generFileByPath(int pro);

    void generOneFileByPath(String path, int pro);

    void updateFatherByPath(int pro);

    void generNameByExp(int pro);

    void generfuncPointerNameByExp(int pro);

    void generOneNameByExp(SamlType t);

    void generNameByExp(String path, int pro);

    void generLevel4_6ByPath(String path, int pro);

    void dealTypedef(int pro);

    void typeDistinctByExp();

    void typeDistinct();

    void includeDistinct();

    void generIncludeName();

}
