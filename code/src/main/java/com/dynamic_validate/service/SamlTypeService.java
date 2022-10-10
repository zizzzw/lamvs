package com.dynamic_validate.service;

import com.dynamic_validate.entity.ErrorClassify;
import com.dynamic_validate.entity.SamlType;

import java.util.List;

public interface SamlTypeService {

    void typeCheckByLevel(int level, int pro, String rep_id);

    int[] getFuncIds(String invocExp);

    List<Integer> getFuncDependParaAndReturn(SamlType func);

    List<Integer> getFuncRecurFathers(SamlType func);

    List<SamlType> getFuncRecurFathers(SamlType func, List<ErrorClassify> errList);

    boolean typeCheckById(int id);

    int typeExist(String type_name);

    List<Integer> mergeNoRepeat(List<Integer> list1, List<Integer> list2);

    boolean typeExist(int typeId, int level);

    List<SamlType> getMembersFromList(String ids);

    void typeSave(List<String> list, String filePath, int pro);
}
