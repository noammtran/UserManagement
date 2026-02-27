// package com.user_servce.back_end.dto;

// import java.io.IOException;

// import com.fasterxml.jackson.core.JsonGenerator;
// import com.fasterxml.jackson.databind.JsonSerializer;
// import com.fasterxml.jackson.databind.SerializerProvider;

// public class StatusFlagSerializer extends JsonSerializer<Integer> {

//     @Override
//     public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//         if (value == null) {
//             gen.writeNull();
//             return;
//         }
//         switch (value) {
//             case 1 -> gen.writeString("Active");
//             case 0 -> gen.writeString("Inactive");
//             default -> gen.writeString(String.valueOf(value));
//         }
//     }
// }
