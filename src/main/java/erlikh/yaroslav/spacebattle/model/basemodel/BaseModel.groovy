package erlikh.yaroslav.spacebattle.model.basemodel

interface BaseModel {

    Object getProperty(String propName)

    void setProperty(String propName, Object newValue)
}