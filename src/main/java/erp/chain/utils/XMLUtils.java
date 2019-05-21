package erp.chain.utils;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyandong on 2017/3/21.
 */
public class XMLUtils{
    public static Map<String, String> xmlToMap(InputStream inputStream) throws DocumentException{
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> nodes = rootElement.elements();
        Map<String, String> map = new HashMap<String, String>();
        for(Element element : nodes){
            map.put(element.getName(), element.getText());
        }
        return map;
    }

    /**
     * JSON 转换为XML
     * <p>
     * 2017年4月27日 15:34:24
     *
     * @param json
     * @return
     */

    public static String jsonToXML(String json){
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setRootName("xml");
        xmlSerializer.setTypeHintsEnabled(false);
        String xmlStr = "";
        JSONObject jobj = JSONObject.fromObject(json);
        xmlStr = xmlSerializer.write(jobj);
        //System.out.println("转换后的参数：" + xmlStr);
        return xmlStr;
    }
}
