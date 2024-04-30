package erlikh.yaroslav.hw4.model.basemodel

interface BaseModel {

    Map<String, Object> attributes = new HashMap<>()

    default Object getAttribute(String propName) {
        attributes.get(propName)
    }

    default setAttribute(String propName, Object value) {
        attributes.put(propName, value)
    }
}