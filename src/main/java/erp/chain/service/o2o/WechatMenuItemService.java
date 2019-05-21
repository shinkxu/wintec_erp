package erp.chain.service.o2o;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信消息
 * Created with IntelliJ IDEA.
 * User: AnCong
 * Date: 2017/1/16
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = {java.lang.RuntimeException.class}, timeout = 120)
public class WechatMenuItemService{
}
