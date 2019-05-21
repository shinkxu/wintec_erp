package erp.chain.controller.online;

import erp.chain.service.online.ReadWriteSplittingService;
import erp.chain.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

/**
 * Created by liuyandong on 2018-07-17.
 */
@Controller
@RequestMapping(value = "/readWriteSplitting")
public class ReadWriteSplittingController {
    @Autowired
    private ReadWriteSplittingService readWriteSplittingService;

    @RequestMapping(value = "read", method = RequestMethod.GET)
    @ResponseBody
    public String read() throws SQLException {
        return BeanUtils.toJsonStr(readWriteSplittingService.read());
    }

    @RequestMapping(value = "write", method = RequestMethod.GET)
    @ResponseBody
    public String write() throws SQLException {
        return BeanUtils.toJsonStr(readWriteSplittingService.write());
    }
}
