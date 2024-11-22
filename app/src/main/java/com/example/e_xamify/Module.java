package com.example.e_xamify;

public class Module {
    private int module_id;
    private String module_name;
    private int institution_id;

    public Module(int module_id, String module_name, int institution_id){
        this.module_id = module_id;
        this.module_name = module_name;
        this.institution_id = institution_id;
    }

    public int getModuleId(){
        return module_id;
    }

    public String getModuleName(){
        return module_name;
    }

    public int getInstitutionId(){
        return institution_id;
    }

    @Override
    public String toString(){
        return module_name;
    }
}
