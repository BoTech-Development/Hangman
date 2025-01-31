package Server.Services;

import java.lang.reflect.Field;
import java.util.Arrays;

/// <summary>
/// Handles all Unique Identifiers of each Class which contains the Property ID.
/// </summary>
public class IDProvider
{
    private String[] AcceptedTypes = {"Session", "User", "Game"};
    private int CurrentUserID = 0;
    private int CurrentSessionID = 0;
    private int CurrentGameID = 0;
    public static final IDProvider Instance = new IDProvider();
    private IDProvider()
    {

    }
    public boolean GetAndSetNewIDForObject(Object obj)
    {
        if(IsInAcceptedTypes(obj.getClass().getName()))
        {
            Field[] fields = obj.getClass().getFields();
            if(fields.length > 0) {
                for (Field field : fields) {
                    if (field.getName().equals("ID"))
                    {
                        try {
                            switch (obj.getClass().getName()) {
                                case "Session":
                                    CurrentSessionID++;
                                    field.setInt(obj, CurrentSessionID);
                                    return true;
                                case "User":
                                    CurrentUserID++;
                                    field.setInt(obj, CurrentUserID);
                                    return true;
                                case "Game":
                                    CurrentGameID++;
                                    field.setInt(obj, CurrentGameID);
                                    return true;
                            }
                        }catch (Exception e) {
                            System.out.println(e);
                        }

                    }
                }
            }
        }
        return false;
    }
    private boolean IsInAcceptedTypes(String className) {
        for (String acceptedType : AcceptedTypes) {
            if (className.equals(acceptedType)) return true;
        }
        return false;
    }
}
