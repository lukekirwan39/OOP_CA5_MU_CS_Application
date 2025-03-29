package org.example.Interfaces;

import org.example.Json.JsonConverter;
import java.util.List;

public interface ConverterInterface
{
    String convertEntityToJson(Object entity);
    String convertListToJson(List<Object> entities);
}
