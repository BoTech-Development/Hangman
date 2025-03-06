package Server.Services.Protocol;

import Abitur.List;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedList;

public class DecoderBase
{
    void RemoveLineBreakFromLines(String[] lines){
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replaceAll("\\r?\\n", "");
        }
    }
    public Object DecodePropertiesString(String line, Class<?> type)
    {
        PropertyNode parentNode = CreatePropertyNodesFromString(line);

        return CreateObjectForPropertyNode(parentNode, type);
    }
    private PropertyNode CreatePropertyNodesFromString(String line){

        PropertyNode mostParentNode;

        PropertyNode parentNode = new PropertyNode();
        parentNode.PropertyName = "!PARENT!";

        mostParentNode = parentNode;

        PropertyNode currentNode = new PropertyNode();
        parentNode.Children.append(currentNode);


        String propertyName = "";
        String propertyValue = "";
        ReadStatus status = ReadStatus.ReadingPropertyName;
        char[] chars = line.toCharArray();
        Character c;
        for(int i = 0; i < chars.length; i++)
        {
            c = chars[i];
            switch(c){
                case '{':
                    if(status == ReadStatus.ReadingPropertyValue){
                        parentNode = currentNode;
                        currentNode = new PropertyNode();
                        parentNode.Children.append(currentNode);
                        currentNode.Parent = parentNode;

                        // Change Status to Reading name because after a Bracket a PropertyName must follow
                        status = ReadStatus.ReadingPropertyName;
                        // In this case the Parent Node will have an empty PropertyValue because the Value will be loaded in the currentNode
                        // But the Name must be set to resolve the parent Value
                        parentNode.PropertyName = propertyName;
                        // Resetting Property Name for the next iteration
                        propertyName = "";
                    }
                    break;
                case '}':
                    if(propertyName.isEmpty() && propertyValue.isEmpty()){
                        // Remove the empty Node
                        parentNode.Children.remove();
                    }
                    else
                    {
                        //Saving the last Value
                        currentNode.PropertyName = propertyName;
                        currentNode.PropertyValue = propertyValue;
                    }
                    // Resetting Buffer Values:
                    propertyName = "";
                    propertyValue = "";
                    // Backtracking
                    currentNode = parentNode;
                    parentNode = parentNode.Parent;
                    // Resetting Current Status
                    status = ReadStatus.ReadingPropertyName;
                    break;
                case '=':
                    status = ReadStatus.ReadingPropertyValue;
                    break;
                case ',':
                    // Fixes the issue that the name game will be deleted in this case, when the current Character c is the , next to the }
                    // Example: ID=0,Current={Game={ID=0,Word=Hellord,Level=0},Tries=0
                    if(chars[i-1] != '}') {
                        currentNode.PropertyName = propertyName;
                        currentNode.PropertyValue = propertyValue;
                    }
                    status = ReadStatus.ReadingPropertyName;

                    // Creating a new Node
                    currentNode = new PropertyNode();
                    parentNode.Children.append(currentNode);
                    currentNode.Parent = parentNode;

                    // Resetting Buffer Values:
                    propertyName = "";
                    propertyValue = "";
                    break;
                default:
                    if(status == ReadStatus.ReadingPropertyName){
                        propertyName += c;
                    }else if(status == ReadStatus.ReadingPropertyValue){
                        propertyValue += c;
                    }
                    break;
            }
        }
        if(!propertyName.isEmpty() && !propertyValue.isEmpty()){
            // Saving the Last Node => When this is the End: UserName=FloFlo,Age=18
            currentNode.PropertyName = propertyName;
            currentNode.PropertyValue = propertyValue;
        }

        return mostParentNode;
    }
    private Object CreateObjectForPropertyNode(PropertyNode node, Class<?> Class)
    {
         try {
             Object object = Class.getDeclaredConstructor().newInstance();
             node.Children.toFirst();
             while (node.Children.hasAccess()) {
                 Field field = Class.getDeclaredField(node.Children.getContent().PropertyName);
                 if(node.Children.getContent().PropertyValue != null) {
                     TryToSetField(object, field, node.Children.getContent().PropertyValue);
                 }
                 else
                 {
                     // The current Field is a Sub Object
                     try {
                         field.set(object, CreateObjectForPropertyNode(node.Children.getContent(), field.getType()));
                     }catch (Exception e){
                         System.out.println("SUB_Field can not be set: " + field.getType().getName());
                     }
                 }
                 node.Children.next();
             }
             return object;
         }catch (Exception e){
             System.out.println("Error creating object for " + Class + ", Can not find a Constructor with no params: Exception MSG: " + e.getMessage());
         }
        return null;
    }
    Object TryToParsePrimitive(String primitiveValue, String typeName)
    {
        try
        {
            switch (typeName) {
                case "int":
                    return Integer.parseInt(primitiveValue);
                case "short":
                    return Short.parseShort(primitiveValue);
                case "long":
                    return Long.parseLong(primitiveValue);
                case "float":
                    return Float.parseFloat(primitiveValue);
                case "double":
                    return Double.parseDouble(primitiveValue);
                case "boolean":
                    return Boolean.parseBoolean(primitiveValue);
                case "byte":
                    return Byte.parseByte(primitiveValue);
                case "char":
                    return primitiveValue.toCharArray()[0];
                case "java.lang.String":
                    return primitiveValue;
                case "java.time.LocalDateTime":
                    return java.time.LocalDateTime.parse(primitiveValue);
            }
        }
        catch(Exception e)
        {
            System.out.println("Can not parse: " + primitiveValue + " to an :" + typeName);
        }
        return null;
    }
    private void TryToSetField(Object object, Field field, String value)
    {
        try
        {
            switch (field.getType().getName()) {
                case "int":
                    field.set(object, Integer.parseInt(value));
                    break;
                case "short":
                    field.set(object, Short.parseShort(value));
                    break;
                case "long":
                    field.set(object, Long.parseLong(value));
                    break;
                case "float":
                    field.set(object, Float.parseFloat(value));
                    break;
                case "double":
                    field.set(object, Double.parseDouble(value));
                    break;
                case "boolean":
                    field.set(object, Boolean.parseBoolean(value));
                    break;
                case "byte":
                    field.set(object, Byte.parseByte(value));
                    break;
                case "char":
                    field.set(object, value.toCharArray()[0]);
                    break;
                case "java.lang.String":
                    field.set(object, value);
                    break;
                case "java.time.LocalDateTime":
                    field.set(object, java.time.LocalDateTime.parse(value));
                    break;
            }
        }
        catch(Exception e)
        {
            System.out.println("Field can not be set: " + field.getType().getName());
        }
    }

    private enum ReadStatus{
        ReadingPropertyName,
        ReadingPropertyValue,
    }
    private class PropertyNode{
        public String PropertyName;
        public String PropertyValue;
        public List<PropertyNode> Children = new List<PropertyNode>();
        public PropertyNode Parent;
        public PropertyNode GetMostParentNode(){
            if(PropertyName.equals("!PARENT!")) {
                return this;
            }else if(Parent != null) {
                return Parent.GetMostParentNode();
            }
            PropertyNode parent = null;
            Children.toFirst();
            while (Children.hasAccess()) {
                parent = Children.getContent().GetMostParentNode();
                if(parent != null) return parent;
                Children.next();
            }
            return null;
        }
    }
}
