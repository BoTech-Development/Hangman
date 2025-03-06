package Server.Services.Protocol;

import Server.Models.Protocol.Request;
import Server.Models.Protocol.Response;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RequestInvoker
{
    private Object controller;
    private int requestId = 0;
    private final HashMap<String, ArrayList<String>> methodDefinitions = new HashMap<>();
    public RequestInvoker(Object controller){
        this.controller = controller;
        methodDefinitions.put("Register", new ArrayList<>(Arrays.asList("ipAddress", "hostname", "userName", "password", "email")));
        methodDefinitions.put("Login", new ArrayList<>(Arrays.asList("userName", "password", "ipAddress", "hostname")));
    }
    public ResponseBuilder Invoke(Request request){

        try {
            Method[] endpoints = controller.getClass().getDeclaredMethods();
            Method endPointToInvoke = null;
            for(Method endPoint : endpoints){
                if(endPoint.getName().equals(request.EndPoint)){ endPointToInvoke = endPoint; break;}
            }
            if(endPointToInvoke != null) {
               /* Parameter[] paramInOrder = endPointToInvoke.getParameters();
                Object[] paramValues = new Object[paramInOrder.length];
                for (int i = 0; i < paramInOrder.length; i++) {
                    Object paramValue = request.Parameters.get(paramInOrder[i].getName());
                    if (paramValue != null) paramValues[i] = paramValue;
                }*/
                // Sorting values
                ArrayList<String> methodDefinition = methodDefinitions.get(request.EndPoint);
                Object[] paramValues = new Object[methodDefinition.size()];
                for(int i = 0; i < methodDefinition.size(); i++){
                    paramValues[i] = request.Parameters.get(methodDefinition.get(i));
                }
                try {
                    Object result = endPointToInvoke.invoke(controller, paramValues);
                    if (result != null) {
                        ResponseBuilder rb = new ResponseBuilder();
                        rb.Create(requestId, result);
                        return rb;
                    }

                } catch (Exception e) {
                    System.out.println("Error by Invoking the Given method: " + e.toString());
                }
            }else{
                System.out.println("Can not fetch method (Endpoint not found): " + request.EndPoint);
            }
        }catch (Exception e){
            System.out.println("Error by Loading Method params: " + e.toString());
        }
        return null;
    }
}
