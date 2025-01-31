package Server.Services;


import java.util.function.Function;
import Abitur.List;
import Server.Models.User;

public class ListService {
    public static Function<User, Boolean> GetStandardUserSearchMethod(String username, String password)
    {
        Function<User, Boolean> searchUserFunc = (User) -> {
            return User.UserName.equals(username) && User.Password.equals(password);
        };
        return searchUserFunc;
    }
    public static <T> List<T> FindAll(List<T> list, Function<T, Boolean> searchFunction)
    {
        List<T> resultList = new List<T>();
        list.toFirst();
        while (list.hasAccess())
        {
            if(searchFunction.apply(list.getContent())) resultList.append(list.getContent());
            list.next();
        }
        return resultList;
    }
    public static <T> T Find(List<T> list, Function<T, Boolean> searchFunction)
    {
        list.toFirst();
        while (list.hasAccess())
        {
            if(searchFunction.apply(list.getContent())) return list.getContent();
            list.next();
        }
        return null;
    }
}
