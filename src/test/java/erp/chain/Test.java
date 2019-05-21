package erp.chain;

import erp.chain.model.SearchModel;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class Test {

    @org.junit.Test
    public void testCamelToUnderscore() {
        SearchModel.camelToUnderscore("QWERTYUIOPLKJHGFDSAZXCVBNM");
        StringUtils.splitByCharacterTypeCamelCase("QWERTYUIOPLKJHGFDSAZXCVBNM");
    }
}
