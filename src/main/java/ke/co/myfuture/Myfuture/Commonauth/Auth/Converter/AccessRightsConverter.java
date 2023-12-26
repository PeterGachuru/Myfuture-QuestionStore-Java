package ke.co.myfuture.Myfuture.Commonauth.Auth.Converter;

import ke.co.myfuture.Myfuture.Commonauth.Auth.Role.AccessRight;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Converter
public class AccessRightsConverter implements AttributeConverter <List<AccessRight>, String>{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(List<AccessRight> accessRights) {
        return accessRights != null && !accessRights.isEmpty() ? this.objectMapper.writeValueAsString(accessRights) : null;
    }

    @SneakyThrows
    @Override
    public List<AccessRight> convertToEntityAttribute(String s) {
//        return s != null && !s.isEmpty() ? this.objectMapper.readValue(s.trim(), new TypeReference<>() {}) : new ArrayList<>();
        if (s == null || s.isEmpty()) {
            return null;
        }

        String[] accessRightNames = this.objectMapper.readValue(s.trim(), String[].class);
        return Arrays.stream(accessRightNames)
                .filter(val ->{
                    AccessRight right = AccessRight.fromString(val);
                    return !Objects.isNull(right);
                }).map(AccessRight::fromString).collect(Collectors.toList());
    }
}
