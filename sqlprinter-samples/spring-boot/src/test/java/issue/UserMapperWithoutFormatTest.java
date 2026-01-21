package issue;

import com.github.dreamroute.sqlprinter.boot.Application;
import com.github.dreamroute.sqlprinter.boot.domain.User;
import com.github.dreamroute.sqlprinter.boot.mapper.UserMapper;
import com.github.dreamroute.sqlprinter.starter.anno.EnableSQLPrinter;
import com.github.dreamroute.sqlprinter.starter.converter.def.DateConverter;
import com.github.dreamroute.sqlprinter.starter.converter.def.EnumConverter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author w.dehai
 */
@SpringBootTest(classes = Application.class)
@EnableSQLPrinter(converters = {DateConverter.class, EnumConverter.class})
class UserMapperWithoutFormatTest {

    @Resource
    private UserMapper userMapper;

    @Test
    public void test() {
//        xxxEntityService.save(XxxEntity.builder().params("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><RequestCall><RequestName>SaltoDBAuditTrail.Read</RequestName></RequestCall>").build());

        User user = new User();
        user.setPassword("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><RequestCall><RequestName>SaltoDBAuditTrail.Read</RequestName><Params><MaxCount>10</MaxCount><StartingFromEventID>95631414</StartingFromEventID><ShowDoorIDAs>0</ShowDoorIDAs><ShowUserIDAs>1</ShowUserIDAs><FilterExpression>(Operation = 17)</FilterExpression></Params></RequestCall>\n");
        userMapper.insert(user);

    }

}
