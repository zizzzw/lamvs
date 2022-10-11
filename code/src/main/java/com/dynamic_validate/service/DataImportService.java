package com.dynamic_validate.service;

public interface DataImportService {
    void generateAlias();

    void demandTree2SamlDemand();

    void importDemandTxt(String filePath, int pro);

    @Deprecated
    void finishDemandExcel(String filePath);

    void importDesignFromSource(String filePath, int pro);

    void importFromOneFile(String file, String filePath, int pro);

}
