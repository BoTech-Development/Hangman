package Server.Services.Protocol;

import Abitur.List;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BuilderBase
{
    public String CreateParamStringForObject(Object object)
    {
        String result = "";
        String newParamString = "";
        boolean firstIteration = true;
        for (Field field : object.getClass().getFields()) {
            if (!newParamString.isEmpty() && !firstIteration) result += ",";
            if (Modifier.isPublic(field.getModifiers())) {
                newParamString = CreatParamString("", object, field);
                if (!newParamString.isEmpty()) result += newParamString;
            }
            firstIteration = false;
        }
        if (result.endsWith(",")) result = result.substring(0, result.length() - 1);
        return result;
    }
    private String CreatParamString(String paramString, Object object, Field field)
    {
        // Since a string and a character are an object in java , they are not considered primitive types.
        if(field.getType().isPrimitive() || field.getType().getName().contains("String") || field.getType().getName().contains("Character"))
        {
            try {
                paramString += field.getName() + "=" + field.get(object).toString();// + ",";
                return paramString;
            } catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        else
        {
            if(field.getType().getName().contains("List"))
            {
                try {
                    Abitur.List<?> list = (List<?>) field.get(object);
                    // It is invalid to create this String which represents an empty Collection: CollectionName={}
                    if(!list.isEmpty()) {
                        list.toFirst();
                        paramString += field.getName() + "=" + "{";
                        while (list.hasAccess()) {
                            Object listContent = list.getContent();
                            if (listContent != null) {
                                for (Field listContentField : listContent.getClass().getFields())
                                    if (Modifier.isPublic(listContentField.getModifiers()))
                                        CreatParamString(paramString, listContent, listContentField);
                            } else {
                                paramString += field.getName() + "=" + field.get(object).toString();
                                //return paramString;
                            }
                            list.next();
                            // Add only a "," to the result string when there is a next item
                            if (list.hasAccess()) paramString += ",";
                        }
                        paramString += "}";
                        return paramString;
                    }
                }catch (Exception e){
                    System.out.println("Error by Serialization: Can not create a serialized List: " + e.getMessage());
                }
            }
            else
            {
                Field[] subFields = field.getType().getFields();
                if(HasNonStaticFields(subFields)) {
                    try {
                        Object subObject = field.get(object);
                        paramString += field.getName() + "={";
                        boolean firstIteration = true;
                        for (Field subField : subFields)
                        {
                            // Here it is important to only add an "," when there is next field
                            if(!firstIteration) paramString += ",";
                            if (Modifier.isPublic(subField.getModifiers()) && !Modifier.isStatic(subField.getModifiers())) {
                                paramString += CreatParamString("", subObject, subField);
                            }
                            firstIteration = false;
                        }
                        paramString += "}";
                        return paramString;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }else{
                    // There are no accessible Fields, which can be accessed directly without a getter or setter
                    try {

                        paramString += field.getName() + "=" + field.get(object).toString();// + ",";
                        return paramString;
                    }catch (Exception e){
                        System.out.println("Error by Serialization: Can not create a serialized field: " + e.getMessage());
                    }
                }
            }
        }
        return "";
    }
    private boolean HasNonStaticFields(Field[] fields){
        for(Field field : fields) if(!Modifier.isStatic(field.getModifiers())) return true;
        return false;
    }
}
