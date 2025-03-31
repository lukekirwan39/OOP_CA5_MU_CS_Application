package org.example.server.DAOs.Interfaces;

import java.util.List;

public interface ConverterInterface
{
    String convertEntityToJson(Object entity);
    String convertListToJson(List<Object> entities);
}
