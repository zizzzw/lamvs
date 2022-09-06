package com.example.dynamic_validate.service;

import com.example.dynamic_validate.entity.SamlList;

public interface SamlListService {

    void listCheck();

    boolean checkOneList(SamlList samlList);

    int insertUpdateListId(String members_str, int listCompound);

    int insertUpdateListId(String members_str, String members_int, int listCompound);
}
