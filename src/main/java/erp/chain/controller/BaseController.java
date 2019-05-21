package erp.chain.controller;

import erp.chain.domain.Pos;
import erp.chain.service.PosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;

/**
 * Created by Xumx on 2015/9/18.
 */
public class BaseController {
    protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    PosService posService;

    protected Pos findPos(BigInteger id) throws Exception {
        Pos p = posService.findById(id);
        if (p == null) {
            throw new Exception("未查询到pos信息");
        }
        return p;
    }

}
