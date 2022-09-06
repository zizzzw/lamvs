package com.example.dynamic_validate.service;

public interface IncludeService {
    void finishQuestExp(int pro);

    void generResult(int pro);

    void generIncludeOrigin(int pro, String rep_id);

    void copyAllIncludeFile(int pro, String rep_id);

    void generNameAndOrigin(int level, int pro);
}
