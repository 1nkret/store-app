package com.example.store;

import com.google.gson.*;
import java.lang.reflect.Type;

public class BaseUserAdapter implements JsonSerializer<BaseUser>, JsonDeserializer<BaseUser> {

    @Override
    public JsonElement serialize(BaseUser src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("type", src.getClass().getSimpleName());
        result.addProperty("id", src.getId());
        result.addProperty("fullName", src.getFullName());
        result.addProperty("phone", src.getPhone());
        result.addProperty("login", src.getLogin());
        result.addProperty("role", src.getRole());

        // Используем рефлексию для доступа к passwordHash
        try {
            java.lang.reflect.Field field = BaseUser.class.getDeclaredField("passwordHash");
            field.setAccessible(true);
            String passwordHash = (String) field.get(src);
            result.addProperty("passwordHash", passwordHash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public BaseUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.get("id").getAsInt();
        String fullName = jsonObject.get("fullName").getAsString();
        String phone = jsonObject.get("phone").getAsString();
        String login = jsonObject.get("login").getAsString();
        String passwordHash = jsonObject.get("passwordHash").getAsString();
        String role = jsonObject.get("role").getAsString();

        if ("ADMIN".equals(role)) {
            return new Admin(id, fullName, phone, login, passwordHash);
        } else {
            return new Customer(id, fullName, phone, login, passwordHash);
        }
    }
}
