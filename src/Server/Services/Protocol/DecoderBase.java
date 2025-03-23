package Server.Services.Protocol;

import Abitur.List;
import Server.Annotations.SerializableCollection;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Objects;

public class DecoderBase
{
    void RemoveLineBreakFromLines(String[] lines){
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].replaceAll("\\r?\\n", "");
        }
    }
    public Object DecodePropertiesString(String line, Class<?> type)
    {

        //Removing the first and last curly bracket:
        if(line.startsWith("{") && line.endsWith("}")) line = line.substring(1, line.length()-1);

        //PropertyNode parentNode = CreatePropertyNodesFromString(line);
        BracketContent parentBracketContent = new BracketContent();
        parentBracketContent.Name = "MOST PARENT";
        DivideString(line,0, parentBracketContent, "");
        PropertyNode parentPropertyNode = new PropertyNode();
        parentPropertyNode.PropertyName = "Most Parent";
        parentBracketContent.Children.toFirst();
        CreatePropertyNodesFromBracketContent(parentBracketContent.Children.getContent(), parentPropertyNode);
        try {
            Object instance = type.getConstructor().newInstance();
            parentPropertyNode.Children.toFirst();
            CreateObjectForPropertyNode(parentPropertyNode.Children.getContent(), type, instance);
            System.out.println("Created " + type + " instance");
            return instance;
        }catch (Exception e){
            System.out.println("Error by invoking constructor: " + e.getMessage());
        }
        return null;

        //return CreateObjectForPropertyNode(parentNode, type);
    }


    private int DivideString(String line, int startIndex, BracketContent Parent, String Name){
        BracketContent current = new BracketContent();
        current.Parent = Parent;
        current.Name = Name;
        Parent.Children.append(current);

        boolean readingCollection = false;
        char[] letters = line.toCharArray();
        int nextIndex = startIndex;
        for(int i = startIndex; i < letters.length; i++){
            if(letters[i] == '{'){
                // it is only a new Item of the Collection when there a to curly brackets successively. => Games={{ID=0,Word=Hellord},{ID=1,Word=Bello},...}
                if(!Name.isEmpty() && (( i - startIndex == 1 || i - startIndex == 0) || readingCollection)) {

                    // It is item of the Collection. Therefore, there is no Name of the Collection
                    i = DivideString(line, i + 1, current, "");
                    nextIndex = i;
                    readingCollection = true;

                }else {
                    // it is a new Collection
                    int kommaIndex = 0;
                    // Find the index of the komma to separate the collection Name
                    for (int j = i -1; j > 0; j--) {
                        if (letters[j] == ',' || letters[j] == '{') {
                            kommaIndex = j;
                            break;
                        }
                    }
                    // The collection-name shouldn't be stored in the current Content => will be stored in the Name of the next Child BracketContent
                    if(nextIndex < kommaIndex) current.Content += line.substring(nextIndex, kommaIndex);
                    String collectionName = line.substring(kommaIndex + 1, i - 1); // + 1, because we do need the "," in the collection-name // - 1 because we don't need the "=" letter in the collection-name
                    i = DivideString(line, i + 1, current, collectionName);
                    nextIndex = i;
                }
            }
            if(i >= letters.length){
                System.out.println("Success: " + line.substring(nextIndex, i));
                current.Content += line.substring(nextIndex, i);
                return 0; // Success
            }
            if(letters[i] == '}'){
                System.out.println(line.substring(nextIndex, i));
                current.Content += line.substring(nextIndex, i);
                return i + 1;
            }
        }
        // if it is only one Line: For instance {Name=Florian,ID=24235}
        current.Content = line;

        return -1;
    }
  /*  private void CreatePropertyNodesFromBracketContent(BracketContent currentBracketContent, PropertyNode parentPropertyNode ){
        PropertyNode newPropertyNode = new PropertyNode();
        newPropertyNode.Parent = parentPropertyNode;
        parentPropertyNode.Children.append(newPropertyNode);
        // Initialize String otherwise you can not invoke .equals() in the CreateObjectForPropertyNode Method.
        newPropertyNode.PropertyName = "";

        if(!currentBracketContent.Name.isEmpty()){
            // it is a collection
            newPropertyNode.PropertyName = currentBracketContent.Name;
            newPropertyNode.PropertyValue = "COLLECTION"; // See in the Children
            currentBracketContent.Children.toFirst();
            // Build a PropertyNode for each item in the Collection
            while(currentBracketContent.Children.hasAccess()){
                CreatePropertyNodesFromBracketContent(currentBracketContent.Children.getContent(), newPropertyNode);
                currentBracketContent.Children.next();
            }
        }else{
            newPropertyNode.PropertyValue = "FIELDS";
           // newPropertyNode.PropertyName = currentBracketContent.Name;
            // it is a normal Param String such as : Age=18,Name=Florian,FavouriteProgrammingLanguage=c#
            String[] fields = currentBracketContent.Content.split(",");
            for(String field : fields){
                String[] property = field.split("=");
                String propertyName = field.split("=")[0];

                // There might be no value set: Name=,Age=10
                String propertyValue = "";
                if(property.length == 2){
                     propertyValue = field.split("=")[1];
                }
                PropertyNode fieldPropertyNode = new PropertyNode();
                fieldPropertyNode.Parent = newPropertyNode;
                newPropertyNode.Children.append(fieldPropertyNode);
                fieldPropertyNode.PropertyName = propertyName;
                fieldPropertyNode.PropertyValue = propertyValue;
            }
        }
        if(!currentBracketContent.Children.isEmpty() && currentBracketContent.Name.isEmpty()){
            currentBracketContent.Children.toFirst();
            while(currentBracketContent.Children.hasAccess()){
                CreatePropertyNodesFromBracketContent(currentBracketContent.Children.getContent(), newPropertyNode);
                currentBracketContent.Children.next();
            }
        }
    }*/

    private void CreatePropertyNodesFromBracketContent(BracketContent currentBracketContent, PropertyNode parentPropertyNode ){
        // Fields
        if(currentBracketContent.Name.isEmpty() && !currentBracketContent.Content.isEmpty()){
            PropertyNode newPropertyNode = new PropertyNode();
            newPropertyNode.Parent = parentPropertyNode;
            newPropertyNode.PropertyName = "";
            newPropertyNode.PropertyValue = "";
            newPropertyNode.PropertyType = PropertyType.Fields;
            parentPropertyNode.Children.append(newPropertyNode);
            // Adding all Fields
            String[] fields = currentBracketContent.Content.split(",");
            for(String field : fields){
                String[] property = field.split("=");
                String propertyName = field.split("=")[0];

                // There might be no value set: Name=,Age=10
                String propertyValue = "";
                if(property.length == 2){
                    propertyValue = field.split("=")[1];
                }
                PropertyNode fieldPropertyNode = new PropertyNode();
                fieldPropertyNode.Parent = newPropertyNode;
                newPropertyNode.Children.append(fieldPropertyNode);
                fieldPropertyNode.PropertyName = propertyName;
                fieldPropertyNode.PropertyValue = propertyValue;
                fieldPropertyNode.PropertyType = PropertyType.Field;
            }

            // Read all Children
            // Could be other Named Fields or Collections
            currentBracketContent.Children.toFirst();
            while (currentBracketContent.Children.hasAccess()){
                CreatePropertyNodesFromBracketContent(currentBracketContent.Children.getContent(), newPropertyNode);
                currentBracketContent.Children.next();
            }
        }
        // NamedFields => Object which is stored in another object
        if(!currentBracketContent.Name.isEmpty() && !currentBracketContent.Content.isEmpty()){
            PropertyNode newPropertyNode = new PropertyNode();
            newPropertyNode.Parent = parentPropertyNode;
            newPropertyNode.PropertyName = currentBracketContent.Name; // Important
            newPropertyNode.PropertyValue = "";
            newPropertyNode.PropertyType = PropertyType.NamedFields;
            parentPropertyNode.Children.append(newPropertyNode);

            // Adding all Fields
            String[] fields = currentBracketContent.Content.split(",");
            for(String field : fields){
                String[] property = field.split("=");
                String propertyName = field.split("=")[0];

                // There might be no value set: Name=,Age=10
                String propertyValue = "";
                if(property.length == 2){
                    propertyValue = field.split("=")[1];
                }
                PropertyNode fieldPropertyNode = new PropertyNode();
                fieldPropertyNode.Parent = newPropertyNode;
                newPropertyNode.Children.append(fieldPropertyNode);
                fieldPropertyNode.PropertyName = propertyName;
                fieldPropertyNode.PropertyValue = propertyValue;
                fieldPropertyNode.PropertyType = PropertyType.Field;
            }

            // Read all Children
            // Could be other Named Fields or Collections
            currentBracketContent.Children.toFirst();
            while (currentBracketContent.Children.hasAccess()){
                CreatePropertyNodesFromBracketContent(currentBracketContent.Children.getContent(), newPropertyNode);
                currentBracketContent.Children.next();
            }
        }
        // Collection
        if(!currentBracketContent.Name.isEmpty() && currentBracketContent.Content.isEmpty()){
            PropertyNode newPropertyNode = new PropertyNode();
            newPropertyNode.Parent = parentPropertyNode;
            newPropertyNode.PropertyName = currentBracketContent.Name; // Important
            newPropertyNode.PropertyValue = "";
            newPropertyNode.PropertyType = PropertyType.Collection;
            parentPropertyNode.Children.append(newPropertyNode);
            // Read all Children
            // Could be other Named Fields or Collections
            currentBracketContent.Children.toFirst();
            while (currentBracketContent.Children.hasAccess()){
                CreatePropertyNodesFromBracketContent(currentBracketContent.Children.getContent(), newPropertyNode);
                currentBracketContent.Children.next();
            }
        }
    }

    @Deprecated(forRemoval = true)
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

    /*private void CreateObjectForPropertyNode(PropertyNode node, Class<?> Class, Object object){
        if(node.PropertyName.equals("Most Parent")){
            System.out.println("Error: You can not Invoke the CreateObjectForPropertyNode() method, with the Most Parent property node.");
        }else if(node.PropertyValue.equals("COLLECTION")){
             // At this Point the Protocol is a little bit inaccurate
            // There could be two Options
            // 1. It is real Collection
            // 2. It is only an Object which is saved in another Object
            // get the Collection or Object Field:
            Field collectionField = null;
            Field objectField = null;
            Field[] fields = Class.getFields();
            for(Field field : fields) {
                if(field.getName().equals(node.PropertyName)){
                    if(field.getType().equals(List.class)){
                        collectionField = field;
                    }else{
                        objectField = field;
                    }
                }
            }
            if(collectionField != null){
                // Is needed to create a new Instance for an Item of the collection. Therefore, it is necessary to know which Generic Type the collection based on.
                SerializableCollection annotation = collectionField.getAnnotation(SerializableCollection.class);
                if(annotation != null) {
                    try {
                        List<Object> collection = (List<Object>) collectionField.getType().getConstructor().newInstance();
                        node.Children.toFirst();
                        while (node.Children.hasAccess()) {
                            Object newItem = annotation.GenericType().getConstructor().newInstance();
                            if(newItem != null) {
                                collection.append(newItem);
                                CreateObjectForPropertyNode(node.Children.getContent(), newItem.getClass(), newItem);
                            }
                            node.Children.next();
                        }
                        collectionField.set(object, collection);

                    } catch (Exception e) {
                        System.out.println("Error by creating a new Collection for " + collectionField + ", Error MSG: " + e.getMessage());
                    }
                }
            }else if(objectField != null){
                try {
                    Object newObject = objectField.getType().getConstructor().newInstance();
                    if(newObject != null) {
                        //Set all Vars or Collection of this Object
                        node.Children.toFirst();
                        while (node.Children.hasAccess()) {
                            CreateObjectForPropertyNode(node.Children.getContent(), newObject.getClass(), newObject);
                            node.Children.next();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error by creating a new Object for " + objectField + ", Error MSG: " + e.getMessage());
                }
            }
        }else if(node.PropertyValue.equals("FIELDS")){
            node.Children.toFirst();
            while(node.Children.hasAccess()){
                // by invoking the method again, the Method will execute the last if Statement or tries to convert the collection.
                CreateObjectForPropertyNode(node.Children.getContent(), Class,object);
                node.Children.next();
            }
        }else{
            Field[] fields = Class.getFields();
            for(Field field : fields){
                // TODO: Check if Field is accessible
                if(field.getName().equals(node.PropertyName) ){
                    Object newValue = TryToParsePrimitive(node.PropertyValue, field.getType().getName());
                    if(object != null){
                        try {
                            field.set(object, newValue);
                            return;
                        }catch (IllegalAccessException e) {
                            System.out.println("Error by creating object for PropertyNode: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }*/
    private void CreateObjectForPropertyNode(PropertyNode node, Class<?> Class, Object object){
       switch(node.PropertyType){
           case Field:
               Field[] fields = Class.getFields();
               for(Field field : fields){
                   // TODO: Check if Field is accessible
                   if(field.getName().equals(node.PropertyName) ){
                       Object newValue = TryToParsePrimitive(node.PropertyValue, field.getType().getName());
                       if(object != null){
                           try {
                               field.set(object, newValue);
                               return;
                           }catch (IllegalAccessException e) {
                               System.out.println("Error by creating object for PropertyNode: " + e.getMessage());
                           }
                       }
                   }
               }
               break;
           case Collection:
               Field collectionField = null;
               for(Field field : Class.getFields()) {
                   if(field.getName().equals(node.PropertyName)){
                       if(field.getType().equals(List.class)){
                           collectionField = field;
                       }
                   }
               }
               if(collectionField != null) {
                   // Is needed to create a new Instance for an Item of the collection. Therefore, it is necessary to know which Generic Type the collection based on.
                   SerializableCollection annotation = collectionField.getAnnotation(SerializableCollection.class);
                   if (annotation != null) {
                       try {
                           List<Object> collection = (List<Object>) collectionField.getType().getConstructor().newInstance();
                           node.Children.toFirst();
                           while (node.Children.hasAccess()) {
                               Object newItem = annotation.GenericType().getConstructor().newInstance();
                               if (newItem != null) {
                                   collection.append(newItem);
                                   CreateObjectForPropertyNode(node.Children.getContent(), newItem.getClass(), newItem);
                               }
                               node.Children.next();
                           }
                           collectionField.set(object, collection);

                       } catch (Exception e) {
                           System.out.println("Error by creating a new Collection for " + collectionField + ", Error MSG: " + e.getMessage());
                       }
                   }
               }
               break;
           case Fields:
               node.Children.toFirst();
               while(node.Children.hasAccess()){
                   CreateObjectForPropertyNode(node.Children.getContent(), Class, object);
                   node.Children.next();
               }
               break;
           case NamedFields:
               Field objectField = null;
               for(Field field : Class.getFields()) {
                   if(field.getName().equals(node.PropertyName)){
                       if(!field.getType().equals(List.class)){
                           objectField = field;

                       }
                   }
               }
               if(objectField != null) {
                   try {
                       Object newObject = objectField.getType().getConstructor().newInstance();
                       if (newObject != null) {
                           //Set all Vars or Collection of this Object
                           node.Children.toFirst();
                           while (node.Children.hasAccess()) {
                               CreateObjectForPropertyNode(node.Children.getContent(), newObject.getClass(), newObject);
                               node.Children.next();
                           }
                           objectField.set(object, newObject);
                       }
                   } catch (Exception e) {
                       System.out.println("Error by creating a new Object for " + objectField + ", Error MSG: " + e.getMessage());
                   }
               }
               break;
       }
    }
    /*    private void TryToSetCollectionField(Object object, Field field, List<Object> collection){
        collection.toFirst();
        while(collection.hasAccess()){

            collection.next();
        }
    }*/
    @Deprecated(forRemoval = true)
    private Object CreateObjectForPropertyNode(PropertyNode node, Class<?> Class)
    {
         try {
             Object object = Class.getDeclaredConstructor().newInstance();
             node.Children.toFirst();
             while (node.Children.hasAccess()) {
                 if (node.Children.getContent().PropertyName != "") {
                     Field field = Class.getDeclaredField(node.Children.getContent().PropertyName);
                     if (node.Children.getContent().PropertyValue != null) {
                         //TryToSetField(object, field, node.Children.getContent().PropertyValue);
                     } else {
                         // The current Field is a Sub Object
                         try {
                             field.set(object, CreateObjectForPropertyNode(node.Children.getContent(), field.getType()));
                         } catch (Exception e) {
                             System.out.println("SUB_Field can not be set: " + field.getType().getName());
                         }
                     }
                     node.Children.next();
                 }
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
                case "java.lang.Integer":
                    return Integer.parseInt(primitiveValue);
                case "java.lang.Short":
                    return Short.parseShort(primitiveValue);
                case "java.lang.Long":
                    return Long.parseLong(primitiveValue);
                case "java.lang.Float":
                    return Float.parseFloat(primitiveValue);
                case "java.lang.Double":
                    return Double.parseDouble(primitiveValue);
                case "java.lang.Boolean":
                    return Boolean.parseBoolean(primitiveValue);
                case "java.lang.Byte":
                    return Byte.parseByte(primitiveValue);
                case "java.lang.Character":
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
/*
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
    }*/

    private enum ReadStatus{
        ReadingPropertyName,
        ReadingPropertyValue,
    }
    private class BracketContent{
        String Content = "";
        String Name = ""; // Name of Property
        BracketContent Parent = null;
        List<BracketContent> Children = new List<>();
    }

    private class PropertyNode{
        public String PropertyName;
        public String PropertyValue;
        public PropertyType PropertyType = DecoderBase.PropertyType.None;
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
    private enum PropertyType{
        None,
        Fields,
        Collection,
        NamedFields,
        Field
    }
}
