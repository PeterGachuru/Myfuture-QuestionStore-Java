package ke.co.myfuture.Myfuture.Commonauth.Auth.Role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * AccessRights enum contains all the atomic privileges in the system.
 * They should follow the convection:
 *
 * @ACCESSRIGHT("user friendly name", "category", "subcategory")
 * @Category: This represents the generalization under which the access right fall e.g. access control, cards,
 * nostros, etc
 * - category too should be named in a way that the user can understand as it will be used
 * to map the roles in the frontend.
 * @Subcategory: This represents the module under which the privilege fall, e.g. ATM, USERS,etc.
 * - the subcategory should be understandable to the normal user too as it will be presented to the user.
 * @NB: In a nutshell, anything inside the constructor i.e. brackets, will be visible to the user. kindly make sure
 * it can be understood by anyone within the bank sector.
 */

@Getter
@RequiredArgsConstructor
public enum AccessRight {
    MODIFY_TOPIC("Modify Topic","WRITE_CONTENT", "TOPICS");


    private final String name;
    private final String category;
    private final String subCategory;

    public static @Nullable AccessRight fromString(String value) {
        try {
            return AccessRight.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }
}
