package nl.tudelft.sem.template.authentication.domain.user;

import java.util.ArrayList;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Converter for the Role value object.
 */
@Converter
public class RolesAttributeConverter implements AttributeConverter<Roles, String> {
    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(Roles attribute) {
        return String.join(SPLIT_CHAR, attribute.getList());
    }

    @Override
    public Roles convertToEntityAttribute(String dbData) {
        String[] roleArray = dbData.split(SPLIT_CHAR);
        ArrayList<String> roleList = new ArrayList<>();
        for (String i : roleArray) {
            roleList.add(i);
        }
        return new Roles(roleList);
    }

}

